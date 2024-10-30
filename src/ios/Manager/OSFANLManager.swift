class OSFANLManager {
    private let inputTransformer: OSFANLInputTransformable
    private let eventValidator: OSFANLEventValidator
    
    init(_ inputTransformer: OSFANLInputTransformable, _ eventValidator: OSFANLEventValidator) {
        self.inputTransformer = inputTransformer
        self.eventValidator = eventValidator
    }
}

extension OSFANLManager: OSFANLManageable {
    private func convert<T>(jsonString: String) -> T? {
        guard let jsonData = jsonString.data(using: .utf8) else { return nil }
        return try? JSONSerialization.jsonObject(with: jsonData, options: .fragmentsAllowed) as? T
    }
    
    func createEventModel(for inputArgument: InputParameterData) throws -> OSFANLOutputModel {
        guard let eventName = inputArgument[OSFANLInputDataFieldKey.event.rawValue] as? String, !eventName.isEmpty else {
            throw OSFANLError.missing(OSFANLInputDataFieldKey.event.rawValue)
        }
        
        var eventParameterArray: [InputParameterData]?
        if let eventParameterString = inputArgument[OSFANLInputDataFieldKey.eventParameters.rawValue] as? String {
            eventParameterArray = self.convert(jsonString: eventParameterString)
        }
        
        var itemArray: [InputItemData]?
        if let itemString = inputArgument[OSFANLInputDataFieldKey.items.rawValue] as? String {
            itemArray = self.convert(jsonString: itemString)
        }
        
        var inputModel = try self.inputTransformer.transform(eventParameterArray, itemArray)
        try self.eventValidator.validate(event: eventName, inputModel.eventParameterData, &inputModel.itemArray)
        
        var outputModelParameterData: OutputParameterData = inputModel.eventParameterData ?? [:]
        if let inputModelItemArray = inputModel.itemArray, !inputModelItemArray.isEmpty {
            outputModelParameterData += [OSFANLInputDataFieldKey.items.rawValue: inputModelItemArray]
        }
        
        return .init(eventName, outputModelParameterData)
    }
}
