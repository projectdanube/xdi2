package xdi2.core.features.nodetypes;

import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3SubSegment;

public interface XdiCollection<EQ extends XdiSubGraph<EQ>, C extends XdiCollection<EQ, C, U, O, I>, U extends XdiMemberUnordered<EQ, C, U, O, I>, O extends XdiMemberOrdered<EQ, C, U, O, I>, I extends XdiMember<EQ, C, U, O, I>> extends XdiSubGraph<C> {

	public U setXdiMemberUnordered(XDI3SubSegment arcXri);
	public U getXdiMemberUnordered(XDI3SubSegment arcXri);
	public ReadOnlyIterator<U> getXdiMembersUnordered();
	public long getXdiMembersUnorderedCount();

	public O setXdiMemberOrdered(long index);
	public O getXdiMemberOrdered(long index);
	public ReadOnlyIterator<O> getXdiMembersOrdered();
	public long getXdiMembersOrderedCount();

	public ReadOnlyIterator<EQ> getXdiMembers(boolean deref);
}
