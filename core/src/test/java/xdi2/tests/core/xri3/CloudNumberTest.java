package xdi2.tests.core.xri3;

import junit.framework.TestCase;
import xdi2.core.constants.XDIConstants;
import xdi2.core.xri3.CloudNumber;
import xdi2.core.xri3.XDI3Segment;

public class CloudNumberTest extends TestCase {

	public void testCloudNumber() throws Exception {

		assertFalse(CloudNumber.isValid(XDI3Segment.create("=markus")));
		assertFalse(CloudNumber.isValid(XDI3Segment.create("=markus*web")));
		assertFalse(CloudNumber.isValid(XDI3Segment.create("[=]")));
		assertTrue(CloudNumber.isValid(XDI3Segment.create("[=]!1111")));
		assertTrue(CloudNumber.isValid(XDI3Segment.create("[=]!1111[=]!2222")));
		assertTrue(CloudNumber.isValid(XDI3Segment.create("[@]!1111")));
		assertTrue(CloudNumber.isValid(XDI3Segment.create("[@]!1111[@]!2222")));

		assertTrue(CloudNumber.isValid(CloudNumber.createRandom(XDIConstants.CS_EQUALS).getXri()));
		assertTrue(CloudNumber.isValid(CloudNumber.createRandom(XDIConstants.CS_AT).getXri()));
	}
}
