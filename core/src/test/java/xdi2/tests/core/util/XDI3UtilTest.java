package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;

public class XDI3UtilTest extends TestCase {

	public void testParentXri() throws Exception {

		XDI3Segment xri = XDI3Segment.create("=a*b*c*d");

		assertEquals(XDI3Util.parentXri(xri, 0), XDI3Segment.create("=a*b*c*d"));
		assertEquals(XDI3Util.parentXri(xri, 1), XDI3Segment.create("=a"));
		assertEquals(XDI3Util.parentXri(xri, -1), XDI3Segment.create("=a*b*c"));
		assertEquals(XDI3Util.parentXri(xri, 2), XDI3Segment.create("=a*b"));
		assertEquals(XDI3Util.parentXri(xri, -2), XDI3Segment.create("=a*b"));
		assertEquals(XDI3Util.parentXri(xri, 3), XDI3Segment.create("=a*b*c"));
		assertEquals(XDI3Util.parentXri(xri, -3), XDI3Segment.create("=a"));
		assertEquals(XDI3Util.parentXri(xri, 4), XDI3Segment.create("=a*b*c*d"));
		assertNull(XDI3Util.parentXri(xri, -4));

		assertEquals(XDI3Util.parentXri(xri, -1), XDI3Segment.create("=a*b*c"));
		assertEquals(XDI3Util.parentXri(XDI3Util.parentXri(xri, -1), -1), XDI3Segment.create("=a*b"));
		assertEquals(XDI3Util.parentXri(XDI3Util.parentXri(XDI3Util.parentXri(xri, -1), -1), -1), XDI3Segment.create("=a"));
		assertNull(XDI3Util.parentXri(XDI3Util.parentXri(XDI3Util.parentXri(XDI3Util.parentXri(xri, -1), -1), -1), -1));
	}

	public void testLocalXri() throws Exception {

		XDI3Segment xri = XDI3Segment.create("=a*b*c*d");

		assertEquals(XDI3Util.localXri(xri, 0), XDI3Segment.create("=a*b*c*d"));
		assertEquals(XDI3Util.localXri(xri, 1), XDI3Segment.create("*d"));
		assertEquals(XDI3Util.localXri(xri, -1), XDI3Segment.create("*b*c*d"));
		assertEquals(XDI3Util.localXri(xri, 2), XDI3Segment.create("*c*d"));
		assertEquals(XDI3Util.localXri(xri, -2), XDI3Segment.create("*c*d"));
		assertEquals(XDI3Util.localXri(xri, 3), XDI3Segment.create("*b*c*d"));
		assertEquals(XDI3Util.localXri(xri, -3), XDI3Segment.create("*d"));
		assertEquals(XDI3Util.localXri(xri, 4), XDI3Segment.create("=a*b*c*d"));
		assertNull(XDI3Util.localXri(xri, -4));

		assertEquals(XDI3Util.localXri(xri, -1), XDI3Segment.create("*b*c*d"));
		assertEquals(XDI3Util.localXri(XDI3Util.localXri(xri, -1), -1), XDI3Segment.create("*c*d"));
		assertEquals(XDI3Util.localXri(XDI3Util.localXri(XDI3Util.localXri(xri, -1), -1), -1), XDI3Segment.create("*d"));
		assertNull(XDI3Util.localXri(XDI3Util.localXri(XDI3Util.localXri(XDI3Util.localXri(xri, -1), -1), -1), -1));
	}

