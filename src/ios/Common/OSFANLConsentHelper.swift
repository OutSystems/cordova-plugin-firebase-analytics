import FirebaseAnalytics
import FirebaseCore

@objc enum ConsentTypeRawValue: Int {
    case adPersonalization = 1
    case adStorage = 2
    case adUserData = 3
    case analyticsStorage = 4
    
    var stringValue: String {
        switch self {
        case .adPersonalization: return "ad_personalization"
        case .adStorage: return "ad_storage"
        case .adUserData: return "ad_user_data"
        case .analyticsStorage: return "analytics_storage"
        }
    }
}

@objc enum ConsentStatusRawValue: Int {
    case granted = 1
    case denied = 2
    
    var stringValue: String {
        switch self {
        case .granted: return "granted"
        case .denied: return "denied"
        }
    }
}

@objc class OSFANLConsentHelper: NSObject {
    @objc static func createConsentModel(_ commandArguments: NSArray) throws -> NSDictionary {
        guard let jsonString = commandArguments[0] as? String,
              let jsonData = jsonString.data(using: .utf8),
              let array = try JSONSerialization.jsonObject(with: jsonData, options: []) as? [[String: Any]] else {
            throw OSFANLError.invalidType("ConsentSettings", type: "JSON")
        }
        
        var firebaseConsentDict: [ConsentType: ConsentStatus] = [:]
        
        for item in array {
            guard let typeRawValue = item["Type"] as? Int,
                  let statusRawValue = item["Status"] as? Int,
                  let consentTypeRawValue = ConsentTypeRawValue(rawValue: typeRawValue),
                  let consentStatusRawValue = ConsentStatusRawValue(rawValue: statusRawValue) else {
                throw OSFANLError.invalidType("Consent Type or Status", type: "Valid value")
            }
            
            let consentType = ConsentType(rawValue: consentTypeRawValue.stringValue)
            let consentStatus = ConsentStatus(rawValue: consentStatusRawValue.stringValue)
            
            if firebaseConsentDict[consentType] != nil {
                throw OSFANLError.duplicateItemsIn(parameter: "ConsentSettings")
            } else {
                firebaseConsentDict[consentType] = consentStatus
            }
        }
        
        if firebaseConsentDict.isEmpty {
            throw OSFANLError.missing("ConsentSettings")
        } else {
            return firebaseConsentDict as NSDictionary
        }
    }
}
