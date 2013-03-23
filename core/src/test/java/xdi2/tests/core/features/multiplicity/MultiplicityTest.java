package xdi2.tests.core.features.multiplicity;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.features.multiplicity.XdiAttributeCollection;
import xdi2.core.features.multiplicity.XdiAttributeMember;
import xdi2.core.features.multiplicity.XdiAttributeSingleton;
import xdi2.core.features.multiplicity.XdiEntityCollection;
import xdi2.core.features.multiplicity.XdiEntityMember;
import xdi2.core.features.multiplicity.XdiEntitySingleton;
import xdi2.core.features.multiplicity.XdiSubGraph;
import xdi2.core.features.roots.PeerRoot;
import xdi2.core.features.roots.Roots;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class MultiplicityTest extends TestCase {

	public void testArcXris() throws Exception {

		assertEquals(Multiplicity.entitySingletonArcXri(XDI3SubSegment.create("+address")), XDI3SubSegment.create("+address"));
		assertEquals(Multiplicity.attributeSingletonArcXri(XDI3SubSegment.create("+tel")), XDI3SubSegment.create("<+tel>"));
		assertEquals(Multiplicity.entityCollectionArcXri(XDI3SubSegment.create("+address")), XDI3SubSegment.create("{+address}"));
		assertEquals(Multiplicity.attributeCollectionArcXri(XDI3SubSegment.create("+tel")), XDI3SubSegment.create("{<+tel>}"));

		assertTrue(Multiplicity.isEntitySingletonArcXri(Multiplicity.entitySingletonArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(Multiplicity.isAttributeSingletonArcXri(Multiplicity.entitySingletonArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(Multiplicity.isEntityCollectionArcXri(Multiplicity.entitySingletonArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(Multiplicity.isAttributeCollectionArcXri(Multiplicity.entitySingletonArcXri(XDI3SubSegment.create("+address"))));

		assertFalse(Multiplicity.isEntitySingletonArcXri(Multiplicity.attributeSingletonArcXri(XDI3SubSegment.create("+tel"))));
		assertTrue(Multiplicity.isAttributeSingletonArcXri(Multiplicity.attributeSingletonArcXri(XDI3SubSegment.create("+tel"))));
		assertFalse(Multiplicity.isEntityCollectionArcXri(Multiplicity.attributeSingletonArcXri(XDI3SubSegment.create("+tel"))));
		assertFalse(Multiplicity.isAttributeCollectionArcXri(Multiplicity.attributeSingletonArcXri(XDI3SubSegment.create("+tel"))));

		assertFalse(Multiplicity.isEntitySingletonArcXri(Multiplicity.entityCollectionArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(Multiplicity.isAttributeSingletonArcXri(Multiplicity.entityCollectionArcXri(XDI3SubSegment.create("+address"))));
		assertTrue(Multiplicity.isEntityCollectionArcXri(Multiplicity.entityCollectionArcXri(XDI3SubSegment.create("+address"))));
		assertFalse(Multiplicity.isAttributeCollectionArcXri(Multiplicity.entityCollectionArcXri(XDI3SubSegment.create("+address"))));

		assertFalse(Multiplicity.isEntitySingletonArcXri(Multiplicity.attributeCollectionArcXri(XDI3SubSegment.create("+tel"))));
		assertFalse(Multiplicity.isAttributeSingletonArcXri(Multiplicity.attributeCollectionArcXri(XDI3SubSegment.create("+tel"))));
		assertFalse(Multiplicity.isEntityCollectionArcXri(Multiplicity.attributeCollectionArcXri(XDI3SubSegment.create("+tel"))));
		assertTrue(Multiplicity.isAttributeCollectionArcXri(Multiplicity.attributeCollectionArcXri(XDI3SubSegment.create("+tel"))));

		assertTrue(Multiplicity.isMemberArcXri(Multiplicity.memberArcXri(XDI3SubSegment.create("!1"))));

		assertEquals(Multiplicity.baseArcXri(XDI3SubSegment.create("+tel")), XDI3SubSegment.create("+tel"));
		assertEquals(Multiplicity.baseArcXri(XDI3SubSegment.create("<+address>")), XDI3SubSegment.create("+address"));
		assertEquals(Multiplicity.baseArcXri(XDI3SubSegment.create("{+tel}")), XDI3SubSegment.create("+tel"));
		assertEquals(Multiplicity.baseArcXri(XDI3SubSegment.create("{<+address>}")), XDI3SubSegment.create("+address"));
	}

	public void testSubGraph() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode root = graph.getRootContextNode();
		ContextNode markus = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));
		PeerRoot peerRoot = Roots.findLocalRoot(graph).findPeerRoot(XDI3Segment.create("=!91F2.8153.F600.AE24"), true);

		assertNotNull(XdiSubGraph.fromContextNode(root));
		assertNotNull(XdiSubGraph.fromContextNode(markus));
		assertNotNull(XdiSubGraph.fromContextNode(peerRoot.getContextNode()));
	}

	public void testContextNodes() throws Exception {	

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));

		assertTrue(XdiSubGraph.fromContextNode(contextNode) instanceof XdiEntitySingleton);

		XdiEntitySingleton addressEntitySingleton = XdiSubGraph.fromContextNode(contextNode).getEntitySingleton(XDI3SubSegment.create("+address"), true);
		XdiAttributeSingleton telAttributeSingleton = XdiSubGraph.fromContextNode(contextNode).getAttributeSingleton(XDI3SubSegment.create("+tel"), true);
		XdiEntityCollection addressCollection = XdiSubGraph.fromContextNode(contextNode).getEntityCollection(XDI3SubSegment.create("+address"), true);
		XdiAttributeCollection telCollection = XdiSubGraph.fromContextNode(contextNode).getAttributeCollection(XDI3SubSegment.create("+tel"), true);

		assertTrue(Multiplicity.isEntitySingletonArcXri(addressEntitySingleton.getContextNode().getArcXri()));
		assertTrue(Multiplicity.isAttributeSingletonArcXri(telAttributeSingleton.getContextNode().getArcXri()));
		assertTrue(Multiplicity.isEntityCollectionArcXri(addressCollection.getContextNode().getArcXri()));
		assertTrue(Multiplicity.isAttributeCollectionArcXri(addressCollection.getContextNode().getArcXri()));

		ContextNode address1ContextNode = addressCollection.createMember().getContextNode();
		ContextNode address2ContextNode = addressCollection.createMember().getContextNode();
		ContextNode tel1ContextNode = telCollection.createMember().getContextNode();
		ContextNode tel2ContextNode = telCollection.createMember().getContextNode();

/*		assertFalse(Multiplicity.isEntityMemberArcXri(tel1ContextNode.getArcXri()));
		assertFalse(Multiplicity.isEntityMemberArcXri(tel2ContextNode.getArcXri()));*/
		assertTrue(Multiplicity.isMemberArcXri(tel1ContextNode.getArcXri()));
		assertTrue(Multiplicity.isMemberArcXri(tel2ContextNode.getArcXri()));

		assertTrue(Multiplicity.isMemberArcXri(address1ContextNode.getArcXri()));
		assertTrue(Multiplicity.isMemberArcXri(address2ContextNode.getArcXri()));
/*		assertFalse(Multiplicity.isAttributeMemberArcXri(passport1ContextNode.getArcXri()));
		assertFalse(Multiplicity.isAttributeMemberArcXri(passport2ContextNode.getArcXri()));*/

		assertEquals(addressCollection.membersSize(), 2);
		assertTrue(new IteratorContains<XdiEntityMember>(addressCollection.members(), XdiEntityMember.fromContextNode(address1ContextNode)).contains());
		assertTrue(new IteratorContains<XdiEntityMember>(addressCollection.members(), XdiEntityMember.fromContextNode(address2ContextNode)).contains());
		assertEquals(telCollection.membersSize(), 2);
		assertTrue(new IteratorContains<XdiAttributeMember>(telCollection.members(), XdiAttributeMember.fromContextNode(tel1ContextNode)).contains());
		assertTrue(new IteratorContains<XdiAttributeMember>(telCollection.members(), XdiAttributeMember.fromContextNode(tel2ContextNode)).contains());
	}
}
