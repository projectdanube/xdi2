package xdi2.tests.core.features.nodetypes;

import junit.framework.TestCase;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

public class InnerRootsTest extends TestCase {

	public void testInnerRootAddresses() throws Exception {

		assertFalse(XdiInnerRoot.isValidXDIArc(XDIArc.create("")));
		assertFalse(XdiInnerRoot.isValidXDIArc(XDIArc.create("(=!1111*!23)")));
		assertTrue(XdiInnerRoot.isValidXDIArc(XDIArc.create("(=a*b/+c*d)")));

		assertEquals(XdiInnerRoot.createInnerRootXDIArc(XDIAddress.create("=a*b"), XDIAddress.create("+c*d")), XDIArc.create("(=a*b/+c*d)"));
		assertEquals(XdiInnerRoot.getSubjectOfInnerRootXDIArc(XDIArc.create("(=a*b/+c*d)")), XDIAddress.create("=a*b"));
		assertEquals(XdiInnerRoot.getPredicateOfInnerRootXDIArc(XDIArc.create("(=a*b/+c*d)")), XDIAddress.create("+c*d"));
	}
}
