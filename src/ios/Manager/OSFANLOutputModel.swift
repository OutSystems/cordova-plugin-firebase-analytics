@objc class OSFANLOutputModel: NSObject {
    @objc let name: String
    @objc let parameters: [String: Any]
    
    init(_ name: String, _ parameters: [String: Any]) {
        self.name = name
        self.parameters = parameters
    }
}
