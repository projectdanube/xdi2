package xdi2.tests.core.features.dictionary;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.util.iterators.IteratorCounter;

public class DictionaryTest extends TestCase {

	public void testXDIArcs() throws Exception {

		assertEquals(Dictionary.instanceXDIArcToDictionaryXDIArc(XDIArc.create("#friend")), XDIArc.create("#(#friend)"));
		assertEquals(Dictionary.dictionaryXDIArcToInstanceXDIArc(XDIArc.create("#(#friend)")), XDIArc.create("#friend"));
		assertEquals(Dictionary.nativeIdentifierToInstanceXDIArc("user_name"), XDIArc.create("#(user_name)"));
		assertEquals(Dictionary.instanceXDIArcToNativeIdentifier(XDIArc.create("#(user_name)")), "user_name");
	}

	public void testTypes() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().setContextNode(XDIArc.create("=markus"));

		XDIAddress type1 = XDIAddress.create("#employee");
		XDIAddress type2 = XDIAddress.create("#person");
		XDIAddress type3 = XDIAddress.create("#developer");

		Dictionary.setContextNodeType(contextNode, type1);
		assertEquals(Dictionary.getContextNodeType(contextNode), type1);
		assertEquals(new IteratorCounter(Dictionary.getContextNodeTypes(contextNode)).count(), 1);
		assertTrue(new IteratorContains<XDIAddress>(Dictionary.getContextNodeTypes(contextNode), type1).contains());
		assertTrue(Dictionary.isContextNodeType(contextNode, type1));

		Dictionary.setContextNodeType(contextNode, type2);
		assertEquals(new IteratorCounter(Dictionary.getContextNodeTypes(contextNode)).count(), 2);
		assertTrue(new IteratorContains<XDIAddress>(Dictionary.getContextNodeTypes(contextNode), type1).contains());
		assertTrue(new IteratorContains<XDIAddress>(Dictionary.getContextNodeTypes(contextNode), type2).contains());
		assertTrue(Dictionary.isContextNodeType(contextNode, type1));
		assertTrue(Dictionary.isContextNodeType(contextNode, type2));

		Dictionary.setContextNodeType(contextNode, type3);
		assertEquals(new IteratorCounter(Dictionary.getContextNodeTypes(contextNode)).count(), 3);
		assertTrue(new IteratorContains<XDIAddress>(Dictionary.getContextNodeTypes(contextNode), type1).contains());
		assertTrue(new IteratorContains<XDIAddress>(Dictionary.getContextNodeTypes(contextNode), type2).contains());
		assertTrue(new IteratorContains<XDIAddress>(Dictionary.getContextNodeTypes(contextNode), type3).contains());
		assertTrue(Dictionary.isContextNodeType(contextNode, type1));
		assertTrue(Dictionary.isContextNodeType(contextNode, type2));
		assertTrue(Dictionary.isContextNodeType(contextNode, type3));

		Dictionary.delContextNodeType(contextNode, type2);
		assertEquals(new IteratorCounter(Dictionary.getContextNodeTypes(contextNode)).count(), 2);
		assertTrue(new IteratorContains<XDIAddress>(Dictionary.getContextNodeTypes(contextNode), type1).contains());
		assertFalse(new IteratorContains<XDIAddress>(Dictionary.getContextNodeTypes(contextNode), type2).contains());
		assertTrue(new IteratorContains<XDIAddress>(Dictionary.getContextNodeTypes(contextNode), type3).contains());
		assertTrue(Dictionary.isContextNodeType(contextNode, type1));
		assertFalse(Dictionary.isContextNodeType(contextNode, type2));
		assertTrue(Dictionary.isContextNodeType(contextNode, type3));

		Dictionary.replaceContextNodeType(contextNode, type3);
		assertEquals(Dictionary.getContextNodeType(contextNode), type3);
		assertEquals(new IteratorCounter(Dictionary.getContextNodeTypes(contextNode)).count(), 1);
		assertFalse(new IteratorContains<XDIAddress>(Dictionary.getContextNodeTypes(contextNode), type1).contains());
		assertFalse(new IteratorContains<XDIAddress>(Dictionary.getContextNodeTypes(contextNode), type2).contains());
		assertTrue(new IteratorContains<XDIAddress>(Dictionary.getContextNodeTypes(contextNode), type3).contains());
		assertFalse(Dictionary.isContextNodeType(contextNode, type1));
		assertFalse(Dictionary.isContextNodeType(contextNode, type2));
		assertTrue(Dictionary.isContextNodeType(contextNode, type3));
		
		graph.close();
	}
}
