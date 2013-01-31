package xdi2.tests.core.features.remoteroots;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.XDI3Segment;

public class RemoteRootsTest extends TestCase {

	public void testRemoteRoots() throws Exception {

		assertTrue(RemoteRoots.isRemoteRootXri(XDI3Segment.create("(=web*markus)")));
		assertTrue(RemoteRoots.isRemoteRootXri(XDI3Segment.create("(=!91F2.8153.F600.AE24)")));
		assertFalse(RemoteRoots.isRemoteRootXri(XDI3Segment.create("=web*markus")));
		assertFalse(RemoteRoots.isRemoteRootXri(XDI3Segment.create("=!91F2.8153.F600.AE24")));
		assertEquals(RemoteRoots.remoteRootXri(XDI3Segment.create("=web*markus")), XDI3Segment.create("(=web*markus)"));
		assertEquals(RemoteRoots.remoteRootXri(XDI3Segment.create("=!91F2.8153.F600.AE24")), XDI3Segment.create("(=!91F2.8153.F600.AE24)"));
		assertEquals(RemoteRoots.xriOfRemoteRootXri(XDI3Segment.create("(=web*markus)")), XDI3Segment.create("=web*markus"));
		assertEquals(RemoteRoots.xriOfRemoteRootXri(XDI3Segment.create("(=!91F2.8153.F600.AE24)")), XDI3Segment.create("=!91F2.8153.F600.AE24"));

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, XDI3Segment.create("=web*markus"), true).getXri(), XDI3Segment.create("(=web*markus)"));
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, XDI3Segment.create("=!91F2.8153.F600.AE24"), true).getXri(), XDI3Segment.create("(=!91F2.8153.F600.AE24)"));
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, XDIConstants.XRI_S_ROOT, true).getXri(), XDI3Segment.create("(())"));
	}

	public void testSelfRemoteRoots() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		RemoteRoots.setSelfRemoteRootContextNode(graph, XDI3Segment.create("=!1111"));

		ContextNode selfRemoteRootContextNode = RemoteRoots.getSelfRemoteRootContextNode(graph);

		assertEquals(selfRemoteRootContextNode.getXri(), XDI3Segment.create("(=!1111)"));
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, XDI3Segment.create("=!1111"), false), selfRemoteRootContextNode);
		assertTrue(RemoteRoots.isRemoteRootContextNode(selfRemoteRootContextNode));
	}
}
