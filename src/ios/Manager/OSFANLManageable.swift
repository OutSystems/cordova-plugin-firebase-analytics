typealias DefaultKeyValueData = [String: Any]

extension DefaultKeyValueData {
    static func += <K, V>(left: inout [K: V], right: [K: V]) {
        for (key, value) in right {
            left[key] = value
        }
    }
}

typealias InputParameterData = [String: String]
typealias InputItemData = DefaultKeyValueData
typealias OutputParameterData = DefaultKeyValueData

@objc protocol OSFANLManageable {
    func createEventModel(for inputArgument: InputParameterData) throws -> OSFANLOutputModel
}
