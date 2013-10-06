package xdi2.core.features.nodetypes;

import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3SubSegment;

public interface XdiCollection<EQC extends XdiCollection<EQC, EQI, C, U, O, I>, EQI extends XdiSubGraph<EQI>, C extends XdiCollection<EQC, EQI, C, U, O, I>, U extends XdiMemberUnordered<EQC, EQI, C, U, O, I>, O extends XdiMemberOrdered<EQC, EQI, C, U, O, I>, I extends XdiMember<EQC, EQI, C, U, O, I>> extends XdiSubGraph<EQC> {

	public U setXdiMemberUnordered(XDI3SubSegment arcXri);
	public U getXdiMemberUnordered(XDI3SubSegment arcXri);
	public ReadOnlyIterator<U> getXdiMembersUnordered();
	public long getXdiMembersUnorderedCount();

	public O setXdiMemberOrdered(long index);
	public O getXdiMemberOrdered(long index);
	public ReadOnlyIterator<O> getXdiMembersOrdered();
	public long getXdiMembersOrderedCount();

	public ReadOnlyIterator<I> getXdiMembers();
	public ReadOnlyIterator<EQI> getXdiMembersDeref();
}
