package xdi2.core.features.nodetypes;

public interface XdiAttributeMember extends XdiAttribute, XdiMember<XdiAttributeCollection, XdiAttributeMemberUnordered, XdiAttributeMemberOrdered, XdiAttributeMember> {

	@Override
	public XdiAttributeCollection getXdiCollection();
}
