package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.impl.XDI3Segment;

public class XRIUtilTest extends TestCase {

	public void testXRIUtil() throws Exception {

		XDI3Segment xri1 = new XDI3Segment("=a*b*c*d");
		XDI3Segment xri2 = new XDI3Segment("($)*b($)*d");

		assertEquals(XRIUtil.parentXri(xri1, -1), new XDI3Segment("=a*b*c"));
		assertEquals(XRIUtil.parentXri(XRIUtil.parentXri(xri1, -1), -1), new XDI3Segment("=a*b"));
		assertEquals(XRIUtil.parentXri(XRIUtil.parentXri(XRIUtil.parentXri(xri1, -1), -1), -1), new XDI3Segment("=a"));
		assertNull(XRIUtil.parentXri(XRIUtil.parentXri(XRIUtil.parentXri(XRIUtil.parentXri(xri1, -1), -1), -1), -1));

		assertEquals(XRIUtil.localXri(xri1, 1), new XDI3Segment("*d"));

		assertEquals(XRIUtil.parentXri(xri1, 1), new XDI3Segment("=a"));
		assertEquals(XRIUtil.parentXri(xri1, -1), new XDI3Segment("=a*b*c"));
		assertEquals(XRIUtil.parentXri(xri1, 2), new XDI3Segment("=a*b"));
		assertEquals(XRIUtil.parentXri(xri1, -2), new XDI3Segment("=a*b"));
		assertEquals(XRIUtil.parentXri(xri1, 3), new XDI3Segment("=a*b*c"));
		assertEquals(XRIUtil.parentXri(xri1, -3), new XDI3Segment("=a"));
		assertEquals(XRIUtil.localXri(xri1, 1), new XDI3Segment("*d"));
		assertEquals(XRIUtil.localXri(xri1, -1), new XDI3Segment("*b*c*d"));
		assertEquals(XRIUtil.localXri(xri1, 2), new XDI3Segment("*c*d"));
		assertEquals(XRIUtil.localXri(xri1, -2), new XDI3Segment("*c*d"));
		assertEquals(XRIUtil.localXri(xri1, 3), new XDI3Segment("*b*c*d"));
		assertEquals(XRIUtil.localXri(xri1, -3), new XDI3Segment("*d"));

		assertTrue(XRIUtil.startsWith(xri1, new XDI3Segment("=a")));
		assertTrue(XRIUtil.startsWith(xri1, new XDI3Segment("=a*b")));
		assertTrue(XRIUtil.startsWith(xri1, new XDI3Segment("=a*b*c")));
		assertTrue(XRIUtil.startsWith(xri1, new XDI3Segment("=a*b*c*d")));
		assertFalse(XRIUtil.startsWith(xri1, new XDI3Segment("=x")));
		assertFalse(XRIUtil.startsWith(xri1, new XDI3Segment("=a*x*c")));

		assertTrue(XRIUtil.startsWith(xri1, new XDI3Segment("($)"), false, true));
		assertTrue(XRIUtil.startsWith(xri1, new XDI3Segment("=a($)"), false, true));
		assertTrue(XRIUtil.startsWith(xri1, new XDI3Segment("($)($)*c"), false, true));
		assertTrue(XRIUtil.startsWith(xri1, new XDI3Segment("($)*b*c*d"), false, true));
		assertFalse(XRIUtil.startsWith(xri1, new XDI3Segment("=x"), false, true));
		assertFalse(XRIUtil.startsWith(xri1, new XDI3Segment("=a*x*c"), false, true));

		assertFalse(XRIUtil.startsWith(xri1, new XDI3Segment("($)"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, new XDI3Segment("=a($)"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, new XDI3Segment("($)($)*c"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, new XDI3Segment("($)*b*c*d"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, new XDI3Segment("=x"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, new XDI3Segment("=a*x*c"), false, false));

		assertTrue(XRIUtil.startsWith(xri2, new XDI3Segment("=a"), true, false));
		assertTrue(XRIUtil.startsWith(xri2, new XDI3Segment("=a*b"), true, false));
		assertTrue(XRIUtil.startsWith(xri2, new XDI3Segment("=a*b*c"), true, false));
		assertTrue(XRIUtil.startsWith(xri2, new XDI3Segment("=a*b*c*d"), true, false));
		assertTrue(XRIUtil.startsWith(xri2, new XDI3Segment("=x"), true, false));
		assertFalse(XRIUtil.startsWith(xri2, new XDI3Segment("=a*x*c"), true, false));

		assertEquals(XRIUtil.relativeXri(xri1, new XDI3Segment("=a")), new XDI3Segment("*b*c*d"));
		assertEquals(XRIUtil.relativeXri(xri1, new XDI3Segment("=a*b")), new XDI3Segment("*c*d"));
		assertEquals(XRIUtil.relativeXri(xri1, new XDI3Segment("=a*b*c")), new XDI3Segment("*d"));
		assertNull(XRIUtil.relativeXri(xri1, new XDI3Segment("=a*b*c*d")));
	}

	public void testRelative() throws Exception {

		XDI3Segment xri1 = new XDI3Segment("=a*b+c!d@e$f*g");

		assertEquals(XRIUtil.relativeXri(xri1, new XDI3Segment("($)"), false, true), new XDI3Segment("*b+c!d@e$f*g"));
		assertEquals(XRIUtil.relativeXri(xri1, new XDI3Segment("($)($)"), false, true), new XDI3Segment("+c!d@e$f*g"));
		assertEquals(XRIUtil.relativeXri(xri1, new XDI3Segment("($$!)"), false, true), new XDI3Segment("+c!d@e$f*g"));
		assertEquals(XRIUtil.relativeXri(xri1, new XDI3Segment("($)($$!)"), false, true), new XDI3Segment("+c!d@e$f*g"));
		assertEquals(XRIUtil.relativeXri(xri1, new XDI3Segment("($$!)($$!)"), false, true), new XDI3Segment("@e$f*g"));
		assertEquals(XRIUtil.relativeXri(xri1, new XDI3Segment("($$!)($)($$!)"), false, true), new XDI3Segment("@e$f*g"));
		assertEquals(XRIUtil.relativeXri(xri1, new XDI3Segment("($$!)($)($$!)($)"), false, true), new XDI3Segment("$f*g"));
		assertEquals(XRIUtil.relativeXri(xri1, new XDI3Segment("($$!)($)($$!)($$!)"), false, true), new XDI3Segment("$f*g"));
		assertNull(XRIUtil.relativeXri(xri1, new XDI3Segment("($$!)($)($$!)($$!)($$!)"), false, true));
	}
}
