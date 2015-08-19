package xdi2.tests.core.syntax;

import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.tests.AbstractTestCase;

public class CloudNumberTest extends AbstractTestCase {

	public void testCloudNumberXDIAddress() throws Exception {

		assertFalse(CloudNumber.isValid(XDIAddress.create("=markus")));
		assertFalse(CloudNumber.isValid(XDIAddress.create("+projectdanube")));
		assertFalse(CloudNumber.isValid(XDIAddress.create("#email")));
		assertFalse(CloudNumber.isValid(XDIAddress.create("$msg")));
		assertTrue(CloudNumber.isValid(XDIAddress.create("=!1111.abcd")));
		assertTrue(CloudNumber.isValid(XDIAddress.create("+!2222")));

		assertFalse(CloudNumber.isValid(XDIAddress.create("(=markus)")));
		assertFalse(CloudNumber.isValid(XDIAddress.create("(+projectdanube)")));
		assertFalse(CloudNumber.isValid(XDIAddress.create("(#email)")));
		assertFalse(CloudNumber.isValid(XDIAddress.create("($msg)")));
		assertFalse(CloudNumber.isValid(XDIAddress.create("(=)")));
		assertFalse(CloudNumber.isValid(XDIAddress.create("(=!1111.abcd)")));
		assertFalse(CloudNumber.isValid(XDIAddress.create("(+!2222)")));
	}

	public void testCloudNumberPeerRootXDIArc() throws Exception {

		assertFalse(CloudNumber.isValid(XDIArc.create("=markus")));
		assertFalse(CloudNumber.isValid(XDIArc.create("+projectdanube")));
		assertFalse(CloudNumber.isValid(XDIArc.create("#email")));
		assertFalse(CloudNumber.isValid(XDIArc.create("$msg")));
		assertFalse(CloudNumber.isValid(XDIArc.create("=!1111.abcd")));
		assertFalse(CloudNumber.isValid(XDIArc.create("+!2222")));

		assertFalse(CloudNumber.isValid(XDIArc.create("(=markus)")));
		assertFalse(CloudNumber.isValid(XDIArc.create("(+projectdanube)")));
		assertFalse(CloudNumber.isValid(XDIArc.create("(#email)")));
		assertFalse(CloudNumber.isValid(XDIArc.create("($msg)")));
		assertFalse(CloudNumber.isValid(XDIArc.create("(=)")));
		assertTrue(CloudNumber.isValid(XDIArc.create("(=!1111.abcd)")));
		assertTrue(CloudNumber.isValid(XDIArc.create("(+!2222)")));
	}

	public void testRandom() throws Exception {

		assertTrue(CloudNumber.isValid(CloudNumber.createRandom(XDIConstants.CS_AUTHORITY_PERSONAL).getXDIAddress()));
		assertTrue(CloudNumber.isValid(CloudNumber.createRandom(XDIConstants.CS_AUTHORITY_PERSONAL).getPeerRootXDIArc()));

		assertTrue(CloudNumber.isValid(CloudNumber.createRandom(XDIConstants.CS_AUTHORITY_LEGAL).getXDIAddress()));
		assertTrue(CloudNumber.isValid(CloudNumber.createRandom(XDIConstants.CS_AUTHORITY_LEGAL).getPeerRootXDIArc()));

		assertTrue(CloudNumber.isValid(CloudNumber.createRandom(XDIConstants.CS_INSTANCE_ORDERED).getXDIAddress()));
		assertTrue(CloudNumber.isValid(CloudNumber.createRandom(XDIConstants.CS_INSTANCE_ORDERED).getPeerRootXDIArc()));

		assertTrue(CloudNumber.isValid(CloudNumber.createRandom(XDIConstants.CS_INSTANCE_UNORDERED).getXDIAddress()));
		assertTrue(CloudNumber.isValid(CloudNumber.createRandom(XDIConstants.CS_INSTANCE_UNORDERED).getPeerRootXDIArc()));
	}

	public void testCloudNumberEquals() throws Exception {

		assertEquals(CloudNumber.create("=!1111.abcd"), CloudNumber.fromXDIAddress(XDIAddress.create("=!1111.abcd")));
		assertEquals(CloudNumber.create("=!1111.abcd"), CloudNumber.fromPeerRootXDIArc(XDIArc.create("(=!1111.abcd)")));

		assertEquals(CloudNumber.create("+!2222"), CloudNumber.fromXDIAddress(XDIAddress.create("+!2222")));
		assertEquals(CloudNumber.create("+!2222"), CloudNumber.fromPeerRootXDIArc(XDIArc.create("(+!2222)")));

		assertNotEquals(CloudNumber.create("=!1111.Abcd"), CloudNumber.fromXDIAddress(XDIAddress.create("=!1111.abCd")));
		assertNotEquals(CloudNumber.create("=!1111.aBcd"), CloudNumber.fromPeerRootXDIArc(XDIArc.create("(=!1111.abcD)")));

		assertNotEquals(CloudNumber.create("+!2222.Abcd"), CloudNumber.fromXDIAddress(XDIAddress.create("+!2222.abCd")));
		assertNotEquals(CloudNumber.create("+!2222.aBcd"), CloudNumber.fromPeerRootXDIArc(XDIArc.create("(+!2222.abcC)")));
	}
}
