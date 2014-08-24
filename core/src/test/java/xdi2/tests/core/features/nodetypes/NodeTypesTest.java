package xdi2.tests.core.features.nodetypes;

import junit.framework.TestCase;
import xdi2.core.features.nodetypes.XdiAbstractMemberOrdered;
import xdi2.core.features.nodetypes.XdiAbstractMemberUnordered;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.syntax.XDIArc;

public class NodeTypesTest extends TestCase {

	public void testarcs() throws Exception {

		assertEquals(XdiEntitySingleton.createXDIArc(XDIArc.create("#address")), XDIArc.create("#address"));
		assertEquals(XdiAttributeSingleton.createXDIArc(XDIArc.create("#address")), XDIArc.create("<#address>"));
		assertEquals(XdiEntityCollection.createXDIArc(XDIArc.create("#address")), XDIArc.create("[#address]"));
		assertEquals(XdiAttributeCollection.createXDIArc(XDIArc.create("#address")), XDIArc.create("[<#address>]"));
		assertEquals(XdiAbstractMemberUnordered.createXDIArc("1", XdiAttributeCollection.class), XDIArc.create("<!1>"));
		assertEquals(XdiAbstractMemberOrdered.createXDIArc("1", XdiAttributeCollection.class), XDIArc.create("<@1>"));
		assertEquals(XdiAbstractMemberUnordered.createXDIArc("1", XdiEntityCollection.class), XDIArc.create("!1"));
		assertEquals(XdiAbstractMemberOrdered.createXDIArc("1", XdiEntityCollection.class), XDIArc.create("@1"));

		assertTrue(XdiEntitySingleton.isValidXDIArc(XdiEntitySingleton.createXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeSingleton.isValidXDIArc(XdiEntitySingleton.createXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiEntityCollection.isValidXDIArc(XdiEntitySingleton.createXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeCollection.isValidXDIArc(XdiEntitySingleton.createXDIArc(XDIArc.create("#address"))));

		assertFalse(XdiEntitySingleton.isValidXDIArc(XdiAttributeSingleton.createXDIArc(XDIArc.create("#address"))));
		assertTrue(XdiAttributeSingleton.isValidXDIArc(XdiAttributeSingleton.createXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiEntityCollection.isValidXDIArc(XdiAttributeSingleton.createXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeCollection.isValidXDIArc(XdiAttributeSingleton.createXDIArc(XDIArc.create("#address"))));

		assertFalse(XdiEntitySingleton.isValidXDIArc(XdiEntityCollection.createXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeSingleton.isValidXDIArc(XdiEntityCollection.createXDIArc(XDIArc.create("#address"))));
		assertTrue(XdiEntityCollection.isValidXDIArc(XdiEntityCollection.createXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeCollection.isValidXDIArc(XdiEntityCollection.createXDIArc(XDIArc.create("#address"))));

		assertFalse(XdiEntitySingleton.isValidXDIArc(XdiAttributeCollection.createXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeSingleton.isValidXDIArc(XdiAttributeCollection.createXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiEntityCollection.isValidXDIArc(XdiAttributeCollection.createXDIArc(XDIArc.create("#address"))));
		assertTrue(XdiAttributeCollection.isValidXDIArc(XdiAttributeCollection.createXDIArc(XDIArc.create("#address"))));

		assertEquals(XdiAbstractContext.getBasearc(XdiEntitySingleton.createXDIArc(XDIArc.create("#address"))), XDIArc.create("#address"));
		assertEquals(XdiAbstractContext.getBasearc(XdiAttributeSingleton.createXDIArc(XDIArc.create("#address"))), XDIArc.create("#address"));
		assertEquals(XdiAbstractContext.getBasearc(XdiEntityCollection.createXDIArc(XDIArc.create("#address"))), XDIArc.create("#address"));
		assertEquals(XdiAbstractContext.getBasearc(XdiAttributeCollection.createXDIArc(XDIArc.create("#address"))), XDIArc.create("#address"));
	}

/*	public void testContextNodes() throws Exception {	

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(XDIArc.create("=markus"));

		assertTrue(XdiSubGraph.fromContextNode(contextNode) instanceof XdiCollection);

		XdiCollection printerEntitySingleton = XdiSubGraph.fromContextNode(contextNode).getEntitySingleton(XDIArc.create("+printer"), true);
		XdiValue telAttributeSingleton = XdiSubGraph.fromContextNode(contextNode).getAttributeSingleton(XDIArc.create("+tel"), true);
		XdiEntityCollection printerCollection = XdiSubGraph.fromContextNode(contextNode).getEntityCollection(XDIArc.create("+printer"), true);
		XdiAttributeCollection telCollection = XdiSubGraph.fromContextNode(contextNode).getAttributeCollection(XDIArc.create("+tel"), true);

		assertTrue(ContextFunctions.isMemberarc(printerEntitySingleton.getContextNode().getArc()));
		assertTrue(ContextFunctions.isAttributeSingletonarc(telAttributeSingleton.getContextNode().getArc()));
		assertTrue(ContextFunctions.isEntityCollectionarc(printerCollection.getContextNode().getArc()));
		assertTrue(ContextFunctions.isAttributeCollectionarc(printerCollection.getContextNode().getArc()));

		ContextNode printer1ContextNode = printerCollection.createMember().getContextNode();
		ContextNode printer2ContextNode = printerCollection.createMember().getContextNode();
		ContextNode tel1ContextNode = telCollection.createMember().getContextNode();
		ContextNode tel2ContextNode = telCollection.createMember().getContextNode();

		assertFalse(Multiplicity.isEntityMemberarc(tel1ContextNode.getArc()));
		assertFalse(Multiplicity.isEntityMemberarc(tel2ContextNode.getArc()));
		assertTrue(ContextFunctions.isMemberarc(tel1ContextNode.getArc()));
		assertTrue(ContextFunctions.isMemberarc(tel2ContextNode.getArc()));

		assertTrue(ContextFunctions.isMemberarc(printer1ContextNode.getArc()));
		assertTrue(ContextFunctions.isMemberarc(printer2ContextNode.getArc()));
		assertFalse(Multiplicity.isAttributeMemberarc(passport1ContextNode.getArc()));
		assertFalse(Multiplicity.isAttributeMemberarc(passport2ContextNode.getArc()));

		assertEquals(printerCollection.membersSize(), 2);
		assertTrue(new IteratorContains<XdiElement>(printerCollection.members(), XdiElement.fromContextNode(printer1ContextNode)).contains());
		assertTrue(new IteratorContains<XdiElement>(printerCollection.members(), XdiElement.fromContextNode(printer2ContextNode)).contains());
		assertEquals(telCollection.membersSize(), 2);
		assertTrue(new IteratorContains<XdiAttributeMember>(telCollection.members(), XdiAttributeMember.fromContextNode(tel1ContextNode)).contains());
		assertTrue(new IteratorContains<XdiAttributeMember>(telCollection.members(), XdiAttributeMember.fromContextNode(tel2ContextNode)).contains());
	}*/
}
