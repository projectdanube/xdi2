package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.XDI3Segment;

public class XRIUtilTest extends TestCase {

	public void testXRIUtil() throws Exception {

		XDI3Segment xri1 = XDI3Segment.create("=a*b*c*d");
		XDI3Segment xri2 = XDI3Segment.create("($)*b($)*d");

		assertEquals(XRIUtil.parentXri(xri1, -1), XDI3Segment.create("=a*b*c"));
		assertEquals(XRIUtil.parentXri(XRIUtil.parentXri(xri1, -1), -1), XDI3Segment.create("=a*b"));
		assertEquals(XRIUtil.parentXri(XRIUtil.parentXri(XRIUtil.parentXri(xri1, -1), -1), -1), XDI3Segment.create("=a"));
		assertNull(XRIUtil.parentXri(XRIUtil.parentXri(XRIUtil.parentXri(XRIUtil.parentXri(xri1, -1), -1), -1), -1));

		assertEquals(XRIUtil.localXri(xri1, 1), XDI3Segment.create("*d"));

		assertEquals(XRIUtil.parentXri(xri1, 1), XDI3Segment.create("=a"));
		assertEquals(XRIUtil.parentXri(xri1, -1), XDI3Segment.create("=a*b*c"));
		assertEquals(XRIUtil.parentXri(xri1, 2), XDI3Segment.create("=a*b"));
		assertEquals(XRIUtil.parentXri(xri1, -2), XDI3Segment.create("=a*b"));
		assertEquals(XRIUtil.parentXri(xri1, 3), XDI3Segment.create("=a*b*c"));
		assertEquals(XRIUtil.parentXri(xri1, -3), XDI3Segment.create("=a"));
		assertEquals(XRIUtil.localXri(xri1, 1), XDI3Segment.create("*d"));
		assertEquals(XRIUtil.localXri(xri1, -1), XDI3Segment.create("*b*c*d"));
		assertEquals(XRIUtil.localXri(xri1, 2), XDI3Segment.create("*c*d"));
		assertEquals(XRIUtil.localXri(xri1, -2), XDI3Segment.create("*c*d"));
		assertEquals(XRIUtil.localXri(xri1, 3), XDI3Segment.create("*b*c*d"));
		assertEquals(XRIUtil.localXri(xri1, -3), XDI3Segment.create("*d"));

		assertTrue(XRIUtil.startsWith(xri1, XDI3Segment.create("=a")));
		assertTrue(XRIUtil.startsWith(xri1, XDI3Segment.create("=a*b")));
		assertTrue(XRIUtil.startsWith(xri1, XDI3Segment.create("=a*b*c")));
		assertTrue(XRIUtil.startsWith(xri1, XDI3Segment.create("=a*b*c*d")));
		assertFalse(XRIUtil.startsWith(xri1, XDI3Segment.create("=x")));
		assertFalse(XRIUtil.startsWith(xri1, XDI3Segment.create("=a*x*c")));

		assertTrue(XRIUtil.startsWith(xri1, XDI3Segment.create("($)"), false, true));
		assertTrue(XRIUtil.startsWith(xri1, XDI3Segment.create("=a($)"), false, true));
		assertTrue(XRIUtil.startsWith(xri1, XDI3Segment.create("($)($)*c"), false, true));
		assertTrue(XRIUtil.startsWith(xri1, XDI3Segment.create("($)*b*c*d"), false, true));
		assertFalse(XRIUtil.startsWith(xri1, XDI3Segment.create("=x"), false, true));
		assertFalse(XRIUtil.startsWith(xri1, XDI3Segment.create("=a*x*c"), false, true));

		assertFalse(XRIUtil.startsWith(xri1, XDI3Segment.create("($)"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, XDI3Segment.create("=a($)"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, XDI3Segment.create("($)($)*c"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, XDI3Segment.create("($)*b*c*d"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, XDI3Segment.create("=x"), false, false));
		assertFalse(XRIUtil.startsWith(xri1, XDI3Segment.create("=a*x*c"), false, false));

		assertTrue(XRIUtil.startsWith(xri2, XDI3Segment.create("=a"), true, false));
		assertTrue(XRIUtil.startsWith(xri2, XDI3Segment.create("=a*b"), true, false));
		assertTrue(XRIUtil.startsWith(xri2, XDI3Segment.create("=a*b*c"), true, false));
		assertTrue(XRIUtil.startsWith(xri2, XDI3Segment.create("=a*b*c*d"), true, false));
		assertTrue(XRIUtil.startsWith(xri2, XDI3Segment.create("=x"), true, false));
		assertFalse(XRIUtil.startsWith(xri2, XDI3Segment.create("=a*x*c"), true, false));

		assertTrue(XRIUtil.endsWith(xri1, XDI3Segment.create("*d")));
		assertTrue(XRIUtil.endsWith(xri1, XDI3Segment.create("*c*d")));
		assertTrue(XRIUtil.endsWith(xri1, XDI3Segment.create("*b*c*d")));
		assertTrue(XRIUtil.endsWith(xri1, XDI3Segment.create("=a*b*c*d")));
		assertFalse(XRIUtil.endsWith(xri1, XDI3Segment.create("*y")));
		assertFalse(XRIUtil.endsWith(xri1, XDI3Segment.create("*b*y*d")));

		assertTrue(XRIUtil.endsWith(xri1, XDI3Segment.create("($)"), false, true));
		assertTrue(XRIUtil.endsWith(xri1, XDI3Segment.create("($)*d"), false, true));
		assertTrue(XRIUtil.endsWith(xri1, XDI3Segment.create("*b($)($)"), false, true));
		assertTrue(XRIUtil.endsWith(xri1, XDI3Segment.create("=a*b*c($)"), false, true));
		assertFalse(XRIUtil.endsWith(xri1, XDI3Segment.create("*y"), false, true));
		assertFalse(XRIUtil.endsWith(xri1, XDI3Segment.create("*b*y*d"), false, true));

		assertFalse(XRIUtil.endsWith(xri1, XDI3Segment.create("($)"), false, false));
		assertFalse(XRIUtil.endsWith(xri1, XDI3Segment.create("($)*d"), false, false));
		assertFalse(XRIUtil.endsWith(xri1, XDI3Segment.create("*b($)($)"), false, false));
		assertFalse(XRIUtil.endsWith(xri1, XDI3Segment.create("=a*b*c($)"), false, false));
		assertFalse(XRIUtil.endsWith(xri1, XDI3Segment.create("*y"), false, false));
		assertFalse(XRIUtil.endsWith(xri1, XDI3Segment.create("*b*y*d"), false, false));

		assertTrue(XRIUtil.endsWith(xri2, XDI3Segment.create("*d"), true, false));
		assertTrue(XRIUtil.endsWith(xri2, XDI3Segment.create("*c*d"), true, false));
		assertTrue(XRIUtil.endsWith(xri2, XDI3Segment.create("*b*c*d"), true, false));
		assertTrue(XRIUtil.endsWith(xri2, XDI3Segment.create("=a*b*c*d"), true, false));
		assertTrue(XRIUtil.endsWith(xri2, XDI3Segment.create("*y*d"), true, false));
		assertFalse(XRIUtil.endsWith(xri2, XDI3Segment.create("*y*c*d"), true, false));

		assertEquals(XRIUtil.reduceXri(xri1, XDI3Segment.create("=a")), XDI3Segment.create("*b*c*d"));
		assertEquals(XRIUtil.reduceXri(xri1, XDI3Segment.create("=a*b")), XDI3Segment.create("*c*d"));
		assertEquals(XRIUtil.reduceXri(xri1, XDI3Segment.create("=a*b*c")), XDI3Segment.create("*d"));
		assertNull(XRIUtil.reduceXri(xri1, XDI3Segment.create("=a*b*c*d")));
	}

	public void testreduce() throws Exception {

		XDI3Segment xri1 = XDI3Segment.create("=a*b+c!d@e$f*g");

		assertEquals(XRIUtil.reduceXri(xri1, XDI3Segment.create("($)"), false, true), XDI3Segment.create("*b+c!d@e$f*g"));
		assertEquals(XRIUtil.reduceXri(xri1, XDI3Segment.create("($)($)"), false, true), XDI3Segment.create("+c!d@e$f*g"));
		assertEquals(XRIUtil.reduceXri(xri1, XDI3Segment.create("($$!)"), false, true), XDI3Segment.create("+c!d@e$f*g"));
		assertEquals(XRIUtil.reduceXri(xri1, XDI3Segment.create("($)($$!)"), false, true), XDI3Segment.create("+c!d@e$f*g"));
		assertEquals(XRIUtil.reduceXri(xri1, XDI3Segment.create("($$!)($$!)"), false, true), XDI3Segment.create("@e$f*g"));
		assertEquals(XRIUtil.reduceXri(xri1, XDI3Segment.create("($$!)($)($$!)"), false, true), XDI3Segment.create("@e$f*g"));
		assertEquals(XRIUtil.reduceXri(xri1, XDI3Segment.create("($$!)($)($$!)($)"), false, true), XDI3Segment.create("$f*g"));
		assertEquals(XRIUtil.reduceXri(xri1, XDI3Segment.create("($$!)($)($$!)($$!)"), false, true), XDI3Segment.create("$f*g"));
		assertNull(XRIUtil.reduceXri(xri1, XDI3Segment.create("($$!)($)($$!)($$!)($$!)"), false, true));
	}
}
