package xdi2.tests.core.features.multiplicity;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.contextfunctions.XdiAttributeCollection;
import xdi2.core.features.contextfunctions.XdiAttributeMember;
import xdi2.core.features.contextfunctions.XdiElement;
import xdi2.core.features.contextfunctions.XdiEntityCollection;
import xdi2.core.features.contextfunctions.XdiMember;
import xdi2.core.features.contextfunctions.XdiSubGraph;
import xdi2.core.features.contextfunctions.XdiValue;
import xdi2.core.features.roots.XdiPeerRoot;
import xdi2.core.features.roots.Roots;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class MultiplicityTest extends TestCase {

	public void testArcXris() throws Exception {

		XdiPeerRoot.
		
		assertEquals(ContextFunctions.entitySingletonArcXri(XDI3SubSegment.create("+address")), XDI3SubSegment.create("+address"));
		assertEquals(ContextFunctions.attributeSingletonArcXri(XDI3SubSegment.create("+tel")), XDI3SubSegment.create("<+tel>"));
		assertEquals(ContextFunctions.entityCollectionArcXri(XDI3SubSegment.create("+address")), XDI3SubSegment.create("{+address}"));
		assertEquals(ContextFunctions.attributeCollectionArcXri(XDI3SubSegment.create("+tel")), XDI3SubSegment.create("{<+tel>}"));

		assertTrue(ContextFunctions.isMemberArcXri(ContextFunctions.entitySingletonArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(ContextFunctions.isAttributeSingletonArcXri(ContextFunctions.entitySingletonArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(ContextFunctions.isEntityCollectionArcXri(ContextFunctions.entitySingletonArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(ContextFunctions.isAttributeCollectionArcXri(ContextFunctions.entitySingletonArcXri(XDI3SubSegment.create("+address"))));

		assertFalse(ContextFunctions.isMemberArcXri(ContextFunctions.attributeSingletonArcXri(XDI3SubSegment.create("+tel"))));
		assertTrue(ContextFunctions.isAttributeSingletonArcXri(ContextFunctions.attributeSingletonArcXri(XDI3SubSegment.create("+tel"))));
		assertFalse(ContextFunctions.isEntityCollectionArcXri(ContextFunctions.attributeSingletonArcXri(XDI3SubSegment.create("+tel"))));
		assertFalse(ContextFunctions.isAttributeCollectionArcXri(ContextFunctions.attributeSingletonArcXri(XDI3SubSegment.create("+tel"))));

		assertFalse(ContextFunctions.isMemberArcXri(ContextFunctions.entityCollectionArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(ContextFunctions.isAttributeSingletonArcXri(ContextFunctions.entityCollectionArcXri(XDI3SubSegment.create("+address"))));
		assertTrue(ContextFunctions.isEntityCollectionArcXri(ContextFunctions.entityCollectionArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(ContextFunctions.isAttributeCollectionArcXri(ContextFunctions.entityCollectionArcXri(XDI3SubSegment.create("+address"))));

		assertFalse(ContextFunctions.isMemberArcXri(ContextFunctions.attributeCollectionArcXri(XDI3SubSegment.create("+tel"))));
		assertFalse(ContextFunctions.isAttributeSingletonArcXri(ContextFunctions.attributeCollectionArcXri(XDI3SubSegment.create("+tel"))));
		assertFalse(ContextFunctions.isEntityCollectionArcXri(ContextFunctions.attributeCollectionArcXri(XDI3SubSegment.create("+tel"))));
		assertTrue(ContextFunctions.isAttributeCollectionArcXri(ContextFunctions.attributeCollectionArcXri(XDI3SubSegment.create("+tel"))));

		assertTrue(ContextFunctions.isMemberArcXri(ContextFunctions.memberArcXri(XDI3SubSegment.create("!1"))));

		assertEquals(ContextFunctions.baseArcXri(XDI3SubSegment.create("+tel")), XDI3SubSegment.create("+tel"));
		assertEquals(ContextFunctions.baseArcXri(XDI3SubSegment.create("<+address>")), XDI3SubSegment.create("+address"));
		assertEquals(ContextFunctions.baseArcXri(XDI3SubSegment.create("{+tel}")), XDI3SubSegment.create("+tel"));
		assertEquals(ContextFunctions.baseArcXri(XDI3SubSegment.create("{<+address>}")), XDI3SubSegment.create("+address"));
	}

	public void testSubGraph() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode root = graph.getRootContextNode();
		ContextNode markus = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));
		XdiPeerRoot peerRoot = Roots.findLocalRoot(graph).findPeerRoot(XDI3Segment.create("=!91F2.8153.F600.AE24"), true);

		assertNotNull(XdiSubGraph.fromContextNode(root));
		assertNotNull(XdiSubGraph.fromContextNode(markus));
		assertNotNull(XdiSubGraph.fromContextNode(peerRoot.getContextNode()));
	}

	public void testContextNodes() throws Exception {	

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));

		assertTrue(XdiSubGraph.fromContextNode(contextNode) instanceof XdiMember);

		XdiMember addressEntitySingleton = XdiSubGraph.fromContextNode(contextNode).getEntitySingleton(XDI3SubSegment.create("+address"), true);
		XdiValue telAttributeSingleton = XdiSubGraph.fromContextNode(contextNode).getAttributeSingleton(XDI3SubSegment.create("+tel"), true);
		XdiEntityCollection addressCollection = XdiSubGraph.fromContextNode(contextNode).getEntityCollection(XDI3SubSegment.create("+address"), true);
		XdiAttributeCollection telCollection = XdiSubGraph.fromContextNode(contextNode).getAttributeCollection(XDI3SubSegment.create("+tel"), true);

		assertTrue(ContextFunctions.isMemberArcXri(addressEntitySingleton.getContextNode().getArcXri()));
		assertTrue(ContextFunctions.isAttributeSingletonArcXri(telAttributeSingleton.getContextNode().getArcXri()));
		assertTrue(ContextFunctions.isEntityCollectionArcXri(addressCollection.getContextNode().getArcXri()));
		assertTrue(ContextFunctions.isAttributeCollectionArcXri(addressCollection.getContextNode().getArcXri()));

		ContextNode address1ContextNode = addressCollection.createMember().getContextNode();
		ContextNode address2ContextNode = addressCollection.createMember().getContextNode();
		ContextNode tel1ContextNode = telCollection.createMember().getContextNode();
		ContextNode tel2ContextNode = telCollection.createMember().getContextNode();

/*		assertFalse(Multiplicity.isEntityMemberArcXri(tel1ContextNode.getArcXri()));
		assertFalse(Multiplicity.isEntityMemberArcXri(tel2ContextNode.getArcXri()));*/
		assertTrue(ContextFunctions.isMemberArcXri(tel1ContextNode.getArcXri()));
		assertTrue(ContextFunctions.isMemberArcXri(tel2ContextNode.getArcXri()));

		assertTrue(ContextFunctions.isMemberArcXri(address1ContextNode.getArcXri()));
		assertTrue(ContextFunctions.isMemberArcXri(address2ContextNode.getArcXri()));
/*		assertFalse(Multiplicity.isAttributeMemberArcXri(passport1ContextNode.getArcXri()));
		assertFalse(Multiplicity.isAttributeMemberArcXri(passport2ContextNode.getArcXri()));*/

		assertEquals(addressCollection.membersSize(), 2);
		assertTrue(new IteratorContains<XdiElement>(addressCollection.members(), XdiElement.fromContextNode(address1ContextNode)).contains());
		assertTrue(new IteratorContains<XdiElement>(addressCollection.members(), XdiElement.fromContextNode(address2ContextNode)).contains());
		assertEquals(telCollection.membersSize(), 2);
		assertTrue(new IteratorContains<XdiAttributeMember>(telCollection.members(), XdiAttributeMember.fromContextNode(tel1ContextNode)).contains());
		assertTrue(new IteratorContains<XdiAttributeMember>(telCollection.members(), XdiAttributeMember.fromContextNode(tel2ContextNode)).contains());
	}
}
