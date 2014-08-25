package xdi2.tests.core.syntax;

import junit.framework.TestCase;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;

public class CloudNumberTest extends TestCase {

	public void testCloudNumber() throws Exception {

		assertFalse(CloudNumber.isValid(XDIAddress.create("=markus")));
		assertFalse(CloudNumber.isValid(XDIAddress.create("=markus*web")));
		assertFalse(CloudNumber.isValid(XDIAddress.create("[=]")));
		assertTrue(CloudNumber.isValid(XDIAddress.create("[=]!1111")));
		assertTrue(CloudNumber.isValid(XDIAddress.create("[=]!1111[=]!2222")));
		assertTrue(CloudNumber.isValid(XDIAddress.create("[+]!1111")));
		assertTrue(CloudNumber.isValid(XDIAddress.create("[+]!1111[+]!2222")));

		assertTrue(CloudNumber.isValid(CloudNumber.createRandom(XDIConstants.CS_AUTHORITY_PERSONAL).getXDIAddress()));
		assertTrue(CloudNumber.isValid(CloudNumber.createRandom(XDIConstants.CS_AUTHORITY_LEGAL).getXDIAddress()));
	}

	public void testIsCloudNumber() throws Exception {

		assertFalse(CloudNumber.isValid(XDIAddress.create("=markus")));
		assertFalse(CloudNumber.isValid(XDIAddress.create("=markus*web")));
		assertFalse(CloudNumber.isValid(XDIAddress.create("[=]")));
		assertTrue(CloudNumber.isValid(XDIAddress.create("[=]!1111")));
		assertTrue(CloudNumber.isValid(XDIAddress.create("[=]!1111[=]!2222")));
		assertTrue(CloudNumber.isValid(XDIAddress.create("[+]!1111")));
		assertTrue(CloudNumber.isValid(XDIAddress.create("[+]!1111[+]!2222")));
	}
}
