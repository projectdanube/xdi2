package xdi2.tests.core.features.multiplicity;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class MultiplicityTest extends TestCase {

	public void testMultiplicity() throws Exception {

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

	public void testMultiplicityContextNodes() throws Exception {	

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(new XRI3SubSegment("=markus"));

		ContextNode ageContextNode = contextNode.createContextNode(Multiplicity.attributeSingletonArcXri("+age"));
		ContextNode telContextNode = contextNode.createContextNode(Multiplicity.attributeCollectionArcXri("+tel"));
		ContextNode resumeContextNode = contextNode.createContextNode(Multiplicity.entitySingletonArcXri("+resume"));
		ContextNode passportContextNode = contextNode.createContextNode(Multiplicity.entityCollectionArcXri("+passport"));

		assertTrue(Multiplicity.isAttributeSingleton(ageContextNode));
		assertTrue(Multiplicity.isAttributeCollection(telContextNode));
		assertTrue(Multiplicity.isEntitySingleton(resumeContextNode));
		assertTrue(Multiplicity.isEntityCollection(passportContextNode));

		ContextNode tel1ContextNode = Multiplicity.createAttributeMember(telContextNode);
		ContextNode tel2ContextNode = Multiplicity.createAttributeMember(telContextNode);
		ContextNode resume1ContextNode = Multiplicity.createAttributeMember(resumeContextNode);
		ContextNode resume2ContextNode = Multiplicity.createAttributeMember(resumeContextNode);

		assertTrue(Multiplicity.isAttributeMember(tel1ContextNode));
		assertTrue(Multiplicity.isAttributeMember(tel2ContextNode));
		assertTrue(Multiplicity.isAttributeMember(resume1ContextNode));
		assertTrue(Multiplicity.isAttributeMember(resume2ContextNode));
	}
}
