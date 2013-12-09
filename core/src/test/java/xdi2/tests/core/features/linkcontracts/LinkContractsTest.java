package xdi2.tests.core.features.linkcontracts;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.GenericLinkContract;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.linkcontracts.PublicLinkContract;
import xdi2.core.features.linkcontracts.RootLinkContract;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.xri3.XDI3Segment;

public class LinkContractsTest extends TestCase {

	public void testLinkContracts() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode1 = graph.setDeepContextNode(XDI3Segment.create("$public$do"));
		ContextNode contextNode2 = graph.setDeepContextNode(XDI3Segment.create("$do"));

		assertTrue(LinkContract.isValid(XdiAbstractEntity.fromContextNode(contextNode1)));
		assertTrue(LinkContract.isValid(XdiAbstractEntity.fromContextNode(contextNode2)));

		LinkContract linkContract1 = LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(contextNode1));
		LinkContract linkContract2 = LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(contextNode2));

		assertTrue(new IteratorContains<LinkContract> (LinkContracts.getAllLinkContracts(graph), linkContract1).contains());
		assertTrue(new IteratorContains<LinkContract> (LinkContracts.getAllLinkContracts(graph), linkContract2).contains());

		assertEquals(linkContract1, LinkContracts.getLinkContract(graph.getDeepContextNode(XDI3Segment.create("$public")), false));
	}

	public void testLinkContractTypes() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		ContextNode c1 = graph.setDeepContextNode(XDI3Segment.create("=bob$to=alice$from+registration$do"));
		GenericLinkContract l1 = (GenericLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c1));

		assertNotNull(l1);
		assertEquals(l1.getAuthorizingParty(), XDI3Segment.create("=bob"));
		assertEquals(l1.getRequestingParty(), XDI3Segment.create("=alice"));
		assertEquals(l1.getTemplateId(), XDI3Segment.create("+registration"));

		ContextNode c2 = graph.setDeepContextNode(XDI3Segment.create("=bob$to=alice$from+registration[$do]!:uuid:0e43479d-834e-085f-3e8a-faa060afe9cf"));
		GenericLinkContract l2 = (GenericLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c2));

		assertNotNull(l2);
		assertEquals(l2.getAuthorizingParty(), XDI3Segment.create("=bob"));
		assertEquals(l2.getRequestingParty(), XDI3Segment.create("=alice"));
		assertEquals(l2.getTemplateId(), XDI3Segment.create("+registration"));

		ContextNode c3 = graph.setDeepContextNode(XDI3Segment.create("[=]!1111$to[=]!2222$from+registration$do"));
		GenericLinkContract l3 = (GenericLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c3));

		assertNotNull(l3);
		assertEquals(l3.getAuthorizingParty(), XDI3Segment.create("[=]!1111"));
		assertEquals(l3.getRequestingParty(), XDI3Segment.create("[=]!2222"));
		assertEquals(l3.getTemplateId(), XDI3Segment.create("+registration"));

		ContextNode c4 = graph.setDeepContextNode(XDI3Segment.create("[=]!1111$to[=]!2222$from+registration[$do]!:uuid:272406ef-1e57-1325-fdba-700e16ac1132"));
		GenericLinkContract l4 = (GenericLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c4));

		assertNotNull(l4);
		assertEquals(l4.getAuthorizingParty(), XDI3Segment.create("[=]!1111"));
		assertEquals(l4.getRequestingParty(), XDI3Segment.create("[=]!2222"));
		assertEquals(l4.getTemplateId(), XDI3Segment.create("+registration"));

		ContextNode c5 = graph.setDeepContextNode(XDI3Segment.create("+friend$to$anon$from$do"));
		GenericLinkContract l5 = (GenericLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c5));

		assertNotNull(l5);
		assertEquals(l5.getAuthorizingParty(), XDI3Segment.create("+friend"));
		assertEquals(l5.getRequestingParty(), XDI3Segment.create("$anon"));
		assertNull(l5.getTemplateId());

		ContextNode c6 = graph.setDeepContextNode(XDI3Segment.create("$do"));
		RootLinkContract l6 = (RootLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c6));

		assertNotNull(l6);

		ContextNode c7 = graph.setDeepContextNode(XDI3Segment.create("$public$do"));
		PublicLinkContract l7 = (PublicLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c7));

		assertNotNull(l7);
	}
}
