package xdi2.tests.core.features.remoteroots;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.impl.XDI3Segment;

public class RemoteRootsTest extends TestCase {

	public void testRemoteRoots() throws Exception {

		assertTrue(RemoteRoots.isRemoteRootXri(new XDI3Segment("(=web*markus)")));
		assertTrue(RemoteRoots.isRemoteRootXri(new XDI3Segment("(=!91F2.8153.F600.AE24)")));
		assertFalse(RemoteRoots.isRemoteRootXri(new XDI3Segment("=web*markus")));
		assertFalse(RemoteRoots.isRemoteRootXri(new XDI3Segment("=!91F2.8153.F600.AE24")));
		assertEquals(RemoteRoots.remoteRootXri(new XDI3Segment("=web*markus")), new XDI3Segment("(=web*markus)"));
		assertEquals(RemoteRoots.remoteRootXri(new XDI3Segment("=!91F2.8153.F600.AE24")), new XDI3Segment("(=!91F2.8153.F600.AE24)"));
		assertEquals(RemoteRoots.xriOfRemoteRootXri(new XDI3Segment("(=web*markus)")), new XDI3Segment("=web*markus"));
		assertEquals(RemoteRoots.xriOfRemoteRootXri(new XDI3Segment("(=!91F2.8153.F600.AE24)")), new XDI3Segment("=!91F2.8153.F600.AE24"));

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, new XDI3Segment("=web*markus"), true).getXri(), new XDI3Segment("(=web*markus)"));
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, new XDI3Segment("=!91F2.8153.F600.AE24"), true).getXri(), new XDI3Segment("(=!91F2.8153.F600.AE24)"));
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, XDIConstants.XRI_S_ROOT, true).getXri(), new XDI3Segment("(())"));
	}

	public void testSelfRemoteRoots() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		RemoteRoots.setSelfRemoteRootContextNode(graph, new XDI3Segment("=!1111"));

		ContextNode selfRemoteRootContextNode = RemoteRoots.getSelfRemoteRootContextNode(graph);

		assertEquals(selfRemoteRootContextNode.getXri(), new XDI3Segment("(=!1111)"));
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, new XDI3Segment("=!1111"), false), selfRemoteRootContextNode);
		assertTrue(RemoteRoots.isRemoteRootContextNode(selfRemoteRootContextNode));
	}
}
