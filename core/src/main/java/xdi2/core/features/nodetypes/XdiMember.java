package xdi2.core.features.nodetypes;

public interface XdiMember<C extends XdiCollection<C, U, O, I>, U extends XdiMemberUnordered<C, U, O, I>, O extends XdiMemberOrdered<C, U, O, I>, I extends XdiMember<C, U, O, I>> extends XdiSubGraph {

	public C getXdiCollection();
}
