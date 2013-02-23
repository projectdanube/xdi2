package xdi2.tests.core.features.roots;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.roots.Roots;
import xdi2.core.impl.memory.MemoryGraphFactory;

public class LocalRootsTest extends TestCase {

	public void testLocalRoots() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		assertEquals(Roots.findLocalRoot(graph).getContextNode(), graph.getRootContextNode());
		assertEquals(Roots.findLocalRoot(graph).getContextNode().getXri(), XDIConstants.XRI_S_CONTEXT);

		graph.close();
	}
}
