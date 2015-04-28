package xdi2.core.features.nodetypes;

import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.ReadOnlyIterator;

public interface XdiCollection<EQC extends XdiCollection<EQC, EQI, C, U, O, I>, EQI extends XdiSubGraph<EQI>, C extends XdiCollection<EQC, EQI, C, U, O, I>, U extends XdiMemberUnordered<EQC, EQI, C, U, O, I>, O extends XdiMemberOrdered<EQC, EQI, C, U, O, I>, I extends XdiMember<EQC, EQI, C, U, O, I>> extends XdiSubGraph<EQC> {

	public U setXdiMemberUnordered(boolean immutable, boolean relative);
	public U setXdiMemberUnordered(XDIArc XDIarc);
	public U getXdiMemberUnordered(XDIArc XDIarc);
	public ReadOnlyIterator<U> getXdiMembersUnordered();
	public long getXdiMembersUnorderedCount();

	public O setXdiMemberOrdered(long index);
	public O getXdiMemberOrdered(long index);
	public ReadOnlyIterator<O> getXdiMembersOrdered();
	public long getXdiMembersOrderedCount();

	public ReadOnlyIterator<I> getXdiMembers();
	public ReadOnlyIterator<EQI> getXdiMembersDeref();
}