	public void testStartsWith() throws Exception {

		XDI3Segment xri1 = XDI3Segment.create("=a*b*c*d");
		XDI3Segment xri2 = XDI3Segment.create("{}*b{}*d");

		assertEquals(XDI3Util.startsWith(xri1, XDI3Segment.create("=a")), XDI3Segment.create("=a"));
		assertEquals(XDI3Util.startsWith(xri1, XDI3Segment.create("=a*b")), XDI3Segment.create("=a*b"));
		assertEquals(XDI3Util.startsWith(xri1, XDI3Segment.create("=a*b*c")), XDI3Segment.create("=a*b*c"));
		assertEquals(XDI3Util.startsWith(xri1, XDI3Segment.create("=a*b*c*d")), XDI3Segment.create("=a*b*c*d"));
		assertNull(XDI3Util.startsWith(xri1, XDI3Segment.create("=x*b")));
		assertNull(XDI3Util.startsWith(xri1, XDI3Segment.create("=a*x*c")));

		assertEquals(XDI3Util.startsWith(xri1, XDI3Segment.create("{}"), false, true), XDI3Segment.create("=a"));
		assertEquals(XDI3Util.startsWith(xri1, XDI3Segment.create("=a{}"), false, true), XDI3Segment.create("=a*b"));
		assertEquals(XDI3Util.startsWith(xri1, XDI3Segment.create("{}{}*c"), false, true), XDI3Segment.create("=a*b*c"));
		assertEquals(XDI3Util.startsWith(xri1, XDI3Segment.create("{}*b*c*d"), false, true), XDI3Segment.create("=a*b*c*d"));
		assertNull(XDI3Util.startsWith(xri1, XDI3Segment.create("=x*b"), false, true));
		assertNull(XDI3Util.startsWith(xri1, XDI3Segment.create("=a*x*c"), false, true));

		assertNull(XDI3Util.startsWith(xri1, XDI3Segment.create("{}"), false, false));
		assertNull(XDI3Util.startsWith(xri1, XDI3Segment.create("=a{}"), false, false));
		assertNull(XDI3Util.startsWith(xri1, XDI3Segment.create("{}{}*c"), false, false));
		assertNull(XDI3Util.startsWith(xri1, XDI3Segment.create("{}*b*c*d"), false, false));
		assertNull(XDI3Util.startsWith(xri1, XDI3Segment.create("=x*b"), false, false));
		assertNull(XDI3Util.startsWith(xri1, XDI3Segment.create("=a*x*c"), false, false));

		assertEquals(XDI3Util.startsWith(xri2, XDI3Segment.create("=a"), true, false), XDI3Segment.create("{}"));
		assertEquals(XDI3Util.startsWith(xri2, XDI3Segment.create("=a*b"), true, false), XDI3Segment.create("{}*b"));
		assertEquals(XDI3Util.startsWith(xri2, XDI3Segment.create("=a*b*c"), true, false), XDI3Segment.create("{}*b{}"));
		assertEquals(XDI3Util.startsWith(xri2, XDI3Segment.create("=a*b*c*d"), true, false), XDI3Segment.create("{}*b{}*d"));
		assertEquals(XDI3Util.startsWith(xri2, XDI3Segment.create("=x*b"), true, false), XDI3Segment.create("{}*b"));
		assertNull(XDI3Util.startsWith(xri2, XDI3Segment.create("=a*x*c"), true, false));

		assertEquals(XDI3Util.startsWith(XDI3Segment.create("=xxx"), XDI3Segment.create("()")), XDI3Segment.create("()"));
		assertNull(XDI3Util.startsWith(XDI3Segment.create("()"), XDI3Segment.create("=xxx")));
	}

