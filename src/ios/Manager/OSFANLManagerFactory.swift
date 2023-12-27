@objc class OSFANLManagerFactory: NSObject {
    @objc static func createManager() -> OSFANLManageable {
        let inputTransformer = OSFANLInputTransformer()
        
        return OSFANLManager(inputTransformer)
    }
}
