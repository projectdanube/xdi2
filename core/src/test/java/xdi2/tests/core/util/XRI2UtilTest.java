package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.util.XRI2Util;
import xdi2.core.xri3.XDI3Segment;

public class XRI2UtilTest extends TestCase {

	public void testCanonicalIdToCloudnumber() throws Exception {

		assertEquals(XRI2Util.canonicalIdToCloudNumber("=!91F2.8153.F600.AE24"), "[=]!:uuid:91f28153-f600-ae24-91f2-8153f600ae24");
		assertEquals(XRI2Util.canonicalIdToCloudNumber("@!F83.62B1.044F.2813"), "[@]!:uuid:0f8362b1-044f-2813-0f83-62b1044f2813");
		assertNull(XRI2Util.canonicalIdToCloudNumber("=!91F2.8153.F600"));
	}

	public void testCloudNumberToCanonicalId() throws Exception {

		assertEquals(XRI2Util.cloudNumberToCanonicalId(XDI3Segment.create("[=]!:uuid:91f28153-f600-ae24-91f2-8153f600ae24")), "=!91F2.8153.F600.AE24");
		assertEquals(XRI2Util.cloudNumberToCanonicalId(XDI3Segment.create("[@]!:uuid:0f8362b1-044f-2813-0f83-62b1044f2813")), "@!F83.62B1.44F.2813");
		assertNull(XRI2Util.cloudNumberToCanonicalId(XDI3Segment.create("[@]!:uuid:0f8362b1-044f")));
	}

	public void testTypeToXdiEntitySingletonArcXri() throws Exception {

		assertEquals(XRI2Util.typeToXdiEntitySingletonArcXri("xri://+i-service*(+contact)*($v*1.0)"), "(+i-service*(+contact)*($v*1.0))");
		assertEquals(XRI2Util.typeToXdiEntitySingletonArcXri("http://openid.net/signon/1.0"), "(http://openid.net/signon/1.0)");
		assertEquals(XRI2Util.typeToXdiEntitySingletonArcXri("xri://$xdi"), "$xdi");
		assertEquals(XRI2Util.typeToXdiEntitySingletonArcXri("describedby"), "(describedby)");
	}
}
