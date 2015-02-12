package xdi2.tests.core.features.nodetypes;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;

public class CommonRootTest extends TestCase {

	public void testCommonRoot() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		assertEquals(XdiCommonRoot.findCommonRoot(graph).getContextNode(), graph.getRootContextNode());
		assertEquals(XdiCommonRoot.findCommonRoot(graph).getContextNode().getXDIAddress(), XDIConstants.XDI_ADD_ROOT);

		graph.close();
	}
}
