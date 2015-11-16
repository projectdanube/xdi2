package xdi2.tests.core.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.LiteralNode;
import xdi2.core.Relation;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.tests.AbstractTestCase;

public abstract class AbstractGraphTest extends AbstractTestCase {

	protected abstract GraphFactory getGraphFactory() throws IOException;
	protected abstract boolean supportsPersistence();

	public void testSimple() throws Exception {

		Graph graph0 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-0");

		ContextNode markus = graph0.getRootContextNode().setContextNode(XDIArc.create("=markus"));
		ContextNode email = markus.setContextNode(XDIArc.create("<#email>"));
		email.setLiteralNode("abc@gmail.com");
		markus.setRelation(XDIAddress.create("#friend"), XDIAddress.create("=drummond"));

		markus = graph0.getRootContextNode().getContextNode(XDIArc.create("=markus"));
		assertNotNull(markus);
		assertFalse(markus.isRootContextNode());
		assertFalse(markus.isEmpty());
		assertFalse(markus.isLeafContextNode());
		assertTrue(markus.containsRelations());
		assertFalse(markus.containsLiteralNode());
		assertTrue(email.isLeafContextNode());
		assertTrue(email.containsLiteralNode());

		ContextNode drummond = graph0.getRootContextNode().getContextNode(XDIArc.create("=drummond"));
		assertNotNull(drummond);
		assertTrue(drummond.isEmpty());
		assertTrue(drummond.isLeafContextNode());

		email.setLiteralNode("xyz@gmail.com");
		assertEquals(graph0.getDeepLiteralNode(XDIAddress.create("=markus<#email>&")).getLiteralData(), "xyz@gmail.com");
		assertEquals(graph0.getDeepContextNode(XDIAddress.create("=markus<#email>")).getLiteralNode().getLiteralData(), "xyz@gmail.com");

		graph0.close();
	}

	public void testMakeGraph() throws Exception {

		Graph graph1 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-1");

		makeGraph(graph1);
		testGraph(graph1);

		graph1.close();
	}

	public void testReopenGraph() throws Exception {

		if (! this.supportsPersistence()) return;

		Graph graph2 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-2");

		makeGraph(graph2);
		graph2.close();

		graph2 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-2");
		testGraph(graph2);

		graph2.close();
	}

	public void testReadJson() throws Exception {

		Graph graph3 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-3");

		XDIReader reader = XDIReaderRegistry.forFormat("XDI/JSON", null);

		reader.read(graph3, AbstractGraphTest.class.getResourceAsStream("test.json")).close();

		testGraph(graph3);

		graph3.getRootContextNode().clear();
		assertTrue(graph3.isEmpty());

		graph3.close();
	}

	public void testReadWriteFormats() throws Exception {

		String[] formats = new String[] { "XDI/JSON", "XDI DISPLAY" };

		for (int i=0; i<formats.length; i++) {

			File file = new File("xdi.out");

			Graph graph4 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-4" + "-" + i);
			Graph graph5 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-5" + "-" + i);

			XDIWriter writer = XDIWriterRegistry.forFormat(formats[i], null);
			XDIReader reader = XDIReaderRegistry.forFormat(formats[i], null);

			FileWriter fileWriter = new FileWriter(file);
			FileReader fileReader = new FileReader(file);

			makeGraph(graph4);
			writer.write(graph4, fileWriter);
			reader.read(graph5, fileReader);

			fileWriter.close();
			fileReader.close();

			testGraph(graph5);
			testGraphsEqual(graph4, graph5);

			graph4.close();
			graph5.close();

			file.delete();
		}
	}

	public void testManipulate() throws Exception {

		Graph graph8 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-8");

		makeGraph(graph8);
		manipulateGraph(graph8);
		testManipulatedGraph(graph8);

		graph8.close();
	}

	public void testManipulateAndReopenGraph() throws Exception {

		if (! this.supportsPersistence()) return;

		Graph graph9 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-9");

		makeGraph(graph9);
		graph9.close();

		graph9 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-9");
		manipulateGraph(graph9);
		graph9.close();

		graph9 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-9");
		testManipulatedGraph(graph9);

		graph9.close();
	}

	public void testCopy() throws Exception {

		Graph graph10 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-10");
		Graph graph11 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-11");

		try {

			makeGraph(graph10);
			CopyUtil.copyGraph(graph10, graph11, null);
			testGraph(graph11);

			assertEquals(graph10, graph11);
			assertEquals(graph10.hashCode(), graph11.hashCode());
			assertEquals(graph10.compareTo(graph11), 0);
			assertEquals(graph11.compareTo(graph10), 0);

			StringWriter buffer1 = new StringWriter();
			StringWriter buffer2 = new StringWriter();
			XDIWriterRegistry.forFormat("XDI/JSON", null).write(graph10, buffer1);
			XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(graph11, buffer2);
			graph10.clear();
			graph11.clear();
			XDIReaderRegistry.forFormat("XDI/JSON", null).read(graph10, new StringReader(buffer1.toString()));
			XDIReaderRegistry.forFormat("XDI DISPLAY", null).read(graph11, new StringReader(buffer2.toString()));

			assertEquals(graph10, graph11);
			assertEquals(graph10.hashCode(), graph11.hashCode());
			assertEquals(graph10.compareTo(graph11), 0);
			assertEquals(graph11.compareTo(graph10), 0);

			graph11.getRootContextNode().setContextNode(XDIArc.create("=xxx"));

			assertNotEquals(graph10, graph11);
			assertNotEquals(graph10.hashCode(), graph11.hashCode());
			assertNotEquals(graph10.compareTo(graph11), 0);
		} finally {

			graph10.close();
			graph11.close();
		}
	}

	public void testDeleteDeep() throws Exception {

		Graph graph12 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-12");
		assertEquals(graph12.getRootContextNode(), graph12.getDeepContextNode(XDIConstants.XDI_ADD_ROOT));
		assertEquals(graph12.getRootContextNode().getXDIAddress(), XDIConstants.XDI_ADD_ROOT);

		graph12.setDeepContextNode(XDIAddress.create("=markus")).setRelation(XDIAddress.create("#friend"), XDIAddress.create("=someone"));
		graph12.getDeepContextNode(XDIAddress.create("=markus")).delete();
		graph12.setDeepContextNode(XDIAddress.create("=markus"));
		assertNull(graph12.getDeepContextNode(XDIAddress.create("=markus")).getRelation(XDIAddress.create("#friend")));

		graph12.close();
	}

	public void testRoot() throws Exception {

		Graph graph13 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-13");
		ContextNode root = graph13.getRootContextNode();

		assertEquals(root.getXDIAddress(), XDIConstants.XDI_ADD_ROOT);
		assertNull(root.getXDIArc());
		assertNull(root.getContextNode());
		assertTrue(root.isRootContextNode());

		assertTrue(graph13.isEmpty());
		assertTrue(root.isEmpty());
		assertFalse(root.containsContextNodes());
		assertFalse(root.containsRelations());
		assertFalse(root.containsLiteralNode());
		assertFalse(root.getContextNodes().hasNext());
		assertFalse(root.getRelations().hasNext());
		assertNull(root.getLiteralNode());
		assertEquals(root.getContextNodeCount(), 0);
		assertEquals(root.getRelationCount(), 0);
		assertFalse(root.getAllContextNodes().hasNext());
		assertFalse(root.getAllRelations().hasNext());
		assertFalse(root.getAllLiterals().hasNext());

		root.setRelation(XDIAddress.create("*arc"), XDIAddress.create("=target"));
		root.setContextNode(XDIArc.create("<#test>")).setLiteralNode("test");

		assertFalse(root.isEmpty());
		assertTrue(root.containsContextNodes());
		assertTrue(root.containsRelations());
		assertFalse(root.containsLiteralNode());
		assertTrue(root.getContextNodes().hasNext());
		assertTrue(root.getRelations().hasNext());
		assertNotNull(root.getRelations().next().follow());
		assertNull(root.getLiteralNode());
		assertEquals(root.getContextNodeCount(), 2);
		assertEquals(root.getRelationCount(), 1);
		assertTrue(root.getAllContextNodes().hasNext());
		assertTrue(root.getAllRelations().hasNext());
		assertTrue(root.getAllLiterals().hasNext());

		root.setContextNode(XDIArc.create("#name"));
		root.setContextNode(XDIArc.create("#email"));

		assertFalse(root.isEmpty());
		assertTrue(root.containsContextNodes());
		assertTrue(root.containsRelations());
		assertFalse(root.containsLiteralNode());
		assertTrue(root.getContextNodes().hasNext());
		assertTrue(root.getRelations().hasNext());
		assertNull(root.getLiteralNode());
		assertEquals(root.getContextNodeCount(), 4);
		assertEquals(root.getRelationCount(), 1);
		assertTrue(root.getAllContextNodes().hasNext());
		assertTrue(root.getAllRelations().hasNext());
		assertTrue(root.getAllLiterals().hasNext());

		root.getRelations().next().follow().delete();
		root.getContextNodes().next().delete();
		root.getContextNodes().next().delete();
		root.getContextNodes().next().delete();

		assertTrue(root.isEmpty());
		assertFalse(root.containsContextNodes());
		assertFalse(root.containsRelations());
		assertFalse(root.containsLiteralNode());
		assertFalse(root.getContextNodes().hasNext());
		assertFalse(root.getRelations().hasNext());
		assertNull(root.getLiteralNode());
		assertEquals(root.getContextNodeCount(), 0);
		assertEquals(root.getRelationCount(), 0);
		assertFalse(root.getAllContextNodes().hasNext());
		assertFalse(root.getAllRelations().hasNext());
		assertFalse(root.getAllLiterals().hasNext());

		graph13.close();
	}

	public void testRelations() throws Exception {

		Graph graph14 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-14");
		ContextNode root = graph14.getRootContextNode();
		ContextNode markus = root.setContextNode(XDIArc.create("=markus"));
		ContextNode target1 = root.setContextNode(XDIArc.create("=test")).setContextNode(XDIArc.create("=target1"));
		ContextNode target2 = root.getContextNode(XDIArc.create("=test")).setContextNode(XDIArc.create("=target2"));
		ContextNode target3 = root.getContextNode(XDIArc.create("=test")).setContextNode(XDIArc.create("=target3"));

		markus.setRelation(XDIAddress.create("#friend"), target1);
		markus.setRelation(XDIAddress.create("#friend"), target2);
		markus.setRelation(XDIAddress.create("+brother"), target3);
		root.setRelation(XDIAddress.create("#rel"), markus);

		assertTrue(root.containsRelations());
		assertTrue(root.containsRelations(XDIAddress.create("#rel")));
		assertTrue(root.containsRelation(XDIAddress.create("#rel"), XDIAddress.create("=markus")));
		assertEquals(root.getRelationCount(), 1);
		assertEquals(root.getRelationCount(XDIAddress.create("#rel")), 1);
		assertEquals(root.getRelation(XDIAddress.create("#rel"), XDIAddress.create("=markus")).follow(), markus);
		assertEquals(root.getRelation(XDIAddress.create("#rel")).follow(), markus);
		assertEquals(new IteratorCounter(root.getRelations(XDIAddress.create("#rel"))).count(), 1);
		assertEquals(new IteratorCounter(root.getRelations()).count(), 1);

		assertTrue(markus.containsRelations());
		assertTrue(markus.containsRelations(XDIAddress.create("#friend")));
		assertTrue(markus.containsRelations(XDIAddress.create("+brother")));
		assertTrue(markus.containsRelation(XDIAddress.create("#friend"), XDIAddress.create("=test=target1")));
		assertTrue(markus.containsRelation(XDIAddress.create("#friend"), XDIAddress.create("=test=target2")));
		assertTrue(markus.containsRelation(XDIAddress.create("+brother"), XDIAddress.create("=test=target3")));
		assertEquals(markus.getRelationCount(), 3);
		assertEquals(markus.getRelationCount(XDIAddress.create("#friend")), 2);
		assertEquals(markus.getRelationCount(XDIAddress.create("+brother")), 1);
		assertNotNull(markus.getRelation(XDIAddress.create("#friend"), XDIAddress.create("=test=target1")));
		assertNotNull(markus.getRelation(XDIAddress.create("#friend"), XDIAddress.create("=test=target2")));
		assertNotNull(markus.getRelation(XDIAddress.create("+brother"), XDIAddress.create("=test=target3")));
		assertNotNull(markus.getRelation(XDIAddress.create("#friend")));
		assertNotNull(markus.getRelation(XDIAddress.create("+brother")));
		assertEquals(new IteratorCounter(markus.getRelations(XDIAddress.create("#friend"))).count(), 2);
		assertEquals(new IteratorCounter(markus.getRelations(XDIAddress.create("+brother"))).count(), 1);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 3);

