package xdi2.tests.core.features.multiplicity;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class MultiplicityTest extends TestCase {

	public void testMultiplicity() throws Exception {

		assertEquals(Multiplicity.attributeSingletonArcXri("+dateofbirth"), new XRI3SubSegment("$!(+dateofbirth)"));
		assertEquals(Multiplicity.attributeCollectionArcXri("+tel"), new XRI3SubSegment("$*(+tel)"));
		assertEquals(Multiplicity.entitySingletonArcXri("+biometrics"), new XRI3SubSegment("+biometrics"));
		assertEquals(Multiplicity.entityCollectionArcXri("+passport"), new XRI3SubSegment("$(+passport)"));

		assertTrue(Multiplicity.isAttributeSingletonArcXri(Multiplicity.attributeSingletonArcXri("+dateofbirth")));
		assertFalse(Multiplicity.isAttributeCollectionArcXri(Multiplicity.attributeSingletonArcXri("+dateofbirth")));
		assertFalse(Multiplicity.isEntitySingletonArcXri(Multiplicity.attributeSingletonArcXri("+dateofbirth")));
		assertFalse(Multiplicity.isEntityCollectionArcXri(Multiplicity.attributeSingletonArcXri("+dateofbirth")));

		assertFalse(Multiplicity.isAttributeSingletonArcXri(Multiplicity.attributeCollectionArcXri("+tel")));
		assertTrue(Multiplicity.isAttributeCollectionArcXri(Multiplicity.attributeCollectionArcXri("+tel")));
		assertFalse(Multiplicity.isEntitySingletonArcXri(Multiplicity.attributeCollectionArcXri("+tel")));
		assertFalse(Multiplicity.isEntityCollectionArcXri(Multiplicity.attributeCollectionArcXri("+tel")));

		assertFalse(Multiplicity.isAttributeSingletonArcXri(Multiplicity.entitySingletonArcXri("+biometrics")));
		assertFalse(Multiplicity.isAttributeCollectionArcXri(Multiplicity.entitySingletonArcXri("+biometrics")));
		assertTrue(Multiplicity.isEntitySingletonArcXri(Multiplicity.entitySingletonArcXri("+biometrics")));
		assertFalse(Multiplicity.isEntityCollectionArcXri(Multiplicity.entitySingletonArcXri("+biometrics")));

		assertFalse(Multiplicity.isAttributeSingletonArcXri(Multiplicity.entityCollectionArcXri("+passport")));
		assertFalse(Multiplicity.isAttributeCollectionArcXri(Multiplicity.entityCollectionArcXri("+passport")));
		assertFalse(Multiplicity.isEntitySingletonArcXri(Multiplicity.entityCollectionArcXri("+passport")));
		assertTrue(Multiplicity.isEntityCollectionArcXri(Multiplicity.entityCollectionArcXri("+passport")));

		assertTrue(Multiplicity.isElementArcXri(Multiplicity.elementArcXri()));
	}

	public void testMultiplicityContextNodes() throws Exception {	

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(new XRI3SubSegment("=markus"));

		ContextNode dateofbirthContextNode = contextNode.createContextNode(Multiplicity.attributeSingletonArcXri("+dateofbirth"));
		ContextNode telContextNode = contextNode.createContextNode(Multiplicity.attributeCollectionArcXri("+tel"));
		ContextNode biometricsContextNode = contextNode.createContextNode(Multiplicity.entitySingletonArcXri("+biometrics"));
		ContextNode passportContextNode = contextNode.createContextNode(Multiplicity.entityCollectionArcXri("+passport"));

		assertTrue(Multiplicity.isAttributeSingleton(dateofbirthContextNode));
		assertTrue(Multiplicity.isAttributeCollection(telContextNode));
		assertTrue(Multiplicity.isEntitySingleton(biometricsContextNode));
		assertTrue(Multiplicity.isEntityCollection(passportContextNode));
	}
}
