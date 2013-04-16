package xdi2.tests.core.features.roots;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.features.roots.XdiLocalRoot;
import xdi2.core.features.roots.XdiPeerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class PeerRootsTest extends TestCase {

	public void testPeerRootXris() throws Exception {
		
		assertFalse(XdiPeerRoot.isPeerRootArcXri(XDI3SubSegment.create("()")));
		assertTrue(XdiPeerRoot.isPeerRootArcXri(XDI3SubSegment.create("(=!1111!23)")));
		assertFalse(XdiPeerRoot.isPeerRootArcXri(XDI3SubSegment.create("(=a*b/+c*d)")));

		assertFalse(XdiPeerRoot.isPeerRootArcXri(XDI3SubSegment.create("[<+c>]")));
		assertFalse(XdiPeerRoot.isPeerRootArcXri(XDI3SubSegment.create("{1}")));
		assertFalse(XdiPeerRoot.isPeerRootArcXri(XDI3SubSegment.create("{[<+(name)>]}")));
		assertFalse(XdiPeerRoot.isPeerRootArcXri(XDI3SubSegment.create("<+(name)>")));
		assertFalse(XdiPeerRoot.isPeerRootArcXri(XDI3SubSegment.create("[+(name)]")));
		assertFalse(XdiPeerRoot.isPeerRootArcXri(XDI3SubSegment.create("+(name)")));

		assertEquals(XdiPeerRoot.createPeerRootArcXri(XDI3Segment.create("=!1111!23")), XDI3SubSegment.create("(=!1111!23)"));
		assertEquals(XdiPeerRoot.getXriOfPeerRootArcXri(XDI3SubSegment.create("(=!1111!23)")), XDI3Segment.create("=!1111!23"));
	}

	public void testPeerRoots() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		assertEquals(XdiLocalRoot.findLocalRoot(graph).findPeerRoot(XDI3Segment.create("=!1111!23"), true).getContextNode().getXri(), XDI3SubSegment.create("(=!1111!23)"));
	}

	public void testSelfPeerRoots() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		XdiLocalRoot.findLocalRoot(graph).setSelfPeerRoot(XDI3Segment.create("=!1111!23"));

		XdiPeerRoot selfPeerRoot = XdiLocalRoot.findLocalRoot(graph).getSelfPeerRoot();

		assertEquals(selfPeerRoot.getContextNode().getXri(), XDI3Segment.create("(=!1111!23)"));
		assertEquals(XdiLocalRoot.findLocalRoot(graph).findPeerRoot(XDI3Segment.create("=!1111!23"), false), selfPeerRoot);
		assertTrue(selfPeerRoot.isSelfPeerRoot());

		graph.close();
	}
}
