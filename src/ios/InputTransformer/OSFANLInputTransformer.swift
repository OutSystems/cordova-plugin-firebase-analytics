struct OSFANLInputTransformer: OSFANLInputTransformable {
    func transform(_ eventParameterArray: [InputParameterData]?, _ itemArray: [InputItemData]?) throws -> OSFANLInputTransformableModel {
        do {
            var eventParameterData: InputParameterData?
            if let eventParameterArray {
                eventParameterData = try self.flat(keyValueMapArray: eventParameterArray)
            }
            let itemArray = try self.transform(itemArray)

            return .init(eventParameterData, itemArray)
        } catch OSFANLError.duplicateKeys {
            throw OSFANLError.duplicateItemsIn(parameter: OSFANLInputDataFieldKey.eventParameters.rawValue)
        }
    }
}

private extension OSFANLInputTransformer {
    func flat(keyValueMapArray array: [InputParameterData]) throws -> InputParameterData {
        let flatKeyValueArray = try array.reduce(into: [InputParameterData]()) { partialResult, current in
            guard let dataFieldKey = current[OSFANLInputDataFieldKey.key.rawValue] as? String,
                  let dataValue = current[OSFANLInputDataFieldKey.value.rawValue] as? String
            else { return }

            let value: StringConvertable
            if let dataField = OSFANLInputDataFieldKey(rawValue: dataFieldKey), dataField.isDecimalType {
                guard let decimalValue = Decimal(string: dataValue) else { throw OSFANLError.invalidType(dataFieldKey, type: Decimal.variableType) }
                value = decimalValue
            } else {
                value = dataValue
            }

            partialResult.append([dataFieldKey: value])
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
