protocol StringConvertable {
    static var variableType: String { get }
}

extension Decimal: StringConvertable {
    static var variableType: String { "Decimal" }
}

extension String: StringConvertable {
    static var variableType: String { "Text" }
}
