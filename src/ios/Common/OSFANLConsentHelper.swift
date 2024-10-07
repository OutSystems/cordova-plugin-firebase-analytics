import FirebaseAnalytics
import FirebaseCore

@objc class OSFANLConsentHelper: NSObject {
    
    @objc static func createConsentModel(_ commandArguments: NSArray) throws -> NSDictionary {
        guard let jsonString = commandArguments[0] as? String,
              let jsonData = jsonString.data(using: .utf8),
              let array = try JSONSerialization.jsonObject(with: jsonData, options: []) as? [[String: Any]] else {
            throw OSFANLError.invalidType("ConsentSettings", type: "JSON")
        }
        
        var firebaseConsentDict: [ConsentType: ConsentStatus] = [:]
        
        for item in array {
            guard let type = item["Type"] as? NSNumber,
                  let status = item["Status"] as? NSNumber else {
                throw OSFANLError.invalidType("Type or Status", type: "NSNumber")
            }
            
            if let consentType = consentTypeFromNumber(type),
               let consentStatus = consentStatusFromNumber(status) {
                if firebaseConsentDict[consentType] != nil {
                    throw OSFANLError.duplicateItemsIn(parameter: "ConsentSettings")
                } else {
                    firebaseConsentDict[consentType] = consentStatus
                }
            } else {
                throw OSFANLError.invalidType("Consent Type or Status", type: "Valid value")
            }
        }
        
        if firebaseConsentDict.isEmpty {
            throw OSFANLError.missing("ConsentSettings")
        } else {
            return firebaseConsentDict as NSDictionary
        }
    }
    
    private static func consentTypeFromNumber(_ num: NSNumber) -> ConsentType? {
        switch num.intValue {
        case 1: return .adPersonalization
        case 2: return .adStorage
        case 3: return .adUserData
        case 4: return .analyticsStorage
        default: return nil
        }
    }

    private static func consentStatusFromNumber(_ num: NSNumber) -> ConsentStatus? {
        switch num.intValue {
        case 1: return .granted
        case 2: return .denied
        default: return nil
        }
    }
}