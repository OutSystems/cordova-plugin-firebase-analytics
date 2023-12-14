protocol OSFANLInputTransformable {
    func transform(_ eventParameterArray: [InputParameterData]?, _ itemArray: [InputItemData]?) throws -> OSFANLInputTransformableModel
}
