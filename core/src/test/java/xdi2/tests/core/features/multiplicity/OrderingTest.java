package xdi2.tests.core.features.multiplicity;

import java.util.Iterator;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.multiplicity.Ordering;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class OrderingTest extends TestCase {

	public void testOrderingArcXris() throws Exception {

		assertEquals(Ordering.indexArcXri(1), XDI3SubSegment.create("$*1"));
		assertEquals(Ordering.indexArcXri(2), XDI3SubSegment.create("$*2"));
		assertEquals(Ordering.arcXriIndex(XDI3SubSegment.create("$*1")), 1);
		assertEquals(Ordering.arcXriIndex(XDI3SubSegment.create("$*2")), 2);
	}

	public void testOrderedContextNodes() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		XDIReaderRegistry.getAuto().read(graph, this.getClass().getResourceAsStream("test-ordering.json"));

		ContextNode tel = graph.findContextNode(XDI3Segment.create("=!1111$*(+tel)"), false);

		Iterator<ContextNode> iterator = Ordering.getOrderedContextNodes(tel);
		assertEquals(iterator.next().getLiteral().getLiteralData(), "+1.206.555.2222");
		assertEquals(iterator.next().getLiteral().getLiteralData(), "+1.206.555.4444");
		assertEquals(iterator.next().getLiteral().getLiteralData(), "+1.206.555.1111");
		assertEquals(iterator.next().getLiteral().getLiteralData(), "+1.206.555.3333");

		graph.close();
	}
}
