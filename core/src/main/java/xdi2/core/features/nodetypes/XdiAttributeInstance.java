package xdi2.core.features.nodetypes;

public interface XdiAttributeInstance extends XdiAttribute, XdiInstance<XdiAttributeCollection, XdiAttribute, XdiAttributeCollection, XdiAttributeInstanceUnordered, XdiAttributeInstanceOrdered, XdiAttributeInstance> {

	@Override
	public XdiAttributeCollection getXdiCollection();
}
