package xdi2.tests.core.features.linkcontracts;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.GenericLinkContract;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.LinkContractTemplate;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.linkcontracts.MetaLinkContract;
import xdi2.core.features.linkcontracts.PublicLinkContract;
import xdi2.core.features.linkcontracts.RootLinkContract;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.GraphUtil;
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

	public void testGenericLinkContract() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		ContextNode c1 = graph.setDeepContextNode(XDI3Segment.create("=bob$to=alice$from=alice+registration$do"));
		GenericLinkContract l1 = (GenericLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c1));

		assertNotNull(l1);
		assertEquals(l1.getAuthorizingAuthority(), XDI3Segment.create("=bob"));
		assertEquals(l1.getRequestingAuthority(), XDI3Segment.create("=alice"));
		assertEquals(l1.getTemplateAuthorityAndId(), XDI3Segment.create("=alice+registration"));

		ContextNode c2 = graph.setDeepContextNode(XDI3Segment.create("=bob$to=alice$from=alice+registration[$do]!:uuid:0e43479d-834e-085f-3e8a-faa060afe9cf"));
		GenericLinkContract l2 = (GenericLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c2));

		assertNotNull(l2);
		assertEquals(l2.getAuthorizingAuthority(), XDI3Segment.create("=bob"));
		assertEquals(l2.getRequestingAuthority(), XDI3Segment.create("=alice"));
		assertEquals(l2.getTemplateAuthorityAndId(), XDI3Segment.create("=alice+registration"));

		ContextNode c3 = graph.setDeepContextNode(XDI3Segment.create("[=]!1111$to[=]!2222$from[=]!2222+registration$do"));
		GenericLinkContract l3 = (GenericLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c3));

		assertNotNull(l3);
		assertEquals(l3.getAuthorizingAuthority(), XDI3Segment.create("[=]!1111"));
		assertEquals(l3.getRequestingAuthority(), XDI3Segment.create("[=]!2222"));
		assertEquals(l3.getTemplateAuthorityAndId(), XDI3Segment.create("[=]!2222+registration"));

		ContextNode c4 = graph.setDeepContextNode(XDI3Segment.create("[=]!1111$to[=]!2222$from[=]!2222+registration[$do]!:uuid:272406ef-1e57-1325-fdba-700e16ac1132"));
		GenericLinkContract l4 = (GenericLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c4));

		assertNotNull(l4);
		assertEquals(l4.getAuthorizingAuthority(), XDI3Segment.create("[=]!1111"));
		assertEquals(l4.getRequestingAuthority(), XDI3Segment.create("[=]!2222"));
		assertEquals(l4.getTemplateAuthorityAndId(), XDI3Segment.create("[=]!2222+registration"));

		ContextNode c5 = graph.setDeepContextNode(XDI3Segment.create("+friend$to$anon$from$do"));
		GenericLinkContract l5 = (GenericLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c5));

		assertNotNull(l5);
		assertEquals(l5.getAuthorizingAuthority(), XDI3Segment.create("+friend"));
		assertEquals(l5.getRequestingAuthority(), XDI3Segment.create("$anon"));
		assertNull(l5.getTemplateAuthorityAndId());
	}

	public void testRootLinkContract() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		GraphUtil.setOwnerXri(graph, XDI3Segment.create("=markus"));
		assertEquals(GraphUtil.getOwnerXri(graph), XDI3Segment.create("=markus"));

		RootLinkContract l = RootLinkContract.findRootLinkContract(graph, true);
		assertNotNull(l);
		assertEquals(l.getXdiEntity().getXri(), XDI3Segment.create("=markus$to=markus$from$do"));

		assertNotNull(RootLinkContract.findRootLinkContract(graph, false));
		assertTrue(LinkContract.fromXdiEntity(l.getXdiEntity()) instanceof RootLinkContract);

		assertEquals(l.getRequestingAuthority(), XDI3Segment.create("=markus"));
		assertEquals(l.getAuthorizingAuthority(), XDI3Segment.create("=markus"));
		assertNull(l.getTemplateAuthorityAndId());
	}

	public void testPublicLinkContract() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		GraphUtil.setOwnerXri(graph, XDI3Segment.create("=markus"));
		assertEquals(GraphUtil.getOwnerXri(graph), XDI3Segment.create("=markus"));

		PublicLinkContract l = PublicLinkContract.findPublicLinkContract(graph, true);
		assertNotNull(l);
		assertEquals(l.getXdiEntity().getXri(), XDI3Segment.create("=markus$to$anon$from$public$do"));

		assertNotNull(PublicLinkContract.findPublicLinkContract(graph, false));
		assertTrue(LinkContract.fromXdiEntity(l.getXdiEntity()) instanceof PublicLinkContract);

		assertEquals(l.getRequestingAuthority(), XDIAuthenticationConstants.XRI_S_ANONYMOUS);
		assertEquals(l.getAuthorizingAuthority(), XDI3Segment.create("=markus"));
		assertEquals(l.getTemplateAuthorityAndId(), XDILinkContractConstants.XRI_S_PUBLIC);
	}

	public void testLinkContractTemplate() throws Exception {

		XDI3Segment xri = XDI3Segment.create("=markus{$from}=markus+registration$do");

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.setDeepContextNode(xri);

		LinkContractTemplate l1 = LinkContractTemplate.findLinkContractTemplate(graph, XDI3Segment.create("=markus"), XDI3Segment.create("+registration"), false);
		assertNotNull(l1);
		assertEquals(l1.getTemplateAuthorityAndId(), XDI3Segment.create("=markus+registration"));

		LinkContractTemplate l2 = LinkContractTemplate.fromXdiEntity(XdiAbstractEntity.fromContextNode(contextNode));
		assertNotNull(l2);
		assertEquals(l2.getTemplateAuthorityAndId(), XDI3Segment.create("=markus+registration"));

		assertEquals(l1, l2);
	}

	public void testMetaLinkContract() throws Exception {

		XDI3Segment xri = XDI3Segment.create("=markus{$to}+registration$do");

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.setDeepContextNode(xri);

		MetaLinkContract l1 = MetaLinkContract.findMetaLinkContract(graph, XDI3Segment.create("=markus"), XDI3Segment.create("+registration"), false);
		assertNotNull(l1);
		assertEquals(l1.getRequestingAuthority(), XDI3Segment.create("=markus"));
		assertEquals(l1.getTemplateId(), XDI3Segment.create("+registration"));

		MetaLinkContract l2 = MetaLinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(contextNode));
		assertNotNull(l2);
		assertEquals(l2.getRequestingAuthority(), XDI3Segment.create("=markus"));
		assertEquals(l2.getTemplateId(), XDI3Segment.create("+registration"));

		assertEquals(l1, l2);
	}
}
