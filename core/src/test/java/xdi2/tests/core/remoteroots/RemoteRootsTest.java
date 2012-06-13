package xdi2.tests.core.remoteroots;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.remoteroots.RemoteRoots;
import xdi2.core.xri3.impl.XRI3Segment;

public class RemoteRootsTest extends TestCase {

	public void testRemoteRoots() throws Exception {

		assertTrue(RemoteRoots.isRemoteRootXri(new XRI3Segment("(=web*markus)")));
		assertTrue(RemoteRoots.isRemoteRootXri(new XRI3Segment("(=!91F2.8153.F600.AE24)")));
		assertFalse(RemoteRoots.isRemoteRootXri(new XRI3Segment("=web*markus")));
		assertFalse(RemoteRoots.isRemoteRootXri(new XRI3Segment("=!91F2.8153.F600.AE24")));
		assertEquals(RemoteRoots.getRemoteRootXri(new XRI3Segment("=web*markus")), new XRI3Segment("(=web*markus)"));
		assertEquals(RemoteRoots.getRemoteRootXri(new XRI3Segment("=!91F2.8153.F600.AE24")), new XRI3Segment("(=!91F2.8153.F600.AE24)"));
		assertEquals(RemoteRoots.getXriOfRemoteRootXri(new XRI3Segment("(=web*markus)")), new XRI3Segment("=web*markus"));
		assertEquals(RemoteRoots.getXriOfRemoteRootXri(new XRI3Segment("(=!91F2.8153.F600.AE24)")), new XRI3Segment("=!91F2.8153.F600.AE24"));

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, new XRI3Segment("=web*markus"), true).getXri(), new XRI3Segment("(=web*markus)"));
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, new XRI3Segment("=!91F2.8153.F600.AE24"), true).getXri(), new XRI3Segment("(=!91F2.8153.F600.AE24)"));
		assertEquals(RemoteRoots.findRemoteRootContextNode(graph, new XRI3Segment("()"), true).getXri(), new XRI3Segment("(())"));
	}
}
