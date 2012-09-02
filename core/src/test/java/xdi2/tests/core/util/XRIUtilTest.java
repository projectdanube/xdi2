package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.impl.XRI3Segment;

public class XRIUtilTest extends TestCase {

	public void testXRIUtil() throws Exception {
		
		XRI3Segment xri = new XRI3Segment("=a*b*c*d");

		assertEquals(XRIUtil.parentXri(xri), new XRI3Segment("=a*b*c"));

		assertEquals(XRIUtil.localXri(xri), new XRI3Segment("*d"));
	
		assertTrue(XRIUtil.startsWith(xri, new XRI3Segment("=a")));
		assertTrue(XRIUtil.startsWith(xri, new XRI3Segment("=a*b")));
		assertTrue(XRIUtil.startsWith(xri, new XRI3Segment("=a*b*c")));
		assertTrue(XRIUtil.startsWith(xri, new XRI3Segment("=a*b*c*d")));
	}
}
