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

		assertTrue(Multiplicity.isAttributeMemberArcXri(Multiplicity.attributeMemberArcXri()));
		assertTrue(Multiplicity.isEntityMemberArcXri(Multiplicity.entityMemberArcXri()));
	}

	public void testMultiplicity() throws Exception {	

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(new XRI3SubSegment("=markus"));

		AttributeSingleton ageAttributeSingleton = Multiplicity.getAttributeSingleton(contextNode, "+age", true);
		AttributeCollection telAttributeCollection = Multiplicity.getAttributeCollection(contextNode, "+tel", true);
		EntitySingleton resumeEntitySingleton = Multiplicity.getEntitySingleton(contextNode, "+resume", true);
		EntityCollection passportEntityCollection = Multiplicity.getEntityCollection(contextNode, "+passport", true);

		assertTrue(Multiplicity.isAttributeSingletonArcXri(ageAttributeSingleton.getContextNode().getArcXri()));
		assertTrue(Multiplicity.isAttributeCollectionArcXri(telAttributeCollection.getContextNode().getArcXri()));
		assertTrue(Multiplicity.isEntitySingletonArcXri(resumeEntitySingleton.getContextNode().getArcXri()));
		assertTrue(Multiplicity.isEntityCollectionArcXri(passportEntityCollection.getContextNode().getArcXri()));

		ContextNode tel1ContextNode = telAttributeCollection.createMember();
		ContextNode tel2ContextNode = telAttributeCollection.createMember();
		ContextNode passport1ContextNode = passportEntityCollection.createMember();
		ContextNode passport2ContextNode = passportEntityCollection.createMember();

		assertTrue(Multiplicity.isAttributeMemberArcXri(tel1ContextNode.getArcXri()));
		assertTrue(Multiplicity.isAttributeMemberArcXri(tel2ContextNode.getArcXri()));
		assertFalse(Multiplicity.isEntityMemberArcXri(tel1ContextNode.getArcXri()));
		assertFalse(Multiplicity.isEntityMemberArcXri(tel2ContextNode.getArcXri()));

		assertFalse(Multiplicity.isAttributeMemberArcXri(passport1ContextNode.getArcXri()));
		assertFalse(Multiplicity.isAttributeMemberArcXri(passport2ContextNode.getArcXri()));
		assertTrue(Multiplicity.isEntityMemberArcXri(passport1ContextNode.getArcXri()));
		assertTrue(Multiplicity.isEntityMemberArcXri(passport2ContextNode.getArcXri()));

		assertEquals(telAttributeCollection.getMemberCount(), 2);
		assertTrue(new IteratorContains(telAttributeCollection.getMembers(), tel1ContextNode).contains());
		assertTrue(new IteratorContains(telAttributeCollection.getMembers(), tel2ContextNode).contains());
		assertEquals(passportEntityCollection.getMemberCount(), 2);
		assertTrue(new IteratorContains(passportEntityCollection.getMembers(), passport1ContextNode).contains());
		assertTrue(new IteratorContains(passportEntityCollection.getMembers(), passport2ContextNode).contains());
	}
}
