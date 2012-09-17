package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.impl.XRI3Segment;

public class XDIUtilTest extends TestCase {

	public void testDataXriAlternatives() throws Exception {

		XRI3Segment alternatives[] = new XRI3Segment[] {
				new XRI3Segment("(data:,+1%20206%20555%201212)"),
				new XRI3Segment("(data:,%2B1%20206%20555%201212)"),
				new XRI3Segment("(data:charset=utf-8,%2B1%20206%20555%201212)"),
				new XRI3Segment("(data:text/plain;charset=utf-8,%2B1%20206%20555%201212)"),
				new XRI3Segment("(data:text/plain;charset=utf-8;base64,KzEgMjA2IDU1NSAxMjEy)"),
				new XRI3Segment("(data:;base64,KzEgMjA2IDU1NSAxMjEy)")
		};

		for (XRI3Segment alternative : alternatives) {

			assertTrue(XDIUtil.isDataXriSegment(alternative));
			assertEquals("+1 206 555 1212", XDIUtil.dataXriSegmentToString(alternative));
		}
	}

	public void testDataXriConversion() throws Exception {

		XRI3Segment dataXriSegments[] = new XRI3Segment[] {
				new XRI3Segment("(data:,+1-206-555-1212)"),
				new XRI3Segment("(data:,33)"),
				new XRI3Segment("(data:,2010J10J10T11:12:13Z)"),
				new XRI3Segment("(data:,+1.206.555.1111)"),
				new XRI3Segment("(data:,Canada)"),
				new XRI3Segment("(data:,New%20Zealand)"),
				new XRI3Segment("(data:,987654321)")
		};

		String strings[] = new String[] {
				"+1-206-555-1212",
				"33",
				"2010J10J10T11:12:13Z",
				"+1.206.555.1111",
				"Canada",
				"New Zealand",
				"987654321"
		};

		assertEquals(dataXriSegments.length, strings.length);

		for (int i=0; i<dataXriSegments.length; i++) {

			assertTrue(XDIUtil.isDataXriSegment(dataXriSegments[i]));
			assertEquals(dataXriSegments[i], XDIUtil.stringToDataXriSegment(strings[i], false));
			assertEquals(strings[i], XDIUtil.dataXriSegmentToString(dataXriSegments[i]));
		}
	}
}
