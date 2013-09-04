package xdi2.core.features.nodetypes;

public interface XdiInstance<C extends XdiClass<C, U, O, I>, U extends XdiInstanceUnordered<C, U, O, I>, O extends XdiInstanceOrdered<C, U, O, I>, I extends XdiInstance<C, U, O, I>> extends XdiSubGraph {

	public C getXdiClass();
}
