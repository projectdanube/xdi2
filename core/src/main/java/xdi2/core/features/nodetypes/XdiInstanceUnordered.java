package xdi2.core.features.nodetypes;

public interface XdiInstanceUnordered extends XdiInstance {

	public XdiClass<? extends XdiInstanceUnordered, ? extends XdiInstanceOrdered, ? extends XdiInstance> getXdiClass();
}
