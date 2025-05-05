typealias DefaultKeyValueData = [String: Any]

extension DefaultKeyValueData {
    static func += <K, V>(left: inout [K: V], right: [K: V]) {
        for (key, value) in right {
            left[key] = value
        }
    }
}

public typealias InputParameterData = [String: Any]
typealias InputItemData = DefaultKeyValueData
typealias OutputParameterData = DefaultKeyValueData

@objc public protocol OSFANLManageable {
    func createEventModel(for inputArgument: InputParameterData) throws -> OSFANLOutputModel
}
