package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.AddressUtil;

public class XDI3UtilTest extends TestCase {

	public void testParentAddress() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b*c*d");

		assertEquals(AddressUtil.parentAddress(xri, 0), XDIAddress.create("=a*b*c*d"));
		assertEquals(AddressUtil.parentAddress(xri, 1), XDIAddress.create("=a"));
		assertEquals(AddressUtil.parentAddress(xri, -1), XDIAddress.create("=a*b*c"));
		assertEquals(AddressUtil.parentAddress(xri, 2), XDIAddress.create("=a*b"));
		assertEquals(AddressUtil.parentAddress(xri, -2), XDIAddress.create("=a*b"));
		assertEquals(AddressUtil.parentAddress(xri, 3), XDIAddress.create("=a*b*c"));
		assertEquals(AddressUtil.parentAddress(xri, -3), XDIAddress.create("=a"));
		assertEquals(AddressUtil.parentAddress(xri, 4), XDIAddress.create("=a*b*c*d"));
		assertNull(AddressUtil.parentAddress(xri, -4));

		assertEquals(AddressUtil.parentAddress(xri, -1), XDIAddress.create("=a*b*c"));
		assertEquals(AddressUtil.parentAddress(AddressUtil.parentAddress(xri, -1), -1), XDIAddress.create("=a*b"));
		assertEquals(AddressUtil.parentAddress(AddressUtil.parentAddress(AddressUtil.parentAddress(xri, -1), -1), -1), XDIAddress.create("=a"));
		assertNull(AddressUtil.parentAddress(AddressUtil.parentAddress(AddressUtil.parentAddress(AddressUtil.parentAddress(xri, -1), -1), -1), -1));
	}

	public void testLocalAddress() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b*c*d");

		assertEquals(AddressUtil.localAddress(xri, 0), XDIAddress.create("=a*b*c*d"));
		assertEquals(AddressUtil.localAddress(xri, 1), XDIAddress.create("*d"));
		assertEquals(AddressUtil.localAddress(xri, -1), XDIAddress.create("*b*c*d"));
		assertEquals(AddressUtil.localAddress(xri, 2), XDIAddress.create("*c*d"));
		assertEquals(AddressUtil.localAddress(xri, -2), XDIAddress.create("*c*d"));
		assertEquals(AddressUtil.localAddress(xri, 3), XDIAddress.create("*b*c*d"));
		assertEquals(AddressUtil.localAddress(xri, -3), XDIAddress.create("*d"));
		assertEquals(AddressUtil.localAddress(xri, 4), XDIAddress.create("=a*b*c*d"));
		assertNull(AddressUtil.localAddress(xri, -4));

		assertEquals(AddressUtil.localAddress(xri, -1), XDIAddress.create("*b*c*d"));
		assertEquals(AddressUtil.localAddress(AddressUtil.localAddress(xri, -1), -1), XDIAddress.create("*c*d"));
		assertEquals(AddressUtil.localAddress(AddressUtil.localAddress(AddressUtil.localAddress(xri, -1), -1), -1), XDIAddress.create("*d"));
		assertNull(AddressUtil.localAddress(AddressUtil.localAddress(AddressUtil.localAddress(AddressUtil.localAddress(xri, -1), -1), -1), -1));
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

	public void testStartAddress() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b*c*d");

		assertEquals(AddressUtil.indexOfAddress(xri, XDIArc.create("*b")), 1);
		assertEquals(AddressUtil.indexOfAddress(xri, XDIArc.create("*c")), 2);
		assertEquals(AddressUtil.indexOfAddress(xri, XDIArc.create("*x")), -1);
	}

	public void testEndAddress() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b*c*d");

		assertEquals(AddressUtil.lastIndexOfAddress(xri, XDIArc.create("*b")), 1);
		assertEquals(AddressUtil.lastIndexOfAddress(xri, XDIArc.create("*c")), 2);
		assertEquals(AddressUtil.lastIndexOfAddress(xri, XDIArc.create("*x")), -1);
	}

	public void testSubAddress() throws Exception {

		XDIAddress xri1 = XDIAddress.create("=bob#x=alice#y+registration#z");
		int index1_1 = AddressUtil.indexOfAddress(xri1, XDIArc.create("#x"));
		int index1_2 = AddressUtil.indexOfAddress(xri1, XDIArc.create("#y"));

		assertEquals(index1_1, 1);
		assertEquals(index1_2, 3);
		assertEquals(AddressUtil.subAddress(xri1, 0, index1_1), XDIAddress.create("=bob"));
		assertEquals(AddressUtil.subAddress(xri1, index1_1 + 1, index1_2), XDIAddress.create("=alice"));

		XDIAddress xri2 = XDIAddress.create("[=]!1111#x[=]!2222#y+registration#z");
		int index2_1 = AddressUtil.indexOfAddress(xri2, XDIArc.create("#x"));
		int index2_2 = AddressUtil.indexOfAddress(xri2, XDIArc.create("#y"));

		assertEquals(index2_1, 2);
		assertEquals(index2_2, 5);
		assertEquals(AddressUtil.subAddress(xri2, 0, index2_1), XDIAddress.create("[=]!1111"));
		assertEquals(AddressUtil.subAddress(xri2, index2_1 + 1, index2_2), XDIAddress.create("[=]!2222"));
	}

	public void testRemoveStartAddress() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b*c*d");

		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("")), XDIAddress.create("=a*b*c*d"));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("=a")), XDIAddress.create("*b*c*d"));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("=a*b")), XDIAddress.create("*c*d"));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("=a*b*c")), XDIAddress.create("*d"));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("=a*b*c*d")), XDIAddress.create(""));
		assertNull(AddressUtil.removeStartAddress(xri, XDIAddress.create("=x")));
	}

	public void testRemoveEndAddress() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b*c*d");

		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("")), XDIAddress.create("=a*b*c*d"));
		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("*d")), XDIAddress.create("=a*b*c"));
		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("*c*d")), XDIAddress.create("=a*b"));
		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("*b*c*d")), XDIAddress.create("=a"));
		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("=a*b*c*d")), XDIAddress.create(""));
		assertNull(AddressUtil.removeEndAddress(xri, XDIAddress.create("*y")));
	}

	public void testConcatAddresss() throws Exception {

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

	public void testRemoveStartAddressVariables() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b+c!d#e$f*g");

		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("{}"), false, true), XDIAddress.create("*b+c!d#e$f*g"));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("{=}"), false, true), XDIAddress.create("*b+c!d#e$f*g"));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("{}{}"), false, true), XDIAddress.create("+c!d#e$f*g"));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("{{=*}}"), false, true), XDIAddress.create("+c!d#e$f*g"));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("{}{*}"), false, true), XDIAddress.create("+c!d#e$f*g"));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("{{*=}}{{!+}}"), false, true), XDIAddress.create("#e$f*g"));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("{{*=}}{}{!}"), false, true), XDIAddress.create("#e$f*g"));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("{{*=}}{}{!}{}"), false, true), XDIAddress.create("$f*g"));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("{{*=}}{}{{!}}{#}"), false, true), XDIAddress.create("$f*g"));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("{{*=}}{}{!}{#}{$}{*}"), false, true), XDIAddress.create(""));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("{{=+*#$!}}"), false, true), XDIAddress.create(""));
		assertNull(AddressUtil.removeStartAddress(xri, XDIAddress.create("{#}"), false, true));
	}

	public void testRemoveEndAddressVariables() throws Exception {

		XDIAddress xri = XDIAddress.create("=a*b+c!d#e$f*g");

		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("{}"), false, true), XDIAddress.create("=a*b+c!d#e$f"));
		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("{*}"), false, true), XDIAddress.create("=a*b+c!d#e$f"));
		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("{}{}"), false, true), XDIAddress.create("=a*b+c!d#e"));
		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("{{$*}}"), false, true), XDIAddress.create("=a*b+c!d#e"));
		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("{$}{}"), false, true), XDIAddress.create("=a*b+c!d#e"));
		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("{{!#}}{{$*}}"), false, true), XDIAddress.create("=a*b+c"));
		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("{!}{}{{$*}}"), false, true), XDIAddress.create("=a*b+c"));
		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("{}{!}{}{{$*}}"), false, true), XDIAddress.create("=a*b"));
		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("{+}{{!}}{}{{$*}}"), false, true), XDIAddress.create("=a*b"));
		assertEquals(AddressUtil.removeEndAddress(xri, XDIAddress.create("{=}{*}{+}{!}{}{{$*}}"), false, true), XDIAddress.create(""));
		assertEquals(AddressUtil.removeStartAddress(xri, XDIAddress.create("{{=+*#$!}}"), false, true), XDIAddress.create(""));
		assertNull(AddressUtil.removeEndAddress(xri, XDIAddress.create("{!}"), false, true));
	}
}
