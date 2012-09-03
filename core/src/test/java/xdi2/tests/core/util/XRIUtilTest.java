package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.constants.XDIConstants;
import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.impl.XRI3Segment;

public class XRIUtilTest extends TestCase {

	public void testXRIUtil() throws Exception {

		XRI3Segment xri1 = new XRI3Segment("=a*b*c*d");
		XRI3Segment xri2 = new XRI3Segment("($)*b($)*d");

		assertEquals(XRIUtil.parentXri(xri1), new XRI3Segment("=a*b*c"));
		assertEquals(XRIUtil.parentXri(XRIUtil.parentXri(xri1)), new XRI3Segment("=a*b"));
		assertEquals(XRIUtil.parentXri(XRIUtil.parentXri(XRIUtil.parentXri(xri1))), new XRI3Segment("=a"));
		assertNull(XRIUtil.parentXri(XRIUtil.parentXri(XRIUtil.parentXri(XRIUtil.parentXri(xri1)))));

		assertEquals(XRIUtil.localXri(xri1), new XRI3Segment("*d"));

		assertTrue(XRIUtil.startsWith(xri1, new XRI3Segment("=a")));
		assertTrue(XRIUtil.startsWith(xri1, new XRI3Segment("=a*b")));
		assertTrue(XRIUtil.startsWith(xri1, new XRI3Segment("=a*b*c")));
		assertTrue(XRIUtil.startsWith(xri1, new XRI3Segment("=a*b*c*d")));
		assertFalse(XRIUtil.startsWith(xri1, new XRI3Segment("=x")));
		assertFalse(XRIUtil.startsWith(xri1, new XRI3Segment("=a*x*c")));

		assertTrue(XRIUtil.startsWith(xri1, new XRI3Segment("($)"), false, true));
		assertTrue(XRIUtil.startsWith(xri1, new XRI3Segment("=a($)"), false, true));
		assertTrue(XRIUtil.startsWith(xri1, new XRI3Segment("($)($)*c"), false, true));
		assertTrue(XRIUtil.startsWith(xri1, new XRI3Segment("($)*b*c*d"), false, true));
		assertFalse(XRIUtil.startsWith(xri1, new XRI3Segment("=x"), false, true));
		assertFalse(XRIUtil.startsWith(xri1, new XRI3Segment("=a*x*c"), false, true));

		assertFalse(XRIUtil.startsWith(xri1, new XRI3Segment("($)"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, new XRI3Segment("=a($)"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, new XRI3Segment("($)($)*c"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, new XRI3Segment("($)*b*c*d"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, new XRI3Segment("=x"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, new XRI3Segment("=a*x*c"), false, false));

		assertTrue(XRIUtil.startsWith(xri2, new XRI3Segment("=a"), true, false));
		assertTrue(XRIUtil.startsWith(xri2, new XRI3Segment("=a*b"), true, false));
		assertTrue(XRIUtil.startsWith(xri2, new XRI3Segment("=a*b*c"), true, false));
		assertTrue(XRIUtil.startsWith(xri2, new XRI3Segment("=a*b*c*d"), true, false));
		assertTrue(XRIUtil.startsWith(xri2, new XRI3Segment("=x"), true, false));
		assertFalse(XRIUtil.startsWith(xri2, new XRI3Segment("=a*x*c"), true, false));

		assertEquals(XRIUtil.relativeXri(xri1, new XRI3Segment("=a")), new XRI3Segment("*b*c*d"));
		assertEquals(XRIUtil.relativeXri(xri1, new XRI3Segment("=a*b")), new XRI3Segment("*c*d"));
		assertEquals(XRIUtil.relativeXri(xri1, new XRI3Segment("=a*b*c")), new XRI3Segment("*d"));
		assertEquals(XRIUtil.relativeXri(xri1, new XRI3Segment("=a*b*c*d")), XDIConstants.XRI_S_CONTEXT);
	}
}
