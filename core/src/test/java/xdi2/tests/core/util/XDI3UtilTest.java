package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;

public class XDI3UtilTest extends TestCase {

	public void testXRIUtil() throws Exception {

		XDI3Segment xri1 = XDI3Segment.create("=a*b*c*d");
		XDI3Segment xri2 = XDI3Segment.create("{}*b{}*d");

		assertEquals(XDI3Util.parentXri(xri1, -1), XDI3Segment.create("=a*b*c"));
		assertEquals(XDI3Util.parentXri(XDI3Util.parentXri(xri1, -1), -1), XDI3Segment.create("=a*b"));
		assertEquals(XDI3Util.parentXri(XDI3Util.parentXri(XDI3Util.parentXri(xri1, -1), -1), -1), XDI3Segment.create("=a"));
		assertNull(XDI3Util.parentXri(XDI3Util.parentXri(XDI3Util.parentXri(XDI3Util.parentXri(xri1, -1), -1), -1), -1));

		assertEquals(XDI3Util.localXri(xri1, 1), XDI3Segment.create("*d"));

		assertEquals(XDI3Util.parentXri(xri1, 1), XDI3Segment.create("=a"));
		assertEquals(XDI3Util.parentXri(xri1, -1), XDI3Segment.create("=a*b*c"));
		assertEquals(XDI3Util.parentXri(xri1, 2), XDI3Segment.create("=a*b"));
		assertEquals(XDI3Util.parentXri(xri1, -2), XDI3Segment.create("=a*b"));
		assertEquals(XDI3Util.parentXri(xri1, 3), XDI3Segment.create("=a*b*c"));
		assertEquals(XDI3Util.parentXri(xri1, -3), XDI3Segment.create("=a"));
		assertEquals(XDI3Util.localXri(xri1, 1), XDI3Segment.create("*d"));
		assertEquals(XDI3Util.localXri(xri1, -1), XDI3Segment.create("*b*c*d"));
		assertEquals(XDI3Util.localXri(xri1, 2), XDI3Segment.create("*c*d"));
		assertEquals(XDI3Util.localXri(xri1, -2), XDI3Segment.create("*c*d"));
		assertEquals(XDI3Util.localXri(xri1, 3), XDI3Segment.create("*b*c*d"));
		assertEquals(XDI3Util.localXri(xri1, -3), XDI3Segment.create("*d"));

		assertTrue(XDI3Util.startsWith(xri1, XDI3Segment.create("=a")));
		assertTrue(XDI3Util.startsWith(xri1, XDI3Segment.create("=a*b")));
		assertTrue(XDI3Util.startsWith(xri1, XDI3Segment.create("=a*b*c")));
		assertTrue(XDI3Util.startsWith(xri1, XDI3Segment.create("=a*b*c*d")));
		assertFalse(XDI3Util.startsWith(xri1, XDI3Segment.create("=x")));
		assertFalse(XDI3Util.startsWith(xri1, XDI3Segment.create("=a*x*c")));

		assertTrue(XDI3Util.startsWith(xri1, XDI3Segment.create("{}"), false, true));
		assertTrue(XDI3Util.startsWith(xri1, XDI3Segment.create("=a{}"), false, true));
		assertTrue(XDI3Util.startsWith(xri1, XDI3Segment.create("{}{}*c"), false, true));
		assertTrue(XDI3Util.startsWith(xri1, XDI3Segment.create("{}*b*c*d"), false, true));
		assertFalse(XDI3Util.startsWith(xri1, XDI3Segment.create("=x"), false, true));
		assertFalse(XDI3Util.startsWith(xri1, XDI3Segment.create("=a*x*c"), false, true));

		assertFalse(XDI3Util.startsWith(xri1, XDI3Segment.create("{}"), false, false));
		assertFalse(XDI3Util.startsWith(xri1, XDI3Segment.create("=a{}"), false, false));
		assertFalse(XDI3Util.startsWith(xri1, XDI3Segment.create("{}{}*c"), false, false));
		assertFalse(XDI3Util.startsWith(xri1, XDI3Segment.create("{}*b*c*d"), false, false));
		assertFalse(XDI3Util.startsWith(xri1, XDI3Segment.create("=x"), false, false));
		assertFalse(XDI3Util.startsWith(xri1, XDI3Segment.create("=a*x*c"), false, false));

		assertTrue(XDI3Util.startsWith(xri2, XDI3Segment.create("=a"), true, false));
		assertTrue(XDI3Util.startsWith(xri2, XDI3Segment.create("=a*b"), true, false));
		assertTrue(XDI3Util.startsWith(xri2, XDI3Segment.create("=a*b*c"), true, false));
		assertTrue(XDI3Util.startsWith(xri2, XDI3Segment.create("=a*b*c*d"), true, false));
		assertTrue(XDI3Util.startsWith(xri2, XDI3Segment.create("=x"), true, false));
		assertFalse(XDI3Util.startsWith(xri2, XDI3Segment.create("=a*x*c"), true, false));

		assertTrue(XDI3Util.endsWith(xri1, XDI3Segment.create("*d")));
		assertTrue(XDI3Util.endsWith(xri1, XDI3Segment.create("*c*d")));
		assertTrue(XDI3Util.endsWith(xri1, XDI3Segment.create("*b*c*d")));
		assertTrue(XDI3Util.endsWith(xri1, XDI3Segment.create("=a*b*c*d")));
		assertFalse(XDI3Util.endsWith(xri1, XDI3Segment.create("*y")));
		assertFalse(XDI3Util.endsWith(xri1, XDI3Segment.create("*b*y*d")));

		assertTrue(XDI3Util.endsWith(xri1, XDI3Segment.create("{}"), false, true));
		assertTrue(XDI3Util.endsWith(xri1, XDI3Segment.create("{}*d"), false, true));
		assertTrue(XDI3Util.endsWith(xri1, XDI3Segment.create("*b{}{}"), false, true));
		assertTrue(XDI3Util.endsWith(xri1, XDI3Segment.create("=a*b*c{}"), false, true));
		assertFalse(XDI3Util.endsWith(xri1, XDI3Segment.create("*y"), false, true));
		assertFalse(XDI3Util.endsWith(xri1, XDI3Segment.create("*b*y*d"), false, true));

		assertFalse(XDI3Util.endsWith(xri1, XDI3Segment.create("{}"), false, false));
		assertFalse(XDI3Util.endsWith(xri1, XDI3Segment.create("{}*d"), false, false));
		assertFalse(XDI3Util.endsWith(xri1, XDI3Segment.create("*b{}{}"), false, false));
		assertFalse(XDI3Util.endsWith(xri1, XDI3Segment.create("=a*b*c{}"), false, false));
		assertFalse(XDI3Util.endsWith(xri1, XDI3Segment.create("*y"), false, false));
		assertFalse(XDI3Util.endsWith(xri1, XDI3Segment.create("*b*y*d"), false, false));

		assertTrue(XDI3Util.endsWith(xri2, XDI3Segment.create("*d"), true, false));
		assertTrue(XDI3Util.endsWith(xri2, XDI3Segment.create("*c*d"), true, false));
		assertTrue(XDI3Util.endsWith(xri2, XDI3Segment.create("*b*c*d"), true, false));
		assertTrue(XDI3Util.endsWith(xri2, XDI3Segment.create("=a*b*c*d"), true, false));
		assertTrue(XDI3Util.endsWith(xri2, XDI3Segment.create("*y*d"), true, false));
		assertFalse(XDI3Util.endsWith(xri2, XDI3Segment.create("*y*c*d"), true, false));

		assertEquals(XDI3Util.reduceXri(xri1, null), XDI3Segment.create("=a*b*c*d"));
		assertEquals(XDI3Util.reduceXri(xri1, XDI3Segment.create("()")), XDI3Segment.create("=a*b*c*d"));
		assertEquals(XDI3Util.reduceXri(xri1, XDI3Segment.create("=a")), XDI3Segment.create("*b*c*d"));
		assertEquals(XDI3Util.reduceXri(xri1, XDI3Segment.create("=a*b")), XDI3Segment.create("*c*d"));
		assertEquals(XDI3Util.reduceXri(xri1, XDI3Segment.create("=a*b*c")), XDI3Segment.create("*d"));
		assertNull(XDI3Util.reduceXri(xri1, XDI3Segment.create("=a*b*c*d")));
	}

