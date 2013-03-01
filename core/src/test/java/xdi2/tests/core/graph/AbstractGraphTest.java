package xdi2.tests.core.graph;

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

public abstract class AbstractGraphTest extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(AbstractGraphTest.class);

	protected abstract Graph openNewGraph(String id) throws IOException;
	protected abstract Graph reopenGraph(Graph graph, String id) throws IOException;

	public void testSimple() throws Exception {

		Graph graph0 = this.openNewGraph(this.getClass().getName() + "-graph-0");

		ContextNode markus = graph0.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));
		markus.createLiteral("test");
		markus.createRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=drummond"));

		markus = graph0.getRootContextNode().getContextNode(XDI3SubSegment.create("=markus"));
		assertNotNull(markus);
		assertFalse(markus.isEmpty());
		assertTrue(markus.isLeafContextNode());
		assertTrue(markus.containsRelations());
		assertTrue(markus.containsLiteral());

		ContextNode drummond = graph0.getRootContextNode().getContextNode(XDI3SubSegment.create("=drummond"));
		assertNotNull(drummond);
		assertTrue(drummond.isEmpty());
		assertTrue(drummond.isLeafContextNode());
		
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

		reader.read(graph3, this.getClass().getResourceAsStream("test.json")).close();
		testGraph(graph3);

		graph3.getRootContextNode().clear();
		reader.read(graph3, this.getClass().getResourceAsStream("test-simple.json")).close();

		graph3.close();
	}

	public void testReadWriteFormats() throws Exception {

		String[] writerFormats = new String[] { "XDI/JSON", "XDI DISPLAY" };
		String[] readerFormats = new String[] { "XDI/JSON", "XDI DISPLAY" };

		assertEquals(writerFormats.length, readerFormats.length);

		for (int i=0; i<readerFormats.length; i++) {

			log.info("#" + i + " Write: " + writerFormats[i] + ", Read: " + readerFormats[i]);

			Graph graph4 = this.openNewGraph(this.getClass().getName() + "-graph-4" + "-" + i);
			Graph graph5 = this.openNewGraph(this.getClass().getName() + "-graph-5" + "-" + i);

			XDIWriter writer = XDIWriterRegistry.forFormat(writerFormats[i], null);
			XDIReader reader = XDIReaderRegistry.forFormat(readerFormats[i], null);

			makeGraph(graph4);
			writer.write(graph4, new FileWriter(new File("test." + i + ".out"))).close();
			reader.read(graph5, new FileReader(new File("test." + i + ".out"))).close();

			testGraph(graph5);
			testGraphsEqual(graph4, graph5);

			graph4.close();
			graph5.close();
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

		graph11.getRootContextNode().createContextNode(XDI3SubSegment.create("=xxx"));

		assertNotEquals(graph10, graph11);
		assertNotEquals(graph10.hashCode(), graph11.hashCode());
		assertNotEquals(graph10.compareTo(graph11), 0);

		graph10.close();
		graph11.close();
	}

	public void testFindAndDelete() throws Exception {

		Graph graph12 = this.openNewGraph(this.getClass().getName() + "-graph-12");
		assertEquals(graph12.getRootContextNode(), graph12.findContextNode(XDIConstants.XRI_S_CONTEXT, false));
		assertEquals(graph12.getRootContextNode().getXri(), XDIConstants.XRI_S_CONTEXT);

		graph12.findContextNode(XDI3Segment.create("=markus"), true).createLiteral("Markus");
		graph12.findContextNode(XDI3Segment.create("=markus"), true).createRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=someone"));
		graph12.findContextNode(XDI3Segment.create("=markus+name+last"), true).createLiteral("Sabadello");
		graph12.findContextNode(XDI3Segment.create("=markus+name+relation"), true).createRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=rel+test"));

		assertNotNull(graph12.findContextNode(XDI3Segment.create("=markus"), false));
		assertNotNull(graph12.findLiteral(XDI3Segment.create("=markus")));
		assertNotNull(graph12.findRelation(XDI3Segment.create("=markus"), XDI3Segment.create("+friend")));
		assertNotNull(graph12.findContextNode(XDI3Segment.create("=markus+name"), false));
		assertNotNull(graph12.findContextNode(XDI3Segment.create("=markus+name+last"), false));
		assertNotNull(graph12.findLiteral(XDI3Segment.create("=markus+name+last")));
		assertNotNull(graph12.findRelation(XDI3Segment.create("=markus+name+relation"), XDI3Segment.create("+rel")));

		graph12.findContextNode(XDI3Segment.create("=markus"), false).delete();

		assertNull(graph12.findContextNode(XDI3Segment.create("=markus"), false));
		assertNull(graph12.findLiteral(XDI3Segment.create("=markus")));
		assertNull(graph12.findRelation(XDI3Segment.create("=markus"), XDI3Segment.create("+friend")));
		assertNull(graph12.findContextNode(XDI3Segment.create("=markus+name"), false));
		assertNull(graph12.findContextNode(XDI3Segment.create("=markus+name+last"), false));
		assertNull(graph12.findLiteral(XDI3Segment.create("=markus+name+last")));
		assertNull(graph12.findRelation(XDI3Segment.create("=markus+name+relation"), XDI3Segment.create("+rel")));

		graph12.findContextNode(XDI3Segment.create("=markus"), true);

		assertNotNull(graph12.findContextNode(XDI3Segment.create("=markus"), false));
		assertNull(graph12.findLiteral(XDI3Segment.create("=markus")));
		assertNull(graph12.findRelation(XDI3Segment.create("=markus"), XDI3Segment.create("+friend")));
		assertNull(graph12.findContextNode(XDI3Segment.create("=markus+name"), false));
		assertNull(graph12.findContextNode(XDI3Segment.create("=markus+name+last"), false));
		assertNull(graph12.findLiteral(XDI3Segment.create("=markus+name+last")));
		assertNull(graph12.findRelation(XDI3Segment.create("=markus+name+relation"), XDI3Segment.create("+rel")));

		graph12.close();
	}

	public void testRoot() throws Exception {

		Graph graph13 = this.openNewGraph(this.getClass().getName() + "-graph-13");
		ContextNode root = graph13.getRootContextNode();

		assertEquals(root.getXri(), XDIConstants.XRI_S_ROOT);
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

		root.createRelation(XDI3Segment.create("*arc"), XDI3Segment.create("=target"));
		root.createLiteral("test");

		assertFalse(root.isEmpty());
		assertTrue(root.containsContextNodes());
		assertTrue(root.containsRelations());
		assertTrue(root.containsLiteral());
		assertTrue(root.getContextNodes().hasNext());
		assertTrue(root.getRelations().hasNext());
		assertNotNull(root.getRelations().next().follow());
		assertNotNull(root.getLiteral());
		assertEquals(root.getContextNodeCount(), 1);
		assertEquals(root.getRelationCount(), 1);

		root.createContextNode(XDI3SubSegment.create("+name"));
		root.createContextNode(XDI3SubSegment.create("+email"));

		assertFalse(root.isEmpty());
		assertTrue(root.containsContextNodes());
		assertTrue(root.containsRelations());
		assertTrue(root.containsLiteral());
		assertTrue(root.getContextNodes().hasNext());
		assertTrue(root.getRelations().hasNext());
		assertNotNull(root.getLiteral());
		assertEquals(root.getContextNodeCount(), 3);
		assertEquals(root.getRelationCount(), 1);

		root.getRelations().next().follow().delete();
		root.getContextNodes().next().delete();
		root.getContextNodes().next().delete();
		root.getLiteral().delete();

		assertTrue(root.isEmpty());
		assertFalse(root.containsContextNodes());
		assertFalse(root.containsRelations());
		assertFalse(root.containsLiteral());
		assertFalse(root.getContextNodes().hasNext());
		assertFalse(root.getRelations().hasNext());
		assertNull(root.getLiteral());
		assertEquals(root.getContextNodeCount(), 0);
		assertEquals(root.getRelationCount(), 0);

		graph13.close();
	}

	public void testRelations() throws Exception {

		Graph graph14 = this.openNewGraph(this.getClass().getName() + "-graph-14");
		ContextNode root = graph14.getRootContextNode();
		ContextNode markus = root.createContextNode(XDI3SubSegment.create("=markus"));
		ContextNode target1 = root.createContextNode(XDI3SubSegment.create("=test")).createContextNode(XDI3SubSegment.create("*target1"));
		ContextNode target2 = root.getContextNode(XDI3SubSegment.create("=test")).createContextNode(XDI3SubSegment.create("*target2"));
		ContextNode target3 = root.getContextNode(XDI3SubSegment.create("=test")).createContextNode(XDI3SubSegment.create("*target3"));

		markus.createRelation(XDI3Segment.create("+friend"), target1);
		markus.createRelation(XDI3Segment.create("+friend"), target2);
		markus.createRelation(XDI3Segment.create("+brother"), target3);
		root.createRelation(XDI3Segment.create("+rel"), markus);

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
		assertTrue(markus.containsRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test*target1")));
		assertTrue(markus.containsRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test*target2")));
		assertTrue(markus.containsRelation(XDI3Segment.create("+brother"), XDI3Segment.create("=test*target3")));
		assertEquals(markus.getRelationCount(), 3);
		assertEquals(markus.getRelationCount(XDI3Segment.create("+friend")), 2);
		assertEquals(markus.getRelationCount(XDI3Segment.create("+brother")), 1);
		assertNotNull(markus.getRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test*target1")));
		assertNotNull(markus.getRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test*target2")));
		assertNotNull(markus.getRelation(XDI3Segment.create("+brother"), XDI3Segment.create("=test*target3")));
		assertNotNull(markus.getRelation(XDI3Segment.create("+friend")));
		assertNotNull(markus.getRelation(XDI3Segment.create("+brother")));
		assertEquals(new IteratorCounter(markus.getRelations(XDI3Segment.create("+friend"))).count(), 2);
		assertEquals(new IteratorCounter(markus.getRelations(XDI3Segment.create("+brother"))).count(), 1);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 3);

		root.deleteRelations();
		markus.deleteRelations(XDI3Segment.create("+friend"));
		markus.deleteRelation(XDI3Segment.create("+brother"), XDI3Segment.create("=test*target3"));

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
		assertFalse(markus.containsRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test*target1")));
		assertFalse(markus.containsRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test*target2")));
		assertFalse(markus.containsRelation(XDI3Segment.create("+brother"), XDI3Segment.create("=test*target3")));
		assertEquals(markus.getRelationCount(), 0);
		assertEquals(markus.getRelationCount(XDI3Segment.create("+friend")), 0);
		assertEquals(markus.getRelationCount(XDI3Segment.create("+brother")), 0);
		assertNull(markus.getRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test*target1")));
		assertNull(markus.getRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=test*target2")));
		assertNull(markus.getRelation(XDI3Segment.create("+brother"), XDI3Segment.create("=test*target3")));
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

		ContextNode c = root.createContextNodes(XDI3Segment.create("+a+b+c"));
		ContextNode b = c.getContextNode();
		ContextNode a = b.getContextNode();

		ContextNode e = c.createContextNodes(XDI3Segment.create("+d+e"));
		ContextNode d = e.getContextNode();

		Relation r = c.createRelation(XDI3Segment.create("+x+y"), b);
		Literal l = e.createLiteral("test");

		assertTrue(a.getContextNode().isRootContextNode());
		assertNull(a.getContextNode().getContextNode());

		assertFalse(a.isLeafContextNode());
		assertFalse(b.isLeafContextNode());
		assertFalse(c.isLeafContextNode());
		assertFalse(d.isLeafContextNode());
		assertTrue(e.isLeafContextNode());

		assertEquals(a.getXri(), XDI3Segment.create("+a"));
		assertEquals(b.getXri(), XDI3Segment.create("+a+b"));
		assertEquals(c.getXri(), XDI3Segment.create("+a+b+c"));
		assertEquals(d.getXri(), XDI3Segment.create("+a+b+c+d"));
		assertEquals(e.getXri(), XDI3Segment.create("+a+b+c+d+e"));
		assertEquals(a.getArcXri(), XDI3Segment.create("+a"));
		assertEquals(b.getArcXri(), XDI3Segment.create("+b"));
		assertEquals(c.getArcXri(), XDI3Segment.create("+c"));
		assertEquals(d.getArcXri(), XDI3Segment.create("+d"));
		assertEquals(e.getArcXri(), XDI3Segment.create("+e"));

		assertEquals(graph15.findContextNode(XDI3Segment.create("+a+b+c+d"), false), d);
		assertEquals(a.findContextNode(XDI3Segment.create("+b+c+d"), false), d);
		assertEquals(b.findContextNode(XDI3Segment.create("+c+d"), false), d);
		assertEquals(graph15.findRelation(XDI3Segment.create("+a+b+c"), XDI3Segment.create("+x+y")), r);
		assertEquals(a.findRelation(XDI3Segment.create("+b+c"), XDI3Segment.create("+x+y")), r);
		assertEquals(b.findRelation(XDI3Segment.create("+c"), XDI3Segment.create("+x+y")), r);
		assertEquals(graph15.findLiteral(XDI3Segment.create("+a+b+c+d+e")), l);
		assertEquals(a.findLiteral(XDI3Segment.create("+b+c+d+e")), l);
		assertEquals(b.findLiteral(XDI3Segment.create("+c+d+e")), l);

		graph15.close();
	}

	public void testIncomingRelations() throws Exception {

		Graph graph16 = this.openNewGraph(this.getClass().getName() + "-graph-16");

		graph16.createStatement(XDI3Statement.create("=markus/+friend/=animesh"));
		graph16.createStatement(XDI3Statement.create("=markus/+friend/=neustar*les"));
		graph16.createStatement(XDI3Statement.create("=!1111*2222/$is/=markus"));

		ContextNode markus = graph16.findContextNode(XDI3Segment.create("=markus"), false);
		ContextNode animesh = graph16.findContextNode(XDI3Segment.create("=animesh"), false);
		ContextNode les = graph16.findContextNode(XDI3Segment.create("=neustar*les"), false);
		ContextNode inumber = graph16.findContextNode(XDI3Segment.create("=!1111*2222"), false);

		assertEquals(graph16.getRootContextNode().getAllRelationCount(), 3);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 2);
		assertEquals(new IteratorCounter(animesh.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(les.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(inumber.getRelations()).count(), 1);
		assertEquals(new IteratorCounter(markus.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(animesh.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(les.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(inumber.getIncomingRelations()).count(), 0);

		Relation friend1 = markus.getRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
		Relation friend2 = markus.getRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=neustar*les"));
		Relation is = graph16.findRelation(XDI3Segment.create("=!1111*2222"), XDI3Segment.create("$is"));

		assertTrue(new IteratorContains<Relation> (graph16.findRelations(XDI3Segment.create("=markus"), XDI3Segment.create("+friend")), friend1).contains());
		assertTrue(new IteratorContains<Relation> (graph16.findRelations(XDI3Segment.create("=markus"), XDI3Segment.create("+friend")), friend2).contains());

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
		assertEquals(new IteratorCounter(les.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(inumber.getIncomingRelations()).count(), 0);

		ContextNode neustar = les.getContextNode();
		neustar.deleteContextNodes();

		assertEquals(graph16.getRootContextNode().getAllRelationCount(), 1);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(inumber.getRelations()).count(), 1);
		assertEquals(new IteratorCounter(markus.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(inumber.getIncomingRelations()).count(), 0);

		graph16.getRootContextNode().deleteContextNode(XDI3SubSegment.create("=markus"));

		assertEquals(graph16.getRootContextNode().getAllRelationCount(), 0);
		assertEquals(new IteratorCounter(inumber.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(inumber.getIncomingRelations()).count(), 0);

		assertEquals(graph16.getRootContextNode().getAllContextNodeCount(), 3);
		assertEquals(graph16.getRootContextNode().getAllRelationCount(), 0);
		assertEquals(graph16.getRootContextNode().getAllLiteralCount(), 0);
		assertEquals(graph16.getRootContextNode().getAllStatementCount(), 3);

		graph16.close();
	}

	public void testExceptions() throws Exception {

		Graph graph17 = this.openNewGraph(this.getClass().getName() + "-graph-17");

		graph17.createStatement(XDI3Statement.create("=markus/!/(data:,Markus%20Sabadello)"));
		graph17.createStatement(XDI3Statement.create("=markus/+friend/=neustar*les"));

		ContextNode root = graph17.getRootContextNode();
		ContextNode markus = graph17.findContextNode(XDI3Segment.create("=markus"), false);
		ContextNode les = graph17.findContextNode(XDI3Segment.create("=neustar*les"), false);

		try { root.createContextNode(XDI3SubSegment.create("=markus")); fail(); } catch (Xdi2GraphException ex) { }
		try { markus.createLiteral("test"); fail(); } catch (Xdi2GraphException ex) { }
		try { markus.createRelation(XDI3Segment.create("+friend"), les); fail(); } catch (Xdi2GraphException ex) { }

		graph17.close();
	}

	public void testTransactions() throws Exception {

		Graph graph18 = this.openNewGraph(this.getClass().getName() + "-graph-18");

		graph18.createStatement(XDI3Statement.create("=markus/!/(data:,Markus%20Sabadello)"));
		graph18.createStatement(XDI3Statement.create("=markus/+friend/=neustar*les"));

		ContextNode markus = graph18.findContextNode(XDI3Segment.create("=markus"), false);

		graph18.beginTransaction();

		markus.deleteLiteral();
		markus.deleteRelations(XDI3Segment.create("+friend"));
		markus.createRelation(XDI3Segment.create("$is+"), XDI3Segment.create("+person"));

		assertFalse(markus.containsLiteral());
		assertFalse(markus.containsRelations(XDI3Segment.create("+friend")));
		assertTrue(markus.containsRelation(XDI3Segment.create("$is+"), XDI3Segment.create("+person")));

		graph18.rollbackTransaction();

		if (graph18.supportsTransactions()) {

			assertTrue(markus.containsLiteral());
			assertTrue(markus.containsRelations(XDI3Segment.create("+friend")));
			assertFalse(markus.containsRelation(XDI3Segment.create("$is+"), XDI3Segment.create("+person")));
		} else {

			assertFalse(markus.containsLiteral());
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

		ContextNode markus = graph19.findContextNode(XDI3Segment.create("=markus"), true);

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
		assertNull(markus.findLiteral(XDI3Segment.create("!not")));
		assertEquals(markus.getAllLiteralCount(), 0);

		assertEquals(markus.getAllStatementCount(), 0);

		graph19.close();
	}

	public void testIllegalArcXris() throws Exception {

		Graph graph20 = this.openNewGraph(this.getClass().getName() + "-graph-20");

		ContextNode markus = graph20.findContextNode(XDI3Segment.create("=markus"), true);

		try {

			markus.createContextNodes(XDI3Segment.create("()"));
			fail();
		} catch (Xdi2GraphException ex){

		}

		try {

			markus.createRelation(XDI3Segment.create("()"), XDI3Segment.create("=animesh"));
			fail();
		} catch (Xdi2GraphException ex){

		}

		try {

			markus.createRelation(XDI3Segment.create("!"), XDI3Segment.create("=animesh"));
			fail();
		} catch (Xdi2GraphException ex){

		}

		graph20.close();
	}

	public void testImplied() throws Exception {

		Graph graph21 = this.openNewGraph(this.getClass().getName() + "-graph-21");

		ContextNode webmarkus = graph21.findContextNode(XDI3Segment.create("=web*markus"), true);
		ContextNode animesh = graph21.findContextNode(XDI3Segment.create("=animesh"), true);
		Relation friend = webmarkus.createRelation(XDI3Segment.create("+friend"), animesh);
		Literal name = webmarkus.createLiteral("Markus Sabadello");
		ContextNode web = webmarkus.getContextNode();

		assertTrue(webmarkus.getStatement().isImplied());
		assertTrue(animesh.getStatement().isImplied());
		assertFalse(friend.getStatement().isImplied());
		assertFalse(name.getStatement().isImplied());
		assertTrue(web.getStatement().isImplied());

		graph21.close();
	}

	public void testStatements() throws Exception {

		Graph graph22 = this.openNewGraph(this.getClass().getName() + "-graph-22");
		Graph graph23 = this.openNewGraph(this.getClass().getName() + "-graph-23");

		ContextNodeStatement statement22_1 = (ContextNodeStatement) graph22.createStatement(XDI3Statement.create("=neustar/()/*les"));
		RelationStatement statement22_2 = (RelationStatement) graph22.createStatement(XDI3Statement.create("=markus/+friend/=neustar*les"));
		LiteralStatement statement22_3 = (LiteralStatement) graph22.createStatement(XDI3Statement.create("=markus$!(+email)/!/(data:,markus.sabadello@gmail.com)"));

		assertNotNull(graph22.findContextNode(XDI3Segment.create("=markus"), false));
		assertNotNull(graph22.findContextNode(XDI3Segment.create("=neustar"), false));
		assertNotNull(graph22.findContextNode(XDI3Segment.create("=neustar*les"), false));
		assertNotNull(graph22.findRelation(XDI3Segment.create("=markus"), XDI3Segment.create("+friend")));
		assertNotNull(graph22.findRelation(XDI3Segment.create("=markus"), XDI3Segment.create("+friend"), XDI3Segment.create("=neustar*les")));
		assertNotNull(graph22.findLiteral(XDI3Segment.create("=markus$!(+email)")));
		assertNotNull(graph22.findLiteral(XDI3Segment.create("=markus$!(+email)"), "markus.sabadello@gmail.com"));

		assertTrue(graph22.containsStatement(XDI3Statement.create("=neustar/()/*les")));
		assertTrue(graph22.containsStatement(XDI3Statement.create("=markus/+friend/=neustar*les")));
		assertTrue(graph22.containsStatement(XDI3Statement.create("=markus$!(+email)/!/(data:,markus.sabadello@gmail.com)")));
		assertEquals(graph22.findStatement(XDI3Statement.create("=neustar/()/*les")).getXri(), XDI3Statement.create("=neustar/()/*les"));
		assertEquals(graph22.findStatement(XDI3Statement.create("=markus/+friend/=neustar*les")).getXri(), "=markus/+friend/=neustar*les");
		assertEquals(graph22.findStatement(XDI3Statement.create("=markus$!(+email)/!/(data:,markus.sabadello@gmail.com)")).getXri(), "=markus$!(+email)/!/(data:,markus.sabadello@gmail.com)");

		assertTrue(graph22.findStatement(XDI3Statement.create("=neustar/()/*les")) instanceof ContextNodeStatement);
		assertTrue(graph22.findStatement(XDI3Statement.create("=markus/+friend/=neustar*les")) instanceof RelationStatement);
		assertTrue(graph22.findStatement(XDI3Statement.create("=markus$!(+email)/!/(data:,markus.sabadello@gmail.com)")) instanceof LiteralStatement);
		assertTrue(graph22.findStatement(XDI3Statement.create("=neustar/()/*les")).getXri().isContextNodeStatement());
		assertTrue(graph22.findStatement(XDI3Statement.create("=markus/+friend/=neustar*les")).getXri().isRelationStatement());
		assertTrue(graph22.findStatement(XDI3Statement.create("=markus$!(+email)/!/(data:,markus.sabadello@gmail.com)")).getXri().isLiteralStatement());

		CopyUtil.copyStatement(statement22_1, graph23, null);
		CopyUtil.copyStatement(statement22_2, graph23, null);
		CopyUtil.copyStatement(statement22_3, graph23, null);

		assertNotNull(graph23.findContextNode(XDI3Segment.create("=markus"), false));
		assertNotNull(graph23.findContextNode(XDI3Segment.create("=neustar"), false));
		assertNotNull(graph23.findContextNode(XDI3Segment.create("=neustar*les"), false));
		assertNotNull(graph23.findRelation(XDI3Segment.create("=markus"), XDI3Segment.create("+friend")));
		assertNotNull(graph23.findRelation(XDI3Segment.create("=markus"), XDI3Segment.create("+friend"), XDI3Segment.create("=neustar*les")));
		assertNotNull(graph23.findLiteral(XDI3Segment.create("=markus$!(+email)")));
		assertNotNull(graph23.findLiteral(XDI3Segment.create("=markus$!(+email)"), "markus.sabadello@gmail.com"));

		assertTrue(graph23.containsStatement(XDI3Statement.create("=neustar/()/*les")));
		assertTrue(graph23.containsStatement(XDI3Statement.create("=markus/+friend/=neustar*les")));
		assertTrue(graph23.containsStatement(XDI3Statement.create("=markus$!(+email)/!/(data:,markus.sabadello@gmail.com)")));
		assertEquals(graph23.findStatement(XDI3Statement.create("=neustar/()/*les")).getXri(), XDI3Statement.create("=neustar/()/*les"));
		assertEquals(graph23.findStatement(XDI3Statement.create("=markus/+friend/=neustar*les")).getXri(), "=markus/+friend/=neustar*les");
		assertEquals(graph23.findStatement(XDI3Statement.create("=markus$!(+email)/!/(data:,markus.sabadello@gmail.com)")).getXri(), "=markus$!(+email)/!/(data:,markus.sabadello@gmail.com)");

		assertTrue(graph23.findStatement(XDI3Statement.create("=neustar/()/*les")) instanceof ContextNodeStatement);
		assertTrue(graph23.findStatement(XDI3Statement.create("=markus/+friend/=neustar*les")) instanceof RelationStatement);
		assertTrue(graph23.findStatement(XDI3Statement.create("=markus$!(+email)/!/(data:,markus.sabadello@gmail.com)")) instanceof LiteralStatement);
		assertTrue(graph23.findStatement(XDI3Statement.create("=neustar/()/*les")).getXri().isContextNodeStatement());
		assertTrue(graph23.findStatement(XDI3Statement.create("=markus/+friend/=neustar*les")).getXri().isRelationStatement());
		assertTrue(graph23.findStatement(XDI3Statement.create("=markus$!(+email)/!/(data:,markus.sabadello@gmail.com)")).getXri().isLiteralStatement());

		graph22.close();
		graph23.close();
	}

	@SuppressWarnings("unused")
	private static void makeGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.createContextNode(XDI3SubSegment.create("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.createContextNode(XDI3SubSegment.create("+passport"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.createContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.createContextNode(XDI3SubSegment.create("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.createContextNode(XDI3SubSegment.create("$v"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.createContextNode(XDI3SubSegment.create("($)"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.createContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.createContextNode(XDI3SubSegment.create("!2"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.createRelation(XDI3Segment.create("$"), XDI3Segment.create("=abc+passport"));
		Literal abcPassport1NumberLiteral = abcPassport1ContextNode.createContextNode(XDI3SubSegment.create("+number")).createLiteral("987654321");
		Literal abcPassport1CountryLiteral = abcPassport1ContextNode.createContextNode(XDI3SubSegment.create("+country")).createLiteral("Canada");
		Literal abcPassport1DLiteral = abcPassport1ContextNode.createContextNode(XDI3SubSegment.create("$d")).createLiteral("2005-01-01T00:00:00Z");
		Literal abcPassport2NumberLiteral = abcPassport2ContextNode.createContextNode(XDI3SubSegment.create("+number")).createLiteral("123456789");
		Literal abcPassport2CountryLiteral = abcPassport2ContextNode.createContextNode(XDI3SubSegment.create("+country")).createLiteral("New Zealand");
		Literal abcPassport2DLiteral = abcPassport2ContextNode.createContextNode(XDI3SubSegment.create("$d")).createLiteral("2010-10-01T00:00:00Z");
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.createContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.createContextNode(XDI3SubSegment.create("!2"));
		Literal abcPassportC1DLiteral = abcPassportC1ContextNode.createContextNode(XDI3SubSegment.create("$d")).createLiteral("2010-11-11T11:11:11Z");
		Literal abcPassportC2DLiteral = abcPassportC2ContextNode.createContextNode(XDI3SubSegment.create("$d")).createLiteral("2010-12-22T22:22:22Z");
		Relation abcPassportRelationV = abcPassportContextNode.createRelation(XDI3Segment.create("$v"), XDI3Segment.create("=abc+passport$v!2"));
		Relation abcPassportRelation1 = abcPassportContextNode.createRelation(XDI3Segment.create("*1"), abcPassport1ContextNode);
		Relation abcPassportRelation2 = abcPassportContextNode.createRelation(XDI3Segment.create("*2"), abcPassport2ContextNode);
		Relation abcTestRelation1 = abcContextNode.createRelation(XDI3Segment.create("+rel"), abcPassport1ContextNode);
		Relation abcTestRelation2 = abcContextNode.createRelation(XDI3Segment.create("+rel"), abcPassport2ContextNode);
	}

	private static void testGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.getContextNode(XDI3SubSegment.create("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.getContextNode(XDI3SubSegment.create("+passport"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("$v"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("($)"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.getContextNode(XDI3SubSegment.create("!2"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.getRelation(XDI3Segment.create("$"));
		Literal abcPassport1NumberLiteral = abcPassport1ContextNode.findLiteral(XDI3Segment.create("+number"));
		Literal abcPassport1CountryLiteral = abcPassport1ContextNode.findLiteral(XDI3Segment.create("+country"));
		Literal abcPassport1DLiteral = abcPassport1ContextNode.findLiteral(XDI3Segment.create("$d"));
		Literal abcPassport2NumberLiteral = abcPassport2ContextNode.findLiteral(XDI3Segment.create("+number"));
		Literal abcPassport2CountryLiteral = abcPassport2ContextNode.findLiteral(XDI3Segment.create("+country"));
		Literal abcPassport2DLiteral = abcPassport2ContextNode.findLiteral(XDI3Segment.create("$d"));
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.getContextNode(XDI3SubSegment.create("!2"));
		Literal abcPassportC1DLiteral = abcPassportC1ContextNode.findLiteral(XDI3Segment.create("$d"));
		Literal abcPassportC2DLiteral = abcPassportC2ContextNode.findLiteral(XDI3Segment.create("$d"));
		Relation abcPassportRelationV = abcPassportContextNode.getRelation(XDI3Segment.create("$v"));
		Relation abcPassportRelation1 = abcPassportContextNode.getRelation(XDI3Segment.create("*1"));
		Relation abcPassportRelation2 = abcPassportContextNode.getRelation(XDI3Segment.create("*2"));
		Relation abcRelation1 = abcContextNode.getRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc+passport!1"));
		Relation abcRelation2 = abcContextNode.getRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc+passport!2"));

		assertEquals(rootContextNode.getXri(), XDIConstants.XRI_S_ROOT);
		assertEquals(abcContextNode.getXri(), XDI3Segment.create("=abc"));
		assertEquals(abcPassportContextNode.getXri(), XDI3Segment.create("=abc+passport"));
		assertEquals(abcPassportVContextNode.getXri(), XDI3Segment.create("=abc+passport$v"));

		assertTrue(rootContextNode.containsContextNode(XDI3SubSegment.create("=abc")));
		assertTrue(abcContextNode.containsContextNode(XDI3SubSegment.create("+passport")));
		assertTrue(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("!1")));
		assertTrue(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("!2")));
		assertTrue(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("$v")));
		assertTrue(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("($)")));
		assertTrue(abcPassportVContextNode.containsContextNode(XDI3SubSegment.create("!1")));
		assertTrue(abcPassportVContextNode.containsContextNode(XDI3SubSegment.create("!2")));
		assertTrue(abcPassportV2ContextNode.containsRelation(XDI3Segment.create("$"), XDI3Segment.create("=abc+passport")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDI3SubSegment.create("+number")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDI3SubSegment.create("+country")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDI3SubSegment.create("$d")));
		assertTrue(abcPassport2ContextNode.containsContextNode(XDI3SubSegment.create("+number")));
		assertTrue(abcPassport2ContextNode.containsContextNode(XDI3SubSegment.create("+country")));
		assertTrue(abcPassport2ContextNode.containsContextNode(XDI3SubSegment.create("$d")));
		assertTrue(abcPassport1ContextNode.getContextNode(XDI3SubSegment.create("+number")).containsLiteral());
		assertTrue(abcPassport1ContextNode.getContextNode(XDI3SubSegment.create("+country")).containsLiteral());
		assertTrue(abcPassport1ContextNode.getContextNode(XDI3SubSegment.create("$d")).containsLiteral());
		assertTrue(abcPassport2ContextNode.getContextNode(XDI3SubSegment.create("+number")).containsLiteral());
		assertTrue(abcPassport2ContextNode.getContextNode(XDI3SubSegment.create("+country")).containsLiteral());
		assertTrue(abcPassport2ContextNode.getContextNode(XDI3SubSegment.create("$d")).containsLiteral());
		assertTrue(abcPassportCContextNode.containsContextNode(XDI3SubSegment.create("!1")));
		assertTrue(abcPassportCContextNode.containsContextNode(XDI3SubSegment.create("!2")));
		assertTrue(abcPassportC1ContextNode.containsContextNode(XDI3SubSegment.create("$d")));
		assertTrue(abcPassportC2ContextNode.containsContextNode(XDI3SubSegment.create("$d")));
		assertTrue(abcPassportC1ContextNode.getContextNode(XDI3SubSegment.create("$d")).containsLiteral());
		assertTrue(abcPassportC2ContextNode.getContextNode(XDI3SubSegment.create("$d")).containsLiteral());
		assertTrue(abcPassportContextNode.containsRelation(XDI3Segment.create("$v"), XDI3Segment.create("=abc+passport$v!2")));
		assertTrue(abcPassportContextNode.containsRelation(XDI3Segment.create("*1"), XDI3Segment.create("=abc+passport!1")));
		assertTrue(abcPassportContextNode.containsRelation(XDI3Segment.create("*2"), XDI3Segment.create("=abc+passport!2")));
		assertTrue(abcContextNode.containsRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc+passport!1")));
		assertTrue(abcContextNode.containsRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc+passport!2")));

		ContextNode contextNodesArray[] = new ContextNode [] {
				graph.findContextNode(XDI3Segment.create("()"), false),
				graph.findContextNode(XDI3Segment.create("=abc"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!1"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!2"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport$v"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport($)"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport$v!1"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport$v!2"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport($)!1"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport($)!2"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!1+number"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!1+country"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!1$d"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!2+number"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!2+country"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!2$d"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport($)!1$d"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport($)!2$d"), false)
		};

		XDI3SubSegment[][] contextNodeArcXrisArray = new XDI3SubSegment[][] {
				new XDI3SubSegment[] { XDI3SubSegment.create("=abc") },
				new XDI3SubSegment[] { XDI3SubSegment.create("+passport") },
				new XDI3SubSegment[] { XDI3SubSegment.create("!1"), XDI3SubSegment.create("!2"), XDI3SubSegment.create("$v"), XDI3SubSegment.create("($)") },
				new XDI3SubSegment[] { XDI3SubSegment.create("+number"), XDI3SubSegment.create("+country"), XDI3SubSegment.create("$d") },
				new XDI3SubSegment[] { XDI3SubSegment.create("+number"), XDI3SubSegment.create("+country"), XDI3SubSegment.create("$d") },
				new XDI3SubSegment[] { XDI3SubSegment.create("!1"), XDI3SubSegment.create("!2") },
				new XDI3SubSegment[] { XDI3SubSegment.create("!1"), XDI3SubSegment.create("!2") },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { XDI3SubSegment.create("$d") },
				new XDI3SubSegment[] { XDI3SubSegment.create("$d") },
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
				new XDI3Segment[] { XDI3Segment.create("$v"), XDI3Segment.create("*1"), XDI3Segment.create("*2") },
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

		assertEquals(contextNodesArray.length, contextNodeArcXrisArray.length);
		assertEquals(contextNodesArray.length, relationArcXrisArray.length);
		assertEquals(contextNodesArray.length, literalsArray.length);

		for (ContextNode contextNode : contextNodesArray) if (contextNode != null) assertTrue(contextNode.getGraph().containsContextNode(contextNode.getXri()));

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(contextNodeArcXrisArray[i]); continue; } else assertNotNull(contextNodeArcXrisArray[i]);

			Set<XDI3SubSegment> arcXris = new HashSet<XDI3SubSegment> (Arrays.asList(contextNodeArcXrisArray[i]));
			assertEquals(arcXris.size(), contextNodesArray[i].getContextNodeCount());
			assertEquals(arcXris.size(), new IteratorCounter(contextNodesArray[i].getContextNodes()).count());
			for (Iterator<ContextNode> it = contextNodesArray[i].getContextNodes(); it.hasNext(); ) assertTrue(arcXris.remove(it.next().getArcXri()));
			assertTrue(arcXris.isEmpty());
		}

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(relationArcXrisArray[i]); continue; } else assertNotNull(relationArcXrisArray[i]);

			List<XDI3Segment> arcXris = new ArrayList<XDI3Segment> (Arrays.asList(relationArcXrisArray[i]));
			for (Iterator<XDI3Segment> it = arcXris.iterator(); it.hasNext(); ) assertTrue(contextNodesArray[i].getGraph().containsRelations(contextNodesArray[i].getXri(), it.next()));
			assertEquals(arcXris.size(), contextNodesArray[i].getRelationCount());
			assertEquals(arcXris.size(), new IteratorCounter(contextNodesArray[i].getRelations()).count());
			for (Iterator<Relation> it = contextNodesArray[i].getRelations(); it.hasNext(); ) assertTrue(arcXris.remove(it.next().getArcXri()));
			assertTrue(arcXris.isEmpty());
		}

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(literalsArray[i]); continue; } else assertNotNull(literalsArray[i]);

			assertEquals(contextNodesArray[i].getGraph().containsLiteral(contextNodesArray[i].getXri()), literalsArray[i].booleanValue());

			assertEquals(contextNodesArray[i].containsLiteral(), literalsArray[i].booleanValue());
		}

		assertNull(rootContextNode.getArcXri());
		assertEquals(XDIConstants.XRI_S_CONTEXT, rootContextNode.getXri());
		assertEquals(XDI3SubSegment.create("=abc"), abcContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc"), abcContextNode.getXri());
		assertEquals(XDI3SubSegment.create("+passport"), abcPassportContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport"), abcPassportContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!1"), abcPassport1ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport!1"), abcPassport1ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!2"), abcPassport2ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport!2"), abcPassport2ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("$v"), abcPassportVContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport$v"), abcPassportVContextNode.getXri());
		assertEquals(XDI3SubSegment.create("($)"), abcPassportCContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport($)"), abcPassportCContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!1"), abcPassportV1ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport$v!1"), abcPassportV1ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!2"), abcPassportV2ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport$v!2"), abcPassportV2ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("$"), abcPassportV2RelationDollar.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport"), abcPassportV2RelationDollar.getTargetContextNodeXri());
		assertEquals(XDI3SubSegment.create("+number"), abcPassport1NumberLiteral.getContextNode().getArcXri());
		assertEquals("987654321", abcPassport1NumberLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("+country"), abcPassport1CountryLiteral.getContextNode().getArcXri());
		assertEquals("Canada", abcPassport1CountryLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("$d"), abcPassport1DLiteral.getContextNode().getArcXri());
		assertEquals("2005-01-01T00:00:00Z", abcPassport1DLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("+number"), abcPassport2NumberLiteral.getContextNode().getArcXri());
		assertEquals("123456789", abcPassport2NumberLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("+country"), abcPassport2CountryLiteral.getContextNode().getArcXri());
		assertEquals("New Zealand", abcPassport2CountryLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("$d"), abcPassport2DLiteral.getContextNode().getArcXri());
		assertEquals("2010-10-01T00:00:00Z", abcPassport2DLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("!1"), abcPassportC1ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport($)!1"), abcPassportC1ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!2"), abcPassportC2ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport($)!2"), abcPassportC2ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("$d"), abcPassportC1DLiteral.getContextNode().getArcXri());
		assertEquals("2010-11-11T11:11:11Z", abcPassportC1DLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("$d"), abcPassportC2DLiteral.getContextNode().getArcXri());
		assertEquals("2010-12-22T22:22:22Z", abcPassportC2DLiteral.getLiteralData());
		assertEquals(XDI3SubSegment.create("$v"), abcPassportRelationV.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport$v!2"), abcPassportRelationV.getTargetContextNodeXri());
		assertEquals(XDI3SubSegment.create("*1"), abcPassportRelation1.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport!1"), abcPassportRelation1.getTargetContextNodeXri());
		assertEquals(XDI3SubSegment.create("*2"), abcPassportRelation2.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport!2"), abcPassportRelation2.getTargetContextNodeXri());
		assertEquals(XDI3SubSegment.create("+rel"), abcRelation1.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport!1"), abcRelation1.getTargetContextNodeXri());
		assertEquals(XDI3SubSegment.create("+rel"), abcRelation2.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport!2"), abcRelation2.getTargetContextNodeXri());

		assertEquals(rootContextNode.getAllContextNodeCount(), 18);
		assertEquals(rootContextNode.getAllRelationCount(), 6);
		assertEquals(rootContextNode.getAllLiteralCount(), 8);
	}

	@SuppressWarnings("unused")
	private static void manipulateGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.getContextNode(XDI3SubSegment.create("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.getContextNode(XDI3SubSegment.create("+passport"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("$v"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("($)"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.getContextNode(XDI3SubSegment.create("!2"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.getRelation(XDI3Segment.create("$"));
		Literal abcPassport1LiteralNumber = abcPassport1ContextNode.findLiteral(XDI3Segment.create("+number"));
		Literal abcPassport1LiteralCountry = abcPassport1ContextNode.findLiteral(XDI3Segment.create("+country"));
		Literal abcPassport1LiteralD = abcPassport1ContextNode.findLiteral(XDI3Segment.create("$d"));
		Literal abcPassport2LiteralNumber = abcPassport2ContextNode.findLiteral(XDI3Segment.create("+number"));
		Literal abcPassport2LiteralCountry = abcPassport2ContextNode.findLiteral(XDI3Segment.create("+country"));
		Literal abcPassport2LiteralD = abcPassport2ContextNode.findLiteral(XDI3Segment.create("$d"));
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.getContextNode(XDI3SubSegment.create("!2"));
		Literal abcPassportC1LiteralD = abcPassportC1ContextNode.findLiteral(XDI3Segment.create("$d"));
		Literal abcPassportC2LiteralD = abcPassportC2ContextNode.findLiteral(XDI3Segment.create("$d"));
		Relation abcPassportRelationV = abcPassportContextNode.getRelation(XDI3Segment.create("$v"));
		Relation abcPassportRelation1 = abcPassportContextNode.getRelation(XDI3Segment.create("*1"));
		Relation abcPassportRelation2 = abcPassportContextNode.getRelation(XDI3Segment.create("*2"));
		Relation abcRelation1 = abcContextNode.getRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc+passport!1"));
		Relation abcRelation2 = abcContextNode.getRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc+passport!2"));

		abcPassport2ContextNode.delete();
		abcPassportC1LiteralD.setLiteralData("2010-03-03T03:03:03Z");
		abcPassportC2LiteralD.getContextNode().delete();
	}

	private static void testManipulatedGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.getContextNode(XDI3SubSegment.create("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.getContextNode(XDI3SubSegment.create("+passport"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("$v"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(XDI3SubSegment.create("($)"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.getContextNode(XDI3SubSegment.create("!2"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.getRelation(XDI3Segment.create("$"));
		Literal abcPassport1LiteralNumber = abcPassport1ContextNode.findLiteral(XDI3Segment.create("+number"));
		Literal abcPassport1LiteralCountry = abcPassport1ContextNode.findLiteral(XDI3Segment.create("+country"));
		Literal abcPassport1LiteralD = abcPassport1ContextNode.findLiteral(XDI3Segment.create("$d"));
		Literal abcPassport2LiteralNumber = null;
		Literal abcPassport2LiteralCountry = null;
		Literal abcPassport2LiteralD = null;
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.getContextNode(XDI3SubSegment.create("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.getContextNode(XDI3SubSegment.create("!2"));
		Literal abcPassportC1LiteralD = abcPassportC1ContextNode.findLiteral(XDI3Segment.create("$d"));
		Literal abcPassportC2LiteralD = abcPassportC2ContextNode.findLiteral(XDI3Segment.create("$d"));
		Relation abcPassportRelationV = abcPassportContextNode.getRelation(XDI3Segment.create("$v"));
		Relation abcPassportRelation1 = abcPassportContextNode.getRelation(XDI3Segment.create("*1"));
		Relation abcPassportRelation2 = abcPassportContextNode.getRelation(XDI3Segment.create("*2"));
		Relation abcRelation1 = abcContextNode.getRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc+passport!1"));
		Relation abcRelation2 = abcContextNode.getRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc+passport!2"));

		assertTrue(rootContextNode.containsContextNode(XDI3SubSegment.create("=abc")));
		assertTrue(abcContextNode.containsContextNode(XDI3SubSegment.create("+passport")));
		assertTrue(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("!1")));
		assertFalse(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("!2")));	// MANIPULATED
		assertTrue(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("$v")));
		assertTrue(abcPassportContextNode.containsContextNode(XDI3SubSegment.create("($)")));
		assertTrue(abcPassportVContextNode.containsContextNode(XDI3SubSegment.create("!1")));
		assertTrue(abcPassportVContextNode.containsContextNode(XDI3SubSegment.create("!2")));
		assertTrue(abcPassportV2ContextNode.containsRelation(XDI3Segment.create("$"), XDI3Segment.create("=abc+passport")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDI3SubSegment.create("+number")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDI3SubSegment.create("+country")));
		assertTrue(abcPassport1ContextNode.containsContextNode(XDI3SubSegment.create("$d")));
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertTrue(abcPassport1ContextNode.getContextNode(XDI3SubSegment.create("+number")).containsLiteral());
		assertTrue(abcPassport1ContextNode.getContextNode(XDI3SubSegment.create("+country")).containsLiteral());
		assertTrue(abcPassport1ContextNode.getContextNode(XDI3SubSegment.create("$d")).containsLiteral());
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertTrue(abcPassportCContextNode.containsContextNode(XDI3SubSegment.create("!1")));
		assertTrue(abcPassportCContextNode.containsContextNode(XDI3SubSegment.create("!2")));
		assertTrue(abcPassportC1ContextNode.containsContextNode(XDI3SubSegment.create("$d")));
		assertFalse(abcPassportC2ContextNode.containsContextNode(XDI3SubSegment.create("$d")));	// MANIPULATED
		assertTrue(abcPassportC1ContextNode.getContextNode(XDI3SubSegment.create("$d")).containsLiteral());
		assertNull(abcPassportC2LiteralD);	// MANIPULATED
		assertTrue(abcPassportContextNode.containsRelation(XDI3Segment.create("$v"), XDI3Segment.create("=abc+passport$v!2")));
		assertTrue(abcPassportContextNode.containsRelation(XDI3Segment.create("*1"), XDI3Segment.create("=abc+passport!1")));
		assertFalse(abcPassportContextNode.containsRelation(XDI3Segment.create("*2"), XDI3Segment.create("=abc+passport!2")));		// MANIPULATED
		assertTrue(abcContextNode.containsRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc+passport!1")));
		assertFalse(abcContextNode.containsRelation(XDI3Segment.create("+rel"), XDI3Segment.create("=abc+passport!2")));		// MANIPULATED

		ContextNode contextNodesArray[] = new ContextNode [] {
				graph.findContextNode(XDI3Segment.create("()"), false),
				graph.findContextNode(XDI3Segment.create("=abc"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!1"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!2"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport$v"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport($)"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport$v!1"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport$v!2"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport($)!1"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport($)!2"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!1+number"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!1+country"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!1$d"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!2+number"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!2+country"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport!2$d"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport($)!1$d"), false),
				graph.findContextNode(XDI3Segment.create("=abc+passport($)!2$d"), false)
		};

		XDI3SubSegment[][] contextNodeArcXrisArray = new XDI3SubSegment[][] {
				new XDI3SubSegment[] { XDI3SubSegment.create("=abc") },
				new XDI3SubSegment[] { XDI3SubSegment.create("+passport") },
				new XDI3SubSegment[] { XDI3SubSegment.create("!1"), XDI3SubSegment.create("$v"), XDI3SubSegment.create("($)") },	// MANIPULATED
				new XDI3SubSegment[] { XDI3SubSegment.create("+number"), XDI3SubSegment.create("+country"), XDI3SubSegment.create("$d") },
				null,	// MANIPULATED
				new XDI3SubSegment[] { XDI3SubSegment.create("!1"), XDI3SubSegment.create("!2") },
				new XDI3SubSegment[] { XDI3SubSegment.create("!1"), XDI3SubSegment.create("!2") },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { XDI3SubSegment.create("$d") },
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
				new XDI3Segment[] { XDI3Segment.create("$v"), XDI3Segment.create("*1") },	// MANIPULATED
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

		assertEquals(contextNodesArray.length, contextNodeArcXrisArray.length);
		assertEquals(contextNodesArray.length, relationArcXrisArray.length);
		assertEquals(contextNodesArray.length, literalsArray.length);

		for (ContextNode contextNode : contextNodesArray) if (contextNode != null) assertTrue(contextNode.getGraph().containsContextNode(contextNode.getXri()));

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(contextNodeArcXrisArray[i]); continue; } else assertNotNull(contextNodeArcXrisArray[i]);

			Set<XDI3SubSegment> arcXris = new HashSet<XDI3SubSegment> (Arrays.asList(contextNodeArcXrisArray[i]));
			assertEquals(arcXris.size(), contextNodesArray[i].getContextNodeCount());
			assertEquals(arcXris.size(), new IteratorCounter(contextNodesArray[i].getContextNodes()).count());
			for (Iterator<ContextNode> it = contextNodesArray[i].getContextNodes(); it.hasNext(); ) assertTrue(arcXris.remove(it.next().getArcXri()));
			assertTrue(arcXris.isEmpty());
		}

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(relationArcXrisArray[i]); continue; } else assertNotNull(relationArcXrisArray[i]);

			List<XDI3Segment> arcXris = new ArrayList<XDI3Segment> (Arrays.asList(relationArcXrisArray[i]));
			for (Iterator<XDI3Segment> it = arcXris.iterator(); it.hasNext(); ) assertTrue(contextNodesArray[i].getGraph().containsRelations(contextNodesArray[i].getXri(), it.next()));
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
		assertEquals(XDI3SubSegment.create("+passport"), abcPassportContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport"), abcPassportContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!1"), abcPassport1ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport!1"), abcPassport1ContextNode.getXri());
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertEquals(XDI3SubSegment.create("$v"), abcPassportVContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport$v"), abcPassportVContextNode.getXri());
		assertEquals(XDI3SubSegment.create("($)"), abcPassportCContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport($)"), abcPassportCContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!1"), abcPassportV1ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport$v!1"), abcPassportV1ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!2"), abcPassportV2ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport$v!2"), abcPassportV2ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("$"), abcPassportV2RelationDollar.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport"), abcPassportV2RelationDollar.getTargetContextNodeXri());
		assertEquals(XDI3SubSegment.create("+number"), abcPassport1LiteralNumber.getContextNode().getArcXri());
		assertEquals("987654321", abcPassport1LiteralNumber.getLiteralData());
		assertEquals(XDI3SubSegment.create("+country"), abcPassport1LiteralCountry.getContextNode().getArcXri());
		assertEquals("Canada", abcPassport1LiteralCountry.getLiteralData());
		assertEquals(XDI3SubSegment.create("$d"), abcPassport1LiteralD.getContextNode().getArcXri());
		assertEquals("2005-01-01T00:00:00Z", abcPassport1LiteralD.getLiteralData());
		assertNull(abcPassport2LiteralNumber);	// MANIPULATED
		assertNull(abcPassport2LiteralNumber);	// MANIPULATED
		assertNull(abcPassport2LiteralCountry);	// MANIPULATED
		assertNull(abcPassport2LiteralCountry);	// MANIPULATED
		assertNull(abcPassport2LiteralD);	// MANIPULATED
		assertNull(abcPassport2LiteralD);	// MANIPULATED
		assertEquals(XDI3SubSegment.create("!1"), abcPassportC1ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport($)!1"), abcPassportC1ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("!2"), abcPassportC2ContextNode.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport($)!2"), abcPassportC2ContextNode.getXri());
		assertEquals(XDI3SubSegment.create("$d"), abcPassportC1LiteralD.getContextNode().getArcXri());
		assertEquals("2010-03-03T03:03:03Z", abcPassportC1LiteralD.getLiteralData());	// MANIPULATED
		assertNull(abcPassportC2LiteralD);	// MANIPULATED
		assertNull(abcPassportC2LiteralD);	// MANIPULATED
		assertEquals(XDI3SubSegment.create("$v"), abcPassportRelationV.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport$v!2"), abcPassportRelationV.getTargetContextNodeXri());
		assertEquals(XDI3SubSegment.create("*1"), abcPassportRelation1.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport!1"), abcPassportRelation1.getTargetContextNodeXri());	// MANIPULATED
		assertNull(abcPassportRelation2);	// MANIPULATED
		assertNull(abcPassportRelation2);	// MANIPULATED
		assertEquals(XDI3SubSegment.create("+rel"), abcRelation1.getArcXri());
		assertEquals(XDI3Segment.create("=abc+passport!1"), abcRelation1.getTargetContextNodeXri());
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

	private static void assertNotEquals(Object o1, Object o2) throws Exception {

		assertFalse(o1.equals(o2));
	}

	private static void assertNotEquals(int i1, int i2) throws Exception {

		assertFalse(i1 == i2);
	}
}
