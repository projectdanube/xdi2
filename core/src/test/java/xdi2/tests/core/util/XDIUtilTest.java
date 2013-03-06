package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.XDI3Segment;

public class XDIUtilTest extends TestCase {

	public void testDataXriAlternatives() throws Exception {

		XDI3Segment alternatives[] = new XDI3Segment[] {
				XDI3Segment.create("(data:,+1%20206%20555%201212)"),
				XDI3Segment.create("(data:,%2B1%20206%20555%201212)"),
				XDI3Segment.create("(data:charset=utf-8,%2B1%20206%20555%201212)"),
				XDI3Segment.create("(data:text/plain;charset=utf-8,%2B1%20206%20555%201212)"),
				XDI3Segment.create("(data:text/plain;charset=utf-8;base64,KzEgMjA2IDU1NSAxMjEy)"),
				XDI3Segment.create("(data:;base64,KzEgMjA2IDU1NSAxMjEy)")
		};

		for (XDI3Segment alternative : alternatives) {

			assertTrue(XDIUtil.isLiteralSegment(alternative));
			assertEquals("+1 206 555 1212", XDIUtil.literalSegmentToString(alternative));
		}
	}

	public void testDataXriConversion() throws Exception {

		XDI3Segment dataXriSegments[] = new XDI3Segment[] {
				XDI3Segment.create("(data:,+1-206-555-1212)"),
				XDI3Segment.create("(data:,33)"),
				XDI3Segment.create("(data:,2010J10J10T11:12:13Z)"),
				XDI3Segment.create("(data:,+1.206.555.1111)"),
				XDI3Segment.create("(data:,Canada)"),
				XDI3Segment.create("(data:,New%20Zealand)"),
				XDI3Segment.create("(data:,987654321)")
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

			assertTrue(XDIUtil.isLiteralSegment(dataXriSegments[i]));
			assertEquals(dataXriSegments[i], XDIUtil.stringToLiteralSegment(strings[i], false));
			assertEquals(strings[i], XDIUtil.literalSegmentToString(dataXriSegments[i]));
		}
	}
}
