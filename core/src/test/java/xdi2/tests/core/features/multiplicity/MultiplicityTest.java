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
import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.core.xri3.impl.XDI3SubSegment;

public class MultiplicityTest extends TestCase {

	public void testArcXris() throws Exception {

		assertEquals(Multiplicity.collectionArcXri(new XDI3SubSegment("+tel")), new XDI3SubSegment("$(+tel)"));
		assertEquals(Multiplicity.entitySingletonArcXri(new XDI3SubSegment("+passport")), new XDI3SubSegment("+passport"));
		assertEquals(Multiplicity.attributeSingletonArcXri(new XDI3SubSegment("+tel")), new XDI3SubSegment("$!(+tel)"));

		assertTrue(Multiplicity.isCollectionArcXri(Multiplicity.collectionArcXri(new XDI3SubSegment("+tel"))));
		assertFalse(Multiplicity.isEntitySingletonArcXri(Multiplicity.collectionArcXri(new XDI3SubSegment("+tel"))));
		assertFalse(Multiplicity.isAttributeSingletonArcXri(Multiplicity.collectionArcXri(new XDI3SubSegment("+tel"))));

		assertFalse(Multiplicity.isCollectionArcXri(Multiplicity.entitySingletonArcXri(new XDI3SubSegment("+passport"))));
		assertTrue(Multiplicity.isEntitySingletonArcXri(Multiplicity.entitySingletonArcXri(new XDI3SubSegment("+passport"))));
		assertFalse(Multiplicity.isAttributeSingletonArcXri(Multiplicity.entitySingletonArcXri(new XDI3SubSegment("+passport"))));

		assertFalse(Multiplicity.isCollectionArcXri(Multiplicity.attributeSingletonArcXri(new XDI3SubSegment("+tel"))));
		assertFalse(Multiplicity.isEntitySingletonArcXri(Multiplicity.attributeSingletonArcXri(new XDI3SubSegment("+tel"))));
		assertTrue(Multiplicity.isAttributeSingletonArcXri(Multiplicity.attributeSingletonArcXri(new XDI3SubSegment("+tel"))));

		assertTrue(Multiplicity.isAttributeMemberArcXri(Multiplicity.attributeMemberArcXri("1")));
		assertTrue(Multiplicity.isAttributeMemberArcXri(Multiplicity.attributeMemberArcXriRandom()));
		assertTrue(Multiplicity.isEntityMemberArcXri(Multiplicity.entityMemberArcXri("1")));
		assertTrue(Multiplicity.isEntityMemberArcXri(Multiplicity.entityMemberArcXriRandom()));

		assertEquals(Multiplicity.baseArcXri(new XDI3SubSegment("$(+tel)")), new XDI3SubSegment("+tel"));
		assertEquals(Multiplicity.baseArcXri(new XDI3SubSegment("+passport")), new XDI3SubSegment("+passport"));
		assertEquals(Multiplicity.baseArcXri(new XDI3SubSegment("$!(+tel)")), new XDI3SubSegment("+tel"));
	}

	public void testSubGraph() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode root = graph.getRootContextNode();
		ContextNode markus = graph.getRootContextNode().createContextNode(new XDI3SubSegment("=markus"));
		ContextNode remoteRoot = RemoteRoots.findRemoteRootContextNode(graph, new XDI3Segment("=!91F2.8153.F600.AE24"), true);

		assertNotNull(XdiSubGraph.fromContextNode(root));
		assertNotNull(XdiSubGraph.fromContextNode(markus));
		assertNotNull(XdiSubGraph.fromContextNode(remoteRoot));
	}

	public void testContextNodes() throws Exception {	

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(new XDI3SubSegment("=markus"));

		assertTrue(XdiSubGraph.fromContextNode(contextNode) instanceof XdiEntitySingleton);

		XdiCollection passportCollection = XdiSubGraph.fromContextNode(contextNode).getCollection(new XDI3SubSegment("+passport"), true);
		XdiCollection telCollection = XdiSubGraph.fromContextNode(contextNode).getCollection(new XDI3SubSegment("+tel"), true);
		XdiEntitySingleton passportEntitySingleton = XdiSubGraph.fromContextNode(contextNode).getEntitySingleton(new XDI3SubSegment("+passport"), true);
		XdiAttributeSingleton telAttributeSingleton = XdiSubGraph.fromContextNode(contextNode).getAttributeSingleton(new XDI3SubSegment("+tel"), true);

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
