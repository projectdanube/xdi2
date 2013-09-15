package xdi2.core.features.nodetypes;

public interface XdiEntityMember extends XdiEntity, XdiMember<XdiEntityClass, XdiEntityMemberUnordered, XdiEntityMemberOrdered, XdiEntityMember> {

	@Override
	public XdiEntityClass getXdiCollection();
}
