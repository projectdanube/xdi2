package xdi2.tests.core.features.multiplicity;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.features.multiplicity.XdiAttributeMember;
import xdi2.core.features.multiplicity.XdiAttributeSingleton;
import xdi2.core.features.multiplicity.XdiCollection;
import xdi2.core.features.multiplicity.XdiEntityMember;
import xdi2.core.features.multiplicity.XdiEntitySingleton;
import xdi2.core.features.multiplicity.XdiSubGraph;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class MultiplicityTest extends TestCase {

	public void testArcXris() throws Exception {

		assertEquals(Multiplicity.collectionArcXri(XDI3SubSegment.create("+tel")), XDI3SubSegment.create("$(+tel)"));
		assertEquals(Multiplicity.entitySingletonArcXri(XDI3SubSegment.create("+passport")), XDI3SubSegment.create("+passport"));
		assertEquals(Multiplicity.attributeSingletonArcXri(XDI3SubSegment.create("+tel")), XDI3SubSegment.create("$!(+tel)"));

		assertTrue(Multiplicity.isCollectionArcXri(Multiplicity.collectionArcXri(XDI3SubSegment.create("+tel"))));
		assertFalse(Multiplicity.isEntitySingletonArcXri(Multiplicity.collectionArcXri(XDI3SubSegment.create("+tel"))));
		assertFalse(Multiplicity.isAttributeSingletonArcXri(Multiplicity.collectionArcXri(XDI3SubSegment.create("+tel"))));

		assertFalse(Multiplicity.isCollectionArcXri(Multiplicity.entitySingletonArcXri(XDI3SubSegment.create("+passport"))));
		assertTrue(Multiplicity.isEntitySingletonArcXri(Multiplicity.entitySingletonArcXri(XDI3SubSegment.create("+passport"))));
		assertFalse(Multiplicity.isAttributeSingletonArcXri(Multiplicity.entitySingletonArcXri(XDI3SubSegment.create("+passport"))));

		assertFalse(Multiplicity.isCollectionArcXri(Multiplicity.attributeSingletonArcXri(XDI3SubSegment.create("+tel"))));
		assertFalse(Multiplicity.isEntitySingletonArcXri(Multiplicity.attributeSingletonArcXri(XDI3SubSegment.create("+tel"))));
		assertTrue(Multiplicity.isAttributeSingletonArcXri(Multiplicity.attributeSingletonArcXri(XDI3SubSegment.create("+tel"))));

		assertTrue(Multiplicity.isAttributeMemberArcXri(Multiplicity.attributeMemberArcXri("1")));
		assertTrue(Multiplicity.isAttributeMemberArcXri(Multiplicity.attributeMemberArcXriRandom()));
		assertTrue(Multiplicity.isEntityMemberArcXri(Multiplicity.entityMemberArcXri("1")));
		assertTrue(Multiplicity.isEntityMemberArcXri(Multiplicity.entityMemberArcXriRandom()));

		assertEquals(Multiplicity.baseArcXri(XDI3SubSegment.create("$(+tel)")), XDI3SubSegment.create("+tel"));
		assertEquals(Multiplicity.baseArcXri(XDI3SubSegment.create("+passport")), XDI3SubSegment.create("+passport"));
		assertEquals(Multiplicity.baseArcXri(XDI3SubSegment.create("$!(+tel)")), XDI3SubSegment.create("+tel"));
	}

	public void testSubGraph() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode root = graph.getRootContextNode();
		ContextNode markus = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));
		ContextNode remoteRoot = RemoteRoots.findRemoteRootContextNode(graph, XDI3Segment.create("=!91F2.8153.F600.AE24"), true);

		assertNotNull(XdiSubGraph.fromContextNode(root));
		assertNotNull(XdiSubGraph.fromContextNode(markus));
		assertNotNull(XdiSubGraph.fromContextNode(remoteRoot));
	}

	public void testContextNodes() throws Exception {	

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));

		assertTrue(XdiSubGraph.fromContextNode(contextNode) instanceof XdiEntitySingleton);

		XdiCollection passportCollection = XdiSubGraph.fromContextNode(contextNode).getCollection(XDI3SubSegment.create("+passport"), true);
		XdiCollection telCollection = XdiSubGraph.fromContextNode(contextNode).getCollection(XDI3SubSegment.create("+tel"), true);
		XdiEntitySingleton passportEntitySingleton = XdiSubGraph.fromContextNode(contextNode).getEntitySingleton(XDI3SubSegment.create("+passport"), true);
		XdiAttributeSingleton telAttributeSingleton = XdiSubGraph.fromContextNode(contextNode).getAttributeSingleton(XDI3SubSegment.create("+tel"), true);

		assertTrue(Multiplicity.isCollectionArcXri(passportCollection.getContextNode().getArcXri()));
		assertTrue(Multiplicity.isEntitySingletonArcXri(passportEntitySingleton.getContextNode().getArcXri()));
		assertTrue(Multiplicity.isAttributeSingletonArcXri(telAttributeSingleton.getContextNode().getArcXri()));

		ContextNode passport1ContextNode = passportCollection.createEntityMember().getContextNode();
		ContextNode passport2ContextNode = passportCollection.createEntityMember().getContextNode();
		ContextNode tel1ContextNode = telCollection.createAttributeMember().getContextNode();
		ContextNode tel2ContextNode = telCollection.createAttributeMember().getContextNode();

		assertFalse(Multiplicity.isEntityMemberArcXri(tel1ContextNode.getArcXri()));
		assertFalse(Multiplicity.isEntityMemberArcXri(tel2ContextNode.getArcXri()));
		assertTrue(Multiplicity.isAttributeMemberArcXri(tel1ContextNode.getArcXri()));
		assertTrue(Multiplicity.isAttributeMemberArcXri(tel2ContextNode.getArcXri()));

		assertTrue(Multiplicity.isEntityMemberArcXri(passport1ContextNode.getArcXri()));
		assertTrue(Multiplicity.isEntityMemberArcXri(passport2ContextNode.getArcXri()));
		assertFalse(Multiplicity.isAttributeMemberArcXri(passport1ContextNode.getArcXri()));
		assertFalse(Multiplicity.isAttributeMemberArcXri(passport2ContextNode.getArcXri()));

		assertEquals(passportCollection.entitiesSize(), 2);
		assertTrue(new IteratorContains<XdiEntityMember>(passportCollection.entities(), XdiEntityMember.fromContextNode(passport1ContextNode)).contains());
		assertTrue(new IteratorContains<XdiEntityMember>(passportCollection.entities(), XdiEntityMember.fromContextNode(passport2ContextNode)).contains());
		assertEquals(telCollection.attributesSize(), 2);
		assertTrue(new IteratorContains<XdiAttributeMember>(telCollection.attributes(), XdiAttributeMember.fromContextNode(tel1ContextNode)).contains());
		assertTrue(new IteratorContains<XdiAttributeMember>(telCollection.attributes(), XdiAttributeMember.fromContextNode(tel2ContextNode)).contains());
	}
}
