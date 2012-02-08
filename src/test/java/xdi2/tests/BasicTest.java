package xdi2.tests;

import java.io.FileReader;

import junit.framework.TestCase;
import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.constants.XDIConstants;
import xdi2.impl.memory.MemoryGraphFactory;
import xdi2.io.XDIReader;
import xdi2.io.XDIReaderRegistry;
import xdi2.io.XDIWriter;
import xdi2.io.XDIWriterRegistry;
import xdi2.xri3.impl.XRI3Segment;
import xdi2.xri3.impl.XRI3SubSegment;

public class BasicTest extends TestCase {

	public void testBasic() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		//		reader.read(graph, new FileReader("test.json"), null);
		//		writer.write(graph, System.out, null);

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.createContextNode(new XRI3SubSegment("=abc"));
		ContextNode passportContextNode = abcContextNode.createContextNode(new XRI3SubSegment("+passport"));
		ContextNode oneContextNode = passportContextNode.createContextNode(new XRI3SubSegment("!1"));

		assertNull(rootContextNode.getArcXri());
		assertEquals(XDIConstants.XRI_CONTEXT, rootContextNode.getXri());
		assertEquals(new XRI3SubSegment("=abc"), abcContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc"), abcContextNode.getXri());
		assertEquals(new XRI3SubSegment("+passport"), passportContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport"), passportContextNode.getXri());
		assertEquals(new XRI3SubSegment("!1"), oneContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!1"), oneContextNode.getXri());
	}

	public void testJson() throws Exception {

		XDIReader reader = XDIReaderRegistry.forFormat("XDI/JSON");
		XDIWriter writer = XDIWriterRegistry.forFormat("XDI/JSON");

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		reader.read(graph, new FileReader("test.json"), null);
		writer.write(graph, System.out, null);
	}
}
