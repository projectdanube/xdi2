package xdi2.core.features.nodetypes;

public interface XdiEntityMember extends XdiEntity, XdiMember<XdiEntityCollection, XdiEntity, XdiEntityCollection, XdiEntityMemberUnordered, XdiEntityMemberOrdered, XdiEntityMember> {

	@Override
	public XdiEntityCollection getXdiCollection();
}
