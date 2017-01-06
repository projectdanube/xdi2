package xdi2.tests.core.features.linkcontracts;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.linkcontracts.instance.ConnectLinkContract;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.linkcontracts.instance.PublicLinkContract;
import xdi2.core.features.linkcontracts.instance.RelationshipLinkContract;
import xdi2.core.features.linkcontracts.instance.RootLinkContract;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.IteratorContains;

public class LinkContractsTest extends TestCase {

	public void testLinkContracts() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode1 = graph.setDeepContextNode(XDIAddress.create("(=alice/$public)$contract"));
		ContextNode contextNode2 = graph.setDeepContextNode(XDIAddress.create("(=alice/=alice)$contract"));

		assertTrue(LinkContract.isValid(XdiAbstractEntity.fromContextNode(contextNode1)));
		assertTrue(LinkContract.isValid(XdiAbstractEntity.fromContextNode(contextNode2)));

		LinkContract linkContract1 = LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(contextNode1));
		LinkContract linkContract2 = LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(contextNode2));

		assertTrue(new IteratorContains<LinkContract> (LinkContracts.getAllLinkContracts(graph), linkContract1).contains());
		assertTrue(new IteratorContains<LinkContract> (LinkContracts.getAllLinkContracts(graph), linkContract2).contains());

		graph.close();
	}

	public void testRelationshipLinkContract() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		ContextNode c1 = graph.setDeepContextNode(XDIAddress.create("(=bob/=alice)=alice#registration$contract"));
		RelationshipLinkContract l1 = (RelationshipLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c1));

		assertNotNull(l1);
		assertEquals(l1.getAuthorizingAuthority(), XDIAddress.create("=bob"));
		assertEquals(l1.getRequestingAuthority(), XDIAddress.create("=alice"));
		assertEquals(l1.getTemplateAuthorityAndId(), XDIAddress.create("=alice#registration"));

		ContextNode c2 = graph.setDeepContextNode(XDIAddress.create("(=bob/=alice)=alice#registration[$contract]*!:uuid:0e43479d-834e-085f-3e8a-faa060afe9cf"));
		RelationshipLinkContract l2 = (RelationshipLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c2));

		assertNotNull(l2);
		assertEquals(l2.getAuthorizingAuthority(), XDIAddress.create("=bob"));
		assertEquals(l2.getRequestingAuthority(), XDIAddress.create("=alice"));
		assertEquals(l2.getTemplateAuthorityAndId(), XDIAddress.create("=alice#registration"));

		ContextNode c3 = graph.setDeepContextNode(XDIAddress.create("(=!1111/=!2222)=!2222#registration$contract"));
		RelationshipLinkContract l3 = (RelationshipLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c3));

		assertNotNull(l3);
		assertEquals(l3.getAuthorizingAuthority(), XDIAddress.create("=!1111"));
		assertEquals(l3.getRequestingAuthority(), XDIAddress.create("=!2222"));
		assertEquals(l3.getTemplateAuthorityAndId(), XDIAddress.create("=!2222#registration"));

		ContextNode c4 = graph.setDeepContextNode(XDIAddress.create("(=!1111/=!2222)=!2222#registration[$contract]*!:uuid:272406ef-1e57-1325-fdba-700e16ac1132"));
		RelationshipLinkContract l4 = (RelationshipLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c4));

		assertNotNull(l4);
		assertEquals(l4.getAuthorizingAuthority(), XDIAddress.create("=!1111"));
		assertEquals(l4.getRequestingAuthority(), XDIAddress.create("=!2222"));
		assertEquals(l4.getTemplateAuthorityAndId(), XDIAddress.create("=!2222#registration"));

		ContextNode c5 = graph.setDeepContextNode(XDIAddress.create("(#friend/$public)$contract"));
		RelationshipLinkContract l5 = (RelationshipLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c5));

		assertNotNull(l5);
		assertEquals(l5.getAuthorizingAuthority(), XDIAddress.create("#friend"));
		assertEquals(l5.getRequestingAuthority(), XDIAddress.create("$public"));
		assertNull(l5.getTemplateAuthorityAndId());

		graph.close();
	}

	public void testNestedRelationshipLinkContract() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		ContextNode c1 = graph.setDeepContextNode(XDIAddress.create("(=bob[$msg]*!:uuid:1234/$connect$push)(=bob/=alice)$defer$push$contract"));
		RelationshipLinkContract l1 = (RelationshipLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c1));

		assertNotNull(l1);
		assertEquals(l1.getAuthorizingAuthority(), XDIAddress.create("=bob"));
		assertEquals(l1.getRequestingAuthority(), XDIAddress.create("=alice"));
		assertEquals(l1.getTemplateAuthorityAndId(), XDIAddress.create("$defer$push"));

		ContextNode c2 = graph.setDeepContextNode(XDIAddress.create("(=bob[$msg]*!:uuid:1234/$connect$push)(=bob/=alice)$defer$push[$contract]*!:uuid:1234"));
		RelationshipLinkContract l2 = (RelationshipLinkContract) LinkContract.fromXdiEntity(XdiAbstractEntity.fromContextNode(c2));

		assertNotNull(l2);
		assertEquals(l2.getAuthorizingAuthority(), XDIAddress.create("=bob"));
		assertEquals(l2.getRequestingAuthority(), XDIAddress.create("=alice"));
		assertEquals(l2.getTemplateAuthorityAndId(), XDIAddress.create("$defer$push"));

		graph.close();
	}

	public void testRootLinkContract() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		GraphUtil.setOwnerXDIAddress(graph, XDIAddress.create("=markus"));
		assertEquals(GraphUtil.getOwnerXDIAddress(graph), XDIAddress.create("=markus"));

		RootLinkContract l = RootLinkContract.findRootLinkContract(graph, true);
		assertNotNull(l);
		assertEquals(l.getXdiEntity().getXDIAddress(), XDIAddress.create("(=markus/=markus)$contract"));

		assertNotNull(RootLinkContract.findRootLinkContract(graph, false));
		assertTrue(LinkContract.fromXdiEntity(l.getXdiEntity()) instanceof RootLinkContract);

		assertEquals(l.getRequestingAuthority(), XDIAddress.create("=markus"));
		assertEquals(l.getAuthorizingAuthority(), XDIAddress.create("=markus"));
		assertNull(l.getTemplateAuthorityAndId());

		graph.close();
	}

	public void testPublicLinkContract() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		GraphUtil.setOwnerXDIAddress(graph, XDIAddress.create("=markus"));
		assertEquals(GraphUtil.getOwnerXDIAddress(graph), XDIAddress.create("=markus"));

		PublicLinkContract l = PublicLinkContract.findPublicLinkContract(graph, true);
		assertNotNull(l);
		assertEquals(l.getXdiEntity().getXDIAddress(), XDIAddress.create("(=markus/$public)$contract"));

		assertNotNull(PublicLinkContract.findPublicLinkContract(graph, false));
		assertTrue(LinkContract.fromXdiEntity(l.getXdiEntity()) instanceof PublicLinkContract);

		assertEquals(l.getRequestingAuthority(), XDILinkContractConstants.XDI_ADD_PUBLIC);
		assertEquals(l.getAuthorizingAuthority(), XDIAddress.create("=markus"));
		assertNull(l.getTemplateAuthorityAndId());

		graph.close();
	}

	public void testConnectLinkContract() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		GraphUtil.setOwnerXDIAddress(graph, XDIAddress.create("=markus"));
		assertEquals(GraphUtil.getOwnerXDIAddress(graph), XDIAddress.create("=markus"));

		ConnectLinkContract l = ConnectLinkContract.findConnectLinkContract(graph, true);
		assertNotNull(l);
		assertEquals(l.getXdiEntity().getXDIAddress(), XDIAddress.create("(=markus/$connect)$contract"));

		assertNotNull(ConnectLinkContract.findConnectLinkContract(graph, false));
		assertTrue(LinkContract.fromXdiEntity(l.getXdiEntity()) instanceof ConnectLinkContract);

		assertEquals(l.getRequestingAuthority(), XDILinkContractConstants.XDI_ADD_CONNECT);
		assertEquals(l.getAuthorizingAuthority(), XDIAddress.create("=markus"));
		assertNull(l.getTemplateAuthorityAndId());

		graph.close();
	}

	public void testLinkContractTemplate() throws Exception {

		XDIAddress XDIaddress = XDIAddress.create("=markus#registration{$contract}");

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode contextNode = graph.setDeepContextNode(XDIaddress);

		LinkContractTemplate l1 = LinkContractTemplate.findLinkContractTemplate(graph, XDIAddress.create("=markus#registration"), false);
		assertNotNull(l1);
		assertEquals(l1.getTemplateAuthorityAndId(), XDIAddress.create("=markus#registration"));

		LinkContractTemplate l2 = LinkContractTemplate.fromXdiEntitySingletonVariable(XdiEntitySingleton.Variable.fromContextNode(contextNode));
		assertNotNull(l2);
		assertEquals(l2.getTemplateAuthorityAndId(), XDIAddress.create("=markus#registration"));

		assertEquals(l1, l2);

		graph.close();
	}

	public void testPermissions() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		LinkContract lc = RelationshipLinkContract.findRelationshipLinkContract(graph, XDIAddress.create("=a"), XDIAddress.create("=b"), null, null, true);
		lc.setPermissionTargetXDIAddress(XDILinkContractConstants.XDI_ADD_GET, XDIAddress.create("=a"));
		lc.setPermissionTargetXDIStatement(XDILinkContractConstants.XDI_ADD_GET, XDIStatement.create("=a/#b/=c"));

		assertTrue(lc.getPermissionTargetXDIAddresses(XDILinkContractConstants.XDI_ADD_GET).hasNext());
		assertEquals(lc.getPermissionTargetXDIAddresses(XDILinkContractConstants.XDI_ADD_GET).next(), XDIAddress.create("=a"));
		assertTrue(lc.getPermissionTargetXDIStatements(XDILinkContractConstants.XDI_ADD_GET).hasNext());
		assertEquals(lc.getPermissionTargetXDIStatements(XDILinkContractConstants.XDI_ADD_GET).next(), XDIStatement.create("=a/#b/=c"));

		lc.delPermissionTargetXDIAddress(XDILinkContractConstants.XDI_ADD_GET, XDIAddress.create("=a"));
		lc.delPermissionTargetXDIStatements(XDILinkContractConstants.XDI_ADD_GET);

		System.out.println(graph.toString("XDI DISPLAY"));

		assertFalse(lc.getPermissionTargetXDIAddresses(XDILinkContractConstants.XDI_ADD_GET).hasNext());
		assertFalse(lc.getPermissionTargetXDIStatements(XDILinkContractConstants.XDI_ADD_GET).hasNext());

		graph.close();
	}
}
