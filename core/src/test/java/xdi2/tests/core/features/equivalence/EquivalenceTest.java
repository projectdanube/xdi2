package xdi2.tests.core.features.equivalence;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.XDI3SubSegment;

public class EquivalenceTest extends TestCase {

	public void testEquivalence() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));
		ContextNode equivalenceContextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=!1111"));

		// test $is

		Equivalence.addEquivalenceContextNode(contextNode, equivalenceContextNode);

		assertEquals(Equivalence.getEquivalenceContextNodes(contextNode).next(), equivalenceContextNode);

		assertEquals(Equivalence.getAllEquivalenceRelations(graph.getRootContextNode()).next(), contextNode.getRelation(XDIDictionaryConstants.XRI_S_IS));
		assertEquals(Equivalence.getIncomingEquivalenceContextNodes(equivalenceContextNode).next(), contextNode);

		Equivalence.getEquivalenceContextNodes(contextNode).next().delete();

		// done

		contextNode.delete();
		assertTrue(graph.isEmpty());
	}

	public void testReference() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));
		ContextNode referenceContextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=!1111"));
		ContextNode privateReferenceContextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=!2222"));

		// test $ref

		Equivalence.setReferenceContextNode(contextNode, referenceContextNode);

		assertEquals(Equivalence.getReferenceContextNode(contextNode), referenceContextNode);
		assertNull(Equivalence.getPrivateReferenceContextNode(contextNode));

		assertEquals(Equivalence.getAllReferenceAndPrivateReferenceRelations(graph.getRootContextNode()).next(), contextNode.getRelation(XDIDictionaryConstants.XRI_S_REF));
		assertEquals(Equivalence.getIncomingReferenceAndPrivateReferenceContextNodes(referenceContextNode).next(), contextNode);

		Equivalence.getReferenceContextNode(contextNode).delete();

		// test $ref!

		Equivalence.setPrivateReferenceContextNode(contextNode, privateReferenceContextNode);

		assertEquals(Equivalence.getPrivateReferenceContextNode(contextNode), privateReferenceContextNode);
		assertNull(Equivalence.getReferenceContextNode(contextNode));

		assertEquals(Equivalence.getAllReferenceAndPrivateReferenceRelations(graph.getRootContextNode()).next(), contextNode.getRelation(XDIDictionaryConstants.XRI_S_REF_BANG));
		assertEquals(Equivalence.getIncomingReferenceAndPrivateReferenceContextNodes(privateReferenceContextNode).next(), contextNode);

		Equivalence.getPrivateReferenceContextNode(contextNode).delete();

		// done

		contextNode.delete();
		assertTrue(graph.isEmpty());
	}
}
