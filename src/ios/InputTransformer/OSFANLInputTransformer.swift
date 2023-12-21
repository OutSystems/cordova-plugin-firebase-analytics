struct OSFANLInputTransformer: OSFANLInputTransformable {
    func transform(_ eventParameterArray: [InputParameterData]?, _ itemArray: [InputItemData]?) throws -> OSFANLInputTransformableModel {
        var eventParameterData: InputParameterData?
        if let eventParameterArray {
            guard let flatResult = try? self.flat(keyValueMapArray: eventParameterArray) else {
                throw OSFANLError.duplicateItemsIn(parameter: OSFANLInputDataFieldKey.eventParameters.rawValue)
            }
            eventParameterData = flatResult
        }
        let itemArray = try self.transform(itemArray)
        
        return .init(eventParameterData, itemArray)
    }
}

private extension OSFANLInputTransformer {
    func flat(keyValueMapArray array: [InputParameterData]) throws -> InputParameterData {
        let flatKeyValueArray = array.map {
            [$0[OSFANLInputDataFieldKey.key.rawValue, default: ""]: $0[OSFANLInputDataFieldKey.value.rawValue, default: ""]]
        }
        let flatKeyValueDictionary = self.flat(dictionaryArray: flatKeyValueArray)
        
        if flatKeyValueDictionary.count != flatKeyValueArray.count { throw OSFANLError.duplicateKeys }
        return flatKeyValueDictionary
    }
    
    func transform(_ itemArray: [InputItemData]?) throws -> [InputItemData]? {
        // if nil or empty, just return it as there's nothing to do here.
        guard let itemArray, !itemArray.isEmpty else {
            return itemArray
        }
        // if there's more than the expected maximum, return the error
        guard itemArray.count <= OSFANLDefaultValues.eventItemsMaximum else {
            throw OSFANLError.tooMany(parameter: OSFANLInputDataFieldKey.items.rawValue, limit: OSFANLDefaultValues.eventItemsMaximum)
        }
        
        var resultArray: [InputItemData] = []
        for itemData in itemArray {
            // there needs to be at least one `item_id` or `item_name`
            guard itemData.keys.contains(where: { [OSFANLInputDataFieldKey.itemId, .itemName].map(\.rawValue).contains($0) }) else {
                throw OSFANLError.missingItemIdName
            }
            
            var resultItem: InputItemData = [:]
            
            let customParametersSplit = Dictionary(grouping: itemData) { $0.key == OSFANLInputDataFieldKey.customParameters.rawValue }
            if let regularParameterArray = customParametersSplit[false] { resultItem += self.flat(tupleArray: regularParameterArray) }
            
            if let customParameterArray = customParametersSplit[true]?.first?.value as? [InputParameterData], !customParameterArray.isEmpty {
                // if there's more than the expected maximum, return the error
                guard customParameterArray.count <= OSFANLDefaultValues.itemCustomParametersMaximum else {
                    throw OSFANLError.tooMany(
                        parameter: OSFANLInputDataFieldKey.customParameters.rawValue, limit: OSFANLDefaultValues.itemCustomParametersMaximum
                    )
                }
                guard let customParameterDictionary = try? self.flat(keyValueMapArray: customParameterArray) else {
                    throw OSFANLError.duplicateItemsIn(parameter: OSFANLInputDataFieldKey.customParameters.rawValue)
                }
                // check if there are repeated keys. If so, return an error.
                if !resultItem.keys.filter({ customParameterDictionary.keys.contains($0) }).isEmpty {
                    throw OSFANLError.duplicateItemsIn(parameter: OSFANLInputDataFieldKey.item.rawValue)
                }
                resultItem += customParameterDictionary
            }
            
            resultArray.append(resultItem)
        }
        
        return resultArray
    }
}

private extension OSFANLInputTransformer {
    func flat<Key, Value>(dictionaryArray: [[Key: Value]]) -> [Key: Value] {
        dictionaryArray
            .flatMap { $0 }
            .reduce(into: [Key: Value]()) { $0[$1.key] = $1.value }
    }
    
    func flat<Key, Value>(tupleArray: [(Key, Value)]) -> [Key: Value] {
        tupleArray
            .reduce(into: [Key: Value]()) { $0[$1.0] = $1.1 }
    }
}
