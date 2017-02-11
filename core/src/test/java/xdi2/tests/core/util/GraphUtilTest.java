package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.GraphUtil;

public class GraphUtilTest extends TestCase {

	private static String TEST_GRAPH_DEREFERENCE = "" +
			"(=!:uuid:1a1a)/$ref/(=!:uuid:1111)" + "\n" +
			"(=!:uuid:1b1b)/$ref/(=!:uuid:1111)" + "\n" +
			"(=!:uuid:1111)/$ref/" + "\n" +
			"/$is$ref/(=!:uuid:1111)" + "\n" +
			"(=!:uuid:1111/=!:uuid:2222)$contract$do/$get/" + "\n" +
			"=!:uuid:1a1a/$ref/=!:uuid:1111" +
			"" +
			"" +
			"";

	private static XDIAddress TEST_CONTEXTNODE_1111 = XDIAddress.create("(=!:uuid:1111)");
	private static XDIAddress TEST_CONTEXTNODE_1a1a = XDIAddress.create("(=!:uuid:1a1a)");
	private static XDIAddress TEST_CONTEXTNODE_1b1b = XDIAddress.create("(=!:uuid:1b1b)");
	private static XDIAddress TEST_CONTEXTNODE_1111_2222_CONTRACT = XDIAddress.create("(=!:uuid:1111/=!:uuid:2222)$contract");
	private static XDIAddress TEST_CONTEXTNODE_1a1a_2222_CONTRACT = XDIAddress.create("(=!:uuid:1a1a/=!:uuid:2222)$contract");

	public void testGetOwnerXDIAddress() throws Exception {
		
		Graph graph = MemoryGraphFactory.getInstance().parseGraph(TEST_GRAPH_DEREFERENCE);

		assertEquals(GraphUtil.getOwnerPeerRootXDIArc(graph), XDIArc.fromComponent(TEST_CONTEXTNODE_1111));
		
		graph.close();
	}
	
	public void testOwnsPeerRootXDIArc() throws Exception {
		
		Graph graph = MemoryGraphFactory.getInstance().parseGraph(TEST_GRAPH_DEREFERENCE);

		assertTrue(GraphUtil.ownsPeerRootXDIArc(graph, XDIArc.fromComponent(TEST_CONTEXTNODE_1111)));
		assertTrue(GraphUtil.ownsPeerRootXDIArc(graph, XDIArc.fromComponent(TEST_CONTEXTNODE_1a1a)));
		assertTrue(GraphUtil.ownsPeerRootXDIArc(graph, XDIArc.fromComponent(TEST_CONTEXTNODE_1b1b)));
		
		graph.close();
	}
	
	public void testDereference() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().parseGraph(TEST_GRAPH_DEREFERENCE);

		ContextNode contextNode1111 = graph.getDeepContextNode(TEST_CONTEXTNODE_1111);
		ContextNode contextNode1a1a = graph.getDeepContextNode(TEST_CONTEXTNODE_1a1a);

		assertTrue(GraphUtil.dereference(contextNode1111).isRootContextNode());
		assertTrue(GraphUtil.dereference(contextNode1a1a).isRootContextNode());
		assertNotNull(graph.getDeepContextNode(TEST_CONTEXTNODE_1111_2222_CONTRACT));
		assertNull(graph.getDeepContextNode(TEST_CONTEXTNODE_1a1a_2222_CONTRACT));
		assertNotNull(GraphUtil.dereference(graph, TEST_CONTEXTNODE_1111_2222_CONTRACT));
		assertNotNull(GraphUtil.dereference(graph, TEST_CONTEXTNODE_1a1a_2222_CONTRACT));

		graph.close();
	}
}
