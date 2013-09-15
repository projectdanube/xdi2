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

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public abstract class AbstractGraphTest extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(AbstractGraphTest.class);

	protected abstract Graph openNewGraph(String id) throws IOException;
	protected abstract Graph reopenGraph(Graph graph, String id) throws IOException;

	public void testSimple() throws Exception {

		Graph graph0 = this.openNewGraph(this.getClass().getName() + "-graph-0");

		ContextNode markus = graph0.getRootContextNode().setContextNode(XDI3SubSegment.create("=markus"));
		ContextNode email = markus.setContextNode(XDI3SubSegment.create("<+email>"));
		ContextNode value = email.setContextNode(XDI3SubSegment.create("&"));
		value.setLiteral("abc@gmail.com");
		markus.setRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=drummond"));

		markus = graph0.getRootContextNode().getContextNode(XDI3SubSegment.create("=markus"));
		assertNotNull(markus);
		assertFalse(markus.isRootContextNode());
		assertFalse(markus.isEmpty());
		assertFalse(markus.isLeafContextNode());
		assertTrue(markus.containsRelations());
		assertFalse(markus.containsLiteral());
		assertTrue(value.isLeafContextNode());
		assertTrue(value.containsLiteral());

		ContextNode drummond = graph0.getRootContextNode().getContextNode(XDI3SubSegment.create("=drummond"));
		assertNotNull(drummond);
		assertTrue(drummond.isEmpty());
		assertTrue(drummond.isLeafContextNode());

		value.setLiteral("xyz@gmail.com");
		assertEquals(graph0.getDeepLiteral(XDI3Segment.create("=markus<+email>&")).getLiteralData(), "xyz@gmail.com");

		graph0.close();
	}

	public void testMakeGraph() throws Exception {

		Graph graph1 = this.openNewGraph(this.getClass().getName() + "-graph-1");

		makeGraph(graph1);
		testGraph(graph1);

		graph1.close();
	}

	public void testReopenGraph() throws Exception {

		Graph graph2 = this.openNewGraph(this.getClass().getName() + "-graph-2");

		makeGraph(graph2);
		graph2 = this.reopenGraph(graph2, this.getClass().getName() + "-graph-2");
		testGraph(graph2);

		graph2.close();
	}

	public void testReadJson() throws Exception {

		Graph graph3 = this.openNewGraph(this.getClass().getName() + "-graph-3");

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

			log.info("#" + i + " Format: " + formats[i]);

			File file = new File("xdi.out");

			Graph graph4 = this.openNewGraph(this.getClass().getName() + "-graph-4" + "-" + i);
			Graph graph5 = this.openNewGraph(this.getClass().getName() + "-graph-5" + "-" + i);

			XDIWriter writer = XDIWriterRegistry.forFormat(formats[i], null);
			XDIReader reader = XDIReaderRegistry.forFormat(formats[i], null);

			makeGraph(graph4);
			writer.write(graph4, new FileWriter(file)).close();
			reader.read(graph5, new FileReader(file)).close();

			testGraph(graph5);
			testGraphsEqual(graph4, graph5);

			graph4.close();
			graph5.close();

			file.delete();
		}
	}

	public void testManipulate() throws Exception {

		Graph graph8 = this.openNewGraph(this.getClass().getName() + "-graph-8");

		makeGraph(graph8);
		manipulateGraph(graph8);
		testManipulatedGraph(graph8);

		graph8.close();
	}

	public void testManipulateAndReopenGraph() throws Exception {

		Graph graph9 = this.openNewGraph(this.getClass().getName() + "-graph-9");

		makeGraph(graph9);
		graph9 = this.reopenGraph(graph9, this.getClass().getName() + "-graph-9");
		manipulateGraph(graph9);
		graph9 = this.reopenGraph(graph9, this.getClass().getName() + "-graph-9");
		testManipulatedGraph(graph9);

		graph9.close();
	}

	public void testCopy() throws Exception {

		Graph graph10 = this.openNewGraph(this.getClass().getName() + "-graph-10");
		Graph graph11 = this.openNewGraph(this.getClass().getName() + "-graph-11");

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

			graph11.getRootContextNode().setContextNode(XDI3SubSegment.create("=xxx"));

			assertNotEquals(graph10, graph11);
			assertNotEquals(graph10.hashCode(), graph11.hashCode());
			assertNotEquals(graph10.compareTo(graph11), 0);
		} finally {

			graph10.close();
			graph11.close();
		}
	}

	public void testDeleteDeep() throws Exception {

		Graph graph12 = this.openNewGraph(this.getClass().getName() + "-graph-12");
		assertEquals(graph12.getRootContextNode(), graph12.getDeepContextNode(XDIConstants.XRI_S_CONTEXT));
		assertEquals(graph12.getRootContextNode().getXri(), XDIConstants.XRI_S_CONTEXT);

		graph12.setDeepRelation(XDI3Segment.create("=markus"), XDI3Segment.create("+friend"), XDI3Segment.create("=someone"));
		graph12.getDeepContextNode(XDI3Segment.create("=markus")).delete();
		graph12.setDeepContextNode(XDI3Segment.create("=markus"));
		assertNull(graph12.getDeepRelation(XDI3Segment.create("=markus"), XDI3Segment.create("+friend")));

		graph12.close();
	}

	public void testRoot() throws Exception {

		Graph graph13 = this.openNewGraph(this.getClass().getName() + "-graph-13");
		ContextNode root = graph13.getRootContextNode();

		assertEquals(root.getXri(), XDIConstants.XRI_S_ROOT);
		assertNull(root.getArcXri());
		assertNull(root.getContextNode());
		assertTrue(root.isRootContextNode());

		assertTrue(graph13.isEmpty());
		assertTrue(root.isEmpty());
		assertFalse(root.containsContextNodes());
		assertFalse(root.containsRelations());
		assertFalse(root.containsLiteral());
		assertFalse(root.getContextNodes().hasNext());
		assertFalse(root.getRelations().hasNext());
		assertNull(root.getLiteral());
		assertEquals(root.getContextNodeCount(), 0);
		assertEquals(root.getRelationCount(), 0);
		assertFalse(root.getAllContextNodes().hasNext());
		assertFalse(root.getAllRelations().hasNext());
		assertFalse(root.getAllLiterals().hasNext());

		root.setRelation(XDI3Segment.create("*arc"), XDI3Segment.create("=target"));
		root.setContextNode(XDI3SubSegment.create("<+test>")).setContextNode(XDI3SubSegment.create("&")).setLiteral("test");

		assertFalse(root.isEmpty());
		assertTrue(root.containsContextNodes());
		assertTrue(root.containsRelations());
		assertFalse(root.containsLiteral());
		assertTrue(root.getContextNodes().hasNext());
		assertTrue(root.getRelations().hasNext());
		assertNotNull(root.getRelations().next().follow());
		assertNull(root.getLiteral());
		assertEquals(root.getContextNodeCount(), 2);
		assertEquals(root.getRelationCount(), 1);
		assertTrue(root.getAllContextNodes().hasNext());
		assertTrue(root.getAllRelations().hasNext());
		assertTrue(root.getAllLiterals().hasNext());

		root.setContextNode(XDI3SubSegment.create("+name"));
		root.setContextNode(XDI3SubSegment.create("+email"));

		assertFalse(root.isEmpty());
		assertTrue(root.containsContextNodes());
		assertTrue(root.containsRelations());
		assertFalse(root.containsLiteral());
		assertTrue(root.getContextNodes().hasNext());
		assertTrue(root.getRelations().hasNext());
		assertNull(root.getLiteral());
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
		assertFalse(root.containsLiteral());
		assertFalse(root.getContextNodes().hasNext());
		assertFalse(root.getRelations().hasNext());
		assertNull(root.getLiteral());
		assertEquals(root.getContextNodeCount(), 0);
		assertEquals(root.getRelationCount(), 0);
		assertFalse(root.getAllContextNodes().hasNext());
		assertFalse(root.getAllRelations().hasNext());
		assertFalse(root.getAllLiterals().hasNext());

		graph13.close();
	}

	public void testRelations() throws Exception {

		Graph graph14 = this.openNewGraph(this.getClass().getName() + "-graph-14");
		ContextNode root = graph14.getRootContextNode();
		ContextNode markus = root.setContextNode(XDI3SubSegment.create("=markus"));
		ContextNode target1 = root.setContextNode(XDI3SubSegment.create("=test")).setContextNode(XDI3SubSegment.create("=target1"));
		ContextNode target2 = root.getContextNode(XDI3SubSegment.create("=test")).setContextNode(XDI3SubSegment.create("=target2"));
		ContextNode target3 = root.getContextNode(XDI3SubSegment.create("=test")).setContextNode(XDI3SubSegment.create("=target3"));

		markus.setRelation(XDI3Segment.create("+friend"), target1);
		markus.setRelation(XDI3Segment.create("+friend"), target2);
		markus.setRelation(XDI3Segment.create("+brother"), target3);
		root.setRelation(XDI3Segment.create("+rel"), markus);

		assertTrue(root.containsRelations());
		assertTrue(root.containsRelations(XDI3Segment.create("+rel")));
		assertTrue(root.containsRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=markus")));
		assertEquals(root.getRelationCount(), 1);
		assertEquals(root.getRelationCount(XDI3Segment.create("+rel")), 1);
		assertEquals(root.getRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=markus")).follow(), markus);
		assertEquals(root.getRelation(XDI3Segment.create("+rel")).follow(), markus);
		assertEquals(new IteratorCounter(root.getRelations(XDI3Segment.create("+rel"))).count(), 1);
		assertEquals(new IteratorCounter(root.getRelations()).count(), 1);

		assertTrue(markus.containsRelations());
		assertTrue(markus.containsRelations(XDI3Segment.create("+friend")));
		assertTrue(markus.containsRelations(XDI3Segment.create("+brother")));
		assertTrue(markus.containsRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test=target1")));
		assertTrue(markus.containsRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test=target2")));
		assertTrue(markus.containsRelation(XDI3Segment.create("+brother"), XDI3Segment.create("=test=target3")));
		assertEquals(markus.getRelationCount(), 3);
		assertEquals(markus.getRelationCount(XDI3Segment.create("+friend")), 2);
		assertEquals(markus.getRelationCount(XDI3Segment.create("+brother")), 1);
		assertNotNull(markus.getRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test=target1")));
		assertNotNull(markus.getRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test=target2")));
		assertNotNull(markus.getRelation(XDI3Segment.create("+brother"), XDI3Segment.create("=test=target3")));
		assertNotNull(markus.getRelation(XDI3Segment.create("+friend")));
		assertNotNull(markus.getRelation(XDI3Segment.create("+brother")));
		assertEquals(new IteratorCounter(markus.getRelations(XDI3Segment.create("+friend"))).count(), 2);
		assertEquals(new IteratorCounter(markus.getRelations(XDI3Segment.create("+brother"))).count(), 1);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 3);

		root.delRelations();
		markus.delRelations(XDI3Segment.create("+friend"));
		markus.delRelation(XDI3Segment.create("+brother"), XDI3Segment.create("=test=target3"));

		assertFalse(root.containsRelations());
		assertFalse(root.containsRelations(XDI3Segment.create("+rel")));
		assertFalse(root.containsRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=markus")));
		assertEquals(root.getRelationCount(), 0);
		assertEquals(root.getRelationCount(XDI3Segment.create("+rel")), 0);
		assertNull(root.getRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=markus")));
		assertNull(root.getRelation(XDI3Segment.create("+rel")));
		assertFalse(root.getRelations(XDI3Segment.create("+rel")).hasNext());
		assertEquals(new IteratorCounter(root.getRelations(XDI3Segment.create("+rel"))).count(), 0);
		assertEquals(new IteratorCounter(root.getRelations()).count(), 0);

		assertFalse(markus.containsRelations());
		assertFalse(markus.containsRelations(XDI3Segment.create("+friend")));
		assertFalse(markus.containsRelations(XDI3Segment.create("+brother")));
		assertFalse(markus.containsRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test=target1")));
		assertFalse(markus.containsRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test=target2")));
		assertFalse(markus.containsRelation(XDI3Segment.create("+brother"), XDI3Segment.create("=test=target3")));
		assertEquals(markus.getRelationCount(), 0);
		assertEquals(markus.getRelationCount(XDI3Segment.create("+friend")), 0);
		assertEquals(markus.getRelationCount(XDI3Segment.create("+brother")), 0);
		assertNull(markus.getRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test=target1")));
		assertNull(markus.getRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test=target2")));
		assertNull(markus.getRelation(XDI3Segment.create("+brother"), XDI3Segment.create("=test=target3")));
		assertNull(markus.getRelation(XDI3Segment.create("+friend")));
		assertNull(markus.getRelation(XDI3Segment.create("+brother")));
		assertEquals(new IteratorCounter(markus.getRelations(XDI3Segment.create("+friend"))).count(), 0);
		assertEquals(new IteratorCounter(markus.getRelations(XDI3Segment.create("+brother"))).count(), 0);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 0);

		graph14.close();
	}

	public void testContextNodeXris() throws Exception {

		Graph graph15 = this.openNewGraph(this.getClass().getName() + "-graph-15");
		ContextNode root = graph15.getRootContextNode();

		ContextNode c = root.setDeepContextNode(XDI3Segment.create("+a+b+c"));
		ContextNode b = c.getContextNode();
		ContextNode a = b.getContextNode();

		ContextNode value = c.setDeepContextNode(XDI3Segment.create("<$t>&"));
		ContextNode d = value.getContextNode();

		Relation r = c.setRelation(XDI3Segment.create("+x+y"), b);
		Literal l = value.setLiteral("test");

		assertTrue(a.getContextNode().isRootContextNode());
		assertNull(a.getContextNode().getContextNode());

		assertFalse(a.isLeafContextNode());
		assertFalse(b.isLeafContextNode());
		assertFalse(c.isLeafContextNode());
		assertFalse(d.isLeafContextNode());
		assertTrue(value.isLeafContextNode());

		assertEquals(a.getXri(), XDI3Segment.create("+a"));
		assertEquals(b.getXri(), XDI3Segment.create("+a+b"));
		assertEquals(c.getXri(), XDI3Segment.create("+a+b+c"));
		assertEquals(d.getXri(), XDI3Segment.create("+a+b+c<$t>"));
		assertEquals(value.getXri(), XDI3Segment.create("+a+b+c<$t>&"));
		assertEquals(a.getArcXri(), XDI3Segment.create("+a"));
		assertEquals(b.getArcXri(), XDI3Segment.create("+b"));
		assertEquals(c.getArcXri(), XDI3Segment.create("+c"));
		assertEquals(d.getArcXri(), XDI3Segment.create("<$t>"));
		assertEquals(value.getArcXri(), XDI3Segment.create("&"));

		assertEquals(graph15.getDeepContextNode(XDI3Segment.create("+a+b+c<$t>")), d);
		assertEquals(a.getDeepContextNode(XDI3Segment.create("+b+c<$t>")), d);
		assertEquals(b.getDeepContextNode(XDI3Segment.create("+c<$t>")), d);
		assertEquals(graph15.getDeepRelation(XDI3Segment.create("+a+b+c"), XDI3Segment.create("+x+y")), r);
		assertEquals(a.getDeepRelation(XDI3Segment.create("+b+c"), XDI3Segment.create("+x+y")), r);
		assertEquals(b.getDeepRelation(XDI3Segment.create("+c"), XDI3Segment.create("+x+y")), r);
		assertEquals(graph15.getDeepLiteral(XDI3Segment.create("+a+b+c<$t>&")), l);
		assertEquals(a.getDeepLiteral(XDI3Segment.create("+b+c<$t>&")), l);
		assertEquals(b.getDeepLiteral(XDI3Segment.create("+c<$t>&")), l);

		graph15.close();
	}

	public void testIncomingRelations() throws Exception {

		Graph graph16 = this.openNewGraph(this.getClass().getName() + "-graph-16");

		graph16.setStatement(XDI3Statement.create("=markus/+friend/=animesh"));
		graph16.setStatement(XDI3Statement.create("=markus/+friend/=neustar=les"));
		graph16.setStatement(XDI3Statement.create("[=]!1111[=]!2222/$is/=markus"));

		ContextNode markus = graph16.getDeepContextNode(XDI3Segment.create("=markus"));
		ContextNode animesh = graph16.getDeepContextNode(XDI3Segment.create("=animesh"));
		ContextNode les = graph16.getDeepContextNode(XDI3Segment.create("=neustar=les"));
		ContextNode inumber = graph16.getDeepContextNode(XDI3Segment.create("[=]!1111[=]!2222"));

		assertEquals(graph16.getRootContextNode().getAllRelationCount(), 3);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 2);
		assertEquals(new IteratorCounter(animesh.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(les.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(inumber.getRelations()).count(), 1);
		assertEquals(new IteratorCounter(markus.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(markus.getIncomingRelations(XDI3Segment.create("$is"))).count(), 1);
		assertEquals(new IteratorCounter(animesh.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(animesh.getIncomingRelations(XDI3Segment.create("+friend"))).count(), 1);
		assertEquals(new IteratorCounter(les.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(les.getIncomingRelations(XDI3Segment.create("+friend"))).count(), 1);
		assertEquals(new IteratorCounter(inumber.getIncomingRelations()).count(), 0);

		Relation friend1 = markus.getRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
		Relation friend2 = markus.getRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=neustar=les"));
		Relation is = graph16.getDeepRelation(XDI3Segment.create("[=]!1111[=]!2222"), XDI3Segment.create("$is"));

		assertTrue(new IteratorContains<Relation> (graph16.getDeepRelations(XDI3Segment.create("=markus"), XDI3Segment.create("+friend")), friend1).contains());
		assertTrue(new IteratorContains<Relation> (graph16.getDeepRelations(XDI3Segment.create("=markus"), XDI3Segment.create("+friend")), friend2).contains());

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
		assertEquals(new IteratorCounter(markus.getIncomingRelations(XDI3Segment.create("$is"))).count(), 1);
		assertEquals(new IteratorCounter(les.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(les.getIncomingRelations(XDI3Segment.create("+friend"))).count(), 1);
		assertEquals(new IteratorCounter(inumber.getIncomingRelations()).count(), 0);

		ContextNode neustar = les.getContextNode();
		neustar.delContextNodes();

		assertEquals(graph16.getRootContextNode().getAllRelationCount(), 1);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(inumber.getRelations()).count(), 1);
		assertEquals(new IteratorCounter(markus.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(markus.getIncomingRelations(XDI3Segment.create("$is"))).count(), 1);
		assertEquals(new IteratorCounter(inumber.getIncomingRelations()).count(), 0);

		graph16.getRootContextNode().delContextNode(XDI3SubSegment.create("=markus"));

		assertEquals(graph16.getRootContextNode().getAllRelationCount(), 0);
		assertEquals(new IteratorCounter(inumber.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(inumber.getIncomingRelations()).count(), 0);

		assertEquals(graph16.getRootContextNode().getAllContextNodeCount(), 5);
		assertEquals(graph16.getRootContextNode().getAllRelationCount(), 0);
		assertEquals(graph16.getRootContextNode().getAllLiteralCount(), 0);
		assertEquals(graph16.getRootContextNode().getAllStatementCount(), 5);

		graph16.close();
	}

	public void testNoExceptions() throws Exception {

		Graph graph17 = this.openNewGraph(this.getClass().getName() + "-graph-17");

		graph17.setStatement(XDI3Statement.create("=markus<+email>&/&/\"Markus Sabadello\""));
		graph17.setStatement(XDI3Statement.create("=markus/+friend/=neustar=les"));

		ContextNode root = graph17.getRootContextNode();
		ContextNode markus = graph17.getDeepContextNode(XDI3Segment.create("=markus"));
		ContextNode les = graph17.getDeepContextNode(XDI3Segment.create("=neustar=les"));

		root.setContextNode(XDI3SubSegment.create("=markus"));
		markus.setDeepContextNode(XDI3Segment.create("<+email>&")).setLiteral("test");
		markus.setRelation(XDI3Segment.create("+friend"), les);

		graph17.close();
	}

	public void testTransactions() throws Exception {

		Graph graph18 = this.openNewGraph(this.getClass().getName() + "-graph-18");

		graph18.setStatement(XDI3Statement.create("=markus<+email>&/&/\"Markus Sabadello\""));
		graph18.setStatement(XDI3Statement.create("=markus/+friend/=neustar=les"));

		ContextNode markus = graph18.getDeepContextNode(XDI3Segment.create("=markus"));
		ContextNode value = markus.getDeepContextNode(XDI3Segment.create("<+email>&"));

		graph18.beginTransaction();

		value.delLiteral();
		markus.delRelations(XDI3Segment.create("+friend"));
		markus.setRelation(XDI3Segment.create("$is+"), XDI3Segment.create("+person"));

		assertFalse(value.containsLiteral());
		assertFalse(markus.containsRelations(XDI3Segment.create("+friend")));
		assertTrue(markus.containsRelation(XDI3Segment.create("$is+"), XDI3Segment.create("+person")));

		graph18.rollbackTransaction();

		if (graph18.supportsTransactions()) {

			assertTrue(value.containsLiteral());
			assertTrue(markus.containsRelations(XDI3Segment.create("+friend")));
			assertFalse(markus.containsRelation(XDI3Segment.create("$is+"), XDI3Segment.create("+person")));
		} else {

			assertFalse(value.containsLiteral());
			assertFalse(markus.containsRelations(XDI3Segment.create("+friend")));
			assertTrue(markus.containsRelation(XDI3Segment.create("$is+"), XDI3Segment.create("+person")));
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

		Graph graph19 = this.openNewGraph(this.getClass().getName() + "-graph-19");

		ContextNode markus = graph19.setDeepContextNode(XDI3Segment.create("=markus"));

		assertNull(markus.getLiteral());
		assertFalse(markus.getContextNodes().hasNext());
		try { markus.getContextNodes().next(); fail(); } catch (NoSuchElementException ex) { }
		assertNull(markus.getContextNode(XDI3SubSegment.create("*not")));
		assertFalse(markus.getAllContextNodes().hasNext());
		try { markus.getAllContextNodes().next(); fail(); } catch (NoSuchElementException ex) { }
		assertFalse(markus.getAllLeafContextNodes().hasNext());
		try { markus.getAllLeafContextNodes().next(); fail(); } catch (NoSuchElementException ex) { }
		assertEquals(markus.getContextNodeCount(), 0);
		assertEquals(markus.getAllContextNodeCount(), 0);

		assertNull(markus.getRelation(XDI3Segment.create("+not"), XDI3Segment.create("=not")));
		assertNull(markus.getRelation(XDI3Segment.create("+not")));
		assertFalse(markus.getRelations(XDI3Segment.create("+not")).hasNext());
		try { markus.getRelations(XDI3Segment.create("+not")).next(); fail(); } catch (NoSuchElementException ex) { }
		assertFalse(markus.getRelations().hasNext());
		try { markus.getRelations().next(); fail(); } catch (NoSuchElementException ex) { }
		try { markus.getIncomingRelations().next(); fail(); } catch (NoSuchElementException ex) { }
		assertFalse(markus.getAllRelations().hasNext());
		try { markus.getAllRelations().next(); fail(); } catch (NoSuchElementException ex) { }
		assertEquals(markus.getRelationCount(XDI3Segment.create("+not")), 0);
		assertEquals(markus.getRelationCount(), 0);
		assertEquals(markus.getAllRelationCount(), 0);

		assertNull(markus.getLiteral());
		assertNull(markus.getDeepLiteral(XDI3Segment.create("!not")));
		assertEquals(markus.getAllLiteralCount(), 0);

		assertEquals(markus.getAllStatementCount(), 0);

		graph19.close();
	}

	public void testIllegalArcXris() throws Exception {

		Graph graph20 = this.openNewGraph(this.getClass().getName() + "-graph-20");

		ContextNode markus = graph20.setDeepContextNode(XDI3Segment.create("=markus"));

		try {

			markus.setContextNode(XDI3SubSegment.create("()"));
			fail();
		} catch (Xdi2GraphException ex){

		}

		try {

			markus.setRelation(XDI3Segment.create("()"), XDI3Segment.create("=animesh"));
			fail();
		} catch (Xdi2GraphException ex){

		}

		try {

			markus.setRelation(XDI3Segment.create("&"), XDI3Segment.create("=animesh"));
			fail();
		} catch (Xdi2GraphException ex){

		}

		graph20.close();
	}

	public void testImplied() throws Exception {

		Graph graph21 = this.openNewGraph(this.getClass().getName() + "-graph-21");

		ContextNode webmarkus = graph21.setDeepContextNode(XDI3Segment.create("=web=markus"));
		ContextNode animesh = graph21.setDeepContextNode(XDI3Segment.create("=animesh"));
		Relation friend = webmarkus.setRelation(XDI3Segment.create("+friend"), animesh);
		ContextNode value = webmarkus.setContextNode(XDI3SubSegment.create("<+name>")).setContextNode(XDI3SubSegment.create("&"));
		Literal name = value.setLiteral("Markus Sabadello");
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

		Graph graph22 = this.openNewGraph(this.getClass().getName() + "-graph-22");
		Graph graph23 = this.openNewGraph(this.getClass().getName() + "-graph-23");

		ContextNodeStatement statement22_1 = (ContextNodeStatement) graph22.setStatement(XDI3Statement.create("=neustar/()/=les"));
		RelationStatement statement22_2 = (RelationStatement) graph22.setStatement(XDI3Statement.create("=markus/+friend/=neustar=les"));
		LiteralStatement statement22_3 = (LiteralStatement) graph22.setStatement(XDI3Statement.create("=markus<+email>&/&/\"markus.sabadello@gmail.com\""));

		assertNotNull(graph22.getDeepContextNode(XDI3Segment.create("=markus")));
		assertNotNull(graph22.getDeepContextNode(XDI3Segment.create("=neustar")));
		assertNotNull(graph22.getDeepContextNode(XDI3Segment.create("=neustar=les")));
		assertNotNull(graph22.getDeepRelation(XDI3Segment.create("=markus"), XDI3Segment.create("+friend")));
		assertNotNull(graph22.getDeepRelation(XDI3Segment.create("=markus"), XDI3Segment.create("+friend"), XDI3Segment.create("=neustar=les")));
		assertNotNull(graph22.getDeepLiteral(XDI3Segment.create("=markus<+email>&")));
		assertNotNull(graph22.getDeepLiteral(XDI3Segment.create("=markus<+email>&"), "markus.sabadello@gmail.com"));

		assertTrue(graph22.containsStatement(XDI3Statement.create("=neustar/()/=les")));
		assertTrue(graph22.containsStatement(XDI3Statement.create("=markus/+friend/=neustar=les")));
		assertTrue(graph22.containsStatement(XDI3Statement.create("=markus<+email>&/&/\"markus.sabadello@gmail.com\"")));
		assertEquals(graph22.getStatement(XDI3Statement.create("=neustar/()/=les")).getXri(), XDI3Statement.create("=neustar/()/=les"));
		assertEquals(graph22.getStatement(XDI3Statement.create("=markus/+friend/=neustar=les")).getXri(), "=markus/+friend/=neustar=les");
		assertEquals(graph22.getStatement(XDI3Statement.create("=markus<+email>&/&/\"markus.sabadello@gmail.com\"")).getXri(), "=markus<+email>&/&/\"markus.sabadello@gmail.com\"");

		assertTrue(graph22.getStatement(XDI3Statement.create("=neustar/()/=les")) instanceof ContextNodeStatement);
		assertTrue(graph22.getStatement(XDI3Statement.create("=markus/+friend/=neustar=les")) instanceof RelationStatement);
		assertTrue(graph22.getStatement(XDI3Statement.create("=markus<+email>&/&/\"markus.sabadello@gmail.com\"")) instanceof LiteralStatement);
		assertTrue(graph22.getStatement(XDI3Statement.create("=neustar/()/=les")).getXri().isContextNodeStatement());
		assertTrue(graph22.getStatement(XDI3Statement.create("=markus/+friend/=neustar=les")).getXri().isRelationStatement());
		assertTrue(graph22.getStatement(XDI3Statement.create("=markus<+email>&/&/\"markus.sabadello@gmail.com\"")).getXri().isLiteralStatement());

		CopyUtil.copyStatement(statement22_1, graph23, null);
		CopyUtil.copyStatement(statement22_2, graph23, null);
		CopyUtil.copyStatement(statement22_3, graph23, null);

		assertNotNull(graph23.getDeepContextNode(XDI3Segment.create("=markus")));
		assertNotNull(graph23.getDeepContextNode(XDI3Segment.create("=neustar")));
		assertNotNull(graph23.getDeepContextNode(XDI3Segment.create("=neustar=les")));
		assertNotNull(graph23.getDeepRelation(XDI3Segment.create("=markus"), XDI3Segment.create("+friend")));
		assertNotNull(graph23.getDeepRelation(XDI3Segment.create("=markus"), XDI3Segment.create("+friend"), XDI3Segment.create("=neustar=les")));
		assertNotNull(graph23.getDeepLiteral(XDI3Segment.create("=markus<+email>&")));
		assertNotNull(graph23.getDeepLiteral(XDI3Segment.create("=markus<+email>&"), "markus.sabadello@gmail.com"));

		assertTrue(graph23.containsStatement(XDI3Statement.create("=neustar/()/=les")));
		assertTrue(graph23.containsStatement(XDI3Statement.create("=markus/+friend/=neustar=les")));
		assertTrue(graph23.containsStatement(XDI3Statement.create("=markus<+email>&/&/\"markus.sabadello@gmail.com\"")));
		assertEquals(graph23.getStatement(XDI3Statement.create("=neustar/()/=les")).getXri(), XDI3Statement.create("=neustar/()/=les"));
		assertEquals(graph23.getStatement(XDI3Statement.create("=markus/+friend/=neustar=les")).getXri(), "=markus/+friend/=neustar=les");
		assertEquals(graph23.getStatement(XDI3Statement.create("=markus<+email>&/&/\"markus.sabadello@gmail.com\"")).getXri(), "=markus<+email>&/&/\"markus.sabadello@gmail.com\"");

		assertTrue(graph23.getStatement(XDI3Statement.create("=neustar/()/=les")) instanceof ContextNodeStatement);
		assertTrue(graph23.getStatement(XDI3Statement.create("=markus/+friend/=neustar=les")) instanceof RelationStatement);
		assertTrue(graph23.getStatement(XDI3Statement.create("=markus<+email>&/&/\"markus.sabadello@gmail.com\"")) instanceof LiteralStatement);
		assertTrue(graph23.getStatement(XDI3Statement.create("=neustar/()/=les")).getXri().isContextNodeStatement());
		assertTrue(graph23.getStatement(XDI3Statement.create("=markus/+friend/=neustar=les")).getXri().isRelationStatement());
		assertTrue(graph23.getStatement(XDI3Statement.create("=markus<+email>&/&/\"markus.sabadello@gmail.com\"")).getXri().isLiteralStatement());

		graph22.close();
		graph23.close();
	}

	public void testCreate() throws Exception {

		Graph graph24 = this.openNewGraph(this.getClass().getName() + "-graph-24");

		ContextNode root = graph24.getRootContextNode();

		root.setContextNode(XDI3SubSegment.create("+a"));
		assertNotNull(root.getContextNode(XDI3SubSegment.create("+a")));
		assertNotNull(root.getDeepContextNode(XDI3Segment.create("+a")));
		assertNotNull(graph24.getDeepContextNode(XDI3Segment.create("+a")));
		root.setContextNode(XDI3SubSegment.create("+a"));
		root.setDeepContextNode(XDI3Segment.create("+a"));
		graph24.setDeepContextNode(XDI3Segment.create("+a"));

		root.setDeepContextNode(XDI3Segment.create("+a+b"));
		assertNotNull(root.getContextNode(XDI3SubSegment.create("+a")).getContextNode(XDI3SubSegment.create("+b")));
		assertNotNull(root.getDeepContextNode(XDI3Segment.create("+a+b")));
		assertNotNull(graph24.getDeepContextNode(XDI3Segment.create("+a+b")));
		root.setContextNode(XDI3SubSegment.create("+a"));
		root.setDeepContextNode(XDI3Segment.create("+a"));
		root.setDeepContextNode(XDI3Segment.create("+a+b"));
		graph24.setDeepContextNode(XDI3Segment.create("+a"));
		graph24.setDeepContextNode(XDI3Segment.create("+a+b"));

		graph24.close();
	}

	public void testSet() throws Exception {

		Graph graph25 = this.openNewGraph(this.getClass().getName() + "-graph-25");

		ContextNode root = graph25.getRootContextNode();

		root.setContextNode(XDI3SubSegment.create("+a"));
		assertNotNull(root.getContextNode(XDI3SubSegment.create("+a")));
		assertNotNull(root.getDeepContextNode(XDI3Segment.create("+a")));
		assertNotNull(graph25.getDeepContextNode(XDI3Segment.create("+a")));
		root.setContextNode(XDI3SubSegment.create("+a"));
		root.setDeepContextNode(XDI3Segment.create("+a"));
		graph25.setDeepContextNode(XDI3Segment.create("+a"));

		root.setDeepContextNode(XDI3Segment.create("+a+b"));
		assertNotNull(root.getContextNode(XDI3SubSegment.create("+a")).getContextNode(XDI3SubSegment.create("+b")));
		assertNotNull(root.getDeepContextNode(XDI3Segment.create("+a+b")));
		assertNotNull(graph25.getDeepContextNode(XDI3Segment.create("+a+b")));
		root.setContextNode(XDI3SubSegment.create("+a"));
		root.setDeepContextNode(XDI3Segment.create("+a"));
		root.setDeepContextNode(XDI3Segment.create("+a+b"));
		graph25.setDeepContextNode(XDI3Segment.create("+a"));
		graph25.setDeepContextNode(XDI3Segment.create("+a+b"));

		graph25.close();
	}

	public void testDelete() throws Exception {

		Graph graph26 = this.openNewGraph(this.getClass().getName() + "-graph-26");

		ContextNode root = graph26.getRootContextNode();

		root.setContextNode(XDI3SubSegment.create("+a"));
		assertTrue(root.containsContextNode(XDI3SubSegment.create("+a")));
		assertNotNull(root.getContextNode(XDI3SubSegment.create("+a")));
		root.delContextNode(XDI3SubSegment.create("+a"));
		assertFalse(graph26.getRootContextNode().containsContextNode(XDI3SubSegment.create("+a")));
		assertFalse(root.containsContextNode(XDI3SubSegment.create("+a")));
		assertNull(graph26.getRootContextNode().getContextNode(XDI3SubSegment.create("+a")));
		assertNull(root.getContextNode(XDI3SubSegment.create("+a")));

		root.setRelation(XDI3Segment.create("+a"), XDI3Segment.create("+b"));
		assertTrue(root.containsRelation(XDI3Segment.create("+a"), XDI3Segment.create("+b")));
		assertNotNull(root.getRelation(XDI3Segment.create("+a"), XDI3Segment.create("+b")));
		root.delRelation(XDI3Segment.create("+a"), XDI3Segment.create("+b"));
		assertFalse(root.containsRelation(XDI3Segment.create("+a"), XDI3Segment.create("+b")));
		assertNull(root.getRelation(XDI3Segment.create("+a"), XDI3Segment.create("+b")));

		root.setDeepContextNode(XDI3Segment.create("<+a>&")).setLiteral("a");
		assertTrue(root.getDeepContextNode(XDI3Segment.create("<+a>&")).containsLiteral("a"));
		assertNotNull(root.getDeepContextNode(XDI3Segment.create("<+a>&")).getLiteral("a"));
		root.getDeepContextNode(XDI3Segment.create("<+a>&")).delLiteral();
		assertFalse(root.getDeepContextNode(XDI3Segment.create("<+a>&")).containsLiteral("a"));
		assertNull(root.getDeepContextNode(XDI3Segment.create("<+a>&")).getLiteral("a"));

		root.getContextNode(XDI3SubSegment.create("+b")).delete();
		root.getDeepContextNode(XDI3Segment.create("<+a>&")).delete();
		root.getDeepContextNode(XDI3Segment.create("<+a>")).delete();

		assertTrue(root.isEmpty());

		graph26.close();
	}

	public void testDeleteContextNodesDeletesRelations() throws Exception {

		Graph graph27 = this.openNewGraph(this.getClass().getName() + "-graph-27");

		Relation r1 = graph27.setDeepRelation(XDI3Segment.create("=animesh"), XDI3Segment.create("+friend"), XDI3Segment.create("=markus"));
		Relation r2 = graph27.setDeepRelation(XDI3Segment.create("=markus"), XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));

		ContextNode markus = r1.follow();
		ContextNode animesh = r2.follow();

		markus.delete();

		assertNotNull(graph27.getDeepContextNode(XDI3Segment.create("=animesh")));

		assertFalse(graph27.getDeepContextNode(XDI3Segment.create("=animesh")).getRelations().hasNext());
		assertFalse(graph27.getDeepContextNode(XDI3Segment.create("=animesh")).getRelations(XDI3Segment.create("+friend")).hasNext());
		assertEquals(graph27.getDeepContextNode(XDI3Segment.create("=animesh")).getRelationCount(), 0);
		assertFalse(graph27.getDeepContextNode(XDI3Segment.create("=animesh")).getIncomingRelations().hasNext());
		assertFalse(graph27.getDeepContextNode(XDI3Segment.create("=animesh")).getIncomingRelations(XDI3Segment.create("+friend")).hasNext());
		assertFalse(animesh.getRelations().hasNext());
		assertFalse(animesh.getRelations(XDI3Segment.create("+friend")).hasNext());
		assertEquals(animesh.getRelationCount(), 0);
		assertFalse(animesh.getIncomingRelations().hasNext());
		assertFalse(animesh.getIncomingRelations(XDI3Segment.create("+friend")).hasNext());

		animesh.delete();

		graph27.setStatement(XDI3Statement.create("+a+b/$i/=x"));
		graph27.setStatement(XDI3Statement.create("=x/$i/=y"));

		graph27.getRootContextNode().getContextNode(XDI3SubSegment.create("+a")).delete();

		assertEquals(graph27.getRootContextNode().getAllRelationCount(), 1);
		assertTrue(graph27.getRootContextNode().getContextNode(XDI3SubSegment.create("=x")).getRelations().hasNext());
		assertFalse(graph27.getRootContextNode().getContextNode(XDI3SubSegment.create("=x")).getIncomingRelations().hasNext());

		graph27.getRootContextNode().getContextNode(XDI3SubSegment.create("=x")).delete();

		assertEquals(graph27.getRootContextNode().getAllRelationCount(), 0);

		graph27.close();
	}

	public void testLiteralData() throws Exception {

		Graph graph28 = this.openNewGraph(this.getClass().getName() + "-graph-28");

		ContextNode c = graph28.setDeepContextNode(XDI3Segment.create("=markus<+test>&"));

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

		c.setLiteral(s);
		assertEquals(c.getLiteral().getLiteralData(), s);
		assertEquals(c.getLiteral().getLiteralDataString(), s);
		assertNull(c.getLiteral().getLiteralDataNumber());
		assertNull(c.getLiteral().getLiteralDataBoolean());
		assertTrue(c.containsLiteral(s));
		assertTrue(c.containsLiteralString(s));

		c.setLiteral(d);
		assertEquals(c.getLiteral().getLiteralData(), d);
		assertNull(c.getLiteral().getLiteralDataString());
		assertEquals(c.getLiteral().getLiteralDataNumber(), d);
		assertNull(c.getLiteral().getLiteralDataBoolean());
		assertTrue(c.containsLiteral(d));
		assertTrue(c.containsLiteralNumber(d));

		c.setLiteral(b);
		assertEquals(c.getLiteral().getLiteralData(), b);
		assertNull(c.getLiteral().getLiteralDataString());
		assertNull(c.getLiteral().getLiteralDataNumber());
		assertEquals(c.getLiteral().getLiteralDataBoolean(), b);
		assertTrue(c.containsLiteral(b));
		assertTrue(c.containsLiteralBoolean(b));

		c.setLiteral(a);
		assertEquals(c.getLiteral().getLiteralData(), a);
		assertNull(c.getLiteral().getLiteralDataString());
		assertNull(c.getLiteral().getLiteralDataNumber());
		assertNull(c.getLiteral().getLiteralDataBoolean());
		assertTrue(c.containsLiteral(a));

		c.setLiteral(o);
		assertEquals(c.getLiteral().getLiteralData(), o);
		assertNull(c.getLiteral().getLiteralDataString());
		assertNull(c.getLiteral().getLiteralDataNumber());
		assertNull(c.getLiteral().getLiteralDataBoolean());
		assertTrue(c.containsLiteral(o));

		c.setLiteral(n);
		assertEquals(c.getLiteral().getLiteralData(), n);
		assertNull(c.getLiteral().getLiteralDataString());
		assertNull(c.getLiteral().getLiteralDataNumber());
		assertNull(c.getLiteral().getLiteralDataBoolean());
		assertTrue(c.containsLiteral(n));

		graph28.close();
	}

	public void testDoubleSet() throws Exception {

		Graph graph29 = this.openNewGraph(this.getClass().getName() + "-graph-29");

		ContextNode c = graph29.setDeepContextNode(XDI3Segment.create("=markus"));
		ContextNode a = graph29.setDeepContextNode(XDI3Segment.create("=animesh"));

		c.setContextNode(XDI3SubSegment.create("<+email>"));
		c.setContextNode(XDI3SubSegment.create("<+email>"));

		c.setRelation(XDI3Segment.create("+friend"), a);
		c.setRelation(XDI3Segment.create("+friend"), a);
		c.setRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
		c.setRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));

		assertEquals(c.getContextNodeCount(), 1);
		assertEquals(c.getAllContextNodeCount(), 1);
		assertEquals(c.getRelationCount(XDI3Segment.create("+friend")), 1);
		assertEquals(c.getRelationCount(), 1);
		assertEquals(c.getAllRelationCount(), 1);

		c.delContextNode(XDI3SubSegment.create("<+email>"));
		c.delRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));

		assertEquals(c.getContextNodeCount(), 0);
		assertEquals(c.getAllContextNodeCount(), 0);
		assertEquals(c.getRelationCount(XDI3Segment.create("+friend")), 0);
		assertEquals(c.getRelationCount(), 0);
		assertEquals(c.getAllRelationCount(), 0);

		graph29.close();
	}

	@SuppressWarnings("unused")
	private static void makeGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.setContextNode(XDI3SubSegment.create("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.setContextNode(XDI3SubSegment.create("[+passport]"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.setContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.setContextNode(XDI3SubSegment.create("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.setContextNode(XDI3SubSegment.create("[$v]"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.setContextNode(XDI3SubSegment.create("[$o]"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.setContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.setContextNode(XDI3SubSegment.create("!2"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.setRelation(XDI3Segment.create("$"), XDI3Segment.create("=abc[+passport]"));
		Literal abcPassport1NumberLiteral = abcPassport1ContextNode.setDeepContextNode(XDI3Segment.create("<+number>&")).setLiteral("987654321");
		Literal abcPassport1CountryLiteral = abcPassport1ContextNode.setDeepContextNode(XDI3Segment.create("<+country>&")).setLiteral("Canada");
		Literal abcPassport1DLiteral = abcPassport1ContextNode.setDeepContextNode(XDI3Segment.create("<$t>&")).setLiteral("2005-01-01T00:00:00Z");
		Literal abcPassport2NumberLiteral = abcPassport2ContextNode.setDeepContextNode(XDI3Segment.create("<+number>&")).setLiteral("123456789");
		Literal abcPassport2CountryLiteral = abcPassport2ContextNode.setDeepContextNode(XDI3Segment.create("<+country>&")).setLiteral("New Zealand");
		Literal abcPassport2DLiteral = abcPassport2ContextNode.setDeepContextNode(XDI3Segment.create("<$t>&")).setLiteral("2010-10-01T00:00:00Z");
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.setContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.setContextNode(XDI3SubSegment.create("!2"));
		Literal abcPassportC1DLiteral = abcPassportC1ContextNode.setDeepContextNode(XDI3Segment.create("<$t>&")).setLiteral("2010-11-11T11:11:11Z");
		Literal abcPassportC2DLiteral = abcPassportC2ContextNode.setDeepContextNode(XDI3Segment.create("<$t>&")).setLiteral("2010-12-22T22:22:22Z");
		Relation abcPassportRelationV = abcPassportContextNode.setRelation(XDI3Segment.create("[$v]"), XDI3Segment.create("=abc[+passport][$v]!2"));
		Relation abcPassportRelation1 = abcPassportContextNode.setRelation(XDI3Segment.create("*1"), abcPassport1ContextNode);
		Relation abcPassportRelation2 = abcPassportContextNode.setRelation(XDI3Segment.create("*2"), abcPassport2ContextNode);
		Relation abcTestRelation1 = abcContextNode.setRelation(XDI3Segment.create("+rel"), abcPassport1ContextNode);
		Relation abcTestRelation2 = abcContextNode.setRelation(XDI3Segment.create("+rel"), abcPassport2ContextNode);
	}

	private static void testGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.getContextNode(XDI3SubSegment.create("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.getContextNode(XDI3SubSegment.create("[+passport]"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("[$v]"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("[$o]"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.getContextNode(XDI3SubSegment.create("!2"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.getRelation(XDI3Segment.create("$"));
		Literal abcPassport1NumberLiteral = abcPassport1ContextNode.getDeepLiteral(XDI3Segment.create("<+number>&"));
		Literal abcPassport1CountryLiteral = abcPassport1ContextNode.getDeepLiteral(XDI3Segment.create("<+country>&"));
		Literal abcPassport1DLiteral = abcPassport1ContextNode.getDeepLiteral(XDI3Segment.create("<$t>&"));
		Literal abcPassport2NumberLiteral = abcPassport2ContextNode.getDeepLiteral(XDI3Segment.create("<+number>&"));
		Literal abcPassport2CountryLiteral = abcPassport2ContextNode.getDeepLiteral(XDI3Segment.create("<+country>&"));
		Literal abcPassport2DLiteral = abcPassport2ContextNode.getDeepLiteral(XDI3Segment.create("<$t>&"));
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.getContextNode(XDI3SubSegment.create("!2"));
		Literal abcPassportC1DLiteral = abcPassportC1ContextNode.getDeepLiteral(XDI3Segment.create("<$t>&"));
		Literal abcPassportC2DLiteral = abcPassportC2ContextNode.getDeepLiteral(XDI3Segment.create("<$t>&"));
		Relation abcPassportRelationV = abcPassportContextNode.getRelation(XDI3Segment.create("[$v]"));
		Relation abcPassportRelation1 = abcPassportContextNode.getRelation(XDI3Segment.create("*1"));
		Relation abcPassportRelation2 = abcPassportContextNode.getRelation(XDI3Segment.create("*2"));
		Relation abcRelation1 = abcContextNode.getRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc[+passport]!1"));
		Relation abcRelation2 = abcContextNode.getRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc[+passport]!2"));

		assertEquals(rootContextNode.getXri(), XDIConstants.XRI_S_ROOT);
		assertEquals(abcContextNode.getXri(), XDI3Segment.create("=abc"));
		assertEquals(abcPassportContextNode.getXri(), XDI3Segment.create("=abc[+passport]"));
		assertEquals(abcPassportVContextNode.getXri(), XDI3Segment.create("=abc[+passport][$v]"));

		assertTrue(rootContextNode.containsContextNode(XDI3SubSegment.create("=abc")));
		assertTrue(abcContextNode.containsContextNode(XDI3SubSegment.create("[+passport]")));
		assertTrue(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("!1")));
		assertTrue(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("!2")));
		assertTrue(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("[$v]")));
		assertTrue(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("[$o]")));
		assertTrue(abcPassportVContextNode.containsContextNode(XDI3SubSegment.create("!1")));
		assertTrue(abcPassportVContextNode.containsContextNode(XDI3SubSegment.create("!2")));
		assertTrue(abcPassportV2ContextNode.containsRelation(XDI3Segment.create("$"), XDI3Segment.create("=abc[+passport]")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDI3SubSegment.create("<+number>")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDI3SubSegment.create("<+country>")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDI3SubSegment.create("<$t>")));
		assertTrue(abcPassport2ContextNode.containsContextNode(XDI3SubSegment.create("<+number>")));
		assertTrue(abcPassport2ContextNode.containsContextNode(XDI3SubSegment.create("<+country>")));
		assertTrue(abcPassport2ContextNode.containsContextNode(XDI3SubSegment.create("<$t>")));
		assertTrue(abcPassport1ContextNode.getDeepContextNode(XDI3Segment.create("<+number>&")).containsLiteral());
		assertTrue(abcPassport1ContextNode.getDeepContextNode(XDI3Segment.create("<+country>&")).containsLiteral());
		assertTrue(abcPassport1ContextNode.getDeepContextNode(XDI3Segment.create("<$t>&")).containsLiteral());
		assertTrue(abcPassport2ContextNode.getDeepContextNode(XDI3Segment.create("<+number>&")).containsLiteral());
		assertTrue(abcPassport2ContextNode.getDeepContextNode(XDI3Segment.create("<+country>&")).containsLiteral());
		assertTrue(abcPassport2ContextNode.getDeepContextNode(XDI3Segment.create("<$t>&")).containsLiteral());
		assertTrue(abcPassportCContextNode.containsContextNode(XDI3SubSegment.create("!1")));
		assertTrue(abcPassportCContextNode.containsContextNode(XDI3SubSegment.create("!2")));
		assertTrue(abcPassportC1ContextNode.containsContextNode(XDI3SubSegment.create("<$t>")));
		assertTrue(abcPassportC2ContextNode.containsContextNode(XDI3SubSegment.create("<$t>")));
		assertTrue(abcPassportC1ContextNode.getDeepContextNode(XDI3Segment.create("<$t>&")).containsLiteral());
		assertTrue(abcPassportC2ContextNode.getDeepContextNode(XDI3Segment.create("<$t>&")).containsLiteral());
		assertTrue(abcPassportContextNode.containsRelation(XDI3Segment.create("[$v]"), XDI3Segment.create("=abc[+passport][$v]!2")));
		assertTrue(abcPassportContextNode.containsRelation(XDI3Segment.create("*1"), XDI3Segment.create("=abc[+passport]!1")));
		assertTrue(abcPassportContextNode.containsRelation(XDI3Segment.create("*2"), XDI3Segment.create("=abc[+passport]!2")));
		assertTrue(abcContextNode.containsRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc[+passport]!1")));
		assertTrue(abcContextNode.containsRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc[+passport]!2")));

		ContextNode contextNodesArray[] = new ContextNode [] {
				graph.getDeepContextNode(XDI3Segment.create("()")),
				graph.getDeepContextNode(XDI3Segment.create("=abc")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!1")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!2")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$v]")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$o]")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$v]!1")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$v]!2")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$o]!1")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$o]!2")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!1<+number>&")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!1<+country>&")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!1<$t>&")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!2<+number>&")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!2<+country>&")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!2<$t>&")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$o]!1<$t>&")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$o]!2<$t>&"))
		};

		XDI3SubSegment[][] contextNodeXriArray = new XDI3SubSegment[][] {
				new XDI3SubSegment[] { XDI3SubSegment.create("=abc") },
				new XDI3SubSegment[] { XDI3SubSegment.create("[+passport]") },
				new XDI3SubSegment[] { XDI3SubSegment.create("!1"), XDI3SubSegment.create("!2"), XDI3SubSegment.create("[$v]"), XDI3SubSegment.create("[$o]") },
				new XDI3SubSegment[] { XDI3SubSegment.create("<+number>"), XDI3SubSegment.create("<+country>"), XDI3SubSegment.create("<$t>") },
				new XDI3SubSegment[] { XDI3SubSegment.create("<+number>"), XDI3SubSegment.create("<+country>"), XDI3SubSegment.create("<$t>") },
				new XDI3SubSegment[] { XDI3SubSegment.create("!1"), XDI3SubSegment.create("!2") },
				new XDI3SubSegment[] { XDI3SubSegment.create("!1"), XDI3SubSegment.create("!2") },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { XDI3SubSegment.create("<$t>") },
				new XDI3SubSegment[] { XDI3SubSegment.create("<$t>") },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { }
		};

		XDI3Segment[][] relationArcXrisArray = new XDI3Segment[][] {
				new XDI3Segment[] { },
				new XDI3Segment[] { XDI3Segment.create("+rel"), XDI3Segment.create("+rel") },
				new XDI3Segment[] { XDI3Segment.create("[$v]"), XDI3Segment.create("*1"), XDI3Segment.create("*2") },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { XDI3Segment.create("$") },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { }
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

		assertEquals(contextNodesArray.length, contextNodeXriArray.length);
		assertEquals(contextNodesArray.length, relationArcXrisArray.length);
		assertEquals(contextNodesArray.length, literalsArray.length);

		for (ContextNode contextNode : contextNodesArray) if (contextNode != null) assertTrue(contextNode.getGraph().getDeepContextNode(contextNode.getXri()) != null);

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(contextNodeXriArray[i]); continue; } else assertNotNull(contextNodeXriArray[i]);

			Set<XDI3SubSegment> arcXris = new HashSet<XDI3SubSegment> (Arrays.asList(contextNodeXriArray[i]));
			assertEquals(arcXris.size(), contextNodesArray[i].getContextNodeCount());
			assertEquals(arcXris.size(), new IteratorCounter(contextNodesArray[i].getContextNodes()).count());
			for (Iterator<ContextNode> it = contextNodesArray[i].getContextNodes(); it.hasNext(); ) assertTrue(arcXris.remove(it.next().getArcXri()));
			assertTrue(arcXris.isEmpty());
		}

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(relationArcXrisArray[i]); continue; } else assertNotNull(relationArcXrisArray[i]);

			List<XDI3Segment> arcXris = new ArrayList<XDI3Segment> (Arrays.asList(relationArcXrisArray[i]));
			for (Iterator<XDI3Segment> it = arcXris.iterator(); it.hasNext(); ) assertTrue(contextNodesArray[i].getGraph().getDeepRelations(contextNodesArray[i].getXri(), it.next()) != null);
			assertEquals(arcXris.size(), contextNodesArray[i].getRelationCount());
			assertEquals(arcXris.size(), new IteratorCounter(contextNodesArray[i].getRelations()).count());
			for (Iterator<Relation> it = contextNodesArray[i].getRelations(); it.hasNext(); ) assertTrue(arcXris.remove(it.next().getArcXri()));
			assertTrue(arcXris.isEmpty());
		}

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(literalsArray[i]); continue; } else assertNotNull(literalsArray[i]);

			assertEquals(contextNodesArray[i].getGraph().getDeepLiteral(contextNodesArray[i].getXri()) != null, literalsArray[i].booleanValue());

			assertEquals(contextNodesArray[i].containsLiteral(), literalsArray[i].booleanValue());
		}

		assertNull(rootContextNode.getArcXri());
		assertEquals(XDIConstants.XRI_S_CONTEXT, rootContextNode.getXri());
		assertEquals(XDI3SubSegment.create("=abc"), abcContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc"), abcContextNode.getXri());
		assertEquals(XDI3SubSegment.create("[+passport]"), abcPassportContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport]"), abcPassportContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!1"), abcPassport1ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport]!1"), abcPassport1ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!2"), abcPassport2ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport]!2"), abcPassport2ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("[$v]"), abcPassportVContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport][$v]"), abcPassportVContextNode.getXri());
		assertEquals(XDI3SubSegment.create("[$o]"), abcPassportCContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport][$o]"), abcPassportCContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!1"), abcPassportV1ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport][$v]!1"), abcPassportV1ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!2"), abcPassportV2ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport][$v]!2"), abcPassportV2ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("$"), abcPassportV2RelationDollar.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport]"), abcPassportV2RelationDollar.getTargetContextNodeXri());
		assertEquals(XDI3SubSegment.create("<+number>"), abcPassport1NumberLiteral.getContextNode().getContextNode().getArcXri());
		assertEquals("987654321", abcPassport1NumberLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("<+country>"), abcPassport1CountryLiteral.getContextNode().getContextNode().getArcXri());
		assertEquals("Canada", abcPassport1CountryLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("<$t>"), abcPassport1DLiteral.getContextNode().getContextNode().getArcXri());
		assertEquals("2005-01-01T00:00:00Z", abcPassport1DLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("<+number>"), abcPassport2NumberLiteral.getContextNode().getContextNode().getArcXri());
		assertEquals("123456789", abcPassport2NumberLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("<+country>"), abcPassport2CountryLiteral.getContextNode().getContextNode().getArcXri());
		assertEquals("New Zealand", abcPassport2CountryLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("<$t>"), abcPassport2DLiteral.getContextNode().getContextNode().getArcXri());
		assertEquals("2010-10-01T00:00:00Z", abcPassport2DLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("!1"), abcPassportC1ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport][$o]!1"), abcPassportC1ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!2"), abcPassportC2ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport][$o]!2"), abcPassportC2ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("<$t>"), abcPassportC1DLiteral.getContextNode().getContextNode().getArcXri());
		assertEquals("2010-11-11T11:11:11Z", abcPassportC1DLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("<$t>"), abcPassportC2DLiteral.getContextNode().getContextNode().getArcXri());
		assertEquals("2010-12-22T22:22:22Z", abcPassportC2DLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("[$v]"), abcPassportRelationV.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport][$v]!2"), abcPassportRelationV.getTargetContextNodeXri());
		assertEquals(XDI3SubSegment.create("*1"), abcPassportRelation1.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport]!1"), abcPassportRelation1.getTargetContextNodeXri());
		assertEquals(XDI3SubSegment.create("*2"), abcPassportRelation2.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport]!2"), abcPassportRelation2.getTargetContextNodeXri());
		assertEquals(XDI3SubSegment.create("+rel"), abcRelation1.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport]!1"), abcRelation1.getTargetContextNodeXri());
		assertEquals(XDI3SubSegment.create("+rel"), abcRelation2.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport]!2"), abcRelation2.getTargetContextNodeXri());

		assertEquals(rootContextNode.getAllContextNodeCount(), 26);
		assertEquals(rootContextNode.getAllRelationCount(), 6);
		assertEquals(rootContextNode.getAllLiteralCount(), 8);
	}

	@SuppressWarnings("unused")
	private static void manipulateGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.getContextNode(XDI3SubSegment.create("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.getContextNode(XDI3SubSegment.create("[+passport]"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("[$v]"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("[$o]"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.getContextNode(XDI3SubSegment.create("!2"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.getRelation(XDI3Segment.create("$"));
		Literal abcPassport1LiteralNumber = abcPassport1ContextNode.getDeepLiteral(XDI3Segment.create("<+number>&"));
		Literal abcPassport1LiteralCountry = abcPassport1ContextNode.getDeepLiteral(XDI3Segment.create("<+country>&"));
		Literal abcPassport1LiteralD = abcPassport1ContextNode.getDeepLiteral(XDI3Segment.create("<$t>&"));
		Literal abcPassport2LiteralNumber = abcPassport2ContextNode.getDeepLiteral(XDI3Segment.create("<+number>&"));
		Literal abcPassport2LiteralCountry = abcPassport2ContextNode.getDeepLiteral(XDI3Segment.create("<+country>&"));
		Literal abcPassport2LiteralD = abcPassport2ContextNode.getDeepLiteral(XDI3Segment.create("<$t>&"));
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.getContextNode(XDI3SubSegment.create("!2"));
		Literal abcPassportC1LiteralD = abcPassportC1ContextNode.getDeepLiteral(XDI3Segment.create("<$t>&"));
		Literal abcPassportC2LiteralD = abcPassportC2ContextNode.getDeepLiteral(XDI3Segment.create("<$t>&"));
		Relation abcPassportRelationV = abcPassportContextNode.getRelation(XDI3Segment.create("[$v]"));
		Relation abcPassportRelation1 = abcPassportContextNode.getRelation(XDI3Segment.create("*1"));
		Relation abcPassportRelation2 = abcPassportContextNode.getRelation(XDI3Segment.create("*2"));
		Relation abcRelation1 = abcContextNode.getRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc[+passport]!1"));
		Relation abcRelation2 = abcContextNode.getRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc[+passport]!2"));

		abcPassport2ContextNode.delete();
		abcPassportC1LiteralD.setLiteralData("2010-03-03T03:03:03Z");
		abcPassportC2LiteralD.getContextNode().getContextNode().delete();
	}

	private static void testManipulatedGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.getContextNode(XDI3SubSegment.create("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.getContextNode(XDI3SubSegment.create("[+passport]"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("[$v]"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("[$o]"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.getContextNode(XDI3SubSegment.create("!2"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.getRelation(XDI3Segment.create("$"));
		Literal abcPassport1NumberLiteral = abcPassport1ContextNode.getDeepLiteral(XDI3Segment.create("<+number>&"));
		Literal abcPassport1CountryLiteral = abcPassport1ContextNode.getDeepLiteral(XDI3Segment.create("<+country>&"));
		Literal abcPassport1LiteralD = abcPassport1ContextNode.getDeepLiteral(XDI3Segment.create("<$t>&"));
		Literal abcPassport2LiteralNumber = null;
		Literal abcPassport2LiteralCountry = null;
		Literal abcPassport2LiteralD = null;
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.getContextNode(XDI3SubSegment.create("!2"));
		Literal abcPassportC1LiteralD = abcPassportC1ContextNode.getDeepLiteral(XDI3Segment.create("<$t>&"));
		Literal abcPassportC2LiteralD = abcPassportC2ContextNode.getDeepLiteral(XDI3Segment.create("<$t>&"));
		Relation abcPassportRelationV = abcPassportContextNode.getRelation(XDI3Segment.create("[$v]"));
		Relation abcPassportRelation1 = abcPassportContextNode.getRelation(XDI3Segment.create("*1"));
		Relation abcPassportRelation2 = abcPassportContextNode.getRelation(XDI3Segment.create("*2"));
		Relation abcRelation1 = abcContextNode.getRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc[+passport]!1"));
		Relation abcRelation2 = abcContextNode.getRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc[+passport]!2"));

		assertTrue(rootContextNode.containsContextNode(XDI3SubSegment.create("=abc")));
		assertTrue(abcContextNode.containsContextNode(XDI3SubSegment.create("[+passport]")));
		assertTrue(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("!1")));
		assertFalse(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("!2")));	// MANIPULATED
		assertTrue(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("[$v]")));
		assertTrue(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("[$o]")));
		assertTrue(abcPassportVContextNode.containsContextNode(XDI3SubSegment.create("!1")));
		assertTrue(abcPassportVContextNode.containsContextNode(XDI3SubSegment.create("!2")));
		assertTrue(abcPassportV2ContextNode.containsRelation(XDI3Segment.create("$"), XDI3Segment.create("=abc[+passport]")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDI3SubSegment.create("<+number>")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDI3SubSegment.create("<+country>")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDI3SubSegment.create("<$t>")));
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertTrue(abcPassport1ContextNode.getDeepContextNode(XDI3Segment.create("<+number>&")).containsLiteral());
		assertTrue(abcPassport1ContextNode.getDeepContextNode(XDI3Segment.create("<+country>&")).containsLiteral());
		assertTrue(abcPassport1ContextNode.getDeepContextNode(XDI3Segment.create("<$t>&")).containsLiteral());
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertTrue(abcPassportCContextNode.containsContextNode(XDI3SubSegment.create("!1")));
		assertTrue(abcPassportCContextNode.containsContextNode(XDI3SubSegment.create("!2")));
		assertTrue(abcPassportC1ContextNode.containsContextNode(XDI3SubSegment.create("<$t>")));
		assertFalse(abcPassportC2ContextNode.containsContextNode(XDI3SubSegment.create("<$t>")));	// MANIPULATED
		assertTrue(abcPassportC1ContextNode.getDeepContextNode(XDI3Segment.create("<$t>&")).containsLiteral());
		assertNull(abcPassportC2LiteralD);	// MANIPULATED
		assertTrue(abcPassportContextNode.containsRelation(XDI3Segment.create("[$v]"), XDI3Segment.create("=abc[+passport][$v]!2")));
		assertTrue(abcPassportContextNode.containsRelation(XDI3Segment.create("*1"), XDI3Segment.create("=abc[+passport]!1")));
		assertFalse(abcPassportContextNode.containsRelation(XDI3Segment.create("*2"), XDI3Segment.create("=abc[+passport]!2")));		// MANIPULATED
		assertTrue(abcContextNode.containsRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc[+passport]!1")));
		assertFalse(abcContextNode.containsRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc[+passport]!2")));		// MANIPULATED

		ContextNode contextNodesArray[] = new ContextNode [] {
				graph.getDeepContextNode(XDI3Segment.create("()")),
				graph.getDeepContextNode(XDI3Segment.create("=abc")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!1")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!2")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$v]")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$o]")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$v]!1")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$v]!2")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$o]!1")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$o]!2")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!1<+number>&")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!1<+country>&")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!1<$t>&")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!2<+number>&")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!2<+country>&")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport]!2<$t>&")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$o]!1<$t>&")),
				graph.getDeepContextNode(XDI3Segment.create("=abc[+passport][$o]!2<$t>&"))
		};

		XDI3SubSegment[][] contextNodeXriArray = new XDI3SubSegment[][] {
				new XDI3SubSegment[] { XDI3SubSegment.create("=abc") },
				new XDI3SubSegment[] { XDI3SubSegment.create("[+passport]") },
				new XDI3SubSegment[] { XDI3SubSegment.create("!1"), XDI3SubSegment.create("[$v]"), XDI3SubSegment.create("[$o]") },	// MANIPULATED
				new XDI3SubSegment[] { XDI3SubSegment.create("<+number>"), XDI3SubSegment.create("<+country>"), XDI3SubSegment.create("<$t>") },
				null,	// MANIPULATED
				new XDI3SubSegment[] { XDI3SubSegment.create("!1"), XDI3SubSegment.create("!2") },
				new XDI3SubSegment[] { XDI3SubSegment.create("!1"), XDI3SubSegment.create("!2") },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { XDI3SubSegment.create("<$t>") },
				new XDI3SubSegment[] { },	// MANIPULATED
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				null,	// MANIPULATED
				null,	// MANIPULATED
				null,	// MANIPULATED
				new XDI3SubSegment[] { },
				null	// MANIPULATED
		};

		XDI3Segment[][] relationArcXrisArray = new XDI3Segment[][] {
				new XDI3Segment[] { },
				new XDI3Segment[] { XDI3Segment.create("+rel") },	// MANIPULATED
				new XDI3Segment[] { XDI3Segment.create("[$v]"), XDI3Segment.create("*1") },	// MANIPULATED
				new XDI3Segment[] { },
				null,	// MANIPULATED
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { XDI3Segment.create("$") },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				new XDI3Segment[] { },
				null,	// MANIPULATED
				null,	// MANIPULATED
				null,	// MANIPULATED
				new XDI3Segment[] { },
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

		assertEquals(contextNodesArray.length, contextNodeXriArray.length);
		assertEquals(contextNodesArray.length, relationArcXrisArray.length);
		assertEquals(contextNodesArray.length, literalsArray.length);

		for (ContextNode contextNode : contextNodesArray) if (contextNode != null) assertTrue(contextNode.getGraph().getDeepContextNode(contextNode.getXri()) != null);

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(contextNodeXriArray[i]); continue; } else assertNotNull(contextNodeXriArray[i]);

			Set<XDI3SubSegment> arcXris = new HashSet<XDI3SubSegment> (Arrays.asList(contextNodeXriArray[i]));
			assertEquals(arcXris.size(), contextNodesArray[i].getContextNodeCount());
			assertEquals(arcXris.size(), new IteratorCounter(contextNodesArray[i].getContextNodes()).count());
			for (Iterator<ContextNode> it = contextNodesArray[i].getContextNodes(); it.hasNext(); ) assertTrue(arcXris.remove(it.next().getArcXri()));
			assertTrue(arcXris.isEmpty());
		}

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(relationArcXrisArray[i]); continue; } else assertNotNull(relationArcXrisArray[i]);

			List<XDI3Segment> arcXris = new ArrayList<XDI3Segment> (Arrays.asList(relationArcXrisArray[i]));
			for (Iterator<XDI3Segment> it = arcXris.iterator(); it.hasNext(); ) assertTrue(contextNodesArray[i].getGraph().getDeepRelations(contextNodesArray[i].getXri(), it.next()) != null);
			assertEquals(arcXris.size(), contextNodesArray[i].getRelationCount());
			assertEquals(arcXris.size(), new IteratorCounter(contextNodesArray[i].getRelations()).count());
			for (Iterator<Relation> it = contextNodesArray[i].getRelations(); it.hasNext(); ) assertTrue(arcXris.remove(it.next().getArcXri()));
			assertTrue(arcXris.isEmpty());
		}

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(literalsArray[i]); continue; } else assertNotNull(literalsArray[i]);

			assertEquals(contextNodesArray[i].containsLiteral(), literalsArray[i].booleanValue());
		}

		assertNull(rootContextNode.getArcXri());
		assertEquals(XDIConstants.XRI_S_CONTEXT, rootContextNode.getXri());
		assertEquals(XDI3SubSegment.create("=abc"), abcContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc"), abcContextNode.getXri());
		assertEquals(XDI3SubSegment.create("[+passport]"), abcPassportContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport]"), abcPassportContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!1"), abcPassport1ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport]!1"), abcPassport1ContextNode.getXri());
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertEquals(XDI3SubSegment.create("[$v]"), abcPassportVContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport][$v]"), abcPassportVContextNode.getXri());
		assertEquals(XDI3SubSegment.create("[$o]"), abcPassportCContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport][$o]"), abcPassportCContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!1"), abcPassportV1ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport][$v]!1"), abcPassportV1ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!2"), abcPassportV2ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport][$v]!2"), abcPassportV2ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("$"), abcPassportV2RelationDollar.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport]"), abcPassportV2RelationDollar.getTargetContextNodeXri());
		assertEquals(XDI3SubSegment.create("<+number>"), abcPassport1NumberLiteral.getContextNode().getContextNode().getArcXri());
		assertEquals("987654321", abcPassport1NumberLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("<+country>"), abcPassport1CountryLiteral.getContextNode().getContextNode().getArcXri());
		assertEquals("Canada", abcPassport1CountryLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("<$t>"), abcPassport1LiteralD.getContextNode().getContextNode().getArcXri());
		assertEquals("2005-01-01T00:00:00Z", abcPassport1LiteralD.getLiteralData());
		assertNull(abcPassport2LiteralNumber);	// MANIPULATED
		assertNull(abcPassport2LiteralNumber);	// MANIPULATED
		assertNull(abcPassport2LiteralCountry);	// MANIPULATED
		assertNull(abcPassport2LiteralCountry);	// MANIPULATED
		assertNull(abcPassport2LiteralD);	// MANIPULATED
		assertNull(abcPassport2LiteralD);	// MANIPULATED
		assertEquals(XDI3SubSegment.create("!1"), abcPassportC1ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport][$o]!1"), abcPassportC1ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!2"), abcPassportC2ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport][$o]!2"), abcPassportC2ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("<$t>"), abcPassportC1LiteralD.getContextNode().getContextNode().getArcXri());
		assertEquals("2010-03-03T03:03:03Z", abcPassportC1LiteralD.getLiteralData());	// MANIPULATED
		assertNull(abcPassportC2LiteralD);	// MANIPULATED
		assertNull(abcPassportC2LiteralD);	// MANIPULATED
		assertEquals(XDI3SubSegment.create("[$v]"), abcPassportRelationV.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport][$v]!2"), abcPassportRelationV.getTargetContextNodeXri());
		assertEquals(XDI3SubSegment.create("*1"), abcPassportRelation1.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport]!1"), abcPassportRelation1.getTargetContextNodeXri());	// MANIPULATED
		assertNull(abcPassportRelation2);	// MANIPULATED
		assertNull(abcPassportRelation2);	// MANIPULATED
		assertEquals(XDI3SubSegment.create("+rel"), abcRelation1.getArcXri());
		assertEquals(XDI3Segment.create("=abc[+passport]!1"), abcRelation1.getTargetContextNodeXri());
		assertNull(abcRelation2);	// MANIPULATED
		assertNull(abcRelation2);	// MANIPULATED

		assertEquals(rootContextNode.getAllContextNodeCount(), 17);	// MANIPULATED
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

	private static void assertNotEquals(Object o1, Object o2) throws Exception {

		assertFalse(o1.equals(o2));
	}

	private static void assertNotEquals(int i1, int i2) throws Exception {

		assertFalse(i1 == i2);
	}
}
