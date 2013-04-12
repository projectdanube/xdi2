package xdi2.tests.core.features.roots;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiAbstractSubGraph;
import xdi2.core.features.roots.XdiInnerRoot;
import xdi2.core.features.roots.XdiLocalRoot;
import xdi2.core.features.roots.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.XDI3Segment;

public class RootsTest extends TestCase {

	public void testSubGraph() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		XdiLocalRoot localRoot = XdiLocalRoot.findLocalRoot(graph);
		XdiPeerRoot peerRoot = localRoot.findPeerRoot(XDI3Segment.create("=!91F2.8153.F600.AE24"), true);
		XdiInnerRoot innerRoot = peerRoot.findInnerRoot(XDI3Segment.create("=!1111"), XDI3Segment.create("$add"), true);

		assertTrue(XdiAbstractSubGraph.fromContextNode(localRoot.getContextNode()) instanceof XdiLocalRoot);
		assertTrue(XdiAbstractSubGraph.fromContextNode(peerRoot.getContextNode()) instanceof XdiPeerRoot);
		assertTrue(XdiAbstractSubGraph.fromContextNode(innerRoot.getContextNode()) instanceof XdiInnerRoot);
	}
}
