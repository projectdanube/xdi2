package xdi2.tests.core.features.nodetypes;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

public class PeerRootsTest extends TestCase {

	public void testPeerRootAddresss() throws Exception {
		
		assertFalse(XdiPeerRoot.isPeerRootArc(XDIArc.create("")));
		assertTrue(XdiPeerRoot.isPeerRootArc(XDIArc.create("([=]!1111!23)")));
		assertFalse(XdiPeerRoot.isPeerRootArc(XDIArc.create("(=a*b/+c*d)")));

		assertFalse(XdiPeerRoot.isPeerRootArc(XDIArc.create("[<+c>]")));
		assertFalse(XdiPeerRoot.isPeerRootArc(XDIArc.create("{1}")));
		assertFalse(XdiPeerRoot.isPeerRootArc(XDIArc.create("{[<+(name)>]}")));
		assertFalse(XdiPeerRoot.isPeerRootArc(XDIArc.create("<+(name)>")));
		assertFalse(XdiPeerRoot.isPeerRootArc(XDIArc.create("[+(name)]")));
		assertFalse(XdiPeerRoot.isPeerRootArc(XDIArc.create("+(name)")));

		assertEquals(XdiPeerRoot.createPeerRootArc(XDIAddress.create("[=]!1111!23")), XDIArc.create("([=]!1111!23)"));
		assertEquals(XdiPeerRoot.getAddressOfPeerRootArc(XDIArc.create("([=]!1111!23)")), XDIAddress.create("[=]!1111!23"));
	}

	public void testPeerRoots() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		assertEquals(XdiLocalRoot.findLocalRoot(graph).getPeerRoot(XDIAddress.create("[=]!1111!23"), true).getContextNode().getAddress(), XDIArc.create("([=]!1111!23)"));
		
		graph.close();
	}

	public void testSelfPeerRoots() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		XdiLocalRoot.findLocalRoot(graph).setSelfPeerRoot(XDIAddress.create("[=]!1111!23"));

		XdiPeerRoot selfPeerRoot = XdiLocalRoot.findLocalRoot(graph).getSelfPeerRoot();

		assertEquals(selfPeerRoot.getContextNode().getAddress(), XDIAddress.create("([=]!1111!23)"));
		assertEquals(XdiLocalRoot.findLocalRoot(graph).getPeerRoot(XDIAddress.create("[=]!1111!23"), false), selfPeerRoot);
		assertTrue(selfPeerRoot.isSelfPeerRoot());

		graph.close();
	}
}