	public void testExpand() throws Exception {

		assertEquals(XDI3Util.expandXri(XDI3Segment.create("+b"), XDI3Segment.create("+a")), XDI3Segment.create("+a+b"));
		assertEquals(XDI3Util.expandXri(XDI3Segment.create("+c"), XDI3Segment.create("+a+b")), XDI3Segment.create("+a+b+c"));
		assertEquals(XDI3Util.expandXri(XDI3Segment.create("+b+c"), XDI3Segment.create("+a")), XDI3Segment.create("+a+b+c"));

		assertEquals(XDI3Util.expandXri(XDI3Segment.create("+a"), XDI3Segment.create("()")), XDI3Segment.create("+a"));
		assertEquals(XDI3Util.expandXri(XDI3Segment.create("+a"), null), XDI3Segment.create("+a"));

		assertEquals(XDI3Util.expandXri(XDI3Segment.create("()"), XDI3Segment.create("+a")), XDI3Segment.create("+a"));
		assertEquals(XDI3Util.expandXri(null, XDI3Segment.create("+a")), XDI3Segment.create("+a"));

		assertEquals(XDI3Util.expandXri(XDI3Segment.create("()"), XDI3Segment.create("()")), XDI3Segment.create("()"));
		assertEquals(XDI3Util.expandXri(null, XDI3Segment.create("()")), XDI3Segment.create("()"));
		assertEquals(XDI3Util.expandXri(XDI3Segment.create("()"), null), XDI3Segment.create("()"));
		assertEquals(XDI3Util.expandXri(null, null), XDI3Segment.create("()"));
	}

