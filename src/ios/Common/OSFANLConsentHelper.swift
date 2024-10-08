import FirebaseAnalytics
import FirebaseCore

@objc enum ConsentTypeRawValue: Int, CustomStringConvertible, CaseIterable {
    case adPersonalization = 1
    case adStorage = 2
    case adUserData = 3
    case analyticsStorage = 4
    
    var description: String {
        return switch self {
        case .adPersonalization: "ad_personalization"
        case .adStorage: "ad_storage"
        case .adUserData: "ad_user_data"
        case .analyticsStorage: "analytics_storage"
        }
    }
    
    static func allOptionsString() -> String {
        let capitalizedDescriptions = allCases.map { $0.description.uppercased() }
        
        if capitalizedDescriptions.count > 1 {
            let lastOption = capitalizedDescriptions.last!
            let allButLast = capitalizedDescriptions.dropLast().joined(separator: ", ")
            return "\(allButLast), or \(lastOption)"
        } else {
            return capitalizedDescriptions.first ?? ""
        }
    }
}

@objc enum ConsentStatusRawValue: Int, CustomStringConvertible, CaseIterable {
    case granted = 1
    case denied = 2
    
    var description: String {
        return switch self {
        case .granted: "granted"
        case .denied: "denied"
        }
    }
    
    static func allOptionsString() -> String {
        let capitalizedDescriptions = allCases.map { $0.description.uppercased() }
        
        if capitalizedDescriptions.count > 1 {
            let lastOption = capitalizedDescriptions.last!
            let allButLast = capitalizedDescriptions.dropLast().joined(separator: ", ")
            return "\(allButLast), or \(lastOption)"
        } else {
            return capitalizedDescriptions.first ?? ""
        }
    }
}

@objc class OSFANLConsentHelper: NSObject {
    @objc static func createConsentModel(_ commandArguments: NSArray) throws -> [ConsentType: ConsentStatus] {
        guard let jsonString = commandArguments[0] as? String,
              let jsonData = jsonString.data(using: .utf8),
              let array = try JSONSerialization.jsonObject(with: jsonData, options: []) as? [[String: Any]] else {
            throw OSFANLError.invalidType("ConsentSettings", type: "JSON")
        }
        
        var firebaseConsentDict: [ConsentType: ConsentStatus] = [:]
        
        for item in array {
            guard let typeRawValue = item["Type"] as? Int,
                  let statusRawValue = item["Status"] as? Int else {
                throw OSFANLError.invalidType("JSON passed Consent Type or Status", type: "Integer")
            }
            
            guard let consentTypeRawValue = ConsentTypeRawValue(rawValue: typeRawValue) else {
                throw OSFANLError.invalidType("Consent Type", type: ConsentTypeRawValue.allOptionsString())
            }
            
            guard let consentStatusRawValue = ConsentStatusRawValue(rawValue: statusRawValue) else {
                throw OSFANLError.invalidType("Consent Status", type: ConsentStatusRawValue.allOptionsString())
            }
            
            let consentType = ConsentType(rawValue: String(describing: consentTypeRawValue))
            let consentStatus = ConsentStatus(rawValue: String(describing: consentStatusRawValue))
            
            if firebaseConsentDict.keys.contains(consentType) {
                throw OSFANLError.duplicateItemsIn(parameter: "ConsentSettings")
            } else {
                firebaseConsentDict[consentType] = consentStatus
            }
        }
        
        if firebaseConsentDict.isEmpty {
            throw OSFANLError.missing("ConsentSettings")
        } else {
            return firebaseConsentDict
        }
    }
}
