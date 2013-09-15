package xdi2.core.features.nodetypes;

public interface XdiAttributeMember extends XdiAttribute, XdiMember<XdiAttributeClass, XdiAttributeMemberUnordered, XdiAttributeMemberOrdered, XdiAttributeMember> {

	@Override
	public XdiAttributeClass getXdiCollection();
}
