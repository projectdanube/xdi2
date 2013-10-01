package xdi2.core.features.nodetypes;

public interface XdiMember<EQ extends XdiSubGraph<EQ>, C extends XdiCollection<EQ, C, U, O, I>, U extends XdiMemberUnordered<EQ, C, U, O, I>, O extends XdiMemberOrdered<EQ, C, U, O, I>, I extends XdiMember<EQ, C, U, O, I>> extends XdiSubGraph<EQ> {

	public C getXdiCollection();
}
