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
	 * @param address The address whose XDI peer root to find.
	 * @param create Whether the XDI peer root should be created, if it does not exist.
	 * @return The XDI peer root.
	 */
	public XdiPeerRoot getPeerRoot(XDIAddress XDIaddress, boolean create);

	/**
	 * Returns an XDI inner root under this XDI root.
	 * @param subject The subject address whose XDI inner root to find.
	 * @param predicate The predicate address whose XDI inner root to find.
	 * @param create Whether the XDI inner root should be created, if it does not exist.
	 * @return The XDI inner root.
	 */
	public XdiInnerRoot getInnerRoot(XDIAddress subject, XDIAddress predicate, boolean create);

	/**
	 * Returns an XDI root under this XDI root.
	 * @param address An address under this XDI root.
	 * @param create Whether the XDI root should be created, if it does not exist.
	 * @return The XDI root.
	 */
	public XdiRoot getRoot(XDIAddress XDIaddress, boolean create);

	/*
	 * Addresses and statements relative to this root
	 */

	public XDIAddress absoluteToRelativeXDIAddress(XDIAddress XDIaddress);

	public XDIAddress relativeToAbsoluteXDIAddress(XDIAddress XDIaddress);

	public XDIStatement absoluteToRelativeXDIStatement(XDIStatement statement);

	public XDIStatement relativeToAbsoluteXDIStatement(XDIStatement statement);

	/*
	 * Helper classes
	 */

	public static class MappingAbsoluteToRelativeXDIAddressIterator extends MappingIterator<XDIAddress, XDIAddress> {

		private XdiRoot xdiRoot;

		public MappingAbsoluteToRelativeXDIAddressIterator(XdiRoot xdiRoot, Iterator<? extends XDIAddress> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDIAddress map(XDIAddress XDIaddress) {

			return this.xdiRoot.absoluteToRelativeXDIAddress(XDIaddress);
		}
	}

	public static class MappingRelativeToAbsoluteXDIAddressIterator extends MappingIterator<XDIAddress, XDIAddress> {

		private XdiRoot xdiRoot;

		public MappingRelativeToAbsoluteXDIAddressIterator(XdiRoot xdiRoot, Iterator<? extends XDIAddress> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDIAddress map(XDIAddress XDIaddress) {

			return this.xdiRoot.relativeToAbsoluteXDIAddress(XDIaddress);
		}
	}

	public static class MappingAbsoluteToRelativeXDIStatementIterator extends MappingIterator<XDIStatement, XDIStatement> {

		private XdiRoot xdiRoot;

		public MappingAbsoluteToRelativeXDIStatementIterator(XdiRoot xdiRoot, Iterator<? extends XDIStatement> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDIStatement map(XDIStatement statement) {

			return this.xdiRoot.absoluteToRelativeXDIStatement(statement);
		}
	}

	public static class MappingRelativeToAbsoluteXDIStatementIterator extends MappingIterator<XDIStatement, XDIStatement> {

		private XdiRoot xdiRoot;

		public MappingRelativeToAbsoluteXDIStatementIterator(XdiRoot xdiRoot, Iterator<? extends XDIStatement> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDIStatement map(XDIStatement statement) {

			return this.xdiRoot.relativeToAbsoluteXDIStatement(statement);
		}
	}
}
