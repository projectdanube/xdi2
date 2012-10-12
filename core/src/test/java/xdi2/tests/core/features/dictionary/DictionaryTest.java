package xdi2.tests.core.features.dictionary;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class DictionaryTest extends TestCase {

	public void testTypes() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(new XRI3SubSegment("=markus"));

		XRI3Segment type1 = new XRI3Segment("+employee");
		XRI3Segment type2 = new XRI3Segment("+person");
		XRI3Segment type3 = new XRI3Segment("+developer");

		Dictionary.addContextNodeType(contextNode, type1);
		assertEquals(Dictionary.getContextNodeType(contextNode), type1);
		assertEquals(new IteratorCounter(Dictionary.getContextNodeTypes(contextNode)).count(), 1);
		assertTrue(new IteratorContains(Dictionary.getContextNodeTypes(contextNode), type1).contains());
		assertTrue(Dictionary.isContextNodeType(contextNode, type1));

		Dictionary.addContextNodeType(contextNode, type2);
		assertEquals(new IteratorCounter(Dictionary.getContextNodeTypes(contextNode)).count(), 2);
		assertTrue(new IteratorContains(Dictionary.getContextNodeTypes(contextNode), type1).contains());
		assertTrue(new IteratorContains(Dictionary.getContextNodeTypes(contextNode), type2).contains());
		assertTrue(Dictionary.isContextNodeType(contextNode, type1));
		assertTrue(Dictionary.isContextNodeType(contextNode, type2));

		Dictionary.addContextNodeType(contextNode, type3);
		assertEquals(new IteratorCounter(Dictionary.getContextNodeTypes(contextNode)).count(), 3);
		assertTrue(new IteratorContains(Dictionary.getContextNodeTypes(contextNode), type1).contains());
		assertTrue(new IteratorContains(Dictionary.getContextNodeTypes(contextNode), type2).contains());
		assertTrue(new IteratorContains(Dictionary.getContextNodeTypes(contextNode), type3).contains());
		assertTrue(Dictionary.isContextNodeType(contextNode, type1));
		assertTrue(Dictionary.isContextNodeType(contextNode, type2));
		assertTrue(Dictionary.isContextNodeType(contextNode, type3));

		Dictionary.removeContextNodeType(contextNode, type2);
		assertEquals(new IteratorCounter(Dictionary.getContextNodeTypes(contextNode)).count(), 2);
		assertTrue(new IteratorContains(Dictionary.getContextNodeTypes(contextNode), type1).contains());
		assertFalse(new IteratorContains(Dictionary.getContextNodeTypes(contextNode), type2).contains());
		assertTrue(new IteratorContains(Dictionary.getContextNodeTypes(contextNode), type3).contains());
		assertTrue(Dictionary.isContextNodeType(contextNode, type1));
		assertFalse(Dictionary.isContextNodeType(contextNode, type2));
		assertTrue(Dictionary.isContextNodeType(contextNode, type3));

		Dictionary.setContextNodeType(contextNode, type3);
		assertEquals(Dictionary.getContextNodeType(contextNode), type3);
		assertEquals(new IteratorCounter(Dictionary.getContextNodeTypes(contextNode)).count(), 1);
		assertFalse(new IteratorContains(Dictionary.getContextNodeTypes(contextNode), type1).contains());
		assertFalse(new IteratorContains(Dictionary.getContextNodeTypes(contextNode), type2).contains());
		assertTrue(new IteratorContains(Dictionary.getContextNodeTypes(contextNode), type3).contains());
		assertFalse(Dictionary.isContextNodeType(contextNode, type1));
		assertFalse(Dictionary.isContextNodeType(contextNode, type2));
		assertTrue(Dictionary.isContextNodeType(contextNode, type3));
	}
}
