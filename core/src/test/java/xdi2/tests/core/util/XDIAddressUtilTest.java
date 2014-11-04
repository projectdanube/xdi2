package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiCollection;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityMember;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiMemberUnordered;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.XDIAddressUtil;

public class XDIAddressUtilTest extends TestCase {

	public void testParentXDIAddress() throws Exception {

		XDIAddress XDIaddress = XDIAddress.create("=a*b*c*d");

		assertEquals(XDIAddressUtil.parentXDIAddress(XDIaddress, 0), XDIAddress.create("=a*b*c*d"));
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIaddress, 1), XDIAddress.create("=a"));
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIaddress, -1), XDIAddress.create("=a*b*c"));
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIaddress, 2), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIaddress, -2), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIaddress, 3), XDIAddress.create("=a*b*c"));
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIaddress, -3), XDIAddress.create("=a"));
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIaddress, 4), XDIAddress.create("=a*b*c*d"));
		assertNull(XDIAddressUtil.parentXDIAddress(XDIaddress, -4));

		assertEquals(XDIAddressUtil.parentXDIAddress(XDIaddress, -1), XDIAddress.create("=a*b*c"));
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIaddress, -1), -1), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIaddress, -1), -1), -1), XDIAddress.create("=a"));
		assertNull(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIaddress, -1), -1), -1), -1));
	}

	public void testLocalXDIAddress() throws Exception {

		XDIAddress XDIaddress = XDIAddress.create("=a*b*c*d");

		assertEquals(XDIAddressUtil.localXDIAddress(XDIaddress, 0), XDIAddress.create("=a*b*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(XDIaddress, 1), XDIAddress.create("*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(XDIaddress, -1), XDIAddress.create("*b*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(XDIaddress, 2), XDIAddress.create("*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(XDIaddress, -2), XDIAddress.create("*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(XDIaddress, 3), XDIAddress.create("*b*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(XDIaddress, -3), XDIAddress.create("*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(XDIaddress, 4), XDIAddress.create("=a*b*c*d"));
		assertNull(XDIAddressUtil.localXDIAddress(XDIaddress, -4));

		assertEquals(XDIAddressUtil.localXDIAddress(XDIaddress, -1), XDIAddress.create("*b*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIaddress, -1), -1), XDIAddress.create("*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIaddress, -1), -1), -1), XDIAddress.create("*d"));
		assertNull(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIaddress, -1), -1), -1), -1));
	}

	public void testStartsWithXDIAddress() throws Exception {

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

	public void testEndsWithXDIAddress() throws Exception {

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

	public void testStartXDIAddress() throws Exception {

		XDIAddress XDIaddress = XDIAddress.create("=a*b*c*d");

		assertEquals(XDIAddressUtil.indexOfXDIArc(XDIaddress, XDIArc.create("*b")), 1);
		assertEquals(XDIAddressUtil.indexOfXDIArc(XDIaddress, XDIArc.create("*c")), 2);
		assertEquals(XDIAddressUtil.indexOfXDIArc(XDIaddress, XDIArc.create("*x")), -1);
	}

	public void testEndXDIAddress() throws Exception {

		XDIAddress XDIaddress = XDIAddress.create("=a*b*c*d");

		assertEquals(XDIAddressUtil.lastIndexOfXDIArc(XDIaddress, XDIArc.create("*b")), 1);
		assertEquals(XDIAddressUtil.lastIndexOfXDIArc(XDIaddress, XDIArc.create("*c")), 2);
		assertEquals(XDIAddressUtil.lastIndexOfXDIArc(XDIaddress, XDIArc.create("*x")), -1);
	}

	public void testSubXDIAddress() throws Exception {

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

	public void testFindXDIAddress() throws Exception {

		XDIAddress XDIaddress = XDIAddress.create("(=a)(=b)(=c/+d)[=]!:uuid:1111<#first><#name>&");
		assertEquals(XDIAddress.create(""), XDIAddressUtil.findXDIAddress(XDIaddress, XdiCommonRoot.class));
		assertEquals(XDIAddress.create("(=a)(=b)"), XDIAddressUtil.findXDIAddress(XDIaddress, XdiPeerRoot.class));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)"), XDIAddressUtil.findXDIAddress(XDIaddress, XdiRoot.class));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)"), XDIAddressUtil.findXDIAddress(XDIaddress, XdiInnerRoot.class));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)[=]"), XDIAddressUtil.findXDIAddress(XDIaddress, XdiCollection.class));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)[=]"), XDIAddressUtil.findXDIAddress(XDIaddress, XdiEntityCollection.class));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)[=]!:uuid:1111"), XDIAddressUtil.findXDIAddress(XDIaddress, XdiEntity.class));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)[=]!:uuid:1111"), XDIAddressUtil.findXDIAddress(XDIaddress, XdiEntityMember.class));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)[=]!:uuid:1111"), XDIAddressUtil.findXDIAddress(XDIaddress, XdiMemberUnordered.class));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)[=]!:uuid:1111<#first><#name>"), XDIAddressUtil.findXDIAddress(XDIaddress, XdiAttribute.class));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)[=]!:uuid:1111<#first><#name>"), XDIAddressUtil.findXDIAddress(XDIaddress, XdiAttributeSingleton.class));
	}

	public void testRemoveStartXDIAddress() throws Exception {

		XDIAddress XDIaddress = XDIAddress.create("=a*b*c*d");

		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("")), XDIAddress.create("=a*b*c*d"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("=a")), XDIAddress.create("*b*c*d"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("=a*b")), XDIAddress.create("*c*d"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("=a*b*c")), XDIAddress.create("*d"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("=a*b*c*d")), XDIAddress.create(""));
		assertNull(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("=x")));
	}

	public void testRemoveEndAddress() throws Exception {

		XDIAddress XDIaddress = XDIAddress.create("=a*b*c*d");

		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("")), XDIAddress.create("=a*b*c*d"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("*d")), XDIAddress.create("=a*b*c"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("*c*d")), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("*b*c*d")), XDIAddress.create("=a"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("=a*b*c*d")), XDIAddress.create(""));
		assertNull(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("*y")));
	}

	public void testConcatXDIAddresses() throws Exception {

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

	public void testRemoveStartXDIAddressVariables() throws Exception {

		XDIAddress XDIaddress = XDIAddress.create("=a*b+c!d#e$f*g");

		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("{}"), false, true), XDIAddress.create("*b+c!d#e$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("{=}"), false, true), XDIAddress.create("*b+c!d#e$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("{}{}"), false, true), XDIAddress.create("+c!d#e$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("{{=*}}"), false, true), XDIAddress.create("+c!d#e$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("{}{*}"), false, true), XDIAddress.create("+c!d#e$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("{{*=}}{{!+}}"), false, true), XDIAddress.create("#e$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("{{*=}}{}{!}"), false, true), XDIAddress.create("#e$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("{{*=}}{}{!}{}"), false, true), XDIAddress.create("$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("{{*=}}{}{{!}}{#}"), false, true), XDIAddress.create("$f*g"));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("{{*=}}{}{!}{#}{$}{*}"), false, true), XDIAddress.create(""));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("{{=+*#$!}}"), false, true), XDIAddress.create(""));
		assertNull(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("{#}"), false, true));
	}

	public void testRemoveEndXDIAddressVariables() throws Exception {

		XDIAddress XDIaddress = XDIAddress.create("=a*b+c!d#e$f*g");

		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("{}"), false, true), XDIAddress.create("=a*b+c!d#e$f"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("{*}"), false, true), XDIAddress.create("=a*b+c!d#e$f"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("{}{}"), false, true), XDIAddress.create("=a*b+c!d#e"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("{{$*}}"), false, true), XDIAddress.create("=a*b+c!d#e"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("{$}{}"), false, true), XDIAddress.create("=a*b+c!d#e"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("{{!#}}{{$*}}"), false, true), XDIAddress.create("=a*b+c"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("{!}{}{{$*}}"), false, true), XDIAddress.create("=a*b+c"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("{}{!}{}{{$*}}"), false, true), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("{+}{{!}}{}{{$*}}"), false, true), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("{=}{*}{+}{!}{}{{$*}}"), false, true), XDIAddress.create(""));
		assertEquals(XDIAddressUtil.removeStartXDIAddress(XDIaddress, XDIAddress.create("{{=+*#$!}}"), false, true), XDIAddress.create(""));
		assertNull(XDIAddressUtil.removeEndXDIAddress(XDIaddress, XDIAddress.create("{!}"), false, true));
	}
}
