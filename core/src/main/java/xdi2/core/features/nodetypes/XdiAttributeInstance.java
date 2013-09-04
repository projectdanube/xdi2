package xdi2.core.features.nodetypes;

public interface XdiAttributeInstance extends XdiAttribute, XdiInstance<XdiAttributeClass, XdiAttributeInstanceUnordered, XdiAttributeInstanceOrdered, XdiAttributeInstance> {

	@Override
	public XdiAttributeClass getXdiClass();
}
