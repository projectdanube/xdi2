package xdi2.tests.core.features.timestamps;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.timestamps.Timestamps;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.XDI3SubSegment;

public class TimestampsTest extends TestCase {

	public void testTimestamps() throws Exception {

		GregorianCalendar calendar = new GregorianCalendar(2010, 11, 22, 11, 22, 33);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date timestamp = calendar.getTime();

		String string = "2010-12-22T11:22:33Z";

		assertEquals(timestamp, Timestamps.stringToTimestamp(string));
		assertEquals(timestamp, Timestamps.stringToTimestamp(Timestamps.timestampToString(timestamp)));
		assertEquals(string, Timestamps.timestampToString(timestamp));
		assertEquals(string, Timestamps.timestampToString(Timestamps.stringToTimestamp(string)));
	}

	public void testTimestampsOnContextNode() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));

		GregorianCalendar calendar = new GregorianCalendar(2010, 11, 22, 11, 22, 33);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date timestamp = calendar.getTime();

		Timestamps.setContextNodeTimestamp(contextNode, timestamp);
		assertEquals(timestamp, Timestamps.getContextNodeTimestamp(contextNode));
	}
}
