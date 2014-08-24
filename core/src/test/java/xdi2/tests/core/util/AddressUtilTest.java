package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.XDIAddressUtil;

public class AddressUtilTest extends TestCase {

	public void testParentAddress() throws Exception {

		XDIAddress address = XDIAddress.create("=a*b*c*d");

		assertEquals(XDIAddressUtil.parentXDIAddress(address, 0), XDIAddress.create("=a*b*c*d"));
		assertEquals(XDIAddressUtil.parentXDIAddress(address, 1), XDIAddress.create("=a"));
		assertEquals(XDIAddressUtil.parentXDIAddress(address, -1), XDIAddress.create("=a*b*c"));
		assertEquals(XDIAddressUtil.parentXDIAddress(address, 2), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.parentXDIAddress(address, -2), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.parentXDIAddress(address, 3), XDIAddress.create("=a*b*c"));
		assertEquals(XDIAddressUtil.parentXDIAddress(address, -3), XDIAddress.create("=a"));
		assertEquals(XDIAddressUtil.parentXDIAddress(address, 4), XDIAddress.create("=a*b*c*d"));
		assertNull(XDIAddressUtil.parentXDIAddress(address, -4));

		assertEquals(XDIAddressUtil.parentXDIAddress(address, -1), XDIAddress.create("=a*b*c"));
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(address, -1), -1), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(address, -1), -1), -1), XDIAddress.create("=a"));
		assertNull(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(address, -1), -1), -1), -1));
	}

	public void testLocalAddress() throws Exception {

		XDIAddress address = XDIAddress.create("=a*b*c*d");

		assertEquals(XDIAddressUtil.localXDIAddress(address, 0), XDIAddress.create("=a*b*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(address, 1), XDIAddress.create("*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(address, -1), XDIAddress.create("*b*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(address, 2), XDIAddress.create("*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(address, -2), XDIAddress.create("*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(address, 3), XDIAddress.create("*b*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(address, -3), XDIAddress.create("*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(address, 4), XDIAddress.create("=a*b*c*d"));
		assertNull(XDIAddressUtil.localXDIAddress(address, -4));

		assertEquals(XDIAddressUtil.localXDIAddress(address, -1), XDIAddress.create("*b*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(address, -1), -1), XDIAddress.create("*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(address, -1), -1), -1), XDIAddress.create("*d"));
		assertNull(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(address, -1), -1), -1), -1));
	}

	public void testStartsWith() throws Exception {

		XDIAddress xri1 = XDIAddress.create("=a*b*c*d");
		XDIAddress xri2 = XDIAddress.create("{}*b{}*d");

		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("=a")), XDIAddress.create("=a"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("=a*b")), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("=a*b*c")), XDIAddress.create("=a*b*c"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("=a*b*c*d")), XDIAddress.create("=a*b*c*d"));
		assertNull(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("=x*b")));
		assertNull(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("=a*x*c")));

		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("{}"), false, true), XDIAddress.create("=a"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("=a{}"), false, true), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("{}{}*c"), false, true), XDIAddress.create("=a*b*c"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("{}*b*c*d"), false, true), XDIAddress.create("=a*b*c*d"));
		assertNull(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("=x*b"), false, true));
		assertNull(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("=a*x*c"), false, true));

		assertNull(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("{}"), false, false));
		assertNull(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("=a{}"), false, false));
		assertNull(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("{}{}*c"), false, false));
		assertNull(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("{}*b*c*d"), false, false));
		assertNull(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("=x*b"), false, false));
		assertNull(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("=a*x*c"), false, false));

		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri2, XDIAddress.create("=a"), true, false), XDIAddress.create("{}"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri2, XDIAddress.create("=a*b"), true, false), XDIAddress.create("{}*b"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri2, XDIAddress.create("=a*b*c"), true, false), XDIAddress.create("{}*b{}"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri2, XDIAddress.create("=a*b*c*d"), true, false), XDIAddress.create("{}*b{}*d"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri2, XDIAddress.create("=x*b"), true, false), XDIAddress.create("{}*b"));
		assertNull(XDIAddressUtil.startsWithXDIAddress(xri2, XDIAddress.create("=a*x*c"), true, false));

		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("{{=*}}*b*c*d"), false, true), xri1);
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("{{=*}}*c*d"), false, true), xri1);
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("{{=*}}*d"), false, true), xri1);
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("{{=*}}"), false, true), xri1);
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("{{=}}{{*}}*c*d"), false, true), xri1);
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("{{=}}{{*}}*d"), false, true), xri1);
		assertEquals(XDIAddressUtil.startsWithXDIAddress(xri1, XDIAddress.create("{{=}}{{*}}"), false, true), xri1);

		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIAddress.create("=xxx"), XDIAddress.create("")), XDIAddress.create(""));
		assertNull(XDIAddressUtil.startsWithXDIAddress(XDIAddress.create(""), XDIAddress.create("=xxx")));
	}

	public void testEndsWith() throws Exception {

		XDIAddress xri1 = XDIAddress.create("=a*b*c*d");
		XDIAddress xri2 = XDIAddress.create("{}*b{}*d");

		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("*d")), XDIAddress.create("*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("*c*d")), XDIAddress.create("*c*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("*b*c*d")), XDIAddress.create("*b*c*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("=a*b*c*d")), XDIAddress.create("=a*b*c*d"));
		assertNull(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("*y*d")));
		assertNull(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("*b*y*d")));

		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("{}"), false, true), XDIAddress.create("*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("{}*d"), false, true), XDIAddress.create("*c*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("*b{}{}"), false, true), XDIAddress.create("*b*c*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("=a*b*c{}"), false, true), XDIAddress.create("=a*b*c*d"));
		assertNull(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("*y*d"), false, true));
		assertNull(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("*b*y*d"), false, true));

		assertNull(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("{}"), false, false));
		assertNull(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("{}*d"), false, false));
		assertNull(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("*b{}{}"), false, false));
		assertNull(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("=a*b*c{}"), false, false));
		assertNull(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("*y*d"), false, false));
		assertNull(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("*b*y*d"), false, false));

		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri2, XDIAddress.create("*d"), true, false), XDIAddress.create("*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri2, XDIAddress.create("*c*d"), true, false), XDIAddress.create("{}*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri2, XDIAddress.create("*b*c*d"), true, false), XDIAddress.create("*b{}*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri2, XDIAddress.create("=a*b*c*d"), true, false), XDIAddress.create("{}*b{}*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri2, XDIAddress.create("*y*d"), true, false), XDIAddress.create("{}*d"));
		assertNull(XDIAddressUtil.endsWithXDIAddress(xri2, XDIAddress.create("*y*c*d"), true, false));

		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("=a*b*c{{=*}}"), false, true), xri1);
		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("=a*b{{=*}}"), false, true), xri1);
		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("=a{{=*}}"), false, true), xri1);
		assertEquals(XDIAddressUtil.endsWithXDIAddress(xri1, XDIAddress.create("{{=*}}"), false, true), xri1);

		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIAddress.create("=xxx"), XDIAddress.create("")), XDIAddress.create(""));
		assertNull(XDIAddressUtil.endsWithXDIAddress(XDIAddress.create(""), XDIAddress.create("=xxx")));
	}

	public void testStartAddress() throws Exception {

		XDIAddress address = XDIAddress.create("=a*b*c*d");

		assertEquals(XDIAddressUtil.indexOfXDIArc(address, XDIArc.create("*b")), 1);
		assertEquals(XDIAddressUtil.indexOfXDIArc(address, XDIArc.create("*c")), 2);
		assertEquals(XDIAddressUtil.indexOfXDIArc(address, XDIArc.create("*x")), -1);
	}

	public void testEndAddress() throws Exception {

		XDIAddress address = XDIAddress.create("=a*b*c*d");

		assertEquals(XDIAddressUtil.lastIndexOfXDIArc(address, XDIArc.create("*b")), 1);
		assertEquals(XDIAddressUtil.lastIndexOfXDIArc(address, XDIArc.create("*c")), 2);
		assertEquals(XDIAddressUtil.lastIndexOfXDIArc(address, XDIArc.create("*x")), -1);
	}

	public void testSubAddress() throws Exception {

		XDIAddress xri1 = XDIAddress.create("=bob#x=alice#y+registration#z");
		int index1_1 = XDIAddressUtil.indexOfXDIArc(xri1, XDIArc.create("#x"));
		int index1_2 = XDIAddressUtil.indexOfXDIArc(xri1, XDIArc.create("#y"));

		assertEquals(index1_1, 1);
		assertEquals(index1_2, 3);
		assertEquals(XDIAddressUtil.subXDIAddress(xri1, 0, index1_1), XDIAddress.create("=bob"));
		assertEquals(XDIAddressUtil.subXDIAddress(xri1, index1_1 + 1, index1_2), XDIAddress.create("=alice"));

		XDIAddress xri2 = XDIAddress.create("[=]!1111#x[=]!2222#y+registration#z");
		int index2_1 = XDIAddressUtil.indexOfXDIArc(xri2, XDIArc.create("#x"));
		int index2_2 = XDIAddressUtil.indexOfXDIArc(xri2, XDIArc.create("#y"));

		assertEquals(index2_1, 2);
		assertEquals(index2_2, 5);
		assertEquals(XDIAddressUtil.subXDIAddress(xri2, 0, index2_1), XDIAddress.create("[=]!1111"));
		assertEquals(XDIAddressUtil.subXDIAddress(xri2, index2_1 + 1, index2_2), XDIAddress.create("[=]!2222"));
	}

	public void testRemoveStartAddress() throws Exception {

		XDIAddress address = XDIAddress.create("=a*b*c*d");

		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("")), XDIAddress.create("=a*b*c*d"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("=a")), XDIAddress.create("*b*c*d"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("=a*b")), XDIAddress.create("*c*d"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("=a*b*c")), XDIAddress.create("*d"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("=a*b*c*d")), XDIAddress.create(""));
		assertNull(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("=x")));
	}

	public void testRemoveEndAddress() throws Exception {

		XDIAddress address = XDIAddress.create("=a*b*c*d");

		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("")), XDIAddress.create("=a*b*c*d"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("*d")), XDIAddress.create("=a*b*c"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("*c*d")), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("*b*c*d")), XDIAddress.create("=a"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("=a*b*c*d")), XDIAddress.create(""));
		assertNull(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("*y")));
	}

	public void testConcatAddresses() throws Exception {

		assertEquals(XDIAddressUtil.concatXDIAddresses(XDIAddress.create("+a"), XDIAddress.create("+b")), XDIAddress.create("+a+b"));
		assertEquals(XDIAddressUtil.concatXDIAddresses(XDIAddress.create("+a+b"), XDIAddress.create("+c")), XDIAddress.create("+a+b+c"));
		assertEquals(XDIAddressUtil.concatXDIAddresses(XDIAddress.create("+a"), XDIAddress.create("+b+c")), XDIAddress.create("+a+b+c"));

		assertEquals(XDIAddressUtil.concatXDIAddresses(XDIAddress.create("+a"), XDIAddress.create("")), XDIAddress.create("+a"));
		assertEquals(XDIAddressUtil.concatXDIAddresses(XDIAddress.create("+a"), (XDIAddress) null), XDIAddress.create("+a"));

		assertEquals(XDIAddressUtil.concatXDIAddresses(XDIAddress.create(""), XDIAddress.create("+a")), XDIAddress.create("+a"));
		assertEquals(XDIAddressUtil.concatXDIAddresses((XDIAddress) null, XDIAddress.create("+a")), XDIAddress.create("+a"));

		assertEquals(XDIAddressUtil.concatXDIAddresses(XDIAddress.create(""), XDIAddress.create("")), XDIAddress.create(""));
		assertEquals(XDIAddressUtil.concatXDIAddresses((XDIAddress) null, XDIAddress.create("")), XDIAddress.create(""));
		assertEquals(XDIAddressUtil.concatXDIAddresses(XDIAddress.create(""), (XDIAddress) null), XDIAddress.create(""));
		assertEquals(XDIAddressUtil.concatXDIAddresses((XDIAddress) null, (XDIAddress) null), XDIAddress.create(""));

		XDIAddress[] xris = new XDIAddress[] {
				XDIAddress.create(""),
				XDIAddress.create("=a+b"),
				XDIAddress.create("+c"),
				XDIAddress.create(""),
				XDIAddress.create("+d+e")
		};

		assertEquals(XDIAddressUtil.concatXDIAddresses(xris), XDIAddress.create("=a+b+c+d+e"));
	}

	public void testRemoveStartAddressVariables() throws Exception {

		XDIAddress address = XDIAddress.create("=a*b+c!d#e$f*g");

		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("{}"), false, true), XDIAddress.create("*b+c!d#e$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("{=}"), false, true), XDIAddress.create("*b+c!d#e$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("{}{}"), false, true), XDIAddress.create("+c!d#e$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("{{=*}}"), false, true), XDIAddress.create("+c!d#e$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("{}{*}"), false, true), XDIAddress.create("+c!d#e$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("{{*=}}{{!+}}"), false, true), XDIAddress.create("#e$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("{{*=}}{}{!}"), false, true), XDIAddress.create("#e$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("{{*=}}{}{!}{}"), false, true), XDIAddress.create("$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("{{*=}}{}{{!}}{#}"), false, true), XDIAddress.create("$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("{{*=}}{}{!}{#}{$}{*}"), false, true), XDIAddress.create(""));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("{{=+*#$!}}"), false, true), XDIAddress.create(""));
		assertNull(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("{#}"), false, true));
	}

	public void testRemoveEndAddressVariables() throws Exception {

		XDIAddress address = XDIAddress.create("=a*b+c!d#e$f*g");

		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("{}"), false, true), XDIAddress.create("=a*b+c!d#e$f"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("{*}"), false, true), XDIAddress.create("=a*b+c!d#e$f"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("{}{}"), false, true), XDIAddress.create("=a*b+c!d#e"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("{{$*}}"), false, true), XDIAddress.create("=a*b+c!d#e"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("{$}{}"), false, true), XDIAddress.create("=a*b+c!d#e"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("{{!#}}{{$*}}"), false, true), XDIAddress.create("=a*b+c"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("{!}{}{{$*}}"), false, true), XDIAddress.create("=a*b+c"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("{}{!}{}{{$*}}"), false, true), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("{+}{{!}}{}{{$*}}"), false, true), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("{=}{*}{+}{!}{}{{$*}}"), false, true), XDIAddress.create(""));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(address, XDIAddress.create("{{=+*#$!}}"), false, true), XDIAddress.create(""));
		assertNull(XDIAddressUtil.removeEndXDIAddress(address, XDIAddress.create("{!}"), false, true));
	}
}
