package xdi2.tests.core.features.nodetypes;

import junit.framework.TestCase;
import xdi2.core.features.nodetypes.XdiAbstractInstanceOrdered;
import xdi2.core.features.nodetypes.XdiAbstractInstanceUnordered;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttributeClass;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiEntityClass;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.xri3.XDI3SubSegment;

public class ContextFunctionsTest extends TestCase {

	public void testArcXris() throws Exception {

		assertEquals(XdiEntitySingleton.createArcXri(XDI3SubSegment.create("+address")), XDI3SubSegment.create("+address"));
		assertEquals(XdiAttributeSingleton.createArcXri(XDI3SubSegment.create("+address")), XDI3SubSegment.create("<+address>"));
		assertEquals(XdiEntityClass.createArcXri(XDI3SubSegment.create("+address")), XDI3SubSegment.create("[+address]"));
		assertEquals(XdiAttributeClass.createArcXri(XDI3SubSegment.create("+address")), XDI3SubSegment.create("[<+address>]"));
		assertEquals(XdiAbstractInstanceUnordered.createArcXri("1", true), XDI3SubSegment.create("<!1>"));
		assertEquals(XdiAbstractInstanceOrdered.createArcXri("1", true), XDI3SubSegment.create("<#1>"));
		assertEquals(XdiAbstractInstanceUnordered.createArcXri("1", false), XDI3SubSegment.create("!1"));
		assertEquals(XdiAbstractInstanceOrdered.createArcXri("1", false), XDI3SubSegment.create("#1"));

		assertTrue(XdiEntitySingleton.isValidArcXri(XdiEntitySingleton.createArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(XdiAttributeSingleton.isValidArcXri(XdiEntitySingleton.createArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(XdiEntityClass.isValidArcXri(XdiEntitySingleton.createArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(XdiAttributeClass.isValidArcXri(XdiEntitySingleton.createArcXri(XDI3SubSegment.create("+address"))));

		assertFalse(XdiEntitySingleton.isValidArcXri(XdiAttributeSingleton.createArcXri(XDI3SubSegment.create("+address"))));
		assertTrue(XdiAttributeSingleton.isValidArcXri(XdiAttributeSingleton.createArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(XdiEntityClass.isValidArcXri(XdiAttributeSingleton.createArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(XdiAttributeClass.isValidArcXri(XdiAttributeSingleton.createArcXri(XDI3SubSegment.create("+address"))));

		assertFalse(XdiEntitySingleton.isValidArcXri(XdiEntityClass.createArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(XdiAttributeSingleton.isValidArcXri(XdiEntityClass.createArcXri(XDI3SubSegment.create("+address"))));
		assertTrue(XdiEntityClass.isValidArcXri(XdiEntityClass.createArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(XdiAttributeClass.isValidArcXri(XdiEntityClass.createArcXri(XDI3SubSegment.create("+address"))));

		assertFalse(XdiEntitySingleton.isValidArcXri(XdiAttributeClass.createArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(XdiAttributeSingleton.isValidArcXri(XdiAttributeClass.createArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(XdiEntityClass.isValidArcXri(XdiAttributeClass.createArcXri(XDI3SubSegment.create("+address"))));
		assertTrue(XdiAttributeClass.isValidArcXri(XdiAttributeClass.createArcXri(XDI3SubSegment.create("+address"))));

		assertEquals(XdiAbstractContext.getBaseArcXri(XdiEntitySingleton.createArcXri(XDI3SubSegment.create("+address"))), XDI3SubSegment.create("+address"));
		assertEquals(XdiAbstractContext.getBaseArcXri(XdiAttributeSingleton.createArcXri(XDI3SubSegment.create("+address"))), XDI3SubSegment.create("+address"));
		assertEquals(XdiAbstractContext.getBaseArcXri(XdiEntityClass.createArcXri(XDI3SubSegment.create("+address"))), XDI3SubSegment.create("+address"));
		assertEquals(XdiAbstractContext.getBaseArcXri(XdiAttributeClass.createArcXri(XDI3SubSegment.create("+address"))), XDI3SubSegment.create("+address"));
	}

/*	public void testContextNodes() throws Exception {	

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));

		assertTrue(XdiSubGraph.fromContextNode(contextNode) instanceof XdiCollection);

		XdiCollection printerEntitySingleton = XdiSubGraph.fromContextNode(contextNode).getEntitySingleton(XDI3SubSegment.create("+printer"), true);
		XdiValue telAttributeSingleton = XdiSubGraph.fromContextNode(contextNode).getAttributeSingleton(XDI3SubSegment.create("+tel"), true);
		XdiEntityCollection printerCollection = XdiSubGraph.fromContextNode(contextNode).getEntityCollection(XDI3SubSegment.create("+printer"), true);
		XdiAttributeCollection telCollection = XdiSubGraph.fromContextNode(contextNode).getAttributeCollection(XDI3SubSegment.create("+tel"), true);

		assertTrue(ContextFunctions.isMemberArcXri(printerEntitySingleton.getContextNode().getArcXri()));
		assertTrue(ContextFunctions.isAttributeSingletonArcXri(telAttributeSingleton.getContextNode().getArcXri()));
		assertTrue(ContextFunctions.isEntityCollectionArcXri(printerCollection.getContextNode().getArcXri()));
		assertTrue(ContextFunctions.isAttributeCollectionArcXri(printerCollection.getContextNode().getArcXri()));

		ContextNode printer1ContextNode = printerCollection.createMember().getContextNode();
		ContextNode printer2ContextNode = printerCollection.createMember().getContextNode();
		ContextNode tel1ContextNode = telCollection.createMember().getContextNode();
		ContextNode tel2ContextNode = telCollection.createMember().getContextNode();

		assertFalse(Multiplicity.isEntityMemberArcXri(tel1ContextNode.getArcXri()));
		assertFalse(Multiplicity.isEntityMemberArcXri(tel2ContextNode.getArcXri()));
		assertTrue(ContextFunctions.isMemberArcXri(tel1ContextNode.getArcXri()));
		assertTrue(ContextFunctions.isMemberArcXri(tel2ContextNode.getArcXri()));

		assertTrue(ContextFunctions.isMemberArcXri(printer1ContextNode.getArcXri()));
		assertTrue(ContextFunctions.isMemberArcXri(printer2ContextNode.getArcXri()));
		assertFalse(Multiplicity.isAttributeMemberArcXri(passport1ContextNode.getArcXri()));
		assertFalse(Multiplicity.isAttributeMemberArcXri(passport2ContextNode.getArcXri()));

		assertEquals(printerCollection.membersSize(), 2);
		assertTrue(new IteratorContains<XdiElement>(printerCollection.members(), XdiElement.fromContextNode(printer1ContextNode)).contains());
		assertTrue(new IteratorContains<XdiElement>(printerCollection.members(), XdiElement.fromContextNode(printer2ContextNode)).contains());
		assertEquals(telCollection.membersSize(), 2);
		assertTrue(new IteratorContains<XdiAttributeMember>(telCollection.members(), XdiAttributeMember.fromContextNode(tel1ContextNode)).contains());
		assertTrue(new IteratorContains<XdiAttributeMember>(telCollection.members(), XdiAttributeMember.fromContextNode(tel2ContextNode)).contains());
	}*/
}
