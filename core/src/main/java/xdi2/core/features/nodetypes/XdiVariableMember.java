package xdi2.core.features.nodetypes;

public interface XdiVariableMember extends XdiVariable, XdiMember<XdiVariableCollection, XdiVariable, XdiVariableCollection, XdiVariableMemberUnordered, XdiVariableMemberOrdered, XdiVariableMember> {

	@Override
	public XdiVariableCollection getXdiCollection();
}
