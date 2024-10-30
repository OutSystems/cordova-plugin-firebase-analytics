enum OSFANLInputDataFieldKey: String {
    case customParameters = "custom_parameters"
    case currency
    case event
    case eventParameters
    case item
    case itemId = "item_id"
    case itemListId = "item_list_id"
    case itemListName = "item_list_name"
    case itemName = "item_name"
    case items
    case key
    case shipping
    case tax
    case transactionId = "transaction_id"
    case value
}

extension OSFANLInputDataFieldKey {
    private static var decimalDataFields: [OSFANLInputDataFieldKey] { [.shipping, .tax, .value] }
    
    var isDecimalType: Bool {
        return OSFANLInputDataFieldKey.decimalDataFields.contains(self)
    }
}
