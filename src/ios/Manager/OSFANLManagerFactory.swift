import Foundation

@objc public class OSFANLManagerFactory: NSObject {
    @objc public static func createManager() -> OSFANLManageable {
        let inputTransformer = OSFANLInputTransformer()
        
        return OSFANLManager(inputTransformer)
    }
}
