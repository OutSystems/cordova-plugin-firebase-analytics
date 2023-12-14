protocol StringConvertable {
    static var variableType: String { get }
    init?(value: String)
}

extension Decimal: StringConvertable {
    static var variableType: String { "Decimal" }
    
    init?(value: String) {
        self.init(string: value)
    }
}

extension String: StringConvertable {
    static var variableType: String { "Text" }
    
    init?(value: String) {
        self.init(stringLiteral: value)
    }
}
