package xdi2.core.features.nodetypes;

public interface XdiEntityInstance extends XdiEntity, XdiInstance<XdiEntityClass, XdiEntityInstanceUnordered, XdiEntityInstanceOrdered, XdiEntityInstance> {

	@Override
	public XdiEntityClass getXdiClass();
}
