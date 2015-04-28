package xdi2.tests.core.syntax;

import junit.framework.TestCase;
import xdi2.core.syntax.CloudName;
import xdi2.core.syntax.XDIAddress;

public class CloudNameTest extends TestCase {

	public void testCloudName() throws Exception {

		assertTrue(CloudName.isValid(XDIAddress.create("=markus")));
		assertTrue(CloudName.isValid(XDIAddress.create("+projectdanube")));
		assertFalse(CloudName.isValid(XDIAddress.create("#email")));
		assertFalse(CloudName.isValid(XDIAddress.create("$msg")));
		assertFalse(CloudName.isValid(XDIAddress.create("=")));
		assertFalse(CloudName.isValid(XDIAddress.create("=!1111")));
		assertFalse(CloudName.isValid(XDIAddress.create("+!2222")));
	}
}
