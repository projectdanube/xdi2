package xdi2.core.features.nodetypes;

public interface XdiInstanceOrdered extends XdiInstance {

	public XdiClass<? extends XdiInstanceUnordered, ? extends XdiInstanceOrdered, ? extends XdiInstance> getXdiClass();
}
