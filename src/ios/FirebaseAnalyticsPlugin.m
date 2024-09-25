#import "FirebaseAnalyticsPlugin.h"
#import "OutSystems-Swift.h"

@import AppTrackingTransparency;
@import FirebaseAnalytics;
@import FirebaseCore;

@interface FirebaseAnalyticsPlugin ()

@property (strong, nonatomic) id<OSFANLManageable> manager;

@end

@implementation FirebaseAnalyticsPlugin

- (void)pluginInitialize {
    NSLog(@"Starting Firebase Analytics plugin");

    if(![FIRApp defaultApp]) {
        [FIRApp configure];
    }
    
    self.manager = [OSFANLManagerFactory createManager];
}

- (void)logEvent:(CDVInvokedUrlCommand *)command {
    NSString* name = [command.arguments objectAtIndex:0];
    NSDictionary* parameters = [command.arguments objectAtIndex:1];

    [FIRAnalytics logEventWithName:name parameters:parameters];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)logECommerceEvent:(CDVInvokedUrlCommand *)command {
    NSDictionary *argumentsDictionary = [command argumentAtIndex:0];
    NSError *error;
    
    OSFANLOutputModel *outputModel = [self.manager createEventModelFor:argumentsDictionary error:&error];
    if (!outputModel && error) {
        [self sendError:error forCallbackId:command.callbackId];
        return;
    }
    
    [FIRAnalytics logEventWithName:outputModel.name parameters:outputModel.parameters];
    [self sendSuccessfulResultforCallbackId:command.callbackId];
}

- (void)setUserId:(CDVInvokedUrlCommand *)command {
    NSString* id = [command.arguments objectAtIndex:0];

    [FIRAnalytics setUserID:id];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)setUserProperty:(CDVInvokedUrlCommand *)command {
    NSString* name = [command.arguments objectAtIndex:0];
    NSString* value = [command.arguments objectAtIndex:1];

    [FIRAnalytics setUserPropertyString:value forName:name];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)setEnabled:(CDVInvokedUrlCommand *)command {
    bool enabled = [[command.arguments objectAtIndex:0] boolValue];

    [FIRAnalytics setAnalyticsCollectionEnabled:enabled];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)setCurrentScreen:(CDVInvokedUrlCommand *)command {
    NSString* screenName = [command.arguments objectAtIndex:0];

    [FIRAnalytics logEventWithName:kFIREventScreenView parameters:@{
        kFIRParameterScreenName: screenName
    }];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)resetAnalyticsData:(CDVInvokedUrlCommand *)command {
    [FIRAnalytics resetAnalyticsData];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)setDefaultEventParameters:(CDVInvokedUrlCommand *)command {
    NSDictionary* params = [command.arguments objectAtIndex:0];

    [FIRAnalytics setDefaultEventParameters:params];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)requestTrackingAuthorization:(CDVInvokedUrlCommand *)command {
    
    bool showInformation = [[command.arguments objectAtIndex:0] boolValue];

    if(showInformation) {
        
        NSString* title = [command.arguments objectAtIndex:1];
        NSString* message = [command.arguments objectAtIndex:2];
        NSString* buttonTitle = [command.arguments objectAtIndex:3];
        
        [self showPermissionInformationPopup:title :message :buttonTitle :^(UIAlertAction *action ) {
            [self showTrackingAuthorizationPopup:command];
        }];
        
    }
    else {
        [self showTrackingAuthorizationPopup:command];
    }
}

- (void)showTrackingAuthorizationPopup:(CDVInvokedUrlCommand *)command {
    
    if (@available(iOS 14, *)) {
        
        NSDictionary *dict = NSBundle.mainBundle.infoDictionary;
        
        if([dict objectForKey:@"NSUserTrackingUsageDescription"]){
            [ATTrackingManager requestTrackingAuthorizationWithCompletionHandler:^(ATTrackingManagerAuthorizationStatus status) {
                BOOL result = status == ATTrackingManagerAuthorizationStatusAuthorized;
                
                CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:result];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }];
            return;
        }
    }
    
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)setConsent:(CDVInvokedUrlCommand*)command
{
    NSDictionary* consentDict = [command.arguments objectAtIndex:0];
    
    if (![consentDict isKindOfClass:[NSDictionary class]]) {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR 
                                                        messageAsString:@"Invalid input: expected a dictionary"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }
    
    NSMutableDictionary* firebaseConsentDict = [NSMutableDictionary dictionary];
    
    for (NSString* key in consentDict) {
        FIRConsentType consentType = [self consentTypeFromString:key];
        FIRConsentStatus consentStatus = [self consentStatusFromString:consentDict[key]];
        
        if (consentType != FIRConsentTypeUnspecified && consentStatus != FIRConsentStatusUnspecified) {
            firebaseConsentDict[consentType] = @(consentStatus);
        } else {
            NSLog(@"Warning: Ignoring invalid consent type or status for key: %@", key);
        }
    }
    
    if (firebaseConsentDict.count > 0) {
        [FIRAnalytics setConsent:firebaseConsentDict];
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    } else {
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR 
                                                        messageAsString:@"No valid consent types provided"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

typedef void (^showPermissionInformationPopupHandler)(UIAlertAction*);
- (void)showPermissionInformationPopup:
(NSString *)title :
(NSString *)message :
(NSString *)buttonTitle :
(showPermissionInformationPopupHandler)confirmationHandler
{
    
    UIAlertController *alert = [UIAlertController
                                alertControllerWithTitle:title
                                message:message
                                preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction *okAction = [UIAlertAction
                               actionWithTitle:buttonTitle
                               style:UIAlertActionStyleDefault
                               handler:confirmationHandler];
    
    [alert addAction:okAction];
    [self.viewController presentViewController:alert animated:YES completion:nil];
}

#pragma mark - Result Callback Methods (used for `LogECommerceEvent`)

- (void)sendSuccessfulResultforCallbackId:(NSString *)callbackId {
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
}

- (void)sendError:(NSError *)error forCallbackId:(NSString *)callbackId {
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:error.userInfo];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
}

#pragma mark - Consent Data conversion methods

- (FIRConsentType)consentTypeFromString:(NSString*)string
{
    if ([string isEqualToString:@"AD_PERSONALIZATION"]) return FIRConsentTypeAdPersonalization;
    if ([string isEqualToString:@"AD_STORAGE"]) return FIRConsentTypeAdStorage;
    if ([string isEqualToString:@"AD_USER_DATA"]) return FIRConsentTypeAdUserData;
    if ([string isEqualToString:@"ANALYTICS_STORAGE"]) return FIRConsentTypeAnalyticsStorage;
    return FIRConsentTypeUnspecified;
}

- (FIRConsentStatus)consentStatusFromString:(NSString*)string
{
    if ([string isEqualToString:@"DENIED"]) return FIRConsentStatusDenied;
    if ([string isEqualToString:@"GRANTED"]) return FIRConsentStatusGranted;
    return FIRConsentStatusUnspecified;
}


@end