	public void testEndsWith() throws Exception {

		XDI3Segment xri1 = XDI3Segment.create("=a*b*c*d");
		XDI3Segment xri2 = XDI3Segment.create("{}*b{}*d");

		assertEquals(XDI3Util.endsWith(xri1, XDI3Segment.create("*d")), XDI3Segment.create("*d"));
		assertEquals(XDI3Util.endsWith(xri1, XDI3Segment.create("*c*d")), XDI3Segment.create("*c*d"));
		assertEquals(XDI3Util.endsWith(xri1, XDI3Segment.create("*b*c*d")), XDI3Segment.create("*b*c*d"));
		assertEquals(XDI3Util.endsWith(xri1, XDI3Segment.create("=a*b*c*d")), XDI3Segment.create("=a*b*c*d"));
		assertNull(XDI3Util.endsWith(xri1, XDI3Segment.create("*y*d")));
		assertNull(XDI3Util.endsWith(xri1, XDI3Segment.create("*b*y*d")));

		assertEquals(XDI3Util.endsWith(xri1, XDI3Segment.create("{}"), false, true), XDI3Segment.create("*d"));
		assertEquals(XDI3Util.endsWith(xri1, XDI3Segment.create("{}*d"), false, true), XDI3Segment.create("*c*d"));
		assertEquals(XDI3Util.endsWith(xri1, XDI3Segment.create("*b{}{}"), false, true), XDI3Segment.create("*b*c*d"));
		assertEquals(XDI3Util.endsWith(xri1, XDI3Segment.create("=a*b*c{}"), false, true), XDI3Segment.create("=a*b*c*d"));
		assertNull(XDI3Util.endsWith(xri1, XDI3Segment.create("*y*d"), false, true));
		assertNull(XDI3Util.endsWith(xri1, XDI3Segment.create("*b*y*d"), false, true));

		assertNull(XDI3Util.endsWith(xri1, XDI3Segment.create("{}"), false, false));
		assertNull(XDI3Util.endsWith(xri1, XDI3Segment.create("{}*d"), false, false));
		assertNull(XDI3Util.endsWith(xri1, XDI3Segment.create("*b{}{}"), false, false));
		assertNull(XDI3Util.endsWith(xri1, XDI3Segment.create("=a*b*c{}"), false, false));
		assertNull(XDI3Util.endsWith(xri1, XDI3Segment.create("*y*d"), false, false));
		assertNull(XDI3Util.endsWith(xri1, XDI3Segment.create("*b*y*d"), false, false));

		assertEquals(XDI3Util.endsWith(xri2, XDI3Segment.create("*d"), true, false), XDI3Segment.create("*d"));
		assertEquals(XDI3Util.endsWith(xri2, XDI3Segment.create("*c*d"), true, false), XDI3Segment.create("{}*d"));
		assertEquals(XDI3Util.endsWith(xri2, XDI3Segment.create("*b*c*d"), true, false), XDI3Segment.create("*b{}*d"));
		assertEquals(XDI3Util.endsWith(xri2, XDI3Segment.create("=a*b*c*d"), true, false), XDI3Segment.create("{}*b{}*d"));
		assertEquals(XDI3Util.endsWith(xri2, XDI3Segment.create("*y*d"), true, false), XDI3Segment.create("{}*d"));
		assertNull(XDI3Util.endsWith(xri2, XDI3Segment.create("*y*c*d"), true, false));

		assertEquals(XDI3Util.endsWith(XDI3Segment.create("=xxx"), XDI3Segment.create("()")), XDI3Segment.create("()"));
		assertNull(XDI3Util.endsWith(XDI3Segment.create("()"), XDI3Segment.create("=xxx")));
	}

	public void testRemoveStartXri() throws Exception {

		XDI3Segment xri1 = XDI3Segment.create("=a*b*c*d");

		assertEquals(XDI3Util.removeStartXri(xri1, XDI3Segment.create("()")), XDI3Segment.create("=a*b*c*d"));
		assertEquals(XDI3Util.removeStartXri(xri1, XDI3Segment.create("=a")), XDI3Segment.create("*b*c*d"));
		assertEquals(XDI3Util.removeStartXri(xri1, XDI3Segment.create("=a*b")), XDI3Segment.create("*c*d"));
		assertEquals(XDI3Util.removeStartXri(xri1, XDI3Segment.create("=a*b*c")), XDI3Segment.create("*d"));
		assertEquals(XDI3Util.removeStartXri(xri1, XDI3Segment.create("=a*b*c*d")), XDI3Segment.create("()"));
		assertNull(XDI3Util.removeStartXri(xri1, XDI3Segment.create("=x")));
	}

