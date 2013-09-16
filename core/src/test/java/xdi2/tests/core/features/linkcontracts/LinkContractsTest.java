package xdi2.tests.core.features.linkcontracts;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.xri3.XDI3Segment;

public class LinkContractsTest extends TestCase {

	public void testLinkContracts() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode1 = graph.setDeepContextNode(XDI3Segment.create("$public$do"));
		ContextNode contextNode2 = graph.setDeepContextNode(XDI3Segment.create("[$do]!:uuid:1111"));

		assertTrue(LinkContract.isValid(XdiAbstractEntity.fromContextNode(contextNode1)));
		assertTrue(LinkContract.isValid(XdiAbstractEntity.fromContextNode(contextNode2)));

		LinkContract linkContract1 = LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(contextNode1));
		LinkContract linkContract2 = LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(contextNode2));

		assertTrue(new IteratorContains<LinkContract> (LinkContracts.getAllLinkContracts(graph), linkContract1).contains());
		assertTrue(new IteratorContains<LinkContract> (LinkContracts.getAllLinkContracts(graph), linkContract2).contains());

		assertEquals(linkContract1, LinkContracts.getLinkContract(graph.getDeepContextNode(XDI3Segment.create("$public")), false));
	}
}
