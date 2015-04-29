package xdi2.core.features.nodetypes;

public interface XdiEntityInstance extends XdiEntity, XdiMember<XdiEntityCollection, XdiEntity, XdiEntityCollection, XdiEntityInstanceUnordered, XdiEntityInstanceOrdered, XdiEntityInstance> {

	@Override
	public XdiEntityCollection getXdiCollection();
}
