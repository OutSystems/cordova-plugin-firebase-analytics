struct OSFANLEventValidator {
    private let eventValidatorMapping: [String: ValidationMethod]
    
    init(eventValidatorMapping: [String: ValidationMethod]) {
        self.eventValidatorMapping = eventValidatorMapping
    }
    
    func validate(event: String, _ eventParameterData: InputParameterData?, _ itemArray: inout [InputItemData]?) throws {
        guard let eventValidation = self.eventValidatorMapping[event] else { throw OSFANLError.unexpected(event) }
        try eventValidation(eventParameterData, &itemArray)
    }
}
