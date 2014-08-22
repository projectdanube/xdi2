package xdi2.tests.core.features.nodetypes;

import junit.framework.TestCase;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

public class InnerRootsTest extends TestCase {

	public void testInnerRootXris() throws Exception {

		assertFalse(XdiInnerRoot.isInnerRootarc(XDIArc.create("")));
		assertFalse(XdiInnerRoot.isInnerRootarc(XDIArc.create("([=]!1111!23)")));
		assertTrue(XdiInnerRoot.isInnerRootarc(XDIArc.create("(=a*b/+c*d)")));

		assertEquals(XdiInnerRoot.createInnerRootarc(XDIAddress.create("=a*b"), XDIAddress.create("+c*d")), XDIArc.create("(=a*b/+c*d)"));
		assertEquals(XdiInnerRoot.getSubjectOfInnerRootXri(XDIArc.create("(=a*b/+c*d)")), XDIAddress.create("=a*b"));
		assertEquals(XdiInnerRoot.getPredicateOfInnerRootXri(XDIArc.create("(=a*b/+c*d)")), XDIAddress.create("+c*d"));
	}
}
