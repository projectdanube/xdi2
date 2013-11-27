package xdi2.tests.core.xri3;

import junit.framework.TestCase;
import xdi2.core.xri3.CloudName;
import xdi2.core.xri3.XDI3Segment;

public class CloudNameTest extends TestCase {

	public void testCloudName() throws Exception {

		assertTrue(CloudName.isValid(XDI3Segment.create("=markus")));
		assertTrue(CloudName.isValid(XDI3Segment.create("=markus*web")));
		assertFalse(CloudName.isValid(XDI3Segment.create("[=]")));
		assertFalse(CloudName.isValid(XDI3Segment.create("[=]!1111")));
		assertFalse(CloudName.isValid(XDI3Segment.create("[=]!1111[=]!2222")));
		assertFalse(CloudName.isValid(XDI3Segment.create("[@]!1111")));
		assertFalse(CloudName.isValid(XDI3Segment.create("[@]!1111[@]!2222")));
	}
}
