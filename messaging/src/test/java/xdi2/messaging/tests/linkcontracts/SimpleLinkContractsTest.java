package xdi2.messaging.tests.linkcontracts;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.linkcontracts.util.XDILinkContractPermission;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.xri3.impl.XDI3Segment;

public class SimpleLinkContractsTest extends TestCase {

	private static final XDIReader autoReader = XDIReaderRegistry.getAuto();

	private static MemoryGraphFactory graphFactory = new MemoryGraphFactory();

	public void testSimpleLinkContracts() throws Exception {

		Graph graph = graphFactory.openGraph();

		autoReader.read(graph, this.getClass().getResourceAsStream("simple.xdi"));
		ContextNode contextNode1111_1 = graph.findContextNode(new XDI3Segment("$(=!1111)$(!1)"), false);
		ContextNode contextNode1111_2 = graph.findContextNode(new XDI3Segment("$(=!1111)$(!2)"), false);
		ContextNode contextNode4444 = graph.findContextNode(new XDI3Segment("@!4444"), false);

		assertEquals(new IteratorCounter(LinkContracts.getAllLinkContracts(graph)).count(), 1);
		assertNull(LinkContracts.getLinkContract(contextNode1111_1, false));
		assertNotNull(LinkContracts.getLinkContract(contextNode1111_2, false));
		assertNotNull(LinkContracts.findLinkContractByAddress(graph, new XDI3Segment("$(=!1111)$(!2)$do")));
		assertNull(LinkContracts.findLinkContractByAddress(graph, new XDI3Segment("$(=!1111)$(!2)")));

		LinkContract linkContract = LinkContracts.getLinkContract(contextNode1111_2, false);
		assertEquals(linkContract.getNodesWithPermission(XDILinkContractPermission.LC_OP_GET).next(), contextNode1111_2);
		assertEquals(linkContract.getAssignees().next(), contextNode4444);
	}
}
