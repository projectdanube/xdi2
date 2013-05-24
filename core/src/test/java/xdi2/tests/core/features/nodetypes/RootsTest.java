package xdi2.tests.core.features.nodetypes;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.XDI3Segment;

public class RootsTest extends TestCase {

	public void testSubGraph() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		XdiLocalRoot localRoot = XdiLocalRoot.findLocalRoot(graph);
		XdiPeerRoot peerRoot = localRoot.findPeerRoot(XDI3Segment.create("[=]!91F2.8153.F600.AE24"), true);
		XdiInnerRoot innerRoot = peerRoot.findInnerRoot(XDI3Segment.create("[=]!1111"), XDI3Segment.create("$add"), true);

		assertTrue(XdiAbstractContext.fromContextNode(localRoot.getContextNode()) instanceof XdiLocalRoot);
		assertTrue(XdiAbstractContext.fromContextNode(peerRoot.getContextNode()) instanceof XdiPeerRoot);
		assertTrue(XdiAbstractContext.fromContextNode(innerRoot.getContextNode()) instanceof XdiInnerRoot);
	}
}
