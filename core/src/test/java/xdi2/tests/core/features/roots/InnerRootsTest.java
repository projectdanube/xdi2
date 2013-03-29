package xdi2.tests.core.features.roots;

import junit.framework.TestCase;
import xdi2.core.features.roots.XdiInnerRoot;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class InnerRootsTest extends TestCase {

	public void testInnerRootXris() throws Exception {

		assertFalse(XdiInnerRoot.isInnerRootArcXri(XDI3SubSegment.create("()")));
		assertFalse(XdiInnerRoot.isInnerRootArcXri(XDI3SubSegment.create("(=!1111!23)")));
		assertTrue(XdiInnerRoot.isInnerRootArcXri(XDI3SubSegment.create("(=a*b/+c*d)")));

		assertEquals(XdiInnerRoot.createInnerRootArcXri(XDI3Segment.create("=a*b"), XDI3Segment.create("+c*d")), XDI3SubSegment.create("(=a*b/+c*d)"));
		assertEquals(XdiInnerRoot.getSubjectOfInnerRootXri(XDI3SubSegment.create("(=a*b/+c*d)")), XDI3Segment.create("=a*b"));
		assertEquals(XdiInnerRoot.getPredicateOfInnerRootXri(XDI3SubSegment.create("(=a*b/+c*d)")), XDI3Segment.create("+c*d"));
	}
}
