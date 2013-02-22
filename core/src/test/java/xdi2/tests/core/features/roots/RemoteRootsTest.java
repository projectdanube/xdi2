package xdi2.tests.core.features.roots;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.features.roots.RemoteRoot;
import xdi2.core.features.roots.Roots;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class RemoteRootsTest extends TestCase {

	public void testRemoteRootXris() throws Exception {

		assertFalse(RemoteRoot.isRemoteRootXri(XDI3SubSegment.create("()")));
		assertTrue(RemoteRoot.isRemoteRootXri(XDI3SubSegment.create("(=!1111!23)")));
		assertFalse(RemoteRoot.isRemoteRootXri(XDI3SubSegment.create("(=a*b/+c*d)")));

		assertFalse(RemoteRoot.isRemoteRootXri(XDI3SubSegment.create("$(+c)")));
		assertFalse(RemoteRoot.isRemoteRootXri(XDI3SubSegment.create("$(!1)")));
		assertFalse(RemoteRoot.isRemoteRootXri(XDI3SubSegment.create("$!(!1)")));

		assertEquals(RemoteRoot.createRemoteRootXri(XDI3Segment.create("=!1111!23")), XDI3SubSegment.create("(=!1111!23)"));
		assertEquals(RemoteRoot.getXriOfRemoteRootXri(XDI3SubSegment.create("(=!1111!23)")), XDI3Segment.create("=!1111!23"));
	}

	public void testRemoteRoots() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		assertEquals(Roots.findLocalRoot(graph).findRemoteRoot(XDI3Segment.create("=!1111!23"), true).getContextNode().getXri(), XDI3SubSegment.create("(=!1111!23)"));
	}

	public void testSelfRemoteRoots() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		Roots.findLocalRoot(graph).setSelfRemoteRoot(XDI3Segment.create("=!1111!23"));

		RemoteRoot selfRemoteRoot = Roots.findLocalRoot(graph).getSelfRemoteRoot();

		assertEquals(selfRemoteRoot.getContextNode().getXri(), XDI3Segment.create("(=!1111!23)"));
		assertEquals(Roots.findLocalRoot(graph).findRemoteRoot(XDI3Segment.create("=!1111!23"), false), selfRemoteRoot);
		assertTrue(selfRemoteRoot.isSelfRemoteRoot());

		graph.close();
	}
}
