@objc public class OSFANLOutputModel: NSObject {
    @objc public let name: String
    @objc public let parameters: [String: Any]
    
    init(_ name: String, _ parameters: [String: Any]) {
        self.name = name
        self.parameters = parameters
    }
}