	public void testRemoveEndXri() throws Exception {

		XDI3Segment xri1 = XDI3Segment.create("=a*b*c*d");

		assertEquals(XDI3Util.removeEndXri(xri1, XDI3Segment.create("()")), XDI3Segment.create("=a*b*c*d"));
		assertEquals(XDI3Util.removeEndXri(xri1, XDI3Segment.create("*d")), XDI3Segment.create("=a*b*c"));
		assertEquals(XDI3Util.removeEndXri(xri1, XDI3Segment.create("*c*d")), XDI3Segment.create("=a*b"));
		assertEquals(XDI3Util.removeEndXri(xri1, XDI3Segment.create("*b*c*d")), XDI3Segment.create("=a"));
		assertEquals(XDI3Util.removeEndXri(xri1, XDI3Segment.create("=a*b*c*d")), XDI3Segment.create("()"));
		assertNull(XDI3Util.removeEndXri(xri1, XDI3Segment.create("*y")));
	}

	public void testConcatXris() throws Exception {

		assertEquals(XDI3Util.concatXris(XDI3Segment.create("+a"), XDI3Segment.create("+b")), XDI3Segment.create("+a+b"));
		assertEquals(XDI3Util.concatXris(XDI3Segment.create("+a+b"), XDI3Segment.create("+c")), XDI3Segment.create("+a+b+c"));
		assertEquals(XDI3Util.concatXris(XDI3Segment.create("+a"), XDI3Segment.create("+b+c")), XDI3Segment.create("+a+b+c"));

		assertEquals(XDI3Util.concatXris(XDI3Segment.create("+a"), XDI3Segment.create("()")), XDI3Segment.create("+a"));
		assertEquals(XDI3Util.concatXris(XDI3Segment.create("+a"), (XDI3Segment) null), XDI3Segment.create("+a"));

		assertEquals(XDI3Util.concatXris(XDI3Segment.create("()"), XDI3Segment.create("+a")), XDI3Segment.create("+a"));
		assertEquals(XDI3Util.concatXris((XDI3Segment) null, XDI3Segment.create("+a")), XDI3Segment.create("+a"));

		assertEquals(XDI3Util.concatXris(XDI3Segment.create("()"), XDI3Segment.create("()")), XDI3Segment.create("()"));
		assertEquals(XDI3Util.concatXris((XDI3Segment) null, XDI3Segment.create("()")), XDI3Segment.create("()"));
		assertEquals(XDI3Util.concatXris(XDI3Segment.create("()"), (XDI3Segment) null), XDI3Segment.create("()"));
		assertEquals(XDI3Util.concatXris((XDI3Segment) null, (XDI3Segment) null), XDI3Segment.create("()"));

		XDI3Segment[] xris = new XDI3Segment[] {
				XDI3Segment.create("()"),
				XDI3Segment.create("=a+b"),
				XDI3Segment.create("+c"),
				XDI3Segment.create("()"),
				XDI3Segment.create("+d+e")
		};

		assertEquals(XDI3Util.concatXris(xris), XDI3Segment.create("=a+b+c+d+e"));
	}

	public void testRemoveStartXriVariables() throws Exception {

		XDI3Segment xri = XDI3Segment.create("=a*b+c!d@e$f*g");

		assertEquals(XDI3Util.removeStartXri(xri, XDI3Segment.create("{}"), false, true), XDI3Segment.create("*b+c!d@e$f*g"));
		assertEquals(XDI3Util.removeStartXri(xri, XDI3Segment.create("{=}"), false, true), XDI3Segment.create("*b+c!d@e$f*g"));
		assertEquals(XDI3Util.removeStartXri(xri, XDI3Segment.create("{}{}"), false, true), XDI3Segment.create("+c!d@e$f*g"));
		assertEquals(XDI3Util.removeStartXri(xri, XDI3Segment.create("{{=*}}"), false, true), XDI3Segment.create("+c!d@e$f*g"));
		assertEquals(XDI3Util.removeStartXri(xri, XDI3Segment.create("{}{*}"), false, true), XDI3Segment.create("+c!d@e$f*g"));
		assertEquals(XDI3Util.removeStartXri(xri, XDI3Segment.create("{{*=}}{{!+}}"), false, true), XDI3Segment.create("@e$f*g"));
		assertEquals(XDI3Util.removeStartXri(xri, XDI3Segment.create("{{*=}}{}{!}"), false, true), XDI3Segment.create("@e$f*g"));
		assertEquals(XDI3Util.removeStartXri(xri, XDI3Segment.create("{{*=}}{}{!}{}"), false, true), XDI3Segment.create("$f*g"));
		assertEquals(XDI3Util.removeStartXri(xri, XDI3Segment.create("{{*=}}{}{{!}}{@}"), false, true), XDI3Segment.create("$f*g"));
		assertEquals(XDI3Util.removeStartXri(xri, XDI3Segment.create("{{*=}}{}{!}{@}{$}{*}"), false, true), XDI3Segment.create("()"));
		assertEquals(XDI3Util.removeStartXri(xri, XDI3Segment.create("{{=+@$*!}}"), false, true), XDI3Segment.create("()"));
		assertNull(XDI3Util.removeStartXri(xri, XDI3Segment.create("{@}"), false, true));
	}

