enum OSFANLError: Error {
    case duplicateItemsIn(parameter: String)
    case duplicateKeys
    case invalidType(_ parameter: String, type: String)
    case logEcommerceEventInputArgumentsIssue
    case missing(_ parameter: String)
    case missingItemIdName
    case tooMany(parameter: String, limit: Int)
    case unexpected(_ event: String)
}

extension OSFANLError: CustomNSError {
    var errorCode: Int {
        switch self {
        case .duplicateItemsIn: return 1
        case .duplicateKeys: return 2
        case .invalidType: return 3
        case .logEcommerceEventInputArgumentsIssue: return 4
        case .missing: return 5
        case .missingItemIdName: return 6
        case .tooMany: return 7
        case .unexpected: return 8
        }
    }
    
    var errorDescription: String? {
        switch self {
        case .duplicateItemsIn(let parameter): return "Parameter '\(parameter)' contains duplicate items."
        case .duplicateKeys: return "The dictionary contains duplicate keys"
        case .invalidType(let parameter, let type): return "Parameter '\(parameter)' must be of type '\(type)'"
        case .logEcommerceEventInputArgumentsIssue: return "There's an issue with the `logECommerceEvent` input arguments."
        case .missing(let parameter): return "Required parameter '\(parameter)' is missing."
        case .missingItemIdName: return "Item requires an ID or a Name."
        case .tooMany(let parameter, let limit): return "Parameter '\(parameter)' must be set to a maximum number of \(limit)."
        case .unexpected(let event): return "Event '\(event)' is not valid."
        }
    }
    
    var errorUserInfo: [String: Any] {
        [
            "code": "OS-PLUG-FANL-\(String(format: "%04d", self.errorCode))",
            "message": self.errorDescription ?? ""
        ]
    }
}
