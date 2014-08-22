package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.MappingIterator;

public interface XdiRoot extends XdiContext<XdiRoot> {

	/*
	 * Finding roots related to this root
	 */

	/**
	 * Returns an XDI peer root under this XDI root.
	 * @param xri The XRI whose XDI peer root to find.
	 * @param create Whether the XDI peer root should be created, if it does not exist.
	 * @return The XDI peer root.
	 */
	public XdiPeerRoot getPeerRoot(XDIAddress xri, boolean create);

	/**
	 * Returns an XDI inner root under this XDI root.
	 * @param subject The subject XRI whose XDI inner root to find.
	 * @param predicate The predicate XRI whose XDI inner root to find.
	 * @param create Whether the XDI inner root should be created, if it does not exist.
	 * @return The XDI inner root.
	 */
	public XdiInnerRoot getInnerRoot(XDIAddress subject, XDIAddress predicate, boolean create);

	/**
	 * Returns an XDI root under this XDI root.
	 * @param xri The XRI contained in the XDI root.
	 * @param create Whether the XDI root should be created, if it does not exist.
	 * @return The XDI root.
	 */
	public XdiRoot getRoot(XDIAddress xri, boolean create);

	/*
	 * XRIs and statements relative to this root
	 */

	public XDIAddress absoluteToRelativeXri(XDIAddress xri);

	public XDIAddress relativeToAbsoluteXri(XDIAddress xri);

	public XDIStatement absoluteToRelativeStatementXri(XDIStatement statementXri);

	public XDIStatement relativeToAbsoluteStatementXri(XDIStatement statementXri);

	/*
	 * Helper classes
	 */

	public static class MappingAbsoluteToRelativeXriIterator extends MappingIterator<XDIAddress, XDIAddress> {

		private XdiRoot xdiRoot;

		public MappingAbsoluteToRelativeXriIterator(XdiRoot xdiRoot, Iterator<? extends XDIAddress> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDIAddress map(XDIAddress xri) {

			return this.xdiRoot.absoluteToRelativeXri(xri);
		}
	}

	public static class MappingRelativeToAbsoluteXriIterator extends MappingIterator<XDIAddress, XDIAddress> {

		private XdiRoot xdiRoot;

		public MappingRelativeToAbsoluteXriIterator(XdiRoot xdiRoot, Iterator<? extends XDIAddress> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDIAddress map(XDIAddress xri) {

			return this.xdiRoot.relativeToAbsoluteXri(xri);
		}
	}

	public static class MappingAbsoluteToRelativeStatementXriIterator extends MappingIterator<XDIStatement, XDIStatement> {

		private XdiRoot xdiRoot;

		public MappingAbsoluteToRelativeStatementXriIterator(XdiRoot xdiRoot, Iterator<? extends XDIStatement> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDIStatement map(XDIStatement statementXri) {

			return this.xdiRoot.absoluteToRelativeStatementXri(statementXri);
		}
	}

	public static class MappingRelativeToAbsoluteStatementXriIterator extends MappingIterator<XDIStatement, XDIStatement> {

		private XdiRoot xdiRoot;

		public MappingRelativeToAbsoluteStatementXriIterator(XdiRoot xdiRoot, Iterator<? extends XDIStatement> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDIStatement map(XDIStatement statementXri) {

			return this.xdiRoot.relativeToAbsoluteStatementXri(statementXri);
		}
	}
}
