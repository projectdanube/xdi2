package xdi2.tests.core.syntax;

import junit.framework.TestCase;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.CloudName;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

public class CloudNameTest extends TestCase {

	public void testCloudNameXDIAddress() throws Exception {

		assertTrue(CloudName.isValid(XDIAddress.create("=markus")));
		assertTrue(CloudName.isValid(XDIAddress.create("+projectdanube")));
		assertFalse(CloudName.isValid(XDIAddress.create("#email")));
		assertFalse(CloudName.isValid(XDIAddress.create("$msg")));
		assertFalse(CloudName.isValid(XDIAddress.create("=")));
		assertFalse(CloudName.isValid(XDIAddress.create("=!1111")));
		assertFalse(CloudName.isValid(XDIAddress.create("+!2222")));

		assertFalse(CloudName.isValid(XDIAddress.create("(=markus)")));
		assertFalse(CloudName.isValid(XDIAddress.create("(+projectdanube)")));
		assertFalse(CloudName.isValid(XDIAddress.create("(#email)")));
		assertFalse(CloudName.isValid(XDIAddress.create("($msg)")));
		assertFalse(CloudName.isValid(XDIAddress.create("(=)")));
		assertFalse(CloudName.isValid(XDIAddress.create("(=!1111)")));
		assertFalse(CloudName.isValid(XDIAddress.create("(+!2222)")));
	}

	public void testCloudNamePeerRootXDIArc() throws Exception {

		assertFalse(CloudName.isValid(XDIArc.create("=markus")));
		assertFalse(CloudName.isValid(XDIArc.create("+projectdanube")));
		assertFalse(CloudName.isValid(XDIArc.create("#email")));
		assertFalse(CloudName.isValid(XDIArc.create("$msg")));
		assertFalse(CloudName.isValid(XDIArc.create("=")));
		assertFalse(CloudName.isValid(XDIArc.create("=!1111")));
		assertFalse(CloudName.isValid(XDIArc.create("+!2222")));

		assertTrue(CloudName.isValid(XDIArc.create("(=markus)")));
		assertTrue(CloudName.isValid(XDIArc.create("(+projectdanube)")));
		assertFalse(CloudName.isValid(XDIArc.create("(#email)")));
		assertFalse(CloudName.isValid(XDIArc.create("($msg)")));
		assertFalse(CloudName.isValid(XDIArc.create("(=)")));
		assertFalse(CloudName.isValid(XDIArc.create("(=!1111)")));
		assertFalse(CloudName.isValid(XDIArc.create("(=!2222)")));
	}

	public void testRandom() throws Exception {

		assertTrue(CloudName.isValid(CloudName.createRandom(XDIConstants.CS_AUTHORITY_PERSONAL).getXDIAddress()));
		assertTrue(CloudName.isValid(CloudName.createRandom(XDIConstants.CS_AUTHORITY_PERSONAL).getPeerRootXDIArc()));

		assertTrue(CloudName.isValid(CloudName.createRandom(XDIConstants.CS_AUTHORITY_LEGAL).getXDIAddress()));
		assertTrue(CloudName.isValid(CloudName.createRandom(XDIConstants.CS_AUTHORITY_LEGAL).getPeerRootXDIArc()));

		assertTrue(CloudName.isValid(CloudName.createRandom(XDIConstants.CS_INSTANCE_ORDERED).getXDIAddress()));
		assertTrue(CloudName.isValid(CloudName.createRandom(XDIConstants.CS_INSTANCE_ORDERED).getPeerRootXDIArc()));

		assertTrue(CloudName.isValid(CloudName.createRandom(XDIConstants.CS_INSTANCE_UNORDERED).getXDIAddress()));
		assertTrue(CloudName.isValid(CloudName.createRandom(XDIConstants.CS_INSTANCE_UNORDERED).getPeerRootXDIArc()));
	}

	public void testCloudNameEquals() throws Exception {

		assertEquals(CloudName.create("=markus"), CloudName.fromXDIAddress(XDIAddress.create("=markus")));
		assertEquals(CloudName.create("=markus"), CloudName.fromPeerRootXDIArc(XDIArc.create("(=markus)")));

		assertEquals(CloudName.create("+projectdanube"), CloudName.fromXDIAddress(XDIAddress.create("+projectdanube")));
		assertEquals(CloudName.create("+projectdanube"), CloudName.fromPeerRootXDIArc(XDIArc.create("(+projectdanube)")));

		assertEquals(CloudName.create("=Markus"), CloudName.fromXDIAddress(XDIAddress.create("=maRkus")));
		assertEquals(CloudName.create("=mArkus"), CloudName.fromPeerRootXDIArc(XDIArc.create("(=marKus)")));

		assertEquals(CloudName.create("+Projectdanube"), CloudName.fromXDIAddress(XDIAddress.create("+prOjectdanube")));
		assertEquals(CloudName.create("+pRojectdanube"), CloudName.fromPeerRootXDIArc(XDIArc.create("(+proJectdanube)")));
	}
}
