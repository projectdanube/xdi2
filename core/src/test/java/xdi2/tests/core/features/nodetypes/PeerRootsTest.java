package xdi2.tests.core.features.nodetypes;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

public class PeerRootsTest extends TestCase {

	public void testPeerRootXDIArces() throws Exception {
		
		assertFalse(XdiPeerRoot.isValidXDIArc(XDIArc.create("")));
		assertTrue(XdiPeerRoot.isValidXDIArc(XDIArc.create("(=!1111)")));
		assertFalse(XdiPeerRoot.isValidXDIArc(XDIArc.create("(=a*b/+c*d)")));

		assertFalse(XdiPeerRoot.isValidXDIArc(XDIArc.create("[<+c>]")));
		assertFalse(XdiPeerRoot.isValidXDIArc(XDIArc.create("{*1}")));
		assertFalse(XdiPeerRoot.isValidXDIArc(XDIArc.create("{[<+(name)>]}")));
		assertFalse(XdiPeerRoot.isValidXDIArc(XDIArc.create("<+(name)>")));
		assertFalse(XdiPeerRoot.isValidXDIArc(XDIArc.create("[+(name)]")));
		assertFalse(XdiPeerRoot.isValidXDIArc(XDIArc.create("+(name)")));

		assertEquals(XdiPeerRoot.createPeerRootXDIArc(XDIAddress.create("=!1111")), XDIArc.create("(=!1111)"));
		assertEquals(XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(XDIArc.create("(=!1111)")), XDIAddress.create("=!1111"));
	}

	public void testPeerRoots() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		assertEquals(XdiCommonRoot.findCommonRoot(graph).getPeerRoot(XDIAddress.create("=!1111"), true).getContextNode().getXDIAddress(), XDIArc.create("(=!1111)"));
		
		graph.close();
	}

	public void testSelfPeerRoots() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		XdiCommonRoot.findCommonRoot(graph).setSelfPeerRoot(XDIAddress.create("=!1111"));

		XdiPeerRoot selfPeerRoot = XdiCommonRoot.findCommonRoot(graph).getSelfPeerRoot();

		assertEquals(selfPeerRoot.getContextNode().getXDIAddress(), XDIAddress.create("(=!1111)"));
		assertEquals(XdiCommonRoot.findCommonRoot(graph).getPeerRoot(XDIAddress.create("=!1111"), false), selfPeerRoot);
		assertTrue(selfPeerRoot.isSelfPeerRoot());

		graph.close();
	}
}
