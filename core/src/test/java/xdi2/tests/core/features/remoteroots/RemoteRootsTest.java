package xdi2.tests.core.features.remoteroots;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.impl.XRI3Segment;

public class RemoteRootsTest extends TestCase {

	public void testRemoteRoots() throws Exception {

		assertTrue(RemoteRoots.isRemoteRootXri(new XRI3Segment("(=web*markus)")));
		assertTrue(RemoteRoots.isRemoteRootXri(new XRI3Segment("(=!91F2.8153.F600.AE24)")));
		assertFalse(RemoteRoots.isRemoteRootXri(new XRI3Segment("=web*markus")));
		assertFalse(RemoteRoots.isRemoteRootXri(new XRI3Segment("=!91F2.8153.F600.AE24")));
		assertEquals(RemoteRoots.remoteRootXri(new XRI3Segment("=web*markus")), new XRI3Segment("(=web*markus)"));
		assertEquals(RemoteRoots.remoteRootXri(new XRI3Segment("=!91F2.8153.F600.AE24")), new XRI3Segment("(=!91F2.8153.F600.AE24)"));
		assertEquals(RemoteRoots.xriOfRemoteRootXri(new XRI3Segment("(=web*markus)")), new XRI3Segment("=web*markus"));
		assertEquals(RemoteRoots.xriOfRemoteRootXri(new XRI3Segment("(=!91F2.8153.F600.AE24)")), new XRI3Segment("=!91F2.8153.F600.AE24"));

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, new XRI3Segment("=web*markus"), true).getXri(), new XRI3Segment("(=web*markus)"));
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, new XRI3Segment("=!91F2.8153.F600.AE24"), true).getXri(), new XRI3Segment("(=!91F2.8153.F600.AE24)"));
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, XDIConstants.XRI_S_ROOT, true).getXri(), new XRI3Segment("(())"));
	}

	public void testSelfRemoteRoots() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		RemoteRoots.setSelfRemoteRootContextNode(graph, new XRI3Segment("=!1111"));

		ContextNode selfRemoteRootContextNode = RemoteRoots.getSelfRemoteRootContextNode(graph);

		assertEquals(selfRemoteRootContextNode.getXri(), new XRI3Segment("(=!1111)"));
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, new XRI3Segment("=!1111"), false), selfRemoteRootContextNode);
		assertTrue(RemoteRoots.isRemoteRootContextNode(selfRemoteRootContextNode));
	}
}
