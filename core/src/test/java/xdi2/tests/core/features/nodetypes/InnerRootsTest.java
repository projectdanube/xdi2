package xdi2.tests.core.features.nodetypes;

import junit.framework.TestCase;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

public class InnerRootsTest extends TestCase {

	public void testInnerRootAddresss() throws Exception {

		assertFalse(XdiInnerRoot.isInnerRootArc(XDIArc.create("")));
		assertFalse(XdiInnerRoot.isInnerRootArc(XDIArc.create("([=]!1111!23)")));
		assertTrue(XdiInnerRoot.isInnerRootArc(XDIArc.create("(=a*b/+c*d)")));

		assertEquals(XdiInnerRoot.createInnerRootarc(XDIAddress.create("=a*b"), XDIAddress.create("+c*d")), XDIArc.create("(=a*b/+c*d)"));
		assertEquals(XdiInnerRoot.getSubjectOfInnerRootArc(XDIArc.create("(=a*b/+c*d)")), XDIAddress.create("=a*b"));
		assertEquals(XdiInnerRoot.getPredicateOfInnerRootArc(XDIArc.create("(=a*b/+c*d)")), XDIAddress.create("+c*d"));
	}
}
