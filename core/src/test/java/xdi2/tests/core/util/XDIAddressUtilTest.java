package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiInnerRoot;
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
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIaddress, -4), XDIConstants.XDI_ADD_ROOT);
		assertNull(XDIAddressUtil.parentXDIAddress(XDIaddress, -5));

		assertEquals(XDIAddressUtil.parentXDIAddress(XDIaddress, -1), XDIAddress.create("=a*b*c"));
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIaddress, -1), -1), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIaddress, -1), -1), -1), XDIAddress.create("=a"));
		assertEquals(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIaddress, -1), -1), -1), -1), XDIConstants.XDI_ADD_ROOT);
		assertNull(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIAddressUtil.parentXDIAddress(XDIaddress, -1), -1), -1), -1), -1));
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
		assertEquals(XDIAddressUtil.localXDIAddress(XDIaddress, -4), XDIConstants.XDI_ADD_ROOT);
		assertNull(XDIAddressUtil.localXDIAddress(XDIaddress, -5));

		assertEquals(XDIAddressUtil.localXDIAddress(XDIaddress, -1), XDIAddress.create("*b*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIaddress, -1), -1), XDIAddress.create("*c*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIaddress, -1), -1), -1), XDIAddress.create("*d"));
		assertEquals(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIaddress, -1), -1), -1), -1), XDIConstants.XDI_ADD_ROOT);
		assertNull(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIAddressUtil.localXDIAddress(XDIaddress, -1), -1), -1), -1), -1));
	}

	// TODO: DISABLED
	public void DISABLEDtestStartsWithXDIAddress() throws Exception {

		XDIAddress XDIaddress1 = XDIAddress.create("=a*b*c*d");
		XDIAddress XDIaddress2 = XDIAddress.create("{}*b{}*d");

		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("=a")), XDIAddress.create("=a"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("=a*b")), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("=a*b*c")), XDIAddress.create("=a*b*c"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("=a*b*c*d")), XDIAddress.create("=a*b*c*d"));
		assertNull(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("=x*b")));
		assertNull(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("=a*x*c")));

		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("{}"), false, true), XDIAddress.create("=a"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("=a{}"), false, true), XDIAddress.create("=a*b"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("{}{}*c"), false, true), XDIAddress.create("=a*b*c"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("{}*b*c*d"), false, true), XDIAddress.create("=a*b*c*d"));
		assertNull(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("=x*b"), false, true));
		assertNull(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("=a*x*c"), false, true));

		assertNull(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("{}"), false, false));
		assertNull(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("=a{}"), false, false));
		assertNull(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("{}{}*c"), false, false));
		assertNull(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("{}*b*c*d"), false, false));
		assertNull(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("=x*b"), false, false));
		assertNull(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("=a*x*c"), false, false));

		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress2, XDIAddress.create("=a"), true, false), XDIAddress.create("{}"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress2, XDIAddress.create("=a*b"), true, false), XDIAddress.create("{}*b"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress2, XDIAddress.create("=a*b*c"), true, false), XDIAddress.create("{}*b{}"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress2, XDIAddress.create("=a*b*c*d"), true, false), XDIAddress.create("{}*b{}*d"));
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress2, XDIAddress.create("=x*b"), true, false), XDIAddress.create("{}*b"));
		assertNull(XDIAddressUtil.startsWithXDIAddress(XDIaddress2, XDIAddress.create("=a*x*c"), true, false));

		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("{{=*}}*b*c*d"), false, true), XDIaddress1);
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("{{=*}}*c*d"), false, true), XDIaddress1);
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("{{=*}}*d"), false, true), XDIaddress1);
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("{{=*}}"), false, true), XDIaddress1);
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("{{=}}{{*}}*c*d"), false, true), XDIaddress1);
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("{{=}}{{*}}*d"), false, true), XDIaddress1);
		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIaddress1, XDIAddress.create("{{=}}{{*}}"), false, true), XDIaddress1);

		assertEquals(XDIAddressUtil.startsWithXDIAddress(XDIAddress.create("=xxx"), XDIAddress.create("")), XDIAddress.create(""));
		assertNull(XDIAddressUtil.startsWithXDIAddress(XDIAddress.create(""), XDIAddress.create("=xxx")));
	}

	// TODO: DISABLED
	public void DISABLEDtestEndsWithXDIAddress() throws Exception {

		XDIAddress XDIaddress1 = XDIAddress.create("=a*b*c*d");
		XDIAddress XDIaddress2 = XDIAddress.create("{}*b{}*d");

		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("*d")), XDIAddress.create("*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("*c*d")), XDIAddress.create("*c*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("*b*c*d")), XDIAddress.create("*b*c*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("=a*b*c*d")), XDIAddress.create("=a*b*c*d"));
		assertNull(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("*y*d")));
		assertNull(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("*b*y*d")));

		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("{}"), false, true), XDIAddress.create("*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("{}*d"), false, true), XDIAddress.create("*c*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("*b{}{}"), false, true), XDIAddress.create("*b*c*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("=a*b*c{}"), false, true), XDIAddress.create("=a*b*c*d"));
		assertNull(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("*y*d"), false, true));
		assertNull(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("*b*y*d"), false, true));

		assertNull(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("{}"), false, false));
		assertNull(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("{}*d"), false, false));
		assertNull(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("*b{}{}"), false, false));
		assertNull(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("=a*b*c{}"), false, false));
		assertNull(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("*y*d"), false, false));
		assertNull(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("*b*y*d"), false, false));

		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress2, XDIAddress.create("*d"), true, false), XDIAddress.create("*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress2, XDIAddress.create("*c*d"), true, false), XDIAddress.create("{}*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress2, XDIAddress.create("*b*c*d"), true, false), XDIAddress.create("*b{}*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress2, XDIAddress.create("=a*b*c*d"), true, false), XDIAddress.create("{}*b{}*d"));
		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress2, XDIAddress.create("*y*d"), true, false), XDIAddress.create("{}*d"));
		assertNull(XDIAddressUtil.endsWithXDIAddress(XDIaddress2, XDIAddress.create("*y*c*d"), true, false));

		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("=a*b*c{{=*}}"), false, true), XDIaddress1);
		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("=a*b{{=*}}"), false, true), XDIaddress1);
		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("=a{{=*}}"), false, true), XDIaddress1);
		assertEquals(XDIAddressUtil.endsWithXDIAddress(XDIaddress1, XDIAddress.create("{{=*}}"), false, true), XDIaddress1);

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

		XDIAddress XDIaddress1 = XDIAddress.create("=bob#x=alice#y+registration#z");
		int index1_1 = XDIAddressUtil.indexOfXDIArc(XDIaddress1, XDIArc.create("#x"));
		int index1_2 = XDIAddressUtil.indexOfXDIArc(XDIaddress1, XDIArc.create("#y"));

		assertEquals(index1_1, 1);
		assertEquals(index1_2, 3);
		assertEquals(XDIAddressUtil.subXDIAddress(XDIaddress1, 0, index1_1), XDIAddress.create("=bob"));
		assertEquals(XDIAddressUtil.subXDIAddress(XDIaddress1, index1_1 + 1, index1_2), XDIAddress.create("=alice"));

		XDIAddress XDIaddress2 = XDIAddress.create("=!1111#x=!2222#y+registration#z");
		int index2_1 = XDIAddressUtil.indexOfXDIArc(XDIaddress2, XDIArc.create("#x"));
		int index2_2 = XDIAddressUtil.indexOfXDIArc(XDIaddress2, XDIArc.create("#y"));

		assertEquals(index2_1, 1);
		assertEquals(index2_2, 3);
		assertEquals(XDIAddressUtil.subXDIAddress(XDIaddress2, 0, index2_1), XDIAddress.create("=!1111"));
		assertEquals(XDIAddressUtil.subXDIAddress(XDIaddress2, index2_1 + 1, index2_2), XDIAddress.create("=!2222"));
	}

	public void testExtractXDIAddress() throws Exception {

		XDIAddress XDIaddress = XDIAddress.create("(=a)(=b)(=c/+d)=!:uuid:1111<#first><#name>");

		assertEquals(XDIAddress.create(""), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiCommonRoot.class, false, false, false, false));
		assertEquals(XDIAddress.create("(=a)(=b)"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiPeerRoot.class, false, false, false, false));
		assertEquals(XDIAddress.create("(=a)"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiPeerRoot.class, true, false, false, false));
		assertEquals(XDIAddress.create("(=b)"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiPeerRoot.class, false, true, false, false));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiRoot.class, false, false, false, false));
		assertEquals(XDIAddress.create("(=c/+d)"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiInnerRoot.class, false, false, false, false));
		assertEquals(XDIAddress.create("=!:uuid:1111"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiEntity.class, false, false, false, false));
		assertEquals(XDIAddress.create("=!:uuid:1111"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiEntitySingleton.class, false, false, false, false));
		assertEquals(XDIAddress.create("<#first><#name>"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiAttribute.class, false, false, false, false));
		assertEquals(XDIAddress.create("<#first><#name>"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiAttributeSingleton.class, false, false, false, false));

		assertEquals(XDIAddress.create(""), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiCommonRoot.class, false, false, true, false));
		assertEquals(XDIAddress.create("(=a)(=b)"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiPeerRoot.class, false, false, true, false));
		assertEquals(XDIAddress.create("(=a)"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiPeerRoot.class, true, false, true, false));
		assertEquals(XDIAddress.create("(=b)"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiPeerRoot.class, false, true, true, false));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiRoot.class, false, false, true, false));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiInnerRoot.class, false, false, true, false));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)=!:uuid:1111"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiEntity.class, false, false, true, false));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)=!:uuid:1111"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiEntitySingleton.class, false, false, true, false));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)=!:uuid:1111<#first><#name>"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiAttribute.class, false, false, true, false));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)=!:uuid:1111<#first><#name>"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiAttributeSingleton.class, false, false, true, false));

		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)=!:uuid:1111<#first><#name>"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiCommonRoot.class, false, false, false, true));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)=!:uuid:1111<#first><#name>"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiPeerRoot.class, false, false, false, true));
		assertEquals(XDIAddress.create("(=a)(=b)(=c/+d)=!:uuid:1111<#first><#name>"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiRoot.class, false, false, false, true));
		assertEquals(XDIAddress.create("(=c/+d)=!:uuid:1111<#first><#name>"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiInnerRoot.class, false, false, false, true));
		assertEquals(XDIAddress.create("=!:uuid:1111<#first><#name>"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiEntity.class, false, false, false, true));
		assertEquals(XDIAddress.create("=!:uuid:1111<#first><#name>"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiEntitySingleton.class, false, false, false, true));
		assertEquals(XDIAddress.create("<#first><#name>"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiAttribute.class, false, false, false, true));
		assertEquals(XDIAddress.create("<#first><#name>"), XDIAddressUtil.extractXDIAddress(XDIaddress, XdiAttributeSingleton.class, false, false, false, true));
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

		XDIAddress[] XDIaddresses = new XDIAddress[] {
				XDIAddress.create(""),
				XDIAddress.create("=a+b"),
				XDIAddress.create("+c"),
				XDIAddress.create(""),
				XDIAddress.create("+d+e")
		};

		assertEquals(XDIAddressUtil.concatXDIAddresses(XDIaddresses), XDIAddress.create("=a+b+c+d+e"));
	}

	// TODO: DISABLED
	public void DISABLEDtestRemoveStartXDIAddressVariables() throws Exception {

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

	// TODO: DISABLED
	public void DISABLEDtestRemoveEndXDIAddressVariables() throws Exception {

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
