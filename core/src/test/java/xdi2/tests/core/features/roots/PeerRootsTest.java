package xdi2.tests.core.features.roots;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.features.roots.PeerRoot;
import xdi2.core.features.roots.Roots;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class PeerRootsTest extends TestCase {

	public void testPeerRootXris() throws Exception {

		assertFalse(PeerRoot.isPeerRootXri(XDI3SubSegment.create("()")));
		assertTrue(PeerRoot.isPeerRootXri(XDI3SubSegment.create("(=!1111!23)")));
		assertFalse(PeerRoot.isPeerRootXri(XDI3SubSegment.create("(=a*b/+c*d)")));

		assertFalse(PeerRoot.isPeerRootXri(XDI3SubSegment.create("$(+c)")));
		assertFalse(PeerRoot.isPeerRootXri(XDI3SubSegment.create("$(!1)")));
		assertFalse(PeerRoot.isPeerRootXri(XDI3SubSegment.create("$!(!1)")));

		assertEquals(PeerRoot.createPeerRootXri(XDI3Segment.create("=!1111!23")), XDI3SubSegment.create("(=!1111!23)"));
		assertEquals(PeerRoot.getXriOfPeerRootXri(XDI3SubSegment.create("(=!1111!23)")), XDI3Segment.create("=!1111!23"));
	}

	public void testPeerRoots() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		assertEquals(Roots.findLocalRoot(graph).findPeerRoot(XDI3Segment.create("=!1111!23"), true).getContextNode().getXri(), XDI3SubSegment.create("(=!1111!23)"));
	}

	public void testSelfPeerRoots() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		Roots.findLocalRoot(graph).setSelfPeerRoot(XDI3Segment.create("=!1111!23"));

		PeerRoot selfPeerRoot = Roots.findLocalRoot(graph).getSelfPeerRoot();

		assertEquals(selfPeerRoot.getContextNode().getXri(), XDI3Segment.create("(=!1111!23)"));
		assertEquals(Roots.findLocalRoot(graph).findPeerRoot(XDI3Segment.create("=!1111!23"), false), selfPeerRoot);
		assertTrue(selfPeerRoot.isSelfPeerRoot());

		graph.close();
	}
}
