package xdi2.core.features.nodetypes;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.MappingIterator;

public interface XdiContext<EQ extends XdiContext<EQ>> extends Serializable, Comparable<XdiContext<?>> {

	/*
	 * General methods
	 */

	public ContextNode getContextNode();
	public Graph getGraph();
	public Graph toGraph();
	public XDIArc getXDIArc();
	public XDIAddress getXDIAddress();
	public XDIArc getBaseXDIArc();

	/*
	 * Equivalence relations
	 */

	public EQ dereference();
	public EQ getReferenceXdiContext();
	public EQ getReplacementXdiContext();
	public Iterator<EQ> getIdentityXdiContexts();

	/*
	 * Finding roots
	 */

	public XdiContext<?> findRoot();
	public XdiCommonRoot findCommonRoot();

	/*
	 * Getting contexts under this context
	 */

	public XdiInnerRoot getXdiInnerRoot(XDIAddress innerRootPredicateAddress, boolean create);
	public XdiEntityCollection getXdiEntityCollection(XDIArc contextNodeXDIArc, boolean create);
	public XdiEntityCollection getXdiEntityCollection(XDIAddress contextNodeXDIAddress, boolean create);
	public XdiAttributeCollection getXdiAttributeCollection(XDIArc contextNodeXDIArc, boolean create);
	public XdiAttributeCollection getXdiAttributeCollection(XDIAddress contextNodeXDIAddress, boolean create);
	public XdiEntitySingleton getXdiEntitySingleton(XDIArc contextNodeXDIArc, boolean create);
	public XdiEntitySingleton getXdiEntitySingleton(XDIAddress contextNodeXDIAddress, boolean create);
	public XdiAttributeSingleton getXdiAttributeSingleton(XDIArc contextNodeXDIArc, boolean create);
	public XdiAttributeSingleton getXdiAttributeSingleton(XDIAddress contextNodeXDIAddress, boolean create);
	public XdiEntity getXdiEntity(XDIArc contextNodeXDIArc, boolean create);
	public XdiEntity getXdiEntity(XDIAddress contextNodeXDIAddress, boolean create);
	public XdiAttribute getXdiAttribute(XDIArc contextNodeXDIArc, boolean create);
	public XdiAttribute getXdiAttribute(XDIAddress contextNodeXDIAddress, boolean create);

	/*
	 * Addresses and statements relative to this context
	 */

	public XDIAddress absoluteToRelativeXDIAddress(XDIAddress XDIaddress);
	public XDIAddress relativeToAbsoluteXDIAddress(XDIAddress XDIaddress);
	public XDIStatement absoluteToRelativeXDIStatement(XDIStatement XDIstatement);
	public XDIStatement relativeToAbsoluteXDIStatement(XDIStatement XDIstatement);

	/*
	 * Helper classes
	 */

	public static class MappingAbsoluteToRelativeXDIAddressIterator extends MappingIterator<XDIAddress, XDIAddress> {

		private XdiContext<?> xdiContext;

		public MappingAbsoluteToRelativeXDIAddressIterator(XdiContext<?> xdiContext, Iterator<? extends XDIAddress> iterator) {

			super(iterator);

			this.xdiContext = xdiContext;
		}

		@Override
		public XDIAddress map(XDIAddress XDIaddress) {

			return this.xdiContext.absoluteToRelativeXDIAddress(XDIaddress);
		}
	}

	public static class MappingRelativeToAbsoluteXDIAddressIterator extends MappingIterator<XDIAddress, XDIAddress> {

		private XdiContext<?> xdiContext;

		public MappingRelativeToAbsoluteXDIAddressIterator(XdiContext<?> xdiContext, Iterator<? extends XDIAddress> iterator) {

			super(iterator);

			this.xdiContext = xdiContext;
		}

		@Override
		public XDIAddress map(XDIAddress XDIaddress) {

			return this.xdiContext.relativeToAbsoluteXDIAddress(XDIaddress);
		}
	}

	public static class MappingAbsoluteToRelativeXDIStatementIterator extends MappingIterator<XDIStatement, XDIStatement> {

		private XdiContext<?> xdiContext;

		public MappingAbsoluteToRelativeXDIStatementIterator(XdiContext<?> xdiContext, Iterator<? extends XDIStatement> iterator) {

			super(iterator);

			this.xdiContext = xdiContext;
		}

		@Override
		public XDIStatement map(XDIStatement statement) {

			return this.xdiContext.absoluteToRelativeXDIStatement(statement);
		}
	}

	public static class MappingRelativeToAbsoluteXDIStatementIterator extends MappingIterator<XDIStatement, XDIStatement> {

		private XdiContext<?> xdiContext;

		public MappingRelativeToAbsoluteXDIStatementIterator(XdiContext<?> xdiContext, Iterator<? extends XDIStatement> iterator) {

			super(iterator);

			this.xdiContext = xdiContext;
		}

		@Override
		public XDIStatement map(XDIStatement statement) {

			return this.xdiContext.relativeToAbsoluteXDIStatement(statement);
		}
	}
}
