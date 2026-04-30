#if canImport(Cordova)
import Cordova
#endif

import AppTrackingTransparency
import FirebaseAnalytics
import FirebaseCore
import UIKit

@objc(FirebaseAnalyticsPlugin)
class FirebaseAnalyticsPlugin: CDVPlugin {
    private var manager: OSFANLManageable?

    override func pluginInitialize() {
        NSLog("Starting Firebase Analytics plugin")

        if FirebaseApp.app() == nil {
            FirebaseApp.configure()
        }

        self.manager = OSFANLManagerFactory.createManager()
    }

    @objc(logEvent:)
    func logEvent(_ command: CDVInvokedUrlCommand) {
        let name = command.argument(at: 0) as? String ?? ""
        let parameters = command.argument(at: 1) as? [String: Any]

        Analytics.logEvent(name, parameters: parameters)
        self.sendSuccessfulResult(forCallbackId: command.callbackId)
    }

    @objc(logECommerceEvent:)
    func logECommerceEvent(_ command: CDVInvokedUrlCommand) {
        guard let argumentsDictionary = command.argument(at: 0) as? [String: Any] else {
            self.sendError(OSFANLError.logEcommerceEventInputArgumentsIssue as NSError, forCallbackId: command.callbackId)
            return
        }

        do {
            let outputModel = try self.manager?.createEventModel(for: argumentsDictionary)
            guard let outputModel else {
                self.sendError(OSFANLError.logEcommerceEventInputArgumentsIssue as NSError, forCallbackId: command.callbackId)
                return
            }

            Analytics.logEvent(outputModel.name, parameters: outputModel.parameters)
            self.sendSuccessfulResult(forCallbackId: command.callbackId)
        } catch {
            self.sendError(error as NSError, forCallbackId: command.callbackId)
        }
    }

    @objc(setUserId:)
    func setUserId(_ command: CDVInvokedUrlCommand) {
        let id = command.argument(at: 0) as? String

        Analytics.setUserID(id)
        self.sendSuccessfulResult(forCallbackId: command.callbackId)
    }

    @objc(setUserProperty:)
    func setUserProperty(_ command: CDVInvokedUrlCommand) {
        let name = command.argument(at: 0) as? String ?? ""
        let value = command.argument(at: 1) as? String

        Analytics.setUserProperty(value, forName: name)
        self.sendSuccessfulResult(forCallbackId: command.callbackId)
    }

    @objc(setEnabled:)
    func setEnabled(_ command: CDVInvokedUrlCommand) {
        let enabled = (command.argument(at: 0) as? Bool) ?? false

        Analytics.setAnalyticsCollectionEnabled(enabled)
        self.sendSuccessfulResult(forCallbackId: command.callbackId)
    }

    @objc(setCurrentScreen:)
    func setCurrentScreen(_ command: CDVInvokedUrlCommand) {
        let screenName = command.argument(at: 0) as? String ?? ""

        Analytics.logEvent(AnalyticsEventScreenView, parameters: [
            AnalyticsParameterScreenName: screenName
        ])
        self.sendSuccessfulResult(forCallbackId: command.callbackId)
    }

    @objc(resetAnalyticsData:)
    func resetAnalyticsData(_ command: CDVInvokedUrlCommand) {
        Analytics.resetAnalyticsData()
        self.sendSuccessfulResult(forCallbackId: command.callbackId)
    }

    @objc(setDefaultEventParameters:)
    func setDefaultEventParameters(_ command: CDVInvokedUrlCommand) {
        let params = command.argument(at: 0) as? [String: Any]

        Analytics.setDefaultEventParameters(params)
        self.sendSuccessfulResult(forCallbackId: command.callbackId)
    }

    @objc(requestTrackingAuthorization:)
    func requestTrackingAuthorization(_ command: CDVInvokedUrlCommand) {
        let showInformation = (command.argument(at: 0) as? Bool) ?? false

        if showInformation {
            let title = command.argument(at: 1) as? String ?? ""
            let message = command.argument(at: 2) as? String ?? ""
            let buttonTitle = command.argument(at: 3) as? String ?? ""

            self.showPermissionInformationPopup(title, message, buttonTitle) { [weak self] _ in
                self?.showTrackingAuthorizationPopup(command)
            }
        } else {
            self.showTrackingAuthorizationPopup(command)
        }
    }

    @objc(setConsent:)
    func setConsent(_ command: CDVInvokedUrlCommand) {
        do {
            let consentModel = try OSFANLConsentHelper.createConsentModel(command.arguments as NSArray)
            Analytics.setConsent(consentModel)
            self.sendSuccessfulResult(forCallbackId: command.callbackId)
        } catch {
            self.sendError(error as NSError, forCallbackId: command.callbackId)
        }
    }
}

private extension FirebaseAnalyticsPlugin {
    func showTrackingAuthorizationPopup(_ command: CDVInvokedUrlCommand) {
        if #available(iOS 14, *), Bundle.main.infoDictionary?["NSUserTrackingUsageDescription"] != nil {
            ATTrackingManager.requestTrackingAuthorization { [weak self] status in
                let result = status == .authorized
                let pluginResult = CDVPluginResult(status: .ok, messageAs: result)
                self?.commandDelegate.send(pluginResult, callbackId: command.callbackId)
            }
            return
        }

        let pluginResult = CDVPluginResult(status: .ok, messageAs: true)
        self.commandDelegate.send(pluginResult, callbackId: command.callbackId)
    }

    func showPermissionInformationPopup(
        _ title: String,
        _ message: String,
        _ buttonTitle: String,
        confirmationHandler: @escaping (UIAlertAction) -> Void
    ) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let okAction = UIAlertAction(title: buttonTitle, style: .default, handler: confirmationHandler)

        alert.addAction(okAction)
        self.viewController.present(alert, animated: true)
    }

    func sendSuccessfulResult(forCallbackId callbackId: String) {
        let pluginResult = CDVPluginResult(status: .ok)
        self.commandDelegate.send(pluginResult, callbackId: callbackId)
    }

    func sendError(_ error: NSError, forCallbackId callbackId: String) {
        let pluginResult = CDVPluginResult(status: .error, messageAs: error.userInfo)
        self.commandDelegate.send(pluginResult, callbackId: callbackId)
    }
}
