package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

public interface XdiRoot extends XdiContext<XdiRoot> {

	/*
	 * Finding roots related to this root
	 */

	/**
	 * Finds and returns the XDI local root for this XDI root.
	 * @return The XDI local root.
	 */
	public XdiLocalRoot findLocalRoot();

	/**
	 * Finds and returns an XDI peer root under this XDI root.
	 * @param xri The XRI whose XDI peer root to find.
	 * @param create Whether the XDI peer root should be created, if it does not exist.
	 * @return The XDI peer root.
	 */
	public XdiPeerRoot findPeerRoot(XDI3Segment xri, boolean create);

	/**
	 * Finds and returns an XDI inner root under this XDI root.
	 * @param subject The subject XRI whose XDI inner root to find.
	 * @param predicate The predicate XRI whose XDI inner root to find.
	 * @param create Whether the XDI inner root should be created, if it does not exist.
	 * @return The XDI inner root.
	 */
	public XdiInnerRoot findInnerRoot(XDI3Segment subject, XDI3Segment predicate, boolean create);

	/**
	 * Finds and returns an XDI root under this XDI root.
	 * @param xri The XRI contained in the XDI root.
	 * @param create Whether the XDI root should be created, if it does not exist.
	 * @return The XDI root.
	 */
	public XdiRoot findRoot(XDI3Segment xri, boolean create);

	/*
	 * XRIs and statements relative to this root
	 */

	public XDI3Segment absoluteToRelativeXri(XDI3Segment xri);

	public XDI3Segment relativeToAbsoluteXri(XDI3Segment xri);

	public XDI3Statement absoluteToRelativeStatementXri(XDI3Statement statementXri);

	public XDI3Statement relativeToAbsoluteStatementXri(XDI3Statement statementXri);

	/*
	 * Helper classes
	 */

	public static class MappingAbsoluteToRelativeXriIterator extends MappingIterator<XDI3Segment, XDI3Segment> {

		private XdiRoot xdiRoot;

		public MappingAbsoluteToRelativeXriIterator(XdiRoot xdiRoot, Iterator<? extends XDI3Segment> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDI3Segment map(XDI3Segment xri) {

			return this.xdiRoot.absoluteToRelativeXri(xri);
		}
	}

	public static class MappingRelativeToAbsoluteXriIterator extends MappingIterator<XDI3Segment, XDI3Segment> {

		private XdiRoot xdiRoot;

		public MappingRelativeToAbsoluteXriIterator(XdiRoot xdiRoot, Iterator<? extends XDI3Segment> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDI3Segment map(XDI3Segment xri) {

			return this.xdiRoot.relativeToAbsoluteXri(xri);
		}
	}

	public static class MappingAbsoluteToRelativeStatementXriIterator extends MappingIterator<XDI3Statement, XDI3Statement> {

		private XdiRoot xdiRoot;

		public MappingAbsoluteToRelativeStatementXriIterator(XdiRoot xdiRoot, Iterator<? extends XDI3Statement> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDI3Statement map(XDI3Statement statementXri) {

			return this.xdiRoot.absoluteToRelativeStatementXri(statementXri);
		}
	}

	public static class MappingRelativeToAbsoluteStatementXriIterator extends MappingIterator<XDI3Statement, XDI3Statement> {

		private XdiRoot xdiRoot;

		public MappingRelativeToAbsoluteStatementXriIterator(XdiRoot xdiRoot, Iterator<? extends XDI3Statement> iterator) {

			super(iterator);

			this.xdiRoot = xdiRoot;
		}

		@Override
		public XDI3Statement map(XDI3Statement statementXri) {

			return this.xdiRoot.relativeToAbsoluteStatementXri(statementXri);
		}
	}
}
