package xdi2.core.features.nodetypes;

import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3SubSegment;

public interface XdiCollection<C extends XdiCollection<C, U, O, I>, U extends XdiMemberUnordered<C, U, O, I>, O extends XdiMemberOrdered<C, U, O, I>, I extends XdiMember<C, U, O, I>> extends XdiSubGraph {

	public U setXdiMemberUnordered(XDI3SubSegment arcXri);
	public U getXdiMemberUnordered(XDI3SubSegment arcXri);
	public ReadOnlyIterator<U> getXdiMembersUnordered();
	public long getXdiMembersUnorderedCount();

	public O setXdiMemberOrdered(long index);
	public O getXdiMemberOrdered(long index);
	public ReadOnlyIterator<O> getXdiMembersOrdered();
	public long getXdiMembersOrderedCount();

	public ReadOnlyIterator<I> getXdiMembers(boolean deref);
}