	public void testRemoveEndXriVariables() throws Exception {

		XDI3Segment xri = XDI3Segment.create("=a*b+c!d@e$f*g");

		assertEquals(XDI3Util.removeEndXri(xri, XDI3Segment.create("{}"), false, true), XDI3Segment.create("=a*b+c!d@e$f"));
		assertEquals(XDI3Util.removeEndXri(xri, XDI3Segment.create("{*}"), false, true), XDI3Segment.create("=a*b+c!d@e$f"));
		assertEquals(XDI3Util.removeEndXri(xri, XDI3Segment.create("{}{}"), false, true), XDI3Segment.create("=a*b+c!d@e"));
		assertEquals(XDI3Util.removeEndXri(xri, XDI3Segment.create("{{$*}}"), false, true), XDI3Segment.create("=a*b+c!d@e"));
		assertEquals(XDI3Util.removeEndXri(xri, XDI3Segment.create("{$}{}"), false, true), XDI3Segment.create("=a*b+c!d@e"));
		assertEquals(XDI3Util.removeEndXri(xri, XDI3Segment.create("{{!@}}{{$*}}"), false, true), XDI3Segment.create("=a*b+c"));
		assertEquals(XDI3Util.removeEndXri(xri, XDI3Segment.create("{!}{}{{$*}}"), false, true), XDI3Segment.create("=a*b+c"));
		assertEquals(XDI3Util.removeEndXri(xri, XDI3Segment.create("{}{!}{}{{$*}}"), false, true), XDI3Segment.create("=a*b"));
		assertEquals(XDI3Util.removeEndXri(xri, XDI3Segment.create("{+}{{!}}{}{{$*}}"), false, true), XDI3Segment.create("=a*b"));
		assertEquals(XDI3Util.removeEndXri(xri, XDI3Segment.create("{=}{*}{+}{!}{}{{$*}}"), false, true), XDI3Segment.create("()"));
		assertEquals(XDI3Util.removeEndXri(xri, XDI3Segment.create("{{=+@$*!}}"), false, true), XDI3Segment.create("()"));
		assertNull(XDI3Util.removeEndXri(xri, XDI3Segment.create("{!}"), false, true));
	}

	public void testIsCloudNumber() throws Exception {

		assertFalse(XDI3Util.isCloudNumber(XDI3Segment.create("=markus")));
		assertFalse(XDI3Util.isCloudNumber(XDI3Segment.create("=markus*web")));
		assertFalse(XDI3Util.isCloudNumber(XDI3Segment.create("[=]")));
		assertTrue(XDI3Util.isCloudNumber(XDI3Segment.create("[=]!1111")));
		assertTrue(XDI3Util.isCloudNumber(XDI3Segment.create("[=]!1111!2222")));
		assertTrue(XDI3Util.isCloudNumber(XDI3Segment.create("[@]!1111")));
		assertTrue(XDI3Util.isCloudNumber(XDI3Segment.create("[@]!1111!2222")));
	}
}
