package xdi2.tests.util;

import junit.framework.TestCase;
import xdi2.util.XDIUtil;
import xdi2.xri3.impl.XRI3Segment;

public class XDIUtilTest extends TestCase {

	public void testVariables() throws Exception {

		XRI3Segment dataXriSegments[] = new XRI3Segment [] {
				new XRI3Segment("(data:,+1-206-555-1212)"),
				new XRI3Segment("(data:,33)"),
				new XRI3Segment("(data:,2010J10J10T11:12:13Z)"),
				new XRI3Segment("(data:,+1.206.555.1111)"),
				new XRI3Segment("(data:,Canada)"),
				new XRI3Segment("(data:,987654321)")
		};

		String strings[] = new String [] {
				"+1-206-555-1212",
				"33",
				"2010J10J10T11:12:13Z",
				"+1.206.555.1111",
				"Canada",
				"987654321"
		};

		assertEquals(dataXriSegments.length, strings.length);

		for (int i=0; i<dataXriSegments.length; i++) {

			assertEquals(dataXriSegments[i], XDIUtil.stringToDataXriSegment(strings[i]));
			assertEquals(strings[i], XDIUtil.dataXriSegmentToString(dataXriSegments[i]));
		}
	}
}