	public void testReduceVariables() throws Exception {

		XDI3Segment xri1 = XDI3Segment.create("=a*b+c!d@e$f*g");

		assertEquals(XDI3Util.reduceXri(xri1, XDI3Segment.create("{}"), false, true), XDI3Segment.create("*b+c!d@e$f*g"));
		assertEquals(XDI3Util.reduceXri(xri1, XDI3Segment.create("{=}"), false, true), XDI3Segment.create("*b+c!d@e$f*g"));
		assertEquals(XDI3Util.reduceXri(xri1, XDI3Segment.create("{}{}"), false, true), XDI3Segment.create("+c!d@e$f*g"));
		assertEquals(XDI3Util.reduceXri(xri1, XDI3Segment.create("{{=*}}"), false, true), XDI3Segment.create("+c!d@e$f*g"));
		assertEquals(XDI3Util.reduceXri(xri1, XDI3Segment.create("{}{*}"), false, true), XDI3Segment.create("+c!d@e$f*g"));
		assertEquals(XDI3Util.reduceXri(xri1, XDI3Segment.create("{{*=}}{{!+}}"), false, true), XDI3Segment.create("@e$f*g"));
		assertEquals(XDI3Util.reduceXri(xri1, XDI3Segment.create("{{*=}}{}{!}"), false, true), XDI3Segment.create("@e$f*g"));
		assertEquals(XDI3Util.reduceXri(xri1, XDI3Segment.create("{{*=}}{}{!}{}"), false, true), XDI3Segment.create("$f*g"));
		assertEquals(XDI3Util.reduceXri(xri1, XDI3Segment.create("{{*=}}{}{{!}}{@}"), false, true), XDI3Segment.create("$f*g"));
		assertNull(XDI3Util.reduceXri(xri1, XDI3Segment.create("{{*=}}{}{!}{@}{*}"), false, true));
		assertNull(XDI3Util.reduceXri(xri1, XDI3Segment.create("{{=+@$*!}}"), false, true));
	}
}
