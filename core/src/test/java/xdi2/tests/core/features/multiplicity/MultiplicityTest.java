package xdi2.tests.core.features.multiplicity;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.multiplicity.AttributeCollection;
import xdi2.core.features.multiplicity.AttributeSingleton;
import xdi2.core.features.multiplicity.EntityCollection;
import xdi2.core.features.multiplicity.EntitySingleton;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class MultiplicityTest extends TestCase {

	public void testArcXris() throws Exception {

		assertEquals(Multiplicity.attributeSingletonArcXri("+age"), new XRI3SubSegment("$!(+age)"));
		assertEquals(Multiplicity.attributeCollectionArcXri("+tel"), new XRI3SubSegment("$*(+tel)"));
		assertEquals(Multiplicity.entitySingletonArcXri("+resume"), new XRI3SubSegment("+resume"));
		assertEquals(Multiplicity.entityCollectionArcXri("+passport"), new XRI3SubSegment("$(+passport)"));

		assertTrue(Multiplicity.isAttributeSingletonArcXri(Multiplicity.attributeSingletonArcXri("+age")));
		assertFalse(Multiplicity.isAttributeCollectionArcXri(Multiplicity.attributeSingletonArcXri("+age")));
		assertFalse(Multiplicity.isEntitySingletonArcXri(Multiplicity.attributeSingletonArcXri("+age")));
		assertFalse(Multiplicity.isEntityCollectionArcXri(Multiplicity.attributeSingletonArcXri("+age")));

		assertFalse(Multiplicity.isAttributeSingletonArcXri(Multiplicity.attributeCollectionArcXri("+tel")));
		assertTrue(Multiplicity.isAttributeCollectionArcXri(Multiplicity.attributeCollectionArcXri("+tel")));
		assertFalse(Multiplicity.isEntitySingletonArcXri(Multiplicity.attributeCollectionArcXri("+tel")));
		assertFalse(Multiplicity.isEntityCollectionArcXri(Multiplicity.attributeCollectionArcXri("+tel")));

		assertFalse(Multiplicity.isAttributeSingletonArcXri(Multiplicity.entitySingletonArcXri("+resume")));
		assertFalse(Multiplicity.isAttributeCollectionArcXri(Multiplicity.entitySingletonArcXri("+resume")));
		assertTrue(Multiplicity.isEntitySingletonArcXri(Multiplicity.entitySingletonArcXri("+resume")));
		assertFalse(Multiplicity.isEntityCollectionArcXri(Multiplicity.entitySingletonArcXri("+resume")));

		assertFalse(Multiplicity.isAttributeSingletonArcXri(Multiplicity.entityCollectionArcXri("+passport")));
		assertFalse(Multiplicity.isAttributeCollectionArcXri(Multiplicity.entityCollectionArcXri("+passport")));
		assertFalse(Multiplicity.isEntitySingletonArcXri(Multiplicity.entityCollectionArcXri("+passport")));
		assertTrue(Multiplicity.isEntityCollectionArcXri(Multiplicity.entityCollectionArcXri("+passport")));

		assertTrue(Multiplicity.isAttributeCollectionMemberArcXri(Multiplicity.attributeCollectionMemberArcXri()));
		assertTrue(Multiplicity.isEntityCollectionMemberArcXri(Multiplicity.entityCollectionMemberArcXri()));
	}

	public void testMultiplicity() throws Exception {	

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(new XRI3SubSegment("=markus"));

		AttributeSingleton ageAttributeSingleton = EntitySingleton.fromContextNode(contextNode).getAttributeSingleton("+age", true);
		AttributeCollection telAttributeCollection = EntitySingleton.fromContextNode(contextNode).getAttributeCollection("+tel", true);
		EntitySingleton resumeEntitySingleton = EntitySingleton.fromContextNode(contextNode).getEntitySingleton("+resume", true);
		EntityCollection passportEntityCollection = EntitySingleton.fromContextNode(contextNode).getEntityCollection("+passport", true);

		assertTrue(Multiplicity.isAttributeSingletonArcXri(ageAttributeSingleton.getContextNode().getArcXri()));
		assertTrue(Multiplicity.isAttributeCollectionArcXri(telAttributeCollection.getContextNode().getArcXri()));
		assertTrue(Multiplicity.isEntitySingletonArcXri(resumeEntitySingleton.getContextNode().getArcXri()));
		assertTrue(Multiplicity.isEntityCollectionArcXri(passportEntityCollection.getContextNode().getArcXri()));

		ContextNode tel1ContextNode = telAttributeCollection.createAttributeSingleton().getContextNode();
		ContextNode tel2ContextNode = telAttributeCollection.createAttributeSingleton().getContextNode();
		ContextNode passport1ContextNode = passportEntityCollection.createEntitySingleton().getContextNode();
		ContextNode passport2ContextNode = passportEntityCollection.createEntitySingleton().getContextNode();

		assertTrue(Multiplicity.isAttributeCollectionMemberArcXri(tel1ContextNode.getArcXri()));
		assertTrue(Multiplicity.isAttributeCollectionMemberArcXri(tel2ContextNode.getArcXri()));
		assertFalse(Multiplicity.isEntityCollectionMemberArcXri(tel1ContextNode.getArcXri()));
		assertFalse(Multiplicity.isEntityCollectionMemberArcXri(tel2ContextNode.getArcXri()));

		assertFalse(Multiplicity.isAttributeCollectionMemberArcXri(passport1ContextNode.getArcXri()));
		assertFalse(Multiplicity.isAttributeCollectionMemberArcXri(passport2ContextNode.getArcXri()));
		assertTrue(Multiplicity.isEntityCollectionMemberArcXri(passport1ContextNode.getArcXri()));
		assertTrue(Multiplicity.isEntityCollectionMemberArcXri(passport2ContextNode.getArcXri()));

		assertEquals(telAttributeCollection.size(), 2);
		assertTrue(new IteratorContains(telAttributeCollection.iterator(), AttributeSingleton.fromContextNode(tel1ContextNode)).contains());
		assertTrue(new IteratorContains(telAttributeCollection.iterator(), AttributeSingleton.fromContextNode(tel2ContextNode)).contains());
		assertEquals(passportEntityCollection.size(), 2);
		assertTrue(new IteratorContains(passportEntityCollection.iterator(), EntitySingleton.fromContextNode(passport1ContextNode)).contains());
		assertTrue(new IteratorContains(passportEntityCollection.iterator(), EntitySingleton.fromContextNode(passport2ContextNode)).contains());
	}
}
