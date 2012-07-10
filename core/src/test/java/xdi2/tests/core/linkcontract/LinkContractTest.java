package xdi2.tests.core.linkcontract;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.AndExpression;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.linkcontracts.NotExpression;
import xdi2.core.features.linkcontracts.OrExpression;
import xdi2.core.features.linkcontracts.Policy;
import xdi2.core.features.linkcontracts.util.XDILinkContractPermission;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;


public class LinkContractTest extends TestCase {
	
	private static final Logger log = LoggerFactory.getLogger(LinkContractTest.class);
	private MemoryGraphFactory graphFactory = new MemoryGraphFactory();
	
	public void testCreateLinkContract() throws Exception{
		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode markus = graph.getRootContextNode().createContextNode(new XRI3SubSegment("=markus"));
		ContextNode animesh = graph.getRootContextNode().createContextNode(new XRI3SubSegment("=animesh"));
		LinkContract linkContract = LinkContracts.getLinkContract(markus, true);
		linkContract.addPermission(XDILinkContractPermission.LC_OP_ALL, graph.getRootContextNode());
		//try adding the same assignee multiple times
		linkContract.addAssignee(animesh);
		linkContract.addAssignee(animesh);
		// ..do things with the link contract here..

		
		// ..do things with the policy here..
		System.out.println("Display the graph\n");		
		System.out.println(graph);
		System.out.println("Display all the Link Contracts in the graph\n");
		Iterator<LinkContract> lcs = LinkContracts.getAllLinkContracts(graph);
		for(;lcs.hasNext();){
			System.out.println(lcs.next());
		}
		System.out.println("Find a link contract by address");
		LinkContract aLC = LinkContracts.findLinkContractByAddress(graph, new XRI3Segment("=markus$do"));
		System.out.println(aLC);
		System.out.println("Remove assignee");
		//linkContract.removeAssignee(animesh);
		System.out.println("Display the graph");
		System.out.println(graph);
		//try to remove permission arcs which do not exist
		linkContract.removePermission(XDILinkContractPermission.LC_OP_ALL, graph.getRootContextNode());
		linkContract.removePermission(XDILinkContractPermission.LC_OP_GET, graph.getRootContextNode());
		System.out.println("Display the graph");
		System.out.println(graph);
		//try to add $all permission along with something else
		//linkContract.addPermission(XDILinkContractPermission.LC_OP_ALL, graph.getRootContextNode());
		linkContract.addPermission(XDILinkContractPermission.LC_OP_GET, graph.getRootContextNode());
		linkContract.addPermission(XDILinkContractPermission.LC_OP_ADD, graph.getRootContextNode());
		System.out.println(linkContract.getNodesWithPermission(XDILinkContractPermission.LC_OP_ALL));
		System.out.println("Display the graph");
		System.out.println(graph);
		//try to remove a wrong context node
		//linkContract.removeAssignee(markus);
		System.out.println("Display the graph");
		System.out.println(graph);
		Policy policy = linkContract.getPolicy(true);
		// ((b & c ) & ( d or e) & !f)
		AndExpression andN = policy.getAndNode(true);

		andN.addLiteralExpression("a");
		//andN.addLiteralExpression("b");
		//andN.addLiteralExpression("c");
		//andN.addLiteralExpression("d");

		OrExpression andNOr1 = andN.getOrNode(true);
		andNOr1.addLiteralExpression("d");
		andNOr1.addLiteralExpression("e");
		//andNOr1.addLiteralExpression("g");
		//andN.removeLiteralExpression("a");
		NotExpression notN = andN.getNotNode(true);
		notN.addLiteralExpression("f");
		notN.addLiteralExpression("g");
		notN.setLiteralExpression("h");

		//policy.setLiteralExpression("a+b+c");
		//System.out.println("Literal Expression="+policy.getLiteralExpression());
		System.out.println("Display the graph-1");
		System.out.println(graph.toString("STATEMENTS_WITH_CONTEXT_STATEMENTS"));
		
		System.out.println("Logic Expr:" + andN.getLogicExpression());
		

		//XDIWriter writer = XDIWriterRegistry.forFormat("STATEMENTS");
		XDIWriter writer = XDIWriterRegistry.forFormat("STATEMENTS_WITH_CONTEXT_STATEMENTS");
		//XDIWriter writer = XDIWriterRegistry.forFormat("XDI/JSON_WITH_CONTEXT_STATEMENTS");
		//XDIWriter writer = XDIWriterRegistry.forFormat("XDI/JSON");
		writer.write(graph, new FileWriter(new File("C:\\identity\\lctest.out")), null).close();
		Graph graph2 = MemoryGraphFactory.getInstance().openGraph();
		XDIReader reader = XDIReaderRegistry.forFormat("STATEMENTS");		
		//XDIReader reader = XDIReaderRegistry.forFormat("XDI/JSON");
		reader.read(graph2, new FileReader(new File("C:\\identity\\lctest.out")), null).close();
		System.out.println("Display the graph-2");
		System.out.println(graph2.toString("STATEMENTS_WITH_CONTEXT_STATEMENTS"));
		
		//Graph graph3 = MemoryGraphFactory.getInstance().openGraph();
		  
		 

	}
	
	public static void main(String args[]){
		LinkContractTest lcTest = new LinkContractTest();
		try {
			lcTest.testCreateLinkContract();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
