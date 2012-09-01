package xdi2.tests.core.features.linkcontracts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
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
import xdi2.core.features.multiplicity.XdiAttributeSingleton;
import xdi2.core.features.multiplicity.XdiEntitySingleton;
import xdi2.core.features.multiplicity.XdiSubGraph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.readers.XDIDisplayReader;
import xdi2.core.io.readers.XDIJSONReader;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class LinkContractsTest extends TestCase {

	private static final Logger log = LoggerFactory
			.getLogger(LinkContractsTest.class);

	private final XDIReader autoReader = XDIReaderRegistry.getAuto();
	private final XDIReader xdiJSONReader = new XDIJSONReader(null);
	private final XDIReader xdiStatementsReader = new XDIDisplayReader(null);

	private final XDIWriter jsonWriter = XDIWriterRegistry.forFormat(
			"XDI/JSON", null);
	private final XDIWriter statementsWriter = XDIWriterRegistry.forFormat(
			"XDI DISPLAY", null);
	
	private final Context cx = Context.enter();
	private final Scriptable scope = cx.initStandardObjects();

	private MemoryGraphFactory graphFactory = new MemoryGraphFactory();

	
	protected Graph openNewGraph(String id) {

		return this.graphFactory.openGraph();
	}

	
	protected Graph reopenGraph(Graph graph, String id) throws IOException {

		return graph;
	}

	public void testDummy() {

	}

	public Graph testReadJson(String fileName) throws Exception {

		Graph graph = this.openNewGraph(fileName + "-graph-json");

		xdiJSONReader
				.read(graph, this.getClass().getResourceAsStream(fileName))
				.close();
		testGraph(graph);

		return graph;
	}

	public Graph testReadStatements(String fileName) throws Exception {

		Graph graph3 = this.openNewGraph(fileName + "-graph-statements");

		xdiStatementsReader.read(graph3,
				this.getClass().getResourceAsStream(fileName)).close();
		testGraph(graph3);

		return graph3;
	}

	public Graph testReadFormatAutoDetect(String fileName) throws Exception {

		Graph graph3 = this.openNewGraph(fileName + "-graph-autodetect");

		if (this.getClass().getResourceAsStream(fileName) != null){
			autoReader.read(graph3, this.getClass().getResourceAsStream(fileName))
			.close();
		
		testGraph(graph3);
		
		}
		return graph3;
	}

	public static void testGraph(Graph graph) throws Exception {

	}

	public static void makeGraph(Graph graph) {

	}

	public void testWriteGraph(Graph graph, String fileName) throws Exception {

		String[] writerFormats = new String[] { "XDI/JSON", "XDI DISPLAY" };

		
		log.info("Write JSON: " + writerFormats[0]);
		XDIWriter writer = XDIWriterRegistry.forFormat(writerFormats[0], null);
		writer.write(graph, new FileWriter(new File("json-" + fileName + ".out"))).close();
		log.info("Write XDI Statements: " + writerFormats[1]);
		writer = XDIWriterRegistry.forFormat(writerFormats[1], null);
		writer.write(graph, new FileWriter(new File("XDIStatements-"+ fileName + ".out"))).close();
		
//		Graph g1 = testReadFormatAutoDetect("XDIStatements-"+ fileName + ".out");
//		assertEquals(g1.compareTo(graph), 0);
//		Graph g2 = testReadFormatAutoDetect("json-"+ fileName + ".out");
//		assertEquals(g2.compareTo(graph), 0);

	}

	public Graph makeGraphWithoutAuth() throws Exception {
		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		//=markus is the owner of the graph
		ContextNode markus = graph.getRootContextNode().createContextNode(
				new XRI3SubSegment("=markus"));		
		ContextNode animesh = graph.getRootContextNode().createContextNode(
				new XRI3SubSegment("=animesh"));
		LinkContract linkContract = LinkContracts.getLinkContract(markus, true,
				false);
		XdiEntitySingleton entitySingleton = XdiSubGraph.fromContextNode(graph.getRootContextNode()).getEntitySingleton(new XRI3SubSegment("$secret"), true);
		XdiAttributeSingleton attributeSingleton = entitySingleton.getAttributeSingleton(new XRI3SubSegment("$token"), true);

		attributeSingleton.getContextNode().createLiteral("secret");
		linkContract.addPermission(XDILinkContractPermission.LC_OP_ALL,
				graph.getRootContextNode());
		linkContract.addAssignee(animesh);
		return graph;
	}
	public Graph makeGraphWithAuth() throws Exception {
		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		//=markus is the owner of the graph
		ContextNode markus = graph.getRootContextNode().createContextNode(
				new XRI3SubSegment("=markus"));		
		ContextNode animesh = graph.getRootContextNode().createContextNode(
				new XRI3SubSegment("=animesh"));
		LinkContract linkContract = LinkContracts.getLinkContract(markus, true,
				true);
		XdiEntitySingleton entitySingleton = XdiSubGraph.fromContextNode(graph.getRootContextNode()).getEntitySingleton(new XRI3SubSegment("$secret"), true);
		XdiAttributeSingleton attributeSingleton = entitySingleton.getAttributeSingleton(new XRI3SubSegment("$token"), true);

		attributeSingleton.getContextNode().createLiteral("secret");
		linkContract.addPermission(XDILinkContractPermission.LC_OP_ALL,
				graph.getRootContextNode());
		linkContract.addAssignee(animesh);
		return graph;
	}
	public Graph makeGraphWithPolicyExpression() throws Exception {
		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		//=markus is the owner of the graph
		ContextNode markus = graph.getRootContextNode().createContextNode(
				new XRI3SubSegment("=markus"));		
		ContextNode animesh = graph.getRootContextNode().createContextNode(
				new XRI3SubSegment("=animesh"));
		LinkContract linkContract = LinkContracts.getLinkContract(markus, true,
				false);
		XdiEntitySingleton entitySingleton = XdiSubGraph.fromContextNode(graph.getRootContextNode()).getEntitySingleton(new XRI3SubSegment("$secret"), true);
		XdiAttributeSingleton attributeSingleton = entitySingleton.getAttributeSingleton(new XRI3SubSegment("$token"), true);

		attributeSingleton.getContextNode().createLiteral("secret");
		linkContract.addPermission(XDILinkContractPermission.LC_OP_ALL,
				graph.getRootContextNode());
		linkContract.addAssignee(animesh);
		Policy policy = linkContract.getPolicy(true);
		AndExpression andN = policy.getAndNode(true);
		NotExpression notN = andN.getNotNode(true);
		notN.addLiteralExpression("5 < 6");
		OrExpression orN = andN.getOrNode(true);
		orN.addLiteralExpression("\"To be\" == \"Not to be\"");
		orN.addLiteralExpression("\"I\" == \"Me\"");
		orN.addLiteralExpression("1 == 1");
		boolean result = policy.getPolicyExpressionComponent().evaluate(cx,scope);
		assertEquals(false, result);
		return graph;
	}
	public void testLinkContracts() throws Exception {
		
		int i = 1;

		try {
			while (true) {

				if (this.getClass().getResourceAsStream("graph" + i + ".xdi") == null)
					break;
				// Graph graph = graphFactory.openGraph();
				// autoReader.read(
				// graph,
				// this.getClass().getResourceAsStream(
				// "graph" + i + ".xdi")).close();
				log.info("Graph " + i);
				String fileName = "graph" + i + ".xdi";
				Graph graph = testReadFormatAutoDetect(fileName);
				Iterator<LinkContract> lcs = LinkContracts.getAllLinkContracts(graph);
				for(;lcs.hasNext();){
					LinkContract lc = lcs.next();
					if(i == 1 || i == 2){
						//check assignee
						Iterator<ContextNode> assignees = lc.getAssignees();
						ContextNode assignee = assignees.next();
						assertEquals(assignee.toString(), "=markus");
						//should be only one node is $is$do 
						assertEquals(assignees.hasNext(), false);
						//check operations permission
						Iterator<ContextNode> getPermissionNodes = lc.getNodesWithPermission(XDILinkContractPermission.LC_OP_GET);
						ContextNode getPerm = getPermissionNodes.next();
						//there should be only one node with $get permission
						assertEquals(getPerm.toString(), "=markus");
						assertEquals(getPermissionNodes.hasNext(), false);
						Iterator<ContextNode> addPermissionNodes = lc.getNodesWithPermission(XDILinkContractPermission.LC_OP_ADD);
						//there should be only one node with $add permission
						ContextNode addPerm = addPermissionNodes.next();
						assertEquals(addPermissionNodes.hasNext(), false);
						assertEquals(addPerm.toString(), "=markus");
						Iterator<ContextNode> modPermissionNodes = lc.getNodesWithPermission(XDILinkContractPermission.LC_OP_ADD);
						//there should be only one node with $add permission
						ContextNode modPerm = modPermissionNodes.next();
						assertEquals(modPermissionNodes.hasNext(), false);
						assertEquals(modPerm.toString(), "=markus");

						
					}
					
				}
				testWriteGraph(graph,fileName);
				graph.close();
				i++;
			}
			Graph g1 = makeGraphWithoutAuth();
			testWriteGraph(g1, "lc-1");
			Graph g2 = makeGraphWithAuth();
			testWriteGraph(g2, "lc-2");
			Graph g3 = makeGraphWithPolicyExpression();
			testWriteGraph(g3, "lc-3");
			
			Context.exit();
			log.info("Done.");

			// assertTrue(i > 1);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

	}

}
