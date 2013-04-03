package xdi2.tests.core.features.multiplicity;

import junit.framework.TestCase;
import xdi2.core.features.contextfunctions.XdiAttributeInstance;
import xdi2.core.features.contextfunctions.XdiAttributeSingleton;
import xdi2.core.features.contextfunctions.XdiCollection;
import xdi2.core.features.contextfunctions.XdiEntityInstance;
import xdi2.core.features.contextfunctions.XdiEntitySingleton;
import xdi2.core.features.contextfunctions.XdiSubGraph;
import xdi2.core.xri3.XDI3SubSegment;

public class MultiplicityTest extends TestCase {

	public void testArcXris() throws Exception {

		assertEquals(XdiEntitySingleton.createEntitySingletonArcXri(XDI3SubSegment.create("+printer")), XDI3SubSegment.create("+printer"));
		assertEquals(XdiAttributeSingleton.createAttributeSingletonArcXri(XDI3SubSegment.create("+email")), XDI3SubSegment.create("<+email>"));
		assertEquals(XdiCollection.createCollectionArcXri(XDI3SubSegment.create("+address")), XDI3SubSegment.create("[+address]"));
		assertEquals(XdiEntityInstance.createEntityMemberArcXri(XDI3SubSegment.create("!1")), XDI3SubSegment.create("!1"));
		assertEquals(XdiAttributeInstance.createAttributeMemberArcXri(XDI3SubSegment.create("!1")), XDI3SubSegment.create("<!1>"));

		assertTrue(XdiEntitySingleton.isValidArcXri(XdiEntitySingleton.createEntitySingletonArcXri(XDI3SubSegment.create("+printer"))));
		assertFalse(XdiAttributeSingleton.isValidArcXri(XdiEntitySingleton.createEntitySingletonArcXri(XDI3SubSegment.create("+email"))));
		assertFalse(XdiCollection.isCollectionArcXri(XdiEntitySingleton.createEntitySingletonArcXri(XDI3SubSegment.create("+address"))));

		assertFalse(XdiEntitySingleton.isValidArcXri(XdiAttributeSingleton.createAttributeSingletonArcXri(XDI3SubSegment.create("+printer"))));
		assertTrue(XdiAttributeSingleton.isValidArcXri(XdiAttributeSingleton.createAttributeSingletonArcXri(XDI3SubSegment.create("+email"))));
		assertFalse(XdiCollection.isCollectionArcXri(XdiAttributeSingleton.createAttributeSingletonArcXri(XDI3SubSegment.create("+address"))));

		assertFalse(XdiEntitySingleton.isValidArcXri(XdiCollection.createCollectionArcXri(XDI3SubSegment.create("+printer"))));
		assertFalse(XdiAttributeSingleton.isValidArcXri(XdiCollection.createCollectionArcXri(XDI3SubSegment.create("+email"))));
		assertTrue(XdiCollection.isCollectionArcXri(XdiCollection.createCollectionArcXri(XDI3SubSegment.create("+address"))));

		assertEquals(XdiSubGraph.getBaseArcXri(XDI3SubSegment.create("+printer")), XDI3SubSegment.create("+printer"));
		assertEquals(XdiSubGraph.getBaseArcXri(XDI3SubSegment.create("<+email>")), XDI3SubSegment.create("+email"));
		assertEquals(XdiSubGraph.getBaseArcXri(XDI3SubSegment.create("[+address]")), XDI3SubSegment.create("+address"));
		assertEquals(XdiSubGraph.getBaseArcXri(XDI3SubSegment.create("!1")), XDI3SubSegment.create("!1"));
		assertEquals(XdiSubGraph.getBaseArcXri(XDI3SubSegment.create("<!1>")), XDI3SubSegment.create("!1"));
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
