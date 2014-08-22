package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.AddressUtil;

public class XDI3UtilTest extends TestCase {

	public void testParentXri() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b*c*d");

		assertEquals(AddressUtil.parentXri(xri, 0), XDIAddress.create("=a*b*c*d"));
		assertEquals(AddressUtil.parentXri(xri, 1), XDIAddress.create("=a"));
		assertEquals(AddressUtil.parentXri(xri, -1), XDIAddress.create("=a*b*c"));
		assertEquals(AddressUtil.parentXri(xri, 2), XDIAddress.create("=a*b"));
		assertEquals(AddressUtil.parentXri(xri, -2), XDIAddress.create("=a*b"));
		assertEquals(AddressUtil.parentXri(xri, 3), XDIAddress.create("=a*b*c"));
		assertEquals(AddressUtil.parentXri(xri, -3), XDIAddress.create("=a"));
		assertEquals(AddressUtil.parentXri(xri, 4), XDIAddress.create("=a*b*c*d"));
		assertNull(AddressUtil.parentXri(xri, -4));

		assertEquals(AddressUtil.parentXri(xri, -1), XDIAddress.create("=a*b*c"));
		assertEquals(AddressUtil.parentXri(AddressUtil.parentXri(xri, -1), -1), XDIAddress.create("=a*b"));
		assertEquals(AddressUtil.parentXri(AddressUtil.parentXri(AddressUtil.parentXri(xri, -1), -1), -1), XDIAddress.create("=a"));
		assertNull(AddressUtil.parentXri(AddressUtil.parentXri(AddressUtil.parentXri(AddressUtil.parentXri(xri, -1), -1), -1), -1));
	}

	public void testLocalXri() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b*c*d");

		assertEquals(AddressUtil.localXri(xri, 0), XDIAddress.create("=a*b*c*d"));
		assertEquals(AddressUtil.localXri(xri, 1), XDIAddress.create("*d"));
		assertEquals(AddressUtil.localXri(xri, -1), XDIAddress.create("*b*c*d"));
		assertEquals(AddressUtil.localXri(xri, 2), XDIAddress.create("*c*d"));
		assertEquals(AddressUtil.localXri(xri, -2), XDIAddress.create("*c*d"));
		assertEquals(AddressUtil.localXri(xri, 3), XDIAddress.create("*b*c*d"));
		assertEquals(AddressUtil.localXri(xri, -3), XDIAddress.create("*d"));
		assertEquals(AddressUtil.localXri(xri, 4), XDIAddress.create("=a*b*c*d"));
		assertNull(AddressUtil.localXri(xri, -4));

		assertEquals(AddressUtil.localXri(xri, -1), XDIAddress.create("*b*c*d"));
		assertEquals(AddressUtil.localXri(AddressUtil.localXri(xri, -1), -1), XDIAddress.create("*c*d"));
		assertEquals(AddressUtil.localXri(AddressUtil.localXri(AddressUtil.localXri(xri, -1), -1), -1), XDIAddress.create("*d"));
		assertNull(AddressUtil.localXri(AddressUtil.localXri(AddressUtil.localXri(AddressUtil.localXri(xri, -1), -1), -1), -1));
	}

	public void testStartsWith() throws Exception {

		XDIAddress xri1 = XDIAddress.create("=a*b*c*d");
		XDIAddress xri2 = XDIAddress.create("{}*b{}*d");

		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("=a")), XDIAddress.create("=a"));
		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("=a*b")), XDIAddress.create("=a*b"));
		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("=a*b*c")), XDIAddress.create("=a*b*c"));
		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("=a*b*c*d")), XDIAddress.create("=a*b*c*d"));
		assertNull(AddressUtil.startsWith(xri1, XDIAddress.create("=x*b")));
		assertNull(AddressUtil.startsWith(xri1, XDIAddress.create("=a*x*c")));

		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("{}"), false, true), XDIAddress.create("=a"));
		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("=a{}"), false, true), XDIAddress.create("=a*b"));
		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("{}{}*c"), false, true), XDIAddress.create("=a*b*c"));
		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("{}*b*c*d"), false, true), XDIAddress.create("=a*b*c*d"));
		assertNull(AddressUtil.startsWith(xri1, XDIAddress.create("=x*b"), false, true));
		assertNull(AddressUtil.startsWith(xri1, XDIAddress.create("=a*x*c"), false, true));

		assertNull(AddressUtil.startsWith(xri1, XDIAddress.create("{}"), false, false));
		assertNull(AddressUtil.startsWith(xri1, XDIAddress.create("=a{}"), false, false));
		assertNull(AddressUtil.startsWith(xri1, XDIAddress.create("{}{}*c"), false, false));
		assertNull(AddressUtil.startsWith(xri1, XDIAddress.create("{}*b*c*d"), false, false));
		assertNull(AddressUtil.startsWith(xri1, XDIAddress.create("=x*b"), false, false));
		assertNull(AddressUtil.startsWith(xri1, XDIAddress.create("=a*x*c"), false, false));

		assertEquals(AddressUtil.startsWith(xri2, XDIAddress.create("=a"), true, false), XDIAddress.create("{}"));
		assertEquals(AddressUtil.startsWith(xri2, XDIAddress.create("=a*b"), true, false), XDIAddress.create("{}*b"));
		assertEquals(AddressUtil.startsWith(xri2, XDIAddress.create("=a*b*c"), true, false), XDIAddress.create("{}*b{}"));
		assertEquals(AddressUtil.startsWith(xri2, XDIAddress.create("=a*b*c*d"), true, false), XDIAddress.create("{}*b{}*d"));
		assertEquals(AddressUtil.startsWith(xri2, XDIAddress.create("=x*b"), true, false), XDIAddress.create("{}*b"));
		assertNull(AddressUtil.startsWith(xri2, XDIAddress.create("=a*x*c"), true, false));

		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("{{=*}}*b*c*d"), false, true), xri1);
		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("{{=*}}*c*d"), false, true), xri1);
		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("{{=*}}*d"), false, true), xri1);
		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("{{=*}}"), false, true), xri1);
		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("{{=}}{{*}}*c*d"), false, true), xri1);
		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("{{=}}{{*}}*d"), false, true), xri1);
		assertEquals(AddressUtil.startsWith(xri1, XDIAddress.create("{{=}}{{*}}"), false, true), xri1);

		assertEquals(AddressUtil.startsWith(XDIAddress.create("=xxx"), XDIAddress.create("")), XDIAddress.create(""));
		assertNull(AddressUtil.startsWith(XDIAddress.create(""), XDIAddress.create("=xxx")));
	}

	public void testEndsWith() throws Exception {

		XDIAddress xri1 = XDIAddress.create("=a*b*c*d");
		XDIAddress xri2 = XDIAddress.create("{}*b{}*d");

		assertEquals(AddressUtil.endsWith(xri1, XDIAddress.create("*d")), XDIAddress.create("*d"));
		assertEquals(AddressUtil.endsWith(xri1, XDIAddress.create("*c*d")), XDIAddress.create("*c*d"));
		assertEquals(AddressUtil.endsWith(xri1, XDIAddress.create("*b*c*d")), XDIAddress.create("*b*c*d"));
		assertEquals(AddressUtil.endsWith(xri1, XDIAddress.create("=a*b*c*d")), XDIAddress.create("=a*b*c*d"));
		assertNull(AddressUtil.endsWith(xri1, XDIAddress.create("*y*d")));
		assertNull(AddressUtil.endsWith(xri1, XDIAddress.create("*b*y*d")));

		assertEquals(AddressUtil.endsWith(xri1, XDIAddress.create("{}"), false, true), XDIAddress.create("*d"));
		assertEquals(AddressUtil.endsWith(xri1, XDIAddress.create("{}*d"), false, true), XDIAddress.create("*c*d"));
		assertEquals(AddressUtil.endsWith(xri1, XDIAddress.create("*b{}{}"), false, true), XDIAddress.create("*b*c*d"));
		assertEquals(AddressUtil.endsWith(xri1, XDIAddress.create("=a*b*c{}"), false, true), XDIAddress.create("=a*b*c*d"));
		assertNull(AddressUtil.endsWith(xri1, XDIAddress.create("*y*d"), false, true));
		assertNull(AddressUtil.endsWith(xri1, XDIAddress.create("*b*y*d"), false, true));

		assertNull(AddressUtil.endsWith(xri1, XDIAddress.create("{}"), false, false));
		assertNull(AddressUtil.endsWith(xri1, XDIAddress.create("{}*d"), false, false));
		assertNull(AddressUtil.endsWith(xri1, XDIAddress.create("*b{}{}"), false, false));
		assertNull(AddressUtil.endsWith(xri1, XDIAddress.create("=a*b*c{}"), false, false));
		assertNull(AddressUtil.endsWith(xri1, XDIAddress.create("*y*d"), false, false));
		assertNull(AddressUtil.endsWith(xri1, XDIAddress.create("*b*y*d"), false, false));

		assertEquals(AddressUtil.endsWith(xri2, XDIAddress.create("*d"), true, false), XDIAddress.create("*d"));
		assertEquals(AddressUtil.endsWith(xri2, XDIAddress.create("*c*d"), true, false), XDIAddress.create("{}*d"));
		assertEquals(AddressUtil.endsWith(xri2, XDIAddress.create("*b*c*d"), true, false), XDIAddress.create("*b{}*d"));
		assertEquals(AddressUtil.endsWith(xri2, XDIAddress.create("=a*b*c*d"), true, false), XDIAddress.create("{}*b{}*d"));
		assertEquals(AddressUtil.endsWith(xri2, XDIAddress.create("*y*d"), true, false), XDIAddress.create("{}*d"));
		assertNull(AddressUtil.endsWith(xri2, XDIAddress.create("*y*c*d"), true, false));

		assertEquals(AddressUtil.endsWith(xri1, XDIAddress.create("=a*b*c{{=*}}"), false, true), xri1);
		assertEquals(AddressUtil.endsWith(xri1, XDIAddress.create("=a*b{{=*}}"), false, true), xri1);
		assertEquals(AddressUtil.endsWith(xri1, XDIAddress.create("=a{{=*}}"), false, true), xri1);
		assertEquals(AddressUtil.endsWith(xri1, XDIAddress.create("{{=*}}"), false, true), xri1);

		assertEquals(AddressUtil.endsWith(XDIAddress.create("=xxx"), XDIAddress.create("")), XDIAddress.create(""));
		assertNull(AddressUtil.endsWith(XDIAddress.create(""), XDIAddress.create("=xxx")));
	}

	public void testStartXri() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b*c*d");

		assertEquals(AddressUtil.indexOfXri(xri, XDIArc.create("*b")), 1);
		assertEquals(AddressUtil.indexOfXri(xri, XDIArc.create("*c")), 2);
		assertEquals(AddressUtil.indexOfXri(xri, XDIArc.create("*x")), -1);
	}

	public void testEndXri() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b*c*d");

		assertEquals(AddressUtil.lastIndexOfXri(xri, XDIArc.create("*b")), 1);
		assertEquals(AddressUtil.lastIndexOfXri(xri, XDIArc.create("*c")), 2);
		assertEquals(AddressUtil.lastIndexOfXri(xri, XDIArc.create("*x")), -1);
	}

	public void testSubXri() throws Exception {

		XDIAddress xri1 = XDIAddress.create("=bob#x=alice#y+registration#z");
		int index1_1 = AddressUtil.indexOfXri(xri1, XDIArc.create("#x"));
		int index1_2 = AddressUtil.indexOfXri(xri1, XDIArc.create("#y"));

		assertEquals(index1_1, 1);
		assertEquals(index1_2, 3);
		assertEquals(AddressUtil.subXri(xri1, 0, index1_1), XDIAddress.create("=bob"));
		assertEquals(AddressUtil.subXri(xri1, index1_1 + 1, index1_2), XDIAddress.create("=alice"));

		XDIAddress xri2 = XDIAddress.create("[=]!1111#x[=]!2222#y+registration#z");
		int index2_1 = AddressUtil.indexOfXri(xri2, XDIArc.create("#x"));
		int index2_2 = AddressUtil.indexOfXri(xri2, XDIArc.create("#y"));

		assertEquals(index2_1, 2);
		assertEquals(index2_2, 5);
		assertEquals(AddressUtil.subXri(xri2, 0, index2_1), XDIAddress.create("[=]!1111"));
		assertEquals(AddressUtil.subXri(xri2, index2_1 + 1, index2_2), XDIAddress.create("[=]!2222"));
	}

	public void testRemoveStartXri() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b*c*d");

		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("")), XDIAddress.create("=a*b*c*d"));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("=a")), XDIAddress.create("*b*c*d"));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("=a*b")), XDIAddress.create("*c*d"));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("=a*b*c")), XDIAddress.create("*d"));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("=a*b*c*d")), XDIAddress.create(""));
		assertNull(AddressUtil.removeStartXri(xri, XDIAddress.create("=x")));
	}

	public void testRemoveEndXri() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b*c*d");

		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("")), XDIAddress.create("=a*b*c*d"));
		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("*d")), XDIAddress.create("=a*b*c"));
		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("*c*d")), XDIAddress.create("=a*b"));
		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("*b*c*d")), XDIAddress.create("=a"));
		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("=a*b*c*d")), XDIAddress.create(""));
		assertNull(AddressUtil.removeEndXri(xri, XDIAddress.create("*y")));
	}

	public void testConcatXris() throws Exception {

		assertEquals(AddressUtil.concatAddresses(XDIAddress.create("+a"), XDIAddress.create("+b")), XDIAddress.create("+a+b"));
		assertEquals(AddressUtil.concatAddresses(XDIAddress.create("+a+b"), XDIAddress.create("+c")), XDIAddress.create("+a+b+c"));
		assertEquals(AddressUtil.concatAddresses(XDIAddress.create("+a"), XDIAddress.create("+b+c")), XDIAddress.create("+a+b+c"));

		assertEquals(AddressUtil.concatAddresses(XDIAddress.create("+a"), XDIAddress.create("")), XDIAddress.create("+a"));
		assertEquals(AddressUtil.concatAddresses(XDIAddress.create("+a"), (XDIAddress) null), XDIAddress.create("+a"));

		assertEquals(AddressUtil.concatAddresses(XDIAddress.create(""), XDIAddress.create("+a")), XDIAddress.create("+a"));
		assertEquals(AddressUtil.concatAddresses((XDIAddress) null, XDIAddress.create("+a")), XDIAddress.create("+a"));

		assertEquals(AddressUtil.concatAddresses(XDIAddress.create(""), XDIAddress.create("")), XDIAddress.create(""));
		assertEquals(AddressUtil.concatAddresses((XDIAddress) null, XDIAddress.create("")), XDIAddress.create(""));
		assertEquals(AddressUtil.concatAddresses(XDIAddress.create(""), (XDIAddress) null), XDIAddress.create(""));
		assertEquals(AddressUtil.concatAddresses((XDIAddress) null, (XDIAddress) null), XDIAddress.create(""));

		XDIAddress[] xris = new XDIAddress[] {
				XDIAddress.create(""),
				XDIAddress.create("=a+b"),
				XDIAddress.create("+c"),
				XDIAddress.create(""),
				XDIAddress.create("+d+e")
		};

		assertEquals(AddressUtil.concatAddresses(xris), XDIAddress.create("=a+b+c+d+e"));
	}

	public void testRemoveStartXriVariables() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b+c!d#e$f*g");

		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("{}"), false, true), XDIAddress.create("*b+c!d#e$f*g"));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("{=}"), false, true), XDIAddress.create("*b+c!d#e$f*g"));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("{}{}"), false, true), XDIAddress.create("+c!d#e$f*g"));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("{{=*}}"), false, true), XDIAddress.create("+c!d#e$f*g"));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("{}{*}"), false, true), XDIAddress.create("+c!d#e$f*g"));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("{{*=}}{{!+}}"), false, true), XDIAddress.create("#e$f*g"));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("{{*=}}{}{!}"), false, true), XDIAddress.create("#e$f*g"));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("{{*=}}{}{!}{}"), false, true), XDIAddress.create("$f*g"));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("{{*=}}{}{{!}}{#}"), false, true), XDIAddress.create("$f*g"));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("{{*=}}{}{!}{#}{$}{*}"), false, true), XDIAddress.create(""));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("{{=+*#$!}}"), false, true), XDIAddress.create(""));
		assertNull(AddressUtil.removeStartXri(xri, XDIAddress.create("{#}"), false, true));
	}

	public void testRemoveEndXriVariables() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b+c!d#e$f*g");

		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("{}"), false, true), XDIAddress.create("=a*b+c!d#e$f"));
		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("{*}"), false, true), XDIAddress.create("=a*b+c!d#e$f"));
		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("{}{}"), false, true), XDIAddress.create("=a*b+c!d#e"));
		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("{{$*}}"), false, true), XDIAddress.create("=a*b+c!d#e"));
		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("{$}{}"), false, true), XDIAddress.create("=a*b+c!d#e"));
		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("{{!#}}{{$*}}"), false, true), XDIAddress.create("=a*b+c"));
		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("{!}{}{{$*}}"), false, true), XDIAddress.create("=a*b+c"));
		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("{}{!}{}{{$*}}"), false, true), XDIAddress.create("=a*b"));
		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("{+}{{!}}{}{{$*}}"), false, true), XDIAddress.create("=a*b"));
		assertEquals(AddressUtil.removeEndXri(xri, XDIAddress.create("{=}{*}{+}{!}{}{{$*}}"), false, true), XDIAddress.create(""));
		assertEquals(AddressUtil.removeStartXri(xri, XDIAddress.create("{{=+*#$!}}"), false, true), XDIAddress.create(""));
		assertNull(AddressUtil.removeEndXri(xri, XDIAddress.create("{!}"), false, true));
	}
}
