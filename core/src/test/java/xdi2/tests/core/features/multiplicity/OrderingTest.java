package xdi2.tests.core.features.multiplicity;

import java.util.Iterator;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.features.multiplicity.XdiAttributeMember;
import xdi2.core.features.multiplicity.XdiCollection;
import xdi2.core.features.ordering.Ordering;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class OrderingTest extends TestCase {

	public void testOrderingArcXris() throws Exception {

		assertEquals(Ordering.indexToXri(1), XDI3SubSegment.create("$*1"));
		assertEquals(Ordering.indexToXri(2), XDI3SubSegment.create("$*2"));
		assertEquals(Ordering.xriToIndex(XDI3SubSegment.create("$*1")), 1);
		assertEquals(Ordering.xriToIndex(XDI3SubSegment.create("$*2")), 2);
	}

	public void testOrderedContextNodes() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		XDIReaderRegistry.getAuto().read(graph, this.getClass().getResourceAsStream("test-ordering.json"));

		ContextNode contextNode = graph.findContextNode(XDI3Segment.create("=!1111$(+tel)"), false);

		Iterator<ContextNode> iterator = Ordering.getOrderedContextNodes(contextNode);
		assertEquals(iterator.next().getLiteral().getLiteralData(), "+1.206.555.2222");
		assertEquals(iterator.next().getLiteral().getLiteralData(), "+1.206.555.4444");
		assertEquals(iterator.next().getLiteral().getLiteralData(), "+1.206.555.1111");
		assertEquals(iterator.next().getLiteral().getLiteralData(), "+1.206.555.3333");
		assertFalse(iterator.hasNext());

		XdiCollection xdiCollection = XdiCollection.fromContextNode(contextNode);

		Iterator<XdiAttributeMember> attributes = xdiCollection.attributes(true, true);
		assertEquals(attributes.next().getContextNode().getLiteral().getLiteralData(), "+1.206.555.2222");
		assertEquals(attributes.next().getContextNode().getLiteral().getLiteralData(), "+1.206.555.4444");
		assertEquals(attributes.next().getContextNode().getLiteral().getLiteralData(), "+1.206.555.1111");
		assertEquals(attributes.next().getContextNode().getLiteral().getLiteralData(), "+1.206.555.3333");
		assertFalse(attributes.hasNext());

		graph.close();
	}

	public void testOrderedRelations() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		XDIReaderRegistry.getAuto().read(graph, this.getClass().getResourceAsStream("test-ordering.json"));

		ContextNode contextNode1 = graph.findContextNode(XDI3Segment.create("=!1111"), false);

		Iterator<Relation> iterator = Ordering.getOrderedRelations(contextNode1, XDI3Segment.create("+friend"));
		assertEquals(iterator.next().follow().getXri(), XDI3Segment.create("=!2222"));
		assertEquals(iterator.next().follow().getXri(), XDI3Segment.create("=!4444"));
		assertEquals(iterator.next().follow().getXri(), XDI3Segment.create("=!5555"));
		assertEquals(iterator.next().follow().getXri(), XDI3Segment.create("=!3333"));
		assertFalse(iterator.hasNext());

		ContextNode contextNode2 = graph.findContextNode(XDI3Segment.create("=x"), false);

		Iterator<Relation> iterator2 = Ordering.getOrderedRelations(contextNode2);
		assertEquals(iterator2.next().follow().getXri(), XDI3Segment.create("=c"));
		assertEquals(iterator2.next().follow().getXri(), XDI3Segment.create("=g"));
		assertEquals(iterator2.next().follow().getXri(), XDI3Segment.create("=f"));
		assertEquals(iterator2.next().follow().getXri(), XDI3Segment.create("=d"));
		assertEquals(iterator2.next().follow().getXri(), XDI3Segment.create("=a"));
		assertEquals(iterator2.next().follow().getXri(), XDI3Segment.create("=h"));
		assertEquals(iterator2.next().follow().getXri(), XDI3Segment.create("=b"));
		assertEquals(iterator2.next().follow().getXri(), XDI3Segment.create("=e"));
		assertFalse(iterator2.hasNext());

		graph.close();
	}
}
