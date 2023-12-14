struct OSFANLInputTransformableModel {
    let eventParameterData: InputParameterData?
    var itemArray: [InputItemData]?
    
    init(_ eventParameterData: InputParameterData?, _ itemArray: [InputItemData]?) {
        self.eventParameterData = eventParameterData
        self.itemArray = itemArray
    }
}
