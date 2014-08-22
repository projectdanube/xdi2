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

		assertEquals(XdiEntitySingleton.createarc(XDIArc.create("#address")), XDIArc.create("#address"));
		assertEquals(XdiAttributeSingleton.createarc(XDIArc.create("#address")), XDIArc.create("<#address>"));
		assertEquals(XdiEntityCollection.createarc(XDIArc.create("#address")), XDIArc.create("[#address]"));
		assertEquals(XdiAttributeCollection.createarc(XDIArc.create("#address")), XDIArc.create("[<#address>]"));
		assertEquals(XdiAbstractMemberUnordered.createarc("1", true), XDIArc.create("<!1>"));
		assertEquals(XdiAbstractMemberOrdered.createarc("1", true), XDIArc.create("<@1>"));
		assertEquals(XdiAbstractMemberUnordered.createarc("1", false), XDIArc.create("!1"));
		assertEquals(XdiAbstractMemberOrdered.createarc("1", false), XDIArc.create("@1"));

		assertTrue(XdiEntitySingleton.isValidarc(XdiEntitySingleton.createarc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeSingleton.isValidarc(XdiEntitySingleton.createarc(XDIArc.create("#address"))));
		assertFalse(XdiEntityCollection.isValidarc(XdiEntitySingleton.createarc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeCollection.isValidarc(XdiEntitySingleton.createarc(XDIArc.create("#address"))));

		assertFalse(XdiEntitySingleton.isValidarc(XdiAttributeSingleton.createarc(XDIArc.create("#address"))));
		assertTrue(XdiAttributeSingleton.isValidarc(XdiAttributeSingleton.createarc(XDIArc.create("#address"))));
		assertFalse(XdiEntityCollection.isValidarc(XdiAttributeSingleton.createarc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeCollection.isValidarc(XdiAttributeSingleton.createarc(XDIArc.create("#address"))));

		assertFalse(XdiEntitySingleton.isValidarc(XdiEntityCollection.createarc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeSingleton.isValidarc(XdiEntityCollection.createarc(XDIArc.create("#address"))));
		assertTrue(XdiEntityCollection.isValidarc(XdiEntityCollection.createarc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeCollection.isValidarc(XdiEntityCollection.createarc(XDIArc.create("#address"))));

		assertFalse(XdiEntitySingleton.isValidarc(XdiAttributeCollection.createarc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeSingleton.isValidarc(XdiAttributeCollection.createarc(XDIArc.create("#address"))));
		assertFalse(XdiEntityCollection.isValidarc(XdiAttributeCollection.createarc(XDIArc.create("#address"))));
		assertTrue(XdiAttributeCollection.isValidarc(XdiAttributeCollection.createarc(XDIArc.create("#address"))));

		assertEquals(XdiAbstractContext.getBasearc(XdiEntitySingleton.createarc(XDIArc.create("#address"))), XDIArc.create("#address"));
		assertEquals(XdiAbstractContext.getBasearc(XdiAttributeSingleton.createarc(XDIArc.create("#address"))), XDIArc.create("#address"));
		assertEquals(XdiAbstractContext.getBasearc(XdiEntityCollection.createarc(XDIArc.create("#address"))), XDIArc.create("#address"));
		assertEquals(XdiAbstractContext.getBasearc(XdiAttributeCollection.createarc(XDIArc.create("#address"))), XDIArc.create("#address"));
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
