package xdi2.core.features.nodetypes;

public interface XdiMemberOrdered<C extends XdiCollection<C, U, O, I>, U extends XdiMemberUnordered<C, U, O, I>, O extends XdiMemberOrdered<C, U, O, I>, I extends XdiMember<C, U, O, I>> extends XdiMember<C, U, O, I> {

}
