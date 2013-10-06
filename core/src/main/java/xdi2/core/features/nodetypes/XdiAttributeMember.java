package xdi2.core.features.nodetypes;

public interface XdiAttributeMember extends XdiAttribute, XdiMember<XdiAttributeCollection, XdiAttribute, XdiAttributeCollection, XdiAttributeMemberUnordered, XdiAttributeMemberOrdered, XdiAttributeMember> {

	@Override
	public XdiAttributeCollection getXdiCollection();
}
