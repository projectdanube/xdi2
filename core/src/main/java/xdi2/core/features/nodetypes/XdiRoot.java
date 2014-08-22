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
	public XdiPeerRoot getPeerRoot(XDIAddress address, boolean create);

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
	public XdiRoot getRoot(XDIAddress address, boolean create);

	/*
	 * Addresses and statements relative to this root
	 */

	public XDIAddress absoluteToRelativeAddress(XDIAddress address);

	public XDIAddress relativeToAbsoluteAddress(XDIAddress address);

	public XDIStatement absoluteToRelativeStatement(XDIStatement statement);

	public XDIStatement relativeToAbsoluteStatement(XDIStatement statement);

	/*
	 * Helper classes
	 */

	public static class MappingAbsoluteToRelativeAddressIterator extends MappingIterator<XDIAddress, XDIAddress> {

		private XdiRoot xdiRoot;

		public MappingAbsoluteToRelativeAddressIterator(XdiRoot xdiRoot, Iterator<? extends XDIAddress> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDIAddress map(XDIAddress address) {

			return this.xdiRoot.absoluteToRelativeAddress(address);
		}
	}

	public static class MappingRelativeToAbsoluteAddressIterator extends MappingIterator<XDIAddress, XDIAddress> {

		private XdiRoot xdiRoot;

		public MappingRelativeToAbsoluteAddressIterator(XdiRoot xdiRoot, Iterator<? extends XDIAddress> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDIAddress map(XDIAddress address) {

			return this.xdiRoot.relativeToAbsoluteAddress(address);
		}
	}

	public static class MappingAbsoluteToRelativeStatementIterator extends MappingIterator<XDIStatement, XDIStatement> {

		private XdiRoot xdiRoot;

		public MappingAbsoluteToRelativeStatementIterator(XdiRoot xdiRoot, Iterator<? extends XDIStatement> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDIStatement map(XDIStatement statement) {

			return this.xdiRoot.absoluteToRelativeStatement(statement);
		}
	}

	public static class MappingRelativeToAbsoluteStatementIterator extends MappingIterator<XDIStatement, XDIStatement> {

		private XdiRoot xdiRoot;

		public MappingRelativeToAbsoluteStatementIterator(XdiRoot xdiRoot, Iterator<? extends XDIStatement> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDIStatement map(XDIStatement statement) {

			return this.xdiRoot.relativeToAbsoluteStatement(statement);
		}
	}
}