		root.delRelations();
		markus.delRelations(XDIAddress.create("#friend"));
		markus.delRelation(XDIAddress.create("+brother"), XDIAddress.create("=test=target3"));

		assertFalse(root.containsRelations());
		assertFalse(root.containsRelations(XDIAddress.create("#rel")));
		assertFalse(root.containsRelation(XDIAddress.create("#rel"), XDIAddress.create("=markus")));
		assertEquals(root.getRelationCount(), 0);
		assertEquals(root.getRelationCount(XDIAddress.create("#rel")), 0);
		assertNull(root.getRelation(XDIAddress.create("#rel"), XDIAddress.create("=markus")));
		assertNull(root.getRelation(XDIAddress.create("#rel")));
		assertFalse(root.getRelations(XDIAddress.create("#rel")).hasNext());
		assertEquals(new IteratorCounter(root.getRelations(XDIAddress.create("#rel"))).count(), 0);
		assertEquals(new IteratorCounter(root.getRelations()).count(), 0);

		assertFalse(markus.containsRelations());
		assertFalse(markus.containsRelations(XDIAddress.create("#friend")));
		assertFalse(markus.containsRelations(XDIAddress.create("+brother")));
		assertFalse(markus.containsRelation(XDIAddress.create("#friend"), XDIAddress.create("=test=target1")));
		assertFalse(markus.containsRelation(XDIAddress.create("#friend"), XDIAddress.create("=test=target2")));
		assertFalse(markus.containsRelation(XDIAddress.create("+brother"), XDIAddress.create("=test=target3")));
		assertEquals(markus.getRelationCount(), 0);
		assertEquals(markus.getRelationCount(XDIAddress.create("#friend")), 0);
		assertEquals(markus.getRelationCount(XDIAddress.create("+brother")), 0);
		assertNull(markus.getRelation(XDIAddress.create("#friend"), XDIAddress.create("=test=target1")));
		assertNull(markus.getRelation(XDIAddress.create("#friend"), XDIAddress.create("=test=target2")));
		assertNull(markus.getRelation(XDIAddress.create("+brother"), XDIAddress.create("=test=target3")));
		assertNull(markus.getRelation(XDIAddress.create("#friend")));
		assertNull(markus.getRelation(XDIAddress.create("+brother")));
		assertEquals(new IteratorCounter(markus.getRelations(XDIAddress.create("#friend"))).count(), 0);
		assertEquals(new IteratorCounter(markus.getRelations(XDIAddress.create("+brother"))).count(), 0);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 0);

		graph14.close();
	}

	public void testContextNodeXDIAddresses() throws Exception {

		Graph graph15 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-15");
		ContextNode root = graph15.getRootContextNode();

		ContextNode c = root.setDeepContextNode(XDIAddress.create("+a+b+c"));
		ContextNode b = c.getContextNode();
		ContextNode a = b.getContextNode();

		ContextNode timestamp = c.setDeepContextNode(XDIAddress.create("+d<$t>"));
		ContextNode d = timestamp.getContextNode();

		Relation r = c.setRelation(XDIAddress.create("#x#y"), b);
		LiteralNode l = timestamp.setLiteralNode("test");

		assertTrue(a.getContextNode().isRootContextNode());
		assertNull(a.getContextNode().getContextNode());

		assertFalse(a.isLeafContextNode());
		assertFalse(b.isLeafContextNode());
		assertFalse(c.isLeafContextNode());
		assertFalse(d.isLeafContextNode());
		assertTrue(timestamp.isLeafContextNode());

		assertEquals(a.getXDIAddress(), XDIAddress.create("+a"));
		assertEquals(b.getXDIAddress(), XDIAddress.create("+a+b"));
		assertEquals(c.getXDIAddress(), XDIAddress.create("+a+b+c"));
		assertEquals(d.getXDIAddress(), XDIAddress.create("+a+b+c+d"));
		assertEquals(timestamp.getXDIAddress(), XDIAddress.create("+a+b+c+d<$t>"));
		assertEquals(a.getXDIArc(), XDIArc.create("+a"));
		assertEquals(b.getXDIArc(), XDIArc.create("+b"));
		assertEquals(c.getXDIArc(), XDIArc.create("+c"));
		assertEquals(d.getXDIArc(), XDIArc.create("+d"));
		assertEquals(timestamp.getXDIArc(), XDIArc.create("<$t>"));

		assertEquals(graph15.getDeepContextNode(XDIAddress.create("+a+b+c+d")), d);
		assertEquals(a.getDeepContextNode(XDIAddress.create("+b+c+d")), d);
		assertEquals(b.getDeepContextNode(XDIAddress.create("+c+d")), d);
		assertEquals(graph15.getDeepContextNode(XDIAddress.create("+a+b+c")).getRelation(XDIAddress.create("#x#y")), r);
		assertEquals(a.getDeepContextNode(XDIAddress.create("+b+c")).getRelation(XDIAddress.create("#x#y")), r);
		assertEquals(b.getDeepContextNode(XDIAddress.create("+c")).getRelation(XDIAddress.create("#x#y")), r);
		assertEquals(graph15.getDeepLiteralNode(XDIAddress.create("+a+b+c+d<$t>&")), l);
		assertEquals(a.getDeepLiteralNode(XDIAddress.create("+b+c+d<$t>&")), l);
		assertEquals(b.getDeepLiteralNode(XDIAddress.create("+c+d<$t>&")), l);

		graph15.close();
	}

	public void testIncomingRelations() throws Exception {

		Graph graph16 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-16");

		graph16.setStatement(XDIStatement.create("=markus/#friend/=animesh"));
		graph16.setStatement(XDIStatement.create("=markus/#friend/=neustar=les"));
		graph16.setStatement(XDIStatement.create("=!:uuid:1111=!:uuid:2222/$is/=markus"));

		ContextNode markus = graph16.getDeepContextNode(XDIAddress.create("=markus"));
		ContextNode animesh = graph16.getDeepContextNode(XDIAddress.create("=animesh"));
		ContextNode les = graph16.getDeepContextNode(XDIAddress.create("=neustar=les"));
		ContextNode inumber = graph16.getDeepContextNode(XDIAddress.create("=!:uuid:1111=!:uuid:2222"));

		assertEquals(graph16.getRootContextNode().getAllRelationCount(), 3);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 2);
		assertEquals(new IteratorCounter(animesh.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(les.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(inumber.getRelations()).count(), 1);
		assertEquals(new IteratorCounter(markus.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(markus.getIncomingRelations(XDIAddress.create("$is"))).count(), 1);
		assertEquals(new IteratorCounter(animesh.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(animesh.getIncomingRelations(XDIAddress.create("#friend"))).count(), 1);
		assertEquals(new IteratorCounter(les.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(les.getIncomingRelations(XDIAddress.create("#friend"))).count(), 1);
		assertEquals(new IteratorCounter(inumber.getIncomingRelations()).count(), 0);

		Relation friend1 = markus.getRelation(XDIAddress.create("#friend"), XDIAddress.create("=animesh"));
		Relation friend2 = markus.getRelation(XDIAddress.create("#friend"), XDIAddress.create("=neustar=les"));
		Relation is = graph16.getDeepContextNode(XDIAddress.create("=!:uuid:1111=!:uuid:2222")).getRelation(XDIAddress.create("$is"));

		assertTrue(new IteratorContains<Relation> (graph16.getDeepContextNode(XDIAddress.create("=markus")).getRelations(XDIAddress.create("#friend")), friend1).contains());
		assertTrue(new IteratorContains<Relation> (graph16.getDeepContextNode(XDIAddress.create("=markus")).getRelations(XDIAddress.create("#friend")), friend2).contains());

		assertEquals(friend1.getContextNode(), markus);
		assertEquals(friend1.follow(), animesh);
		assertEquals(friend2.getContextNode(), markus);
		assertEquals(friend2.follow(), les);
		assertEquals(is.getContextNode(), inumber);
		assertEquals(is.follow(), markus);

		animesh.delete();

		assertEquals(graph16.getRootContextNode().getAllRelationCount(), 2);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 1);
		assertEquals(new IteratorCounter(les.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(inumber.getRelations()).count(), 1);
		assertEquals(new IteratorCounter(markus.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(markus.getIncomingRelations(XDIAddress.create("$is"))).count(), 1);
		assertEquals(new IteratorCounter(les.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(les.getIncomingRelations(XDIAddress.create("#friend"))).count(), 1);
		assertEquals(new IteratorCounter(inumber.getIncomingRelations()).count(), 0);

		ContextNode neustar = les.getContextNode();
		neustar.delete();

		assertEquals(graph16.getRootContextNode().getAllRelationCount(), 1);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(inumber.getRelations()).count(), 1);
		assertEquals(new IteratorCounter(markus.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(markus.getIncomingRelations(XDIAddress.create("$is"))).count(), 1);
		assertEquals(new IteratorCounter(inumber.getIncomingRelations()).count(), 0);

		graph16.getRootContextNode().delContextNode(XDIArc.create("=markus"));

		assertEquals(graph16.getRootContextNode().getAllRelationCount(), 0);
		assertEquals(new IteratorCounter(inumber.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(inumber.getIncomingRelations()).count(), 0);

		assertEquals(graph16.getRootContextNode().getAllContextNodeCount(), 2);
		assertEquals(graph16.getRootContextNode().getAllRelationCount(), 0);
		assertEquals(graph16.getRootContextNode().getAllLiteralCount(), 0);
		assertEquals(graph16.getRootContextNode().getAllStatementCount(), 2);

		graph16.close();
	}

	public void testNoExceptions() throws Exception {

		Graph graph17 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-17");

		graph17.setStatement(XDIStatement.create("=markus<#email>/&/\"Markus Sabadello\""));
		graph17.setStatement(XDIStatement.create("=markus/#friend/=neustar=les"));

		ContextNode root = graph17.getRootContextNode();
		ContextNode markus = graph17.getDeepContextNode(XDIAddress.create("=markus"));
		ContextNode les = graph17.getDeepContextNode(XDIAddress.create("=neustar=les"));

		root.setContextNode(XDIArc.create("=markus"));
		markus.setDeepContextNode(XDIAddress.create("<#email>")).setLiteralNode("test");
		markus.setRelation(XDIAddress.create("#friend"), les);

		graph17.close();
	}

	public void testTransactions() throws Exception {

		Graph graph18 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-18");

		graph18.setStatement(XDIStatement.create("=markus<#email>/&/\"Markus Sabadello\""));
		graph18.setStatement(XDIStatement.create("=markus/#friend/=neustar=les"));

		ContextNode markus = graph18.getDeepContextNode(XDIAddress.create("=markus"));
		ContextNode attribute = markus.getDeepContextNode(XDIAddress.create("<#email>"));

		graph18.beginTransaction();

		attribute.delLiteralNode();
		markus.delRelations(XDIAddress.create("#friend"));
		markus.setRelation(XDIAddress.create("$is#"), XDIAddress.create("#person"));

		assertFalse(attribute.containsLiteralNode());
		assertFalse(markus.containsRelations(XDIAddress.create("#friend")));
		assertTrue(markus.containsRelation(XDIAddress.create("$is#"), XDIAddress.create("#person")));

		graph18.rollbackTransaction();

		if (graph18.supportsTransactions()) {

			assertTrue(attribute.containsLiteralNode());
			assertTrue(markus.containsRelations(XDIAddress.create("#friend")));
			assertFalse(markus.containsRelation(XDIAddress.create("$is#"), XDIAddress.create("#person")));
		} else {

			assertFalse(attribute.containsLiteralNode());
			assertFalse(markus.containsRelations(XDIAddress.create("#friend")));
			assertTrue(markus.containsRelation(XDIAddress.create("$is#"), XDIAddress.create("#person")));
		}

		graph18.beginTransaction();
		graph18.clear();
		graph18.rollbackTransaction();

		assertTrue(graph18.supportsTransactions() ? ! graph18.isEmpty() : graph18.isEmpty());

		graph18.beginTransaction();
		graph18.clear();
		graph18.commitTransaction();

		assertTrue(graph18.isEmpty());

		graph18.close();
	}

	public void testEmptyContextNode() throws Exception {

		Graph graph19 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-19");

		ContextNode markus = graph19.setDeepContextNode(XDIAddress.create("=markus"));

		assertNull(markus.getLiteralNode());
		assertFalse(markus.getContextNodes().hasNext());
		try { markus.getContextNodes().next(); fail(); } catch (NoSuchElementException ex) { }
		assertNull(markus.getContextNode(XDIArc.create("*not")));
		assertNull(markus.getDeepContextNode(XDIAddress.create("*not")));
		assertFalse(markus.getAllContextNodes().hasNext());
		try { markus.getAllContextNodes().next(); fail(); } catch (NoSuchElementException ex) { }
		assertFalse(markus.getAllLeafContextNodes().hasNext());
		try { markus.getAllLeafContextNodes().next(); fail(); } catch (NoSuchElementException ex) { }
		assertEquals(markus.getContextNodeCount(), 0);
		assertEquals(markus.getAllContextNodeCount(), 0);

		assertNull(markus.getRelation(XDIAddress.create("+not"), XDIAddress.create("=not")));
		assertNull(markus.getRelation(XDIAddress.create("+not")));
		assertFalse(markus.getRelations(XDIAddress.create("+not")).hasNext());
		try { markus.getRelations(XDIAddress.create("+not")).next(); fail(); } catch (NoSuchElementException ex) { }
		assertFalse(markus.getRelations().hasNext());
		try { markus.getRelations().next(); fail(); } catch (NoSuchElementException ex) { }
		try { markus.getIncomingRelations().next(); fail(); } catch (NoSuchElementException ex) { }
		assertFalse(markus.getAllRelations().hasNext());
		try { markus.getAllRelations().next(); fail(); } catch (NoSuchElementException ex) { }
		assertEquals(markus.getRelationCount(XDIAddress.create("+not")), 0);
		assertEquals(markus.getRelationCount(), 0);
		assertEquals(markus.getAllRelationCount(), 0);

		assertNull(markus.getLiteralNode());
		assertNull(markus.getDeepLiteralNode(XDIAddress.create("<*!not>&")));
		assertEquals(markus.getAllLiteralCount(), 0);

		assertEquals(markus.getAllStatementCount(), 0);

		graph19.close();
	}

	public void testIllegalarcs() throws Exception {

		Graph graph20 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-20");

		ContextNode markus = graph20.setDeepContextNode(XDIAddress.create("=markus"));

		try { markus.setContextNode(XDIArc.create("")); fail(); } catch (Xdi2GraphException ex) { }
		try { markus.setRelation(XDIAddress.create(""), XDIAddress.create("=animesh")); fail(); } catch (Xdi2GraphException ex) { }
		try { markus.setRelation(XDIAddress.create("&"), XDIAddress.create("=animesh")); fail(); } catch (Xdi2GraphException ex) { }

		Equivalence.setReferenceContextNode(markus, XDIAddress.create("=!:uuid:1234"));

		try { markus.setContextNode(XDIArc.create("<#email>")); fail(); } catch (Xdi2GraphException ex) { }
		try { markus.setRelation(XDIAddress.create("#friend"), XDIAddress.create("=animesh")); fail(); } catch (Xdi2GraphException ex) { }
		try { markus.setLiteralNode("hello"); fail(); } catch (Xdi2GraphException ex) { }

		Equivalence.getReferenceContextNode(markus).delete();
		markus.setRelation(XDIAddress.create("#friend"), XDIAddress.create("=animesh"));

		try { Equivalence.setReferenceContextNode(markus, XDIAddress.create("=!:uuid:1234")); fail(); } catch (Xdi2GraphException ex) { }

		graph20.close();
	}

	public void testImplied() throws Exception {

		Graph graph21 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-21");

		ContextNode webmarkus = graph21.setDeepContextNode(XDIAddress.create("=web=markus"));
		ContextNode animesh = graph21.setDeepContextNode(XDIAddress.create("=animesh"));
		Relation friend = webmarkus.setRelation(XDIAddress.create("#friend"), animesh);
		ContextNode value = webmarkus.setContextNode(XDIArc.create("<#name>"));
		LiteralNode name = value.setLiteralNode("Markus Sabadello");
		ContextNode web = webmarkus.getContextNode();

		assertTrue(webmarkus.getStatement().isImplied());
		assertTrue(animesh.getStatement().isImplied());
		assertFalse(friend.getStatement().isImplied());
		assertTrue(value.getStatement().isImplied());
		assertFalse(name.getStatement().isImplied());
		assertTrue(web.getStatement().isImplied());

		graph21.close();
	}

	public void testStatements() throws Exception {

		Graph graph22 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-22");
		Graph graph23 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-23");

		ContextNodeStatement statement22_1 = (ContextNodeStatement) graph22.setStatement(XDIStatement.create("=neustar//=les"));
		RelationStatement statement22_2 = (RelationStatement) graph22.setStatement(XDIStatement.create("=markus/#friend/=neustar=les"));
		LiteralStatement statement22_3 = (LiteralStatement) graph22.setStatement(XDIStatement.create("=markus<#email>/&/\"markus@projectdanube.org\""));

		assertNotNull(graph22.getDeepContextNode(XDIAddress.create("=markus")));
		assertNotNull(graph22.getDeepContextNode(XDIAddress.create("=neustar")));
		assertNotNull(graph22.getDeepContextNode(XDIAddress.create("=neustar=les")));
		assertNotNull(graph22.getDeepContextNode(XDIAddress.create("=markus")).getRelation(XDIAddress.create("#friend")));
		assertNotNull(graph22.getDeepContextNode(XDIAddress.create("=markus")).getRelation(XDIAddress.create("#friend"), XDIAddress.create("=neustar=les")));
		assertNotNull(graph22.getDeepContextNode(XDIAddress.create("=markus<#email>")).getLiteralNode());
		assertNotNull(graph22.getDeepContextNode(XDIAddress.create("=markus<#email>")).getLiteralNode("markus@projectdanube.org"));

		assertTrue(graph22.containsStatement(XDIStatement.create("=neustar//=les")));
		assertTrue(graph22.containsStatement(XDIStatement.create("=markus/#friend/=neustar=les")));
		assertTrue(graph22.containsStatement(XDIStatement.create("=markus<#email>/&/\"markus@projectdanube.org\"")));
		assertEquals(graph22.getStatement(XDIStatement.create("=neustar//=les")).getXDIStatement(), XDIStatement.create("=neustar//=les"));
		assertEquals(graph22.getStatement(XDIStatement.create("=markus/#friend/=neustar=les")).getXDIStatement(), "=markus/#friend/=neustar=les");
		assertEquals(graph22.getStatement(XDIStatement.create("=markus<#email>/&/\"markus@projectdanube.org\"")).getXDIStatement(), "=markus<#email>/&/\"markus@projectdanube.org\"");

		assertTrue(graph22.getStatement(XDIStatement.create("=neustar//=les")) instanceof ContextNodeStatement);
		assertTrue(graph22.getStatement(XDIStatement.create("=markus/#friend/=neustar=les")) instanceof RelationStatement);
		assertTrue(graph22.getStatement(XDIStatement.create("=markus<#email>/&/\"markus@projectdanube.org\"")) instanceof LiteralStatement);
		assertTrue(graph22.getStatement(XDIStatement.create("=neustar//=les")).getXDIStatement().isContextNodeStatement());
		assertTrue(graph22.getStatement(XDIStatement.create("=markus/#friend/=neustar=les")).getXDIStatement().isRelationStatement());
		assertTrue(graph22.getStatement(XDIStatement.create("=markus<#email>/&/\"markus@projectdanube.org\"")).getXDIStatement().isLiteralStatement());

		CopyUtil.copyStatement(statement22_1, graph23, null);
		CopyUtil.copyStatement(statement22_2, graph23, null);
		CopyUtil.copyStatement(statement22_3, graph23, null);

		assertNotNull(graph23.getDeepContextNode(XDIAddress.create("=markus")));
		assertNotNull(graph23.getDeepContextNode(XDIAddress.create("=neustar")));
		assertNotNull(graph23.getDeepContextNode(XDIAddress.create("=neustar=les")));
		assertNotNull(graph23.getDeepContextNode(XDIAddress.create("=markus")).getRelation(XDIAddress.create("#friend")));
		assertNotNull(graph23.getDeepContextNode(XDIAddress.create("=markus")).getRelation(XDIAddress.create("#friend"), XDIAddress.create("=neustar=les")));
		assertNotNull(graph23.getDeepContextNode(XDIAddress.create("=markus<#email>")).getLiteralNode());
		assertNotNull(graph23.getDeepContextNode(XDIAddress.create("=markus<#email>")).getLiteralNode("markus@projectdanube.org"));

		assertTrue(graph23.containsStatement(XDIStatement.create("=neustar//=les")));
		assertTrue(graph23.containsStatement(XDIStatement.create("=markus/#friend/=neustar=les")));
		assertTrue(graph23.containsStatement(XDIStatement.create("=markus<#email>/&/\"markus@projectdanube.org\"")));
		assertEquals(graph23.getStatement(XDIStatement.create("=neustar//=les")).getXDIStatement(), XDIStatement.create("=neustar//=les"));
		assertEquals(graph23.getStatement(XDIStatement.create("=markus/#friend/=neustar=les")).getXDIStatement(), "=markus/#friend/=neustar=les");
		assertEquals(graph23.getStatement(XDIStatement.create("=markus<#email>/&/\"markus@projectdanube.org\"")).getXDIStatement(), "=markus<#email>/&/\"markus@projectdanube.org\"");

		assertTrue(graph23.getStatement(XDIStatement.create("=neustar//=les")) instanceof ContextNodeStatement);
		assertTrue(graph23.getStatement(XDIStatement.create("=markus/#friend/=neustar=les")) instanceof RelationStatement);
		assertTrue(graph23.getStatement(XDIStatement.create("=markus<#email>/&/\"markus@projectdanube.org\"")) instanceof LiteralStatement);
		assertTrue(graph23.getStatement(XDIStatement.create("=neustar//=les")).getXDIStatement().isContextNodeStatement());
		assertTrue(graph23.getStatement(XDIStatement.create("=markus/#friend/=neustar=les")).getXDIStatement().isRelationStatement());
		assertTrue(graph23.getStatement(XDIStatement.create("=markus<#email>/&/\"markus@projectdanube.org\"")).getXDIStatement().isLiteralStatement());

		graph22.close();
		graph23.close();
	}

	public void testCreate() throws Exception {

		Graph graph24 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-24");

		ContextNode root = graph24.getRootContextNode();

		root.setContextNode(XDIArc.create("+a"));
		assertNotNull(root.getContextNode(XDIArc.create("+a")));
		assertNotNull(root.getDeepContextNode(XDIAddress.create("+a")));
		assertNotNull(graph24.getDeepContextNode(XDIAddress.create("+a")));
		root.setContextNode(XDIArc.create("+a"));
		root.setDeepContextNode(XDIAddress.create("+a"));
		graph24.setDeepContextNode(XDIAddress.create("+a"));

		root.setDeepContextNode(XDIAddress.create("+a+b"));
		assertNotNull(root.getContextNode(XDIArc.create("+a")).getContextNode(XDIArc.create("+b")));
		assertNotNull(root.getDeepContextNode(XDIAddress.create("+a+b")));
		assertNotNull(graph24.getDeepContextNode(XDIAddress.create("+a+b")));
		root.setContextNode(XDIArc.create("+a"));
		root.setDeepContextNode(XDIAddress.create("+a"));
		root.setDeepContextNode(XDIAddress.create("+a+b"));
		graph24.setDeepContextNode(XDIAddress.create("+a"));
		graph24.setDeepContextNode(XDIAddress.create("+a+b"));

		graph24.close();
	}

	public void testSet() throws Exception {

		Graph graph25 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-25");

		ContextNode root = graph25.getRootContextNode();

		root.setContextNode(XDIArc.create("+a"));
		assertNotNull(root.getContextNode(XDIArc.create("+a")));
		assertNotNull(root.getDeepContextNode(XDIAddress.create("+a")));
		assertNotNull(graph25.getDeepContextNode(XDIAddress.create("+a")));
		root.setContextNode(XDIArc.create("+a"));
		root.setDeepContextNode(XDIAddress.create("+a"));
		graph25.setDeepContextNode(XDIAddress.create("+a"));

		root.setDeepContextNode(XDIAddress.create("+a+b"));
		assertNotNull(root.getContextNode(XDIArc.create("+a")).getContextNode(XDIArc.create("+b")));
		assertNotNull(root.getDeepContextNode(XDIAddress.create("+a+b")));
		assertNotNull(graph25.getDeepContextNode(XDIAddress.create("+a+b")));
		root.setContextNode(XDIArc.create("+a"));
		root.setDeepContextNode(XDIAddress.create("+a"));
		root.setDeepContextNode(XDIAddress.create("+a+b"));
		graph25.setDeepContextNode(XDIAddress.create("+a"));
		graph25.setDeepContextNode(XDIAddress.create("+a+b"));

		graph25.close();
	}

	public void testDelete() throws Exception {

		Graph graph26 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-26");

		ContextNode root = graph26.getRootContextNode();

		root.setContextNode(XDIArc.create("+a"));
		assertTrue(root.containsContextNode(XDIArc.create("+a")));
		assertNotNull(root.getContextNode(XDIArc.create("+a")));
		root.delContextNode(XDIArc.create("+a"));
		assertFalse(graph26.getRootContextNode().containsContextNode(XDIArc.create("+a")));
		assertFalse(root.containsContextNode(XDIArc.create("+a")));
		assertNull(graph26.getRootContextNode().getContextNode(XDIArc.create("+a")));
		assertNull(root.getContextNode(XDIArc.create("+a")));

		root.setRelation(XDIAddress.create("+a"), XDIAddress.create("#b"));
		assertTrue(root.containsRelation(XDIAddress.create("+a"), XDIAddress.create("#b")));
		assertNotNull(root.getRelation(XDIAddress.create("+a"), XDIAddress.create("#b")));
		root.delRelation(XDIAddress.create("+a"), XDIAddress.create("#b"));
		assertFalse(root.containsRelation(XDIAddress.create("+a"), XDIAddress.create("#b")));
		assertNull(root.getRelation(XDIAddress.create("+a"), XDIAddress.create("#b")));

		root.setDeepContextNode(XDIAddress.create("<#a>")).setLiteralNode("a");
		assertTrue(root.getDeepContextNode(XDIAddress.create("<#a>")).containsLiteralNode("a"));
		assertNotNull(root.getDeepContextNode(XDIAddress.create("<#a>")).getLiteralNode("a"));
		assertNotNull(root.getDeepLiteralNode(XDIAddress.create("<#a>&")));
		root.getDeepContextNode(XDIAddress.create("<#a>")).delLiteralNode();
		assertFalse(root.getDeepContextNode(XDIAddress.create("<#a>")).containsLiteralNode("a"));
		assertNull(root.getDeepContextNode(XDIAddress.create("<#a>")).getLiteralNode("a"));
		assertNull(root.getDeepLiteralNode(XDIAddress.create("<#a>&")));

		root.getContextNode(XDIArc.create("#b")).delete();
		root.getDeepContextNode(XDIAddress.create("<#a>")).delete();

		assertTrue(root.isEmpty());

		graph26.close();
	}

	public void testDeleteContextNodesDeletesRelations() throws Exception {

		Graph graph27 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-27");

		Relation r1 = graph27.setDeepContextNode(XDIAddress.create("=animesh")).setRelation(XDIAddress.create("#friend"), XDIAddress.create("=markus"));
		Relation r2 = graph27.setDeepContextNode(XDIAddress.create("=markus")).setRelation(XDIAddress.create("#friend"), XDIAddress.create("=animesh"));

		ContextNode markus = (ContextNode) r1.follow();
		ContextNode animesh = (ContextNode) r2.follow();

		markus.delete();

		assertNotNull(graph27.getDeepContextNode(XDIAddress.create("=animesh")));

		assertFalse(graph27.getDeepContextNode(XDIAddress.create("=animesh")).getRelations().hasNext());
		assertFalse(graph27.getDeepContextNode(XDIAddress.create("=animesh")).getRelations(XDIAddress.create("#friend")).hasNext());
		assertEquals(graph27.getDeepContextNode(XDIAddress.create("=animesh")).getRelationCount(), 0);
		assertFalse(graph27.getDeepContextNode(XDIAddress.create("=animesh")).getIncomingRelations().hasNext());
		assertFalse(graph27.getDeepContextNode(XDIAddress.create("=animesh")).getIncomingRelations(XDIAddress.create("#friend")).hasNext());
		assertFalse(animesh.getRelations().hasNext());
		assertFalse(animesh.getRelations(XDIAddress.create("#friend")).hasNext());
		assertEquals(animesh.getRelationCount(), 0);
		assertFalse(animesh.getIncomingRelations().hasNext());
		assertFalse(animesh.getIncomingRelations(XDIAddress.create("#friend")).hasNext());

		animesh.delete();

		graph27.setStatement(XDIStatement.create("+a+b/$i/=x"));
		graph27.setStatement(XDIStatement.create("=x/$i/=y"));

		graph27.getRootContextNode().getContextNode(XDIArc.create("+a")).delete();

		assertEquals(graph27.getRootContextNode().getAllRelationCount(), 1);
		assertTrue(graph27.getRootContextNode().getContextNode(XDIArc.create("=x")).getRelations().hasNext());
		assertFalse(graph27.getRootContextNode().getContextNode(XDIArc.create("=x")).getIncomingRelations().hasNext());

		graph27.getRootContextNode().getContextNode(XDIArc.create("=x")).delete();

		assertEquals(graph27.getRootContextNode().getAllRelationCount(), 0);

		graph27.close();
	}

	public void testLiteralData() throws Exception {

		Graph graph28 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-28");

		ContextNode c = graph28.setDeepContextNode(XDIAddress.create("=markus<#test>"));

		String s = new String("Markus Sabadello");
		Double d = new Double(34);
		Boolean b = new Boolean(false);
		JsonArray a = new JsonArray();
		JsonObject o = new JsonObject();
		Object n = null;

		a.add(new JsonPrimitive("test"));
		a.add(new JsonPrimitive(Integer.valueOf(5)));
		a.add(new JsonPrimitive(Boolean.FALSE));

		o.add("one", new JsonPrimitive("Markus Sabadello"));
		o.add("two", new JsonPrimitive(Integer.valueOf(34)));
		o.add("three", new JsonPrimitive(Boolean.FALSE));

		c.setLiteralNode(s);
		assertEquals(c.getLiteralNode().getLiteralData(), s);
		assertEquals(c.getLiteralNode().getLiteralDataString(), s);
		assertNull(c.getLiteralNode().getLiteralDataNumber());
		assertNull(c.getLiteralNode().getLiteralDataBoolean());
		assertTrue(c.containsLiteralNode(s));
		assertTrue(c.containsLiteralString(s));

		c.setLiteralNode(d);
		assertEquals(c.getLiteralNode().getLiteralData(), d);
		assertNull(c.getLiteralNode().getLiteralDataString());
		assertEquals(c.getLiteralNode().getLiteralDataNumber(), d);
		assertNull(c.getLiteralNode().getLiteralDataBoolean());
		assertTrue(c.containsLiteralNode(d));
		assertTrue(c.containsLiteralNumber(d));

		c.setLiteralNode(b);
		assertEquals(c.getLiteralNode().getLiteralData(), b);
		assertNull(c.getLiteralNode().getLiteralDataString());
		assertNull(c.getLiteralNode().getLiteralDataNumber());
		assertEquals(c.getLiteralNode().getLiteralDataBoolean(), b);
		assertTrue(c.containsLiteralNode(b));
		assertTrue(c.containsLiteralBoolean(b));

		c.setLiteralNode(a);
		assertEquals(c.getLiteralNode().getLiteralData(), a);
		assertNull(c.getLiteralNode().getLiteralDataString());
		assertNull(c.getLiteralNode().getLiteralDataNumber());
		assertNull(c.getLiteralNode().getLiteralDataBoolean());
		assertTrue(c.containsLiteralNode(a));

		c.setLiteralNode(o);
		assertEquals(c.getLiteralNode().getLiteralData(), o);
		assertNull(c.getLiteralNode().getLiteralDataString());
		assertNull(c.getLiteralNode().getLiteralDataNumber());
		assertNull(c.getLiteralNode().getLiteralDataBoolean());
		assertTrue(c.containsLiteralNode(o));

		c.setLiteralNode(n);
		assertEquals(c.getLiteralNode().getLiteralData(), n);
		assertNull(c.getLiteralNode().getLiteralDataString());
		assertNull(c.getLiteralNode().getLiteralDataNumber());
		assertNull(c.getLiteralNode().getLiteralDataBoolean());
		assertTrue(c.containsLiteralNode(n));

		graph28.close();
	}

	public void testDoubleSet() throws Exception {

		Graph graph29 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-29");

		ContextNode c = graph29.setDeepContextNode(XDIAddress.create("=markus"));
		ContextNode a = graph29.setDeepContextNode(XDIAddress.create("=animesh"));

		c.setContextNode(XDIArc.create("<#email>"));
		c.setContextNode(XDIArc.create("<#email>"));

		c.setRelation(XDIAddress.create("#friend"), a);
		c.setRelation(XDIAddress.create("#friend"), a);
		c.setRelation(XDIAddress.create("#friend"), XDIAddress.create("=animesh"));
		c.setRelation(XDIAddress.create("#friend"), XDIAddress.create("=animesh"));

		assertEquals(c.getContextNodeCount(), 1);
		assertEquals(c.getAllContextNodeCount(), 1);
		assertEquals(c.getRelationCount(XDIAddress.create("#friend")), 1);
		assertEquals(c.getRelationCount(), 1);
		assertEquals(c.getAllRelationCount(), 1);

		c.delContextNode(XDIArc.create("<#email>"));
		c.delRelation(XDIAddress.create("#friend"), XDIAddress.create("=animesh"));

		assertEquals(c.getContextNodeCount(), 0);
		assertEquals(c.getAllContextNodeCount(), 0);
		assertEquals(c.getRelationCount(XDIAddress.create("#friend")), 0);
		assertEquals(c.getRelationCount(), 0);
		assertEquals(c.getAllRelationCount(), 0);

		graph29.close();
	}

	public void testInnerRoots() throws Exception {

		Graph graph30 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-30");

		ContextNode innerRootContextNode = graph30.getRootContextNode().setContextNode(XDIArc.create("(=a=b=c/+d)"));

		assertNotNull(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("(=a=b=c/+d)")));
		assertNotNull(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("=a=b=c")));
		assertNotNull(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("=a=b=c")).getRelation(XDIAddress.create("+d")));
		assertEquals(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("=a=b=c")).getRelation(XDIAddress.create("+d")).follow(), innerRootContextNode);
		assertEquals(graph30.getRootContextNode().getAllContextNodeCount(), 4);
		assertEquals(graph30.getRootContextNode().getAllRelationCount(), 1);
		assertEquals(graph30.getRootContextNode().getAllStatementCount(), 5);

		XdiInnerRoot innerRoot = XdiInnerRoot.fromContextNode(innerRootContextNode);

		assertEquals(innerRoot.getSubjectOfInnerRoot(), XDIAddress.create("=a=b=c"));
		assertEquals(innerRoot.getPredicateOfInnerRoot(), XDIAddress.create("+d"));

		graph30.getRootContextNode().getContextNode(XDIArc.create("=a")).delContextNode(XDIArc.create("=b"));

		assertNull(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("(=a=b=c/+d)")));
		assertNull(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("=a=b=c")));
		assertEquals(graph30.getRootContextNode().getAllContextNodeCount(), 1);
		assertEquals(graph30.getRootContextNode().getAllRelationCount(), 0);
		assertEquals(graph30.getRootContextNode().getAllStatementCount(), 1);

		graph30.getRootContextNode().setContextNode(XDIArc.create("(=a=b=c/+d)"));

		graph30.clear();

		assertNull(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("(=a=b=c/+d)")));
		assertNull(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("=a=b=c")));
		assertEquals(graph30.getRootContextNode().getAllContextNodeCount(), 0);
		assertEquals(graph30.getRootContextNode().getAllRelationCount(), 0);
		assertEquals(graph30.getRootContextNode().getAllStatementCount(), 0);

		XdiCommonRoot.findCommonRoot(graph30).getInnerRoot(XDIAddress.create("=a=b=c"), XDIAddress.create("+d"), true);

		assertNotNull(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("(=a=b=c/+d)")));
		assertNotNull(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("=a=b=c")));
		assertNotNull(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("=a=b=c")).getRelation(XDIAddress.create("+d")));
		assertEquals(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("=a=b=c")).getRelation(XDIAddress.create("+d")).follow(), innerRootContextNode);
		assertEquals(graph30.getRootContextNode().getAllContextNodeCount(), 4);
		assertEquals(graph30.getRootContextNode().getAllRelationCount(), 1);
		assertEquals(graph30.getRootContextNode().getAllStatementCount(), 5);

		graph30.clear();

		graph30.setDeepContextNode(XDIAddress.create("=a=b=c")).setRelation(XDIAddress.create("+d"), XDIAddress.create("(=a=b=c/+d)"));

		assertNotNull(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("(=a=b=c/+d)")));
		assertNotNull(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("=a=b=c")));
		assertNotNull(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("=a=b=c")).getRelation(XDIAddress.create("+d")));
		assertEquals(graph30.getRootContextNode().getDeepContextNode(XDIAddress.create("=a=b=c")).getRelation(XDIAddress.create("+d")).follow(), innerRootContextNode);
		assertEquals(graph30.getRootContextNode().getAllContextNodeCount(), 4);
		assertEquals(graph30.getRootContextNode().getAllRelationCount(), 1);
		assertEquals(graph30.getRootContextNode().getAllStatementCount(), 5);

		graph30.clear();

		graph30.setStatement(XDIStatement.create("(=a/+b)(=x/+y)(=mm/+nn)=oo/+pp/=qq"));

		graph30.clear();

		graph30.setDeepContextNode(XDIAddress.create("=a=b=x")).setRelation(XDIAddress.create("+d"), XDIAddress.create("(=a=b=c/+d)"));
		graph30.setDeepContextNode(XDIAddress.create("=a=b=c")).setRelation(XDIAddress.create("+x"), XDIAddress.create("(=a=b=c/+d)"));

		assertEquals(graph30.getRootContextNode().getAllStatementCount(), 8);

		graph30.clear();

		assertTrue(graph30.isEmpty());

		graph30.close();
	}

	public void testDeleteCyclicRelation() throws Exception {

		Graph graph31 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-31");

		graph31.setStatement(XDIStatement.create("=a=b=c=d=e/+x/=a=b=c"));
		graph31.setStatement(XDIStatement.create("=m=n=o/+y/=a=b=c=d"));

		graph31.getDeepContextNode(XDIAddress.create("=a=b")).delete();

		assertEquals(graph31.getRootContextNode().getAllContextNodeCount(), 4);
		assertEquals(graph31.getRootContextNode().getAllRelationCount(), 0);
		assertEquals(graph31.getRootContextNode().getAllStatementCount(), 4);

		graph31.close();
	}

	public void testSetContextNodeSetInnerRoot() throws Exception {

		Graph graph32 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-32");

		graph32.getRootContextNode().setContextNode(XDIArc.create("(=a/#b)"));
		assertEquals(graph32.getAllStatementCount(), 3);
		assertTrue(graph32.containsStatement(XDIStatement.create("//(=a/#b)")));
		assertTrue(graph32.containsStatement(XDIStatement.create("//=a")));
		assertTrue(graph32.containsStatement(XDIStatement.create("=a/#b/(=a/#b)")));
		graph32.clear();

		graph32.getRootContextNode().setContextNode(XDIArc.create("(=a/)"));
		assertEquals(graph32.getAllStatementCount(), 2);
		assertTrue(graph32.containsStatement(XDIStatement.create("//(=a/)")));
		assertTrue(graph32.containsStatement(XDIStatement.create("//=a")));
		graph32.clear();

		graph32.getRootContextNode().setContextNode(XDIArc.create("(/#b)"));
		assertEquals(graph32.getAllStatementCount(), 2);
		assertTrue(graph32.containsStatement(XDIStatement.create("//(/#b)")));
		assertTrue(graph32.containsStatement(XDIStatement.create("/#b/(/#b)")));
		graph32.clear();

		graph32.getRootContextNode().setContextNode(XDIArc.create("(/)"));
		assertEquals(graph32.getAllStatementCount(), 1);
		assertTrue(graph32.containsStatement(XDIStatement.create("//(/)")));
		graph32.clear();

		graph32.getRootContextNode().setContextNode(XDIArc.create("(=a)"));
		assertEquals(graph32.getAllStatementCount(), 1);
		assertTrue(graph32.containsStatement(XDIStatement.create("//(=a)")));
		graph32.clear();

		graph32.getRootContextNode().setContextNode(XDIArc.create("()"));
		assertEquals(graph32.getAllStatementCount(), 1);
		assertTrue(graph32.containsStatement(XDIStatement.create("//()")));
		graph32.clear();

		graph32.close();
	}

	public void testDelRelationDelInnerRoot() throws Exception {

		Graph graph33 = this.getGraphFactory().openGraph(this.getClass().getName() + "-graph-33");

		graph33.getRootContextNode().setContextNode(XDIArc.create("(=a/#b)"));
		assertEquals(graph33.getAllStatementCount(), 3);
		graph33.getRootContextNode().getContextNode(XDIArc.create("=a")).delRelations(XDIAddress.create("#b"));
		assertEquals(graph33.getAllStatementCount(), 1);
		assertFalse(graph33.containsStatement(XDIStatement.create("//(=a/#b)")));
		assertTrue(graph33.containsStatement(XDIStatement.create("//=a")));
		assertFalse(graph33.containsStatement(XDIStatement.create("=a/#b/(=a/#b)")));
		graph33.clear();

		graph33.getRootContextNode().setContextNode(XDIArc.create("(/#b)"));
		assertEquals(graph33.getAllStatementCount(), 2);
		graph33.getRootContextNode().delRelations(XDIAddress.create("#b"));
		assertEquals(graph33.getAllStatementCount(), 0);
		assertFalse(graph33.containsStatement(XDIStatement.create("//(/#b)")));
		assertFalse(graph33.containsStatement(XDIStatement.create("/#b/(/#b)")));
		graph33.clear();

		graph33.close();
	}

	/*
	 * Helper methods
	 */

	@SuppressWarnings("unused")
	private static void makeGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.setContextNode(XDIArc.create("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.setContextNode(XDIArc.create("[#passport]"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.setContextNode(XDIArc.create("*!:uuid:1111"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.setContextNode(XDIArc.create("*!:uuid:2222"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.setContextNode(XDIArc.create("[$v]"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.setContextNode(XDIArc.create("[$o]"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.setContextNode(XDIArc.create("*!:uuid:1111"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.setContextNode(XDIArc.create("*!:uuid:2222"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.setRelation(XDIAddress.create("$"), XDIAddress.create("=abc[#passport]"));
		LiteralNode abcPassport1NumberLiteral = abcPassport1ContextNode.setDeepContextNode(XDIAddress.create("<#number>")).setLiteralNode("987654321");
		LiteralNode abcPassport1CountryLiteral = abcPassport1ContextNode.setDeepContextNode(XDIAddress.create("<#country>")).setLiteralNode("Canada");
		LiteralNode abcPassport1DLiteral = abcPassport1ContextNode.setDeepContextNode(XDIAddress.create("<$t>")).setLiteralNode("2005-01-01T00:00:00Z");
		LiteralNode abcPassport2NumberLiteral = abcPassport2ContextNode.setDeepContextNode(XDIAddress.create("<#number>")).setLiteralNode("123456789");
		LiteralNode abcPassport2CountryLiteral = abcPassport2ContextNode.setDeepContextNode(XDIAddress.create("<#country>")).setLiteralNode("New Zealand");
		LiteralNode abcPassport2DLiteral = abcPassport2ContextNode.setDeepContextNode(XDIAddress.create("<$t>")).setLiteralNode("2010-10-01T00:00:00Z");
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.setContextNode(XDIArc.create("*!:uuid:1111"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.setContextNode(XDIArc.create("*!:uuid:2222"));
		LiteralNode abcPassportC1DLiteral = abcPassportC1ContextNode.setDeepContextNode(XDIAddress.create("<$t>")).setLiteralNode("2010-11-11T11:11:11Z");
		LiteralNode abcPassportC2DLiteral = abcPassportC2ContextNode.setDeepContextNode(XDIAddress.create("<$t>")).setLiteralNode("2010-12-22T22:22:22Z");
		Relation abcPassportRelationV = abcPassportContextNode.setRelation(XDIAddress.create("[$v]"), XDIAddress.create("=abc[#passport][$v]*!:uuid:2222"));
		Relation abcPassportRelation1 = abcPassportContextNode.setRelation(XDIAddress.create("@1"), abcPassport1ContextNode);
		Relation abcPassportRelation2 = abcPassportContextNode.setRelation(XDIAddress.create("@2"), abcPassport2ContextNode);
		Relation abcTestRelation1 = abcContextNode.setRelation(XDIAddress.create("#rel"), abcPassport1ContextNode);
		Relation abcTestRelation2 = abcContextNode.setRelation(XDIAddress.create("#rel"), abcPassport2ContextNode);
	}

	private static void testGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.getContextNode(XDIArc.create("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.getContextNode(XDIArc.create("[#passport]"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.getContextNode(XDIArc.create("*!:uuid:1111"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.getContextNode(XDIArc.create("*!:uuid:2222"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.getContextNode(XDIArc.create("[$v]"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(XDIArc.create("[$o]"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.getContextNode(XDIArc.create("*!:uuid:1111"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.getContextNode(XDIArc.create("*!:uuid:2222"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.getRelation(XDIAddress.create("$"));
		LiteralNode abcPassport1NumberLiteral = abcPassport1ContextNode.getDeepLiteralNode(XDIAddress.create("<#number>&"));
		LiteralNode abcPassport1CountryLiteral = abcPassport1ContextNode.getDeepLiteralNode(XDIAddress.create("<#country>&"));
		LiteralNode abcPassport1DLiteral = abcPassport1ContextNode.getDeepLiteralNode(XDIAddress.create("<$t>&"));
		LiteralNode abcPassport2NumberLiteral = abcPassport2ContextNode.getDeepLiteralNode(XDIAddress.create("<#number>&"));
		LiteralNode abcPassport2CountryLiteral = abcPassport2ContextNode.getDeepLiteralNode(XDIAddress.create("<#country>&"));
		LiteralNode abcPassport2DLiteral = abcPassport2ContextNode.getDeepLiteralNode(XDIAddress.create("<$t>&"));
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.getContextNode(XDIArc.create("*!:uuid:1111"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.getContextNode(XDIArc.create("*!:uuid:2222"));
		LiteralNode abcPassportC1DLiteral = abcPassportC1ContextNode.getDeepLiteralNode(XDIAddress.create("<$t>&"));
		LiteralNode abcPassportC2DLiteral = abcPassportC2ContextNode.getDeepLiteralNode(XDIAddress.create("<$t>&"));
		Relation abcPassportRelationV = abcPassportContextNode.getRelation(XDIAddress.create("[$v]"));
		Relation abcPassportRelation1 = abcPassportContextNode.getRelation(XDIAddress.create("@1"));
		Relation abcPassportRelation2 = abcPassportContextNode.getRelation(XDIAddress.create("@2"));
		Relation abcRelation1 = abcContextNode.getRelation(XDIAddress.create("#rel"), XDIAddress.create("=abc[#passport]*!:uuid:1111"));
		Relation abcRelation2 = abcContextNode.getRelation(XDIAddress.create("#rel"), XDIAddress.create("=abc[#passport]*!:uuid:2222"));

		assertEquals(rootContextNode.getXDIAddress(), XDIConstants.XDI_ADD_ROOT);
		assertEquals(abcContextNode.getXDIAddress(), XDIAddress.create("=abc"));
		assertEquals(abcPassportContextNode.getXDIAddress(), XDIAddress.create("=abc[#passport]"));
		assertEquals(abcPassportVContextNode.getXDIAddress(), XDIAddress.create("=abc[#passport][$v]"));

		assertTrue(rootContextNode.containsContextNode(XDIArc.create("=abc")));
		assertTrue(abcContextNode.containsContextNode(XDIArc.create("[#passport]")));
		assertTrue(abcPassportContextNode.containsContextNode(XDIArc.create("*!:uuid:1111")));
		assertTrue(abcPassportContextNode.containsContextNode(XDIArc.create("*!:uuid:2222")));
		assertTrue(abcPassportContextNode.containsContextNode(XDIArc.create("[$v]")));
		assertTrue(abcPassportContextNode.containsContextNode(XDIArc.create("[$o]")));
		assertTrue(abcPassportVContextNode.containsContextNode(XDIArc.create("*!:uuid:1111")));
		assertTrue(abcPassportVContextNode.containsContextNode(XDIArc.create("*!:uuid:2222")));
		assertTrue(abcPassportV2ContextNode.containsRelation(XDIAddress.create("$"), XDIAddress.create("=abc[#passport]")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDIArc.create("<#number>")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDIArc.create("<#country>")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDIArc.create("<$t>")));
		assertTrue(abcPassport2ContextNode.containsContextNode(XDIArc.create("<#number>")));
		assertTrue(abcPassport2ContextNode.containsContextNode(XDIArc.create("<#country>")));
		assertTrue(abcPassport2ContextNode.containsContextNode(XDIArc.create("<$t>")));
		assertTrue(abcPassport1ContextNode.getDeepContextNode(XDIAddress.create("<#number>")).containsLiteralNode());
		assertTrue(abcPassport1ContextNode.getDeepContextNode(XDIAddress.create("<#country>")).containsLiteralNode());
		assertTrue(abcPassport1ContextNode.getDeepContextNode(XDIAddress.create("<$t>")).containsLiteralNode());
		assertTrue(abcPassport2ContextNode.getDeepContextNode(XDIAddress.create("<#number>")).containsLiteralNode());
		assertTrue(abcPassport2ContextNode.getDeepContextNode(XDIAddress.create("<#country>")).containsLiteralNode());
		assertTrue(abcPassport2ContextNode.getDeepContextNode(XDIAddress.create("<$t>")).containsLiteralNode());
		assertTrue(abcPassportCContextNode.containsContextNode(XDIArc.create("*!:uuid:1111")));
		assertTrue(abcPassportCContextNode.containsContextNode(XDIArc.create("*!:uuid:2222")));
		assertTrue(abcPassportC1ContextNode.containsContextNode(XDIArc.create("<$t>")));
		assertTrue(abcPassportC2ContextNode.containsContextNode(XDIArc.create("<$t>")));
		assertTrue(abcPassportC1ContextNode.getDeepContextNode(XDIAddress.create("<$t>")).containsLiteralNode());
		assertTrue(abcPassportC2ContextNode.getDeepContextNode(XDIAddress.create("<$t>")).containsLiteralNode());
		assertTrue(abcPassportContextNode.containsRelation(XDIAddress.create("[$v]"), XDIAddress.create("=abc[#passport][$v]*!:uuid:2222")));
		assertTrue(abcPassportContextNode.containsRelation(XDIAddress.create("@1"), XDIAddress.create("=abc[#passport]*!:uuid:1111")));
		assertTrue(abcPassportContextNode.containsRelation(XDIAddress.create("@2"), XDIAddress.create("=abc[#passport]*!:uuid:2222")));
		assertTrue(abcContextNode.containsRelation(XDIAddress.create("#rel"), XDIAddress.create("=abc[#passport]*!:uuid:1111")));
		assertTrue(abcContextNode.containsRelation(XDIAddress.create("#rel"), XDIAddress.create("=abc[#passport]*!:uuid:2222")));

		ContextNode contextNodesArray[] = new ContextNode [] {
				graph.getDeepContextNode(XDIAddress.create("")),
				graph.getDeepContextNode(XDIAddress.create("=abc")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:1111")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:2222")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$v]")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$o]")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$v]*!:uuid:1111")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$v]*!:uuid:2222")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$o]*!:uuid:1111")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$o]*!:uuid:2222")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:1111<#number>")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:1111<#country>")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:1111<$t>")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:2222<#number>")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:2222<#country>")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:2222<$t>")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$o]*!:uuid:1111<$t>")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$o]*!:uuid:2222<$t>"))
		};

		XDIArc[][] contextNodeXDIAddressArray = new XDIArc[][] {
				new XDIArc[] { XDIArc.create("=abc") },
				new XDIArc[] { XDIArc.create("[#passport]") },
				new XDIArc[] { XDIArc.create("*!:uuid:1111"), XDIArc.create("*!:uuid:2222"), XDIArc.create("[$v]"), XDIArc.create("[$o]") },
				new XDIArc[] { XDIArc.create("<#number>"), XDIArc.create("<#country>"), XDIArc.create("<$t>") },
				new XDIArc[] { XDIArc.create("<#number>"), XDIArc.create("<#country>"), XDIArc.create("<$t>") },
				new XDIArc[] { XDIArc.create("*!:uuid:1111"), XDIArc.create("*!:uuid:2222") },
				new XDIArc[] { XDIArc.create("*!:uuid:1111"), XDIArc.create("*!:uuid:2222") },
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { XDIArc.create("<$t>") },
				new XDIArc[] { XDIArc.create("<$t>") },
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { }
		};

		XDIAddress[][] relationAddressesArray = new XDIAddress[][] {
				new XDIAddress[] { },
				new XDIAddress[] { XDIAddress.create("#rel"), XDIAddress.create("#rel") },
				new XDIAddress[] { XDIAddress.create("[$v]"), XDIAddress.create("@1"), XDIAddress.create("@2") },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { XDIAddress.create("$") },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { }
		};

		Boolean[] literalsArray = new Boolean [] {
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.TRUE,
				Boolean.TRUE,
				Boolean.TRUE,
				Boolean.TRUE,
				Boolean.TRUE,
				Boolean.TRUE,
				Boolean.TRUE,
				Boolean.TRUE
		};

		assertEquals(contextNodesArray.length, contextNodeXDIAddressArray.length);
		assertEquals(contextNodesArray.length, relationAddressesArray.length);
		assertEquals(contextNodesArray.length, literalsArray.length);

		for (ContextNode contextNode : contextNodesArray) if (contextNode != null) assertTrue(contextNode.getGraph().getDeepContextNode(contextNode.getXDIAddress()) != null);

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(contextNodeXDIAddressArray[i]); continue; } else assertNotNull(contextNodeXDIAddressArray[i]);

			Set<XDIArc> arcs = new HashSet<XDIArc> (Arrays.asList(contextNodeXDIAddressArray[i]));
			assertEquals(arcs.size(), contextNodesArray[i].getContextNodeCount());
			assertEquals(arcs.size(), new IteratorCounter(contextNodesArray[i].getContextNodes()).count());
			for (Iterator<ContextNode> it = contextNodesArray[i].getContextNodes(); it.hasNext(); ) assertTrue(arcs.remove(it.next().getXDIArc()));
			assertTrue(arcs.isEmpty());
		}

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(relationAddressesArray[i]); continue; } else assertNotNull(relationAddressesArray[i]);

			List<XDIAddress> arcs = new ArrayList<XDIAddress> (Arrays.asList(relationAddressesArray[i]));
			for (Iterator<XDIAddress> it = arcs.iterator(); it.hasNext(); ) assertTrue(contextNodesArray[i].getGraph().getDeepContextNode(contextNodesArray[i].getXDIAddress()).getRelations(it.next()) != null);
			assertEquals(arcs.size(), contextNodesArray[i].getRelationCount());
			assertEquals(arcs.size(), new IteratorCounter(contextNodesArray[i].getRelations()).count());
			for (Iterator<Relation> it = contextNodesArray[i].getRelations(); it.hasNext(); ) assertTrue(arcs.remove(it.next().getXDIAddress()));
			assertTrue(arcs.isEmpty());
		}

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(literalsArray[i]); continue; } else assertNotNull(literalsArray[i]);

			assertEquals(contextNodesArray[i].containsLiteralNode(), literalsArray[i].booleanValue());
		}

		assertNull(rootContextNode.getXDIArc());
		assertEquals(XDIConstants.XDI_ADD_ROOT, rootContextNode.getXDIAddress());
		assertEquals(XDIArc.create("=abc"), abcContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc"), abcContextNode.getXDIAddress());
		assertEquals(XDIArc.create("[#passport]"), abcPassportContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport]"), abcPassportContextNode.getXDIAddress());
		assertEquals(XDIArc.create("*!:uuid:1111"), abcPassport1ContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport]*!:uuid:1111"), abcPassport1ContextNode.getXDIAddress());
		assertEquals(XDIArc.create("*!:uuid:2222"), abcPassport2ContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport]*!:uuid:2222"), abcPassport2ContextNode.getXDIAddress());
		assertEquals(XDIArc.create("[$v]"), abcPassportVContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport][$v]"), abcPassportVContextNode.getXDIAddress());
		assertEquals(XDIArc.create("[$o]"), abcPassportCContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport][$o]"), abcPassportCContextNode.getXDIAddress());
		assertEquals(XDIArc.create("*!:uuid:1111"), abcPassportV1ContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport][$v]*!:uuid:1111"), abcPassportV1ContextNode.getXDIAddress());
		assertEquals(XDIArc.create("*!:uuid:2222"), abcPassportV2ContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport][$v]*!:uuid:2222"), abcPassportV2ContextNode.getXDIAddress());
		assertEquals(XDIArc.create("$"), abcPassportV2RelationDollar.getXDIAddress());
		assertEquals(XDIAddress.create("=abc[#passport]"), abcPassportV2RelationDollar.getTargetXDIAddress());
		assertEquals(XDIArc.create("<#number>"), abcPassport1NumberLiteral.getContextNode().getXDIArc());
		assertEquals("987654321", abcPassport1NumberLiteral.getLiteralData());
		assertEquals(XDIArc.create("<#country>"), abcPassport1CountryLiteral.getContextNode().getXDIArc());
		assertEquals("Canada", abcPassport1CountryLiteral.getLiteralData());
		assertEquals(XDIArc.create("<$t>"), abcPassport1DLiteral.getContextNode().getXDIArc());
		assertEquals("2005-01-01T00:00:00Z", abcPassport1DLiteral.getLiteralData());
		assertEquals(XDIArc.create("<#number>"), abcPassport2NumberLiteral.getContextNode().getXDIArc());
		assertEquals("123456789", abcPassport2NumberLiteral.getLiteralData());
		assertEquals(XDIArc.create("<#country>"), abcPassport2CountryLiteral.getContextNode().getXDIArc());
		assertEquals("New Zealand", abcPassport2CountryLiteral.getLiteralData());
		assertEquals(XDIArc.create("<$t>"), abcPassport2DLiteral.getContextNode().getXDIArc());
		assertEquals("2010-10-01T00:00:00Z", abcPassport2DLiteral.getLiteralData());
		assertEquals(XDIArc.create("*!:uuid:1111"), abcPassportC1ContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport][$o]*!:uuid:1111"), abcPassportC1ContextNode.getXDIAddress());
		assertEquals(XDIArc.create("*!:uuid:2222"), abcPassportC2ContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport][$o]*!:uuid:2222"), abcPassportC2ContextNode.getXDIAddress());
		assertEquals(XDIArc.create("<$t>"), abcPassportC1DLiteral.getContextNode().getXDIArc());
		assertEquals("2010-11-11T11:11:11Z", abcPassportC1DLiteral.getLiteralData());
		assertEquals(XDIArc.create("<$t>"), abcPassportC2DLiteral.getContextNode().getXDIArc());
		assertEquals("2010-12-22T22:22:22Z", abcPassportC2DLiteral.getLiteralData());
		assertEquals(XDIArc.create("[$v]"), abcPassportRelationV.getXDIAddress());
		assertEquals(XDIAddress.create("=abc[#passport][$v]*!:uuid:2222"), abcPassportRelationV.getTargetXDIAddress());
		assertEquals(XDIArc.create("@1"), abcPassportRelation1.getXDIAddress());
		assertEquals(XDIAddress.create("=abc[#passport]*!:uuid:1111"), abcPassportRelation1.getTargetXDIAddress());
		assertEquals(XDIArc.create("@2"), abcPassportRelation2.getXDIAddress());
		assertEquals(XDIAddress.create("=abc[#passport]*!:uuid:2222"), abcPassportRelation2.getTargetXDIAddress());
		assertEquals(XDIArc.create("#rel"), abcRelation1.getXDIAddress());
		assertEquals(XDIAddress.create("=abc[#passport]*!:uuid:1111"), abcRelation1.getTargetXDIAddress());
		assertEquals(XDIArc.create("#rel"), abcRelation2.getXDIAddress());
		assertEquals(XDIAddress.create("=abc[#passport]*!:uuid:2222"), abcRelation2.getTargetXDIAddress());

		assertEquals(rootContextNode.getAllContextNodeCount(), 18);
		assertEquals(rootContextNode.getAllRelationCount(), 6);
		assertEquals(rootContextNode.getAllLiteralCount(), 8);
	}

	@SuppressWarnings("unused")
	private static void manipulateGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.getContextNode(XDIArc.create("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.getContextNode(XDIArc.create("[#passport]"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.getContextNode(XDIArc.create("*!:uuid:1111"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.getContextNode(XDIArc.create("*!:uuid:2222"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.getContextNode(XDIArc.create("[$v]"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(XDIArc.create("[$o]"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.getContextNode(XDIArc.create("*!:uuid:1111"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.getContextNode(XDIArc.create("*!:uuid:2222"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.getRelation(XDIAddress.create("$"));
		LiteralNode abcPassport1LiteralNumber = abcPassport1ContextNode.getDeepLiteralNode(XDIAddress.create("<#number>&"));
		LiteralNode abcPassport1LiteralCountry = abcPassport1ContextNode.getDeepLiteralNode(XDIAddress.create("<#country>&"));
		LiteralNode abcPassport1LiteralD = abcPassport1ContextNode.getDeepLiteralNode(XDIAddress.create("<$t>&"));
		LiteralNode abcPassport2LiteralNumber = abcPassport2ContextNode.getDeepLiteralNode(XDIAddress.create("<#number>&"));
		LiteralNode abcPassport2LiteralCountry = abcPassport2ContextNode.getDeepLiteralNode(XDIAddress.create("<#country>&"));
		LiteralNode abcPassport2LiteralD = abcPassport2ContextNode.getDeepLiteralNode(XDIAddress.create("<$t>&"));
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.getContextNode(XDIArc.create("*!:uuid:1111"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.getContextNode(XDIArc.create("*!:uuid:2222"));
		LiteralNode abcPassportC1LiteralD = abcPassportC1ContextNode.getDeepLiteralNode(XDIAddress.create("<$t>&"));
		LiteralNode abcPassportC2LiteralD = abcPassportC2ContextNode.getDeepLiteralNode(XDIAddress.create("<$t>&"));
		Relation abcPassportRelationV = abcPassportContextNode.getRelation(XDIAddress.create("[$v]"));
		Relation abcPassportRelation1 = abcPassportContextNode.getRelation(XDIAddress.create("@1"));
		Relation abcPassportRelation2 = abcPassportContextNode.getRelation(XDIAddress.create("@2"));
		Relation abcRelation1 = abcContextNode.getRelation(XDIAddress.create("#rel"), XDIAddress.create("=abc[#passport]*!:uuid:1111"));
		Relation abcRelation2 = abcContextNode.getRelation(XDIAddress.create("#rel"), XDIAddress.create("=abc[#passport]*!:uuid:2222"));

		abcPassport2ContextNode.delete();
		abcPassportC1LiteralD.setLiteralData("2010-03-03T03:03:03Z");
		abcPassportC2LiteralD.getContextNode().delete();
	}

	private static void testManipulatedGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.getContextNode(XDIArc.create("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.getContextNode(XDIArc.create("[#passport]"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.getContextNode(XDIArc.create("*!:uuid:1111"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.getContextNode(XDIArc.create("*!:uuid:2222"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.getContextNode(XDIArc.create("[$v]"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(XDIArc.create("[$o]"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.getContextNode(XDIArc.create("*!:uuid:1111"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.getContextNode(XDIArc.create("*!:uuid:2222"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.getRelation(XDIAddress.create("$"));
		LiteralNode abcPassport1NumberLiteral = abcPassport1ContextNode.getDeepLiteralNode(XDIAddress.create("<#number>&"));
		LiteralNode abcPassport1CountryLiteral = abcPassport1ContextNode.getDeepLiteralNode(XDIAddress.create("<#country>&"));
		LiteralNode abcPassport1LiteralD = abcPassport1ContextNode.getDeepLiteralNode(XDIAddress.create("<$t>&"));
		LiteralNode abcPassport2LiteralNumber = null;
		LiteralNode abcPassport2LiteralCountry = null;
		LiteralNode abcPassport2LiteralD = null;
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.getContextNode(XDIArc.create("*!:uuid:1111"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.getContextNode(XDIArc.create("*!:uuid:2222"));
		LiteralNode abcPassportC1LiteralD = abcPassportC1ContextNode.getDeepLiteralNode(XDIAddress.create("<$t>&"));
		LiteralNode abcPassportC2LiteralD = abcPassportC2ContextNode == null ? null : abcPassportC2ContextNode.getDeepLiteralNode(XDIAddress.create("<$t>&"));
		Relation abcPassportRelationV = abcPassportContextNode.getRelation(XDIAddress.create("[$v]"));
		Relation abcPassportRelation1 = abcPassportContextNode.getRelation(XDIAddress.create("@1"));
		Relation abcPassportRelation2 = abcPassportContextNode.getRelation(XDIAddress.create("@2"));
		Relation abcRelation1 = abcContextNode.getRelation(XDIAddress.create("#rel"), XDIAddress.create("=abc[#passport]*!:uuid:1111"));
		Relation abcRelation2 = abcContextNode.getRelation(XDIAddress.create("#rel"), XDIAddress.create("=abc[#passport]*!:uuid:2222"));

		assertTrue(rootContextNode.containsContextNode(XDIArc.create("=abc")));
		assertTrue(abcContextNode.containsContextNode(XDIArc.create("[#passport]")));
		assertTrue(abcPassportContextNode.containsContextNode(XDIArc.create("*!:uuid:1111")));
		assertFalse(abcPassportContextNode.containsContextNode(XDIArc.create("*!:uuid:2222")));	// MANIPULATED
		assertTrue(abcPassportContextNode.containsContextNode(XDIArc.create("[$v]")));
		assertTrue(abcPassportContextNode.containsContextNode(XDIArc.create("[$o]")));
		assertTrue(abcPassportVContextNode.containsContextNode(XDIArc.create("*!:uuid:1111")));
		assertTrue(abcPassportVContextNode.containsContextNode(XDIArc.create("*!:uuid:2222")));
		assertTrue(abcPassportV2ContextNode.containsRelation(XDIAddress.create("$"), XDIAddress.create("=abc[#passport]")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDIArc.create("<#number>")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDIArc.create("<#country>")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDIArc.create("<$t>")));
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertTrue(abcPassport1ContextNode.getDeepContextNode(XDIAddress.create("<#number>")).containsLiteralNode());
		assertTrue(abcPassport1ContextNode.getDeepContextNode(XDIAddress.create("<#country>")).containsLiteralNode());
		assertTrue(abcPassport1ContextNode.getDeepContextNode(XDIAddress.create("<$t>")).containsLiteralNode());
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertTrue(abcPassportCContextNode.containsContextNode(XDIArc.create("*!:uuid:1111")));
		assertTrue(abcPassportCContextNode.containsContextNode(XDIArc.create("*!:uuid:2222")));
		assertTrue(abcPassportC1ContextNode.containsContextNode(XDIArc.create("<$t>")));
		assertFalse(abcPassportC2ContextNode.containsContextNode(XDIArc.create("<$t>")));	// MANIPULATED
		assertTrue(abcPassportC1ContextNode.getDeepContextNode(XDIAddress.create("<$t>")).containsLiteralNode());
		assertNull(abcPassportC2LiteralD);	// MANIPULATED
		assertTrue(abcPassportContextNode.containsRelation(XDIAddress.create("[$v]"), XDIAddress.create("=abc[#passport][$v]*!:uuid:2222")));
		assertTrue(abcPassportContextNode.containsRelation(XDIAddress.create("@1"), XDIAddress.create("=abc[#passport]*!:uuid:1111")));
		assertFalse(abcPassportContextNode.containsRelation(XDIAddress.create("@2"), XDIAddress.create("=abc[#passport]*!:uuid:2222")));		// MANIPULATED
		assertTrue(abcContextNode.containsRelation(XDIAddress.create("#rel"), XDIAddress.create("=abc[#passport]*!:uuid:1111")));
		assertFalse(abcContextNode.containsRelation(XDIAddress.create("#rel"), XDIAddress.create("=abc[#passport]*!:uuid:2222")));		// MANIPULATED

		ContextNode contextNodesArray[] = new ContextNode [] {
				graph.getDeepContextNode(XDIAddress.create("")),
				graph.getDeepContextNode(XDIAddress.create("=abc")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:1111")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:2222")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$v]")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$o]")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$v]*!:uuid:1111")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$v]*!:uuid:2222")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$o]*!:uuid:1111")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$o]*!:uuid:2222")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:1111<#number>")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:1111<#country>")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:1111<$t>")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:2222<#number>")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:2222<#country>")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport]*!:uuid:2222<$t>")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$o]*!:uuid:1111<$t>")),
				graph.getDeepContextNode(XDIAddress.create("=abc[#passport][$o]*!:uuid:2222<$t>"))
		};

		XDIArc[][] contextNodeXDIAddressArray = new XDIArc[][] {
				new XDIArc[] { XDIArc.create("=abc") },
				new XDIArc[] { XDIArc.create("[#passport]") },
				new XDIArc[] { XDIArc.create("*!:uuid:1111"), XDIArc.create("[$v]"), XDIArc.create("[$o]") },	// MANIPULATED
				new XDIArc[] { XDIArc.create("<#number>"), XDIArc.create("<#country>"), XDIArc.create("<$t>") },
				null,	// MANIPULATED
				new XDIArc[] { XDIArc.create("*!:uuid:1111"), XDIArc.create("*!:uuid:2222") },
				new XDIArc[] { XDIArc.create("*!:uuid:1111"), XDIArc.create("*!:uuid:2222") },
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { XDIArc.create("<$t>") },
				new XDIArc[] { },	// MANIPULATED
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { },
				null,	// MANIPULATED
				null,	// MANIPULATED
				null,	// MANIPULATED
				new XDIArc[] { },
				null	// MANIPULATED
		};

		XDIAddress[][] relationAddressesArray = new XDIAddress[][] {
				new XDIAddress[] { },
				new XDIAddress[] { XDIAddress.create("#rel") },	// MANIPULATED
				new XDIAddress[] { XDIAddress.create("[$v]"), XDIAddress.create("@1") },	// MANIPULATED
				new XDIAddress[] { },
				null,	// MANIPULATED
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { XDIAddress.create("$") },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { },
				new XDIAddress[] { },
				null,	// MANIPULATED
				null,	// MANIPULATED
				null,	// MANIPULATED
				new XDIAddress[] { },
				null	// MANIPULATED
		};

		Boolean[] literalsArray = new Boolean [] {
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				null,	// MANIPULATED
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.TRUE,
				Boolean.TRUE,
				Boolean.TRUE,
				null,	// MANIPULATED
				null,	// MANIPULATED
				null,	// MANIPULATED
				Boolean.TRUE,
				null	// MANIPULATED
		};

		assertEquals(contextNodesArray.length, contextNodeXDIAddressArray.length);
		assertEquals(contextNodesArray.length, relationAddressesArray.length);
		assertEquals(contextNodesArray.length, literalsArray.length);

		for (ContextNode contextNode : contextNodesArray) if (contextNode != null) assertTrue(contextNode.getGraph().getDeepContextNode(contextNode.getXDIAddress()) != null);

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(contextNodeXDIAddressArray[i]); continue; } else assertNotNull(contextNodeXDIAddressArray[i]);

			Set<XDIArc> arcs = new HashSet<XDIArc> (Arrays.asList(contextNodeXDIAddressArray[i]));
			assertEquals(arcs.size(), contextNodesArray[i].getContextNodeCount());
			assertEquals(arcs.size(), new IteratorCounter(contextNodesArray[i].getContextNodes()).count());
			for (Iterator<ContextNode> it = contextNodesArray[i].getContextNodes(); it.hasNext(); ) assertTrue(arcs.remove(it.next().getXDIArc()));
			assertTrue(arcs.isEmpty());
		}

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(relationAddressesArray[i]); continue; } else assertNotNull(relationAddressesArray[i]);

			List<XDIAddress> arcs = new ArrayList<XDIAddress> (Arrays.asList(relationAddressesArray[i]));
			for (Iterator<XDIAddress> it = arcs.iterator(); it.hasNext(); ) assertTrue(contextNodesArray[i].getGraph().getDeepContextNode(contextNodesArray[i].getXDIAddress()).getRelations(it.next()) != null);
			assertEquals(arcs.size(), contextNodesArray[i].getRelationCount());
			assertEquals(arcs.size(), new IteratorCounter(contextNodesArray[i].getRelations()).count());
			for (Iterator<Relation> it = contextNodesArray[i].getRelations(); it.hasNext(); ) assertTrue(arcs.remove(it.next().getXDIAddress()));
			assertTrue(arcs.isEmpty());
		}

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(literalsArray[i]); continue; } else assertNotNull(literalsArray[i]);

			assertEquals(contextNodesArray[i].containsLiteralNode(), literalsArray[i].booleanValue());
		}

		assertNull(rootContextNode.getXDIArc());
		assertEquals(XDIConstants.XDI_ADD_ROOT, rootContextNode.getXDIAddress());
		assertEquals(XDIArc.create("=abc"), abcContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc"), abcContextNode.getXDIAddress());
		assertEquals(XDIArc.create("[#passport]"), abcPassportContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport]"), abcPassportContextNode.getXDIAddress());
		assertEquals(XDIArc.create("*!:uuid:1111"), abcPassport1ContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport]*!:uuid:1111"), abcPassport1ContextNode.getXDIAddress());
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertEquals(XDIArc.create("[$v]"), abcPassportVContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport][$v]"), abcPassportVContextNode.getXDIAddress());
		assertEquals(XDIArc.create("[$o]"), abcPassportCContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport][$o]"), abcPassportCContextNode.getXDIAddress());
		assertEquals(XDIArc.create("*!:uuid:1111"), abcPassportV1ContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport][$v]*!:uuid:1111"), abcPassportV1ContextNode.getXDIAddress());
		assertEquals(XDIArc.create("*!:uuid:2222"), abcPassportV2ContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport][$v]*!:uuid:2222"), abcPassportV2ContextNode.getXDIAddress());
		assertEquals(XDIArc.create("$"), abcPassportV2RelationDollar.getXDIAddress());
		assertEquals(XDIAddress.create("=abc[#passport]"), abcPassportV2RelationDollar.getTargetXDIAddress());
		assertEquals(XDIArc.create("<#number>"), abcPassport1NumberLiteral.getContextNode().getXDIArc());
		assertEquals("987654321", abcPassport1NumberLiteral.getLiteralData());
		assertEquals(XDIArc.create("<#country>"), abcPassport1CountryLiteral.getContextNode().getXDIArc());
		assertEquals("Canada", abcPassport1CountryLiteral.getLiteralData());
		assertEquals(XDIArc.create("<$t>"), abcPassport1LiteralD.getContextNode().getXDIArc());
		assertEquals("2005-01-01T00:00:00Z", abcPassport1LiteralD.getLiteralData());
		assertNull(abcPassport2LiteralNumber);	// MANIPULATED
		assertNull(abcPassport2LiteralNumber);	// MANIPULATED
		assertNull(abcPassport2LiteralCountry);	// MANIPULATED
		assertNull(abcPassport2LiteralCountry);	// MANIPULATED
		assertNull(abcPassport2LiteralD);	// MANIPULATED
		assertNull(abcPassport2LiteralD);	// MANIPULATED
		assertEquals(XDIArc.create("*!:uuid:1111"), abcPassportC1ContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport][$o]*!:uuid:1111"), abcPassportC1ContextNode.getXDIAddress());
		assertEquals(XDIArc.create("*!:uuid:2222"), abcPassportC2ContextNode.getXDIArc());
		assertEquals(XDIAddress.create("=abc[#passport][$o]*!:uuid:2222"), abcPassportC2ContextNode.getXDIAddress());
		assertEquals(XDIArc.create("<$t>"), abcPassportC1LiteralD.getContextNode().getXDIArc());
		assertEquals("2010-03-03T03:03:03Z", abcPassportC1LiteralD.getLiteralData());	// MANIPULATED
		assertNull(abcPassportC2LiteralD);	// MANIPULATED
		assertNull(abcPassportC2LiteralD);	// MANIPULATED
		assertEquals(XDIArc.create("[$v]"), abcPassportRelationV.getXDIAddress());
		assertEquals(XDIAddress.create("=abc[#passport][$v]*!:uuid:2222"), abcPassportRelationV.getTargetXDIAddress());
		assertEquals(XDIArc.create("@1"), abcPassportRelation1.getXDIAddress());
		assertEquals(XDIAddress.create("=abc[#passport]*!:uuid:1111"), abcPassportRelation1.getTargetXDIAddress());	// MANIPULATED
		assertNull(abcPassportRelation2);	// MANIPULATED
		assertNull(abcPassportRelation2);	// MANIPULATED
		assertEquals(XDIArc.create("#rel"), abcRelation1.getXDIAddress());
		assertEquals(XDIAddress.create("=abc[#passport]*!:uuid:1111"), abcRelation1.getTargetXDIAddress());
		assertNull(abcRelation2);	// MANIPULATED
		assertNull(abcRelation2);	// MANIPULATED

		assertEquals(rootContextNode.getAllContextNodeCount(), 13);	// MANIPULATED
		assertEquals(rootContextNode.getAllRelationCount(), 4);	// MANIPULATED
		assertEquals(rootContextNode.getAllLiteralCount(), 4);	// MANIPULATED
	}

	private static void testGraphsEqual(Graph graph1, Graph graph2) throws Exception {

		ContextNode rootContextNode1 = graph1.getRootContextNode();
		ContextNode rootContextNode2 = graph2.getRootContextNode();

		assertTrue(rootContextNode1.getContextNodeCount() == rootContextNode2.getContextNodeCount());
		assertTrue(rootContextNode1.getAllContextNodeCount() == rootContextNode2.getAllContextNodeCount());
		assertTrue(rootContextNode1.getAllRelationCount() == rootContextNode2.getAllRelationCount());
		assertTrue(rootContextNode1.getAllLiteralCount() == rootContextNode2.getAllLiteralCount());
	}
}
