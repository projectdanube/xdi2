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
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

public abstract class AbstractGraphTest extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(AbstractGraphTest.class);

	protected abstract Graph openNewGraph(String id) throws IOException;
	protected abstract Graph reopenGraph(Graph graph, String id) throws IOException;

	public void testSimple() throws Exception {

		Graph graph0 = this.openNewGraph(this.getClass().getName() + "-graph-0");

		ContextNode markus = graph0.getRootContextNode().createContextNode(new XRI3SubSegment("=markus"));
		markus.createLiteral("test");
		markus.createRelation(new XRI3Segment("+friend"), new XRI3Segment("=drummond"));

		markus = graph0.getRootContextNode().getContextNode(new XRI3SubSegment("=markus"));
		assertNotNull(markus);
		assertTrue(markus.containsRelations());
		assertTrue(markus.containsLiteral());

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

		graph11.getRootContextNode().createContextNode(new XRI3SubSegment("=xxx"));

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

		graph12.findContextNode(new XRI3Segment("=markus"), true).createLiteral("Markus");
		graph12.findContextNode(new XRI3Segment("=markus"), true).createRelation(new XRI3Segment("+friend"), new XRI3Segment("=someone"));
		graph12.findContextNode(new XRI3Segment("=markus+name+last"), true).createLiteral("Sabadello");
		graph12.findContextNode(new XRI3Segment("=markus+name+relation"), true).createRelation(new XRI3Segment("+rel"), new XRI3Segment("=rel+test"));

		assertNotNull(graph12.findContextNode(new XRI3Segment("=markus"), false));
		assertNotNull(graph12.findLiteral(new XRI3Segment("=markus")));
		assertNotNull(graph12.findRelation(new XRI3Segment("=markus"), new XRI3Segment("+friend")));
		assertNotNull(graph12.findContextNode(new XRI3Segment("=markus+name"), false));
		assertNotNull(graph12.findContextNode(new XRI3Segment("=markus+name+last"), false));
		assertNotNull(graph12.findLiteral(new XRI3Segment("=markus+name+last")));
		assertNotNull(graph12.findRelation(new XRI3Segment("=markus+name+relation"), new XRI3Segment("+rel")));

		graph12.findContextNode(new XRI3Segment("=markus"), false).delete();

		assertNull(graph12.findContextNode(new XRI3Segment("=markus"), false));
		assertNull(graph12.findLiteral(new XRI3Segment("=markus")));
		assertNull(graph12.findRelation(new XRI3Segment("=markus"), new XRI3Segment("+friend")));
		assertNull(graph12.findContextNode(new XRI3Segment("=markus+name"), false));
		assertNull(graph12.findContextNode(new XRI3Segment("=markus+name+last"), false));
		assertNull(graph12.findLiteral(new XRI3Segment("=markus+name+last")));
		assertNull(graph12.findRelation(new XRI3Segment("=markus+name+relation"), new XRI3Segment("+rel")));

		graph12.findContextNode(new XRI3Segment("=markus"), true);

		assertNotNull(graph12.findContextNode(new XRI3Segment("=markus"), false));
		assertNull(graph12.findLiteral(new XRI3Segment("=markus")));
		assertNull(graph12.findRelation(new XRI3Segment("=markus"), new XRI3Segment("+friend")));
		assertNull(graph12.findContextNode(new XRI3Segment("=markus+name"), false));
		assertNull(graph12.findContextNode(new XRI3Segment("=markus+name+last"), false));
		assertNull(graph12.findLiteral(new XRI3Segment("=markus+name+last")));
		assertNull(graph12.findRelation(new XRI3Segment("=markus+name+relation"), new XRI3Segment("+rel")));

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

		root.createRelation(new XRI3Segment("*arc"), new XRI3Segment("=target"));
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

		root.createContextNode(new XRI3SubSegment("+name"));
		root.createContextNode(new XRI3SubSegment("+email"));

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
		ContextNode markus = root.createContextNode(new XRI3SubSegment("=markus"));
		ContextNode target1 = root.createContextNode(new XRI3SubSegment("=test")).createContextNode(new XRI3SubSegment("*target1"));
		ContextNode target2 = root.getContextNode(new XRI3SubSegment("=test")).createContextNode(new XRI3SubSegment("*target2"));
		ContextNode target3 = root.getContextNode(new XRI3SubSegment("=test")).createContextNode(new XRI3SubSegment("*target3"));

		markus.createRelation(new XRI3Segment("+friend"), target1);
		markus.createRelation(new XRI3Segment("+friend"), target2);
		markus.createRelation(new XRI3Segment("+brother"), target3);
		root.createRelation(new XRI3Segment("+rel"), markus);

		assertTrue(root.containsRelations());
		assertTrue(root.containsRelations(new XRI3Segment("+rel")));
		assertTrue(root.containsRelation(new XRI3Segment("+rel"), new XRI3Segment("=markus")));
		assertEquals(root.getRelationCount(), 1);
		assertEquals(root.getRelationCount(new XRI3Segment("+rel")), 1);
		assertEquals(root.getRelation(new XRI3Segment("+rel"), new XRI3Segment("=markus")).follow(), markus);
		assertEquals(root.getRelation(new XRI3Segment("+rel")).follow(), markus);
		assertEquals(new IteratorCounter(root.getRelations(new XRI3Segment("+rel"))).count(), 1);
		assertEquals(new IteratorCounter(root.getRelations()).count(), 1);

		assertTrue(markus.containsRelations());
		assertTrue(markus.containsRelations(new XRI3Segment("+friend")));
		assertTrue(markus.containsRelations(new XRI3Segment("+brother")));
		assertTrue(markus.containsRelation(new XRI3Segment("+friend"), new XRI3Segment("=test*target1")));
		assertTrue(markus.containsRelation(new XRI3Segment("+friend"), new XRI3Segment("=test*target2")));
		assertTrue(markus.containsRelation(new XRI3Segment("+brother"), new XRI3Segment("=test*target3")));
		assertEquals(markus.getRelationCount(), 3);
		assertEquals(markus.getRelationCount(new XRI3Segment("+friend")), 2);
		assertEquals(markus.getRelationCount(new XRI3Segment("+brother")), 1);
		assertNotNull(markus.getRelation(new XRI3Segment("+friend"), new XRI3Segment("=test*target1")));
		assertNotNull(markus.getRelation(new XRI3Segment("+friend"), new XRI3Segment("=test*target2")));
		assertNotNull(markus.getRelation(new XRI3Segment("+brother"), new XRI3Segment("=test*target3")));
		assertNotNull(markus.getRelation(new XRI3Segment("+friend")));
		assertNotNull(markus.getRelation(new XRI3Segment("+brother")));
		assertEquals(new IteratorCounter(markus.getRelations(new XRI3Segment("+friend"))).count(), 2);
		assertEquals(new IteratorCounter(markus.getRelations(new XRI3Segment("+brother"))).count(), 1);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 3);

		root.deleteRelations();
		markus.deleteRelations(new XRI3Segment("+friend"));
		markus.deleteRelation(new XRI3Segment("+brother"), new XRI3Segment("=test*target3"));

		assertFalse(root.containsRelations());
		assertFalse(root.containsRelations(new XRI3Segment("+rel")));
		assertFalse(root.containsRelation(new XRI3Segment("+rel"), new XRI3Segment("=markus")));
		assertEquals(root.getRelationCount(), 0);
		assertEquals(root.getRelationCount(new XRI3Segment("+rel")), 0);
		assertNull(root.getRelation(new XRI3Segment("+rel"), new XRI3Segment("=markus")));
		assertNull(root.getRelation(new XRI3Segment("+rel")));
		assertFalse(root.getRelations(new XRI3Segment("+rel")).hasNext());
		assertEquals(new IteratorCounter(root.getRelations(new XRI3Segment("+rel"))).count(), 0);
		assertEquals(new IteratorCounter(root.getRelations()).count(), 0);

		assertFalse(markus.containsRelations());
		assertFalse(markus.containsRelations(new XRI3Segment("+friend")));
		assertFalse(markus.containsRelations(new XRI3Segment("+brother")));
		assertFalse(markus.containsRelation(new XRI3Segment("+friend"), new XRI3Segment("=test*target1")));
		assertFalse(markus.containsRelation(new XRI3Segment("+friend"), new XRI3Segment("=test*target2")));
		assertFalse(markus.containsRelation(new XRI3Segment("+brother"), new XRI3Segment("=test*target3")));
		assertEquals(markus.getRelationCount(), 0);
		assertEquals(markus.getRelationCount(new XRI3Segment("+friend")), 0);
		assertEquals(markus.getRelationCount(new XRI3Segment("+brother")), 0);
		assertNull(markus.getRelation(new XRI3Segment("+friend"), new XRI3Segment("=test*target1")));
		assertNull(markus.getRelation(new XRI3Segment("+friend"), new XRI3Segment("=test*target2")));
		assertNull(markus.getRelation(new XRI3Segment("+brother"), new XRI3Segment("=test*target3")));
		assertNull(markus.getRelation(new XRI3Segment("+friend")));
		assertNull(markus.getRelation(new XRI3Segment("+brother")));
		assertEquals(new IteratorCounter(markus.getRelations(new XRI3Segment("+friend"))).count(), 0);
		assertEquals(new IteratorCounter(markus.getRelations(new XRI3Segment("+brother"))).count(), 0);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 0);

		graph14.close();
	}

	public void testContextNodeXris() throws Exception {

		Graph graph15 = this.openNewGraph(this.getClass().getName() + "-graph-15");
		ContextNode root = graph15.getRootContextNode();

		ContextNode c = root.createContextNodes(new XRI3Segment("+a+b+c"));
		ContextNode b = c.getContextNode();
		ContextNode a = b.getContextNode();

		ContextNode e = c.createContextNodes(new XRI3Segment("+d+e"));
		ContextNode d = e.getContextNode();

		Relation r = c.createRelation(new XRI3Segment("+x+y"), b);
		Literal l = e.createLiteral("test");

		assertTrue(a.getContextNode().isRootContextNode());
		assertNull(a.getContextNode().getContextNode());

		assertFalse(a.isLeafContextNode());
		assertFalse(b.isLeafContextNode());
		assertFalse(c.isLeafContextNode());
		assertFalse(d.isLeafContextNode());
		assertTrue(e.isLeafContextNode());

		assertEquals(a.getXri(), new XRI3Segment("+a"));
		assertEquals(b.getXri(), new XRI3Segment("+a+b"));
		assertEquals(c.getXri(), new XRI3Segment("+a+b+c"));
		assertEquals(d.getXri(), new XRI3Segment("+a+b+c+d"));
		assertEquals(e.getXri(), new XRI3Segment("+a+b+c+d+e"));
		assertEquals(a.getArcXri(), new XRI3Segment("+a"));
		assertEquals(b.getArcXri(), new XRI3Segment("+b"));
		assertEquals(c.getArcXri(), new XRI3Segment("+c"));
		assertEquals(d.getArcXri(), new XRI3Segment("+d"));
		assertEquals(e.getArcXri(), new XRI3Segment("+e"));

		assertEquals(graph15.findContextNode(new XRI3Segment("+a+b+c+d"), false), d);
		assertEquals(a.findContextNode(new XRI3Segment("+b+c+d"), false), d);
		assertEquals(b.findContextNode(new XRI3Segment("+c+d"), false), d);
		assertEquals(graph15.findRelation(new XRI3Segment("+a+b+c"), new XRI3Segment("+x+y")), r);
		assertEquals(a.findRelation(new XRI3Segment("+b+c"), new XRI3Segment("+x+y")), r);
		assertEquals(b.findRelation(new XRI3Segment("+c"), new XRI3Segment("+x+y")), r);
		assertEquals(graph15.findLiteral(new XRI3Segment("+a+b+c+d+e")), l);
		assertEquals(a.findLiteral(new XRI3Segment("+b+c+d+e")), l);
		assertEquals(b.findLiteral(new XRI3Segment("+c+d+e")), l);

		graph15.close();
	}

	public void testIncomingRelations() throws Exception {

		Graph graph16 = this.openNewGraph(this.getClass().getName() + "-graph-16");

		graph16.addStatement(StatementUtil.fromString("=markus/+friend/=animesh"));
		graph16.addStatement(StatementUtil.fromString("=markus/+friend/=neustar*les"));
		graph16.addStatement(StatementUtil.fromString("=!1111*2222/$is/=markus"));

		ContextNode markus = graph16.findContextNode(new XRI3Segment("=markus"), false);
		ContextNode animesh = graph16.findContextNode(new XRI3Segment("=animesh"), false);
		ContextNode les = graph16.findContextNode(new XRI3Segment("=neustar*les"), false);
		ContextNode inumber = graph16.findContextNode(new XRI3Segment("=!1111*2222"), false);

		assertEquals(graph16.getRootContextNode().getAllRelationCount(), 3);
		assertEquals(new IteratorCounter(markus.getRelations()).count(), 2);
		assertEquals(new IteratorCounter(animesh.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(les.getRelations()).count(), 0);
		assertEquals(new IteratorCounter(inumber.getRelations()).count(), 1);
		assertEquals(new IteratorCounter(markus.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(animesh.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(les.getIncomingRelations()).count(), 1);
		assertEquals(new IteratorCounter(inumber.getIncomingRelations()).count(), 0);

		Relation friend1 = markus.getRelation(new XRI3Segment("+friend"), new XRI3Segment("=animesh"));
		Relation friend2 = markus.getRelation(new XRI3Segment("+friend"), new XRI3Segment("=neustar*les"));
		Relation is = graph16.findRelation(new XRI3Segment("=!1111*2222"), new XRI3Segment("$is"));

		assertTrue(new IteratorContains(graph16.findRelations(new XRI3Segment("=markus"), new XRI3Segment("+friend")), friend1).contains());
		assertTrue(new IteratorContains(graph16.findRelations(new XRI3Segment("=markus"), new XRI3Segment("+friend")), friend2).contains());

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

		graph16.getRootContextNode().deleteContextNode(new XRI3SubSegment("=markus"));

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

		graph17.addStatement(StatementUtil.fromString("=markus/!/(data:,Markus%20Sabadello)"));
		graph17.addStatement(StatementUtil.fromString("=markus/+friend/=neustar*les"));

		ContextNode root = graph17.getRootContextNode();
		ContextNode markus = graph17.findContextNode(new XRI3Segment("=markus"), false);
		ContextNode les = graph17.findContextNode(new XRI3Segment("=neustar*les"), false);

		try { root.createContextNode(new XRI3SubSegment("=markus")); fail(); } catch (Xdi2GraphException ex) { }
		try { markus.createLiteral("test"); fail(); } catch (Xdi2GraphException ex) { }
		try { markus.createRelation(new XRI3Segment("+friend"), les); fail(); } catch (Xdi2GraphException ex) { }

		graph17.close();
	}

	public void testTransactions() throws Exception {

		Graph graph18 = this.openNewGraph(this.getClass().getName() + "-graph-18");

		graph18.addStatement(StatementUtil.fromString("=markus/!/(data:,Markus%20Sabadello)"));
		graph18.addStatement(StatementUtil.fromString("=markus/+friend/=neustar*les"));

		ContextNode markus = graph18.findContextNode(new XRI3Segment("=markus"), false);

		graph18.beginTransaction();

		markus.deleteLiteral();
		markus.deleteRelations(new XRI3Segment("+friend"));
		markus.createRelation(new XRI3Segment("$is+"), new XRI3Segment("+person"));

		assertFalse(markus.containsLiteral());
		assertFalse(markus.containsRelations(new XRI3Segment("+friend")));
		assertTrue(markus.containsRelation(new XRI3Segment("$is+"), new XRI3Segment("+person")));

		graph18.rollbackTransaction();

		if (graph18.supportsTransactions()) {

			assertTrue(markus.containsLiteral());
			assertTrue(markus.containsRelations(new XRI3Segment("+friend")));
			assertFalse(markus.containsRelation(new XRI3Segment("$is+"), new XRI3Segment("+person")));
		} else {

			assertFalse(markus.containsLiteral());
			assertFalse(markus.containsRelations(new XRI3Segment("+friend")));
			assertTrue(markus.containsRelation(new XRI3Segment("$is+"), new XRI3Segment("+person")));
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

		ContextNode markus = graph19.findContextNode(new XRI3Segment("=markus"), true);

		assertNull(markus.getLiteral());
		assertFalse(markus.getContextNodes().hasNext());
		try { markus.getContextNodes().next(); fail(); } catch (NoSuchElementException ex) { }
		assertNull(markus.getContextNode(new XRI3SubSegment("*not")));
		assertFalse(markus.getAllContextNodes().hasNext());
		try { markus.getAllContextNodes().next(); fail(); } catch (NoSuchElementException ex) { }
		assertFalse(markus.getAllLeafContextNodes().hasNext());
		try { markus.getAllLeafContextNodes().next(); fail(); } catch (NoSuchElementException ex) { }
		assertEquals(markus.getContextNodeCount(), 0);
		assertEquals(markus.getAllContextNodeCount(), 0);

		assertNull(markus.getRelation(new XRI3Segment("+not"), new XRI3Segment("=not")));
		assertNull(markus.getRelation(new XRI3Segment("+not")));
		assertFalse(markus.getRelations(new XRI3Segment("+not")).hasNext());
		try { markus.getRelations(new XRI3Segment("+not")).next(); fail(); } catch (NoSuchElementException ex) { }
		assertFalse(markus.getRelations().hasNext());
		try { markus.getRelations().next(); fail(); } catch (NoSuchElementException ex) { }
		try { markus.getIncomingRelations().next(); fail(); } catch (NoSuchElementException ex) { }
		assertFalse(markus.getAllRelations().hasNext());
		try { markus.getAllRelations().next(); fail(); } catch (NoSuchElementException ex) { }
		assertEquals(markus.getRelationCount(new XRI3Segment("+not")), 0);
		assertEquals(markus.getRelationCount(), 0);
		assertEquals(markus.getAllRelationCount(), 0);

		assertNull(markus.getLiteral());
		assertNull(markus.getLiteralInContextNode(new XRI3SubSegment("!not")));
		assertEquals(markus.getAllLiteralCount(), 0);

		assertEquals(markus.getAllStatementCount(), 1);

		graph19.close();
	}

	public void testIllegalArcXris() throws Exception {

		Graph graph20 = this.openNewGraph(this.getClass().getName() + "-graph-20");

		ContextNode markus = graph20.findContextNode(new XRI3Segment("=markus"), true);

		try {

			markus.createContextNodes(new XRI3Segment("()"));
			fail();
		} catch (Xdi2GraphException ex){

		}

		try {

			markus.createRelation(new XRI3Segment("()"), new XRI3Segment("=animesh"));
			fail();
		} catch (Xdi2GraphException ex){

		}

		try {

			markus.createRelation(new XRI3Segment("!"), new XRI3Segment("=animesh"));
			fail();
		} catch (Xdi2GraphException ex){

		}

		graph20.close();
	}

	public void testImplied() throws Exception {

		Graph graph21 = this.openNewGraph(this.getClass().getName() + "-graph-21");

		ContextNode webmarkus = graph21.findContextNode(new XRI3Segment("=web*markus"), true);
		ContextNode animesh = graph21.findContextNode(new XRI3Segment("=animesh"), true);
		Relation friend = webmarkus.createRelation(new XRI3Segment("+friend"), animesh);
		Literal name = webmarkus.createLiteral("Markus Sabadello");
		ContextNode web = webmarkus.getContextNode();

		assertTrue(StatementUtil.isImplied(webmarkus.getStatement()));
		assertTrue(StatementUtil.isImplied(animesh.getStatement()));
		assertFalse(StatementUtil.isImplied(friend.getStatement()));
		assertFalse(StatementUtil.isImplied(name.getStatement()));
		assertTrue(StatementUtil.isImplied(web.getStatement()));

		graph21.close();
	}

	@SuppressWarnings("unused")
	private static void makeGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.createContextNode(new XRI3SubSegment("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.createContextNode(new XRI3SubSegment("+passport"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.createContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.createContextNode(new XRI3SubSegment("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.createContextNode(new XRI3SubSegment("$v"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.createContextNode(new XRI3SubSegment("($)"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.createContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.createContextNode(new XRI3SubSegment("!2"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.createRelation(new XRI3Segment("$"), new XRI3Segment("=abc+passport"));
		Literal abcPassport1NumberLiteral = abcPassport1ContextNode.createContextNode(new XRI3SubSegment("+number")).createLiteral("987654321");
		Literal abcPassport1CountryLiteral = abcPassport1ContextNode.createContextNode(new XRI3SubSegment("+country")).createLiteral("Canada");
		Literal abcPassport1DLiteral = abcPassport1ContextNode.createContextNode(new XRI3SubSegment("$d")).createLiteral("2005-01-01T00:00:00Z");
		Literal abcPassport2NumberLiteral = abcPassport2ContextNode.createContextNode(new XRI3SubSegment("+number")).createLiteral("123456789");
		Literal abcPassport2CountryLiteral = abcPassport2ContextNode.createContextNode(new XRI3SubSegment("+country")).createLiteral("New Zealand");
		Literal abcPassport2DLiteral = abcPassport2ContextNode.createContextNode(new XRI3SubSegment("$d")).createLiteral("2010-10-01T00:00:00Z");
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.createContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.createContextNode(new XRI3SubSegment("!2"));
		Literal abcPassportC1DLiteral = abcPassportC1ContextNode.createContextNode(new XRI3SubSegment("$d")).createLiteral("2010-11-11T11:11:11Z");
		Literal abcPassportC2DLiteral = abcPassportC2ContextNode.createContextNode(new XRI3SubSegment("$d")).createLiteral("2010-12-22T22:22:22Z");
		Relation abcPassportRelationV = abcPassportContextNode.createRelation(new XRI3Segment("$v"), new XRI3Segment("=abc+passport$v!2"));
		Relation abcPassportRelation1 = abcPassportContextNode.createRelation(new XRI3Segment("*1"), abcPassport1ContextNode);
		Relation abcPassportRelation2 = abcPassportContextNode.createRelation(new XRI3Segment("*2"), abcPassport2ContextNode);
		Relation abcTestRelation1 = abcContextNode.createRelation(new XRI3Segment("+rel"), abcPassport1ContextNode);
		Relation abcTestRelation2 = abcContextNode.createRelation(new XRI3Segment("+rel"), abcPassport2ContextNode);
	}

	private static void testGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.getContextNode(new XRI3SubSegment("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.getContextNode(new XRI3SubSegment("+passport"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("$v"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("($)"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.getContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.getContextNode(new XRI3SubSegment("!2"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.getRelation(new XRI3Segment("$"));
		Literal abcPassport1NumberLiteral = abcPassport1ContextNode.getLiteralInContextNode(new XRI3SubSegment("+number"));
		Literal abcPassport1CountryLiteral = abcPassport1ContextNode.getLiteralInContextNode(new XRI3SubSegment("+country"));
		Literal abcPassport1DLiteral = abcPassport1ContextNode.getLiteralInContextNode(new XRI3SubSegment("$d"));
		Literal abcPassport2NumberLiteral = abcPassport2ContextNode.getLiteralInContextNode(new XRI3SubSegment("+number"));
		Literal abcPassport2CountryLiteral = abcPassport2ContextNode.getLiteralInContextNode(new XRI3SubSegment("+country"));
		Literal abcPassport2DLiteral = abcPassport2ContextNode.getLiteralInContextNode(new XRI3SubSegment("$d"));
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.getContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.getContextNode(new XRI3SubSegment("!2"));
		Literal abcPassportC1DLiteral = abcPassportC1ContextNode.getLiteralInContextNode(new XRI3SubSegment("$d"));
		Literal abcPassportC2DLiteral = abcPassportC2ContextNode.getLiteralInContextNode(new XRI3SubSegment("$d"));
		Relation abcPassportRelationV = abcPassportContextNode.getRelation(new XRI3Segment("$v"));
		Relation abcPassportRelation1 = abcPassportContextNode.getRelation(new XRI3Segment("*1"));
		Relation abcPassportRelation2 = abcPassportContextNode.getRelation(new XRI3Segment("*2"));
		Relation abcRelation1 = abcContextNode.getRelation(new XRI3Segment("+rel"), new XRI3Segment("=abc+passport!1"));
		Relation abcRelation2 = abcContextNode.getRelation(new XRI3Segment("+rel"), new XRI3Segment("=abc+passport!2"));

		assertEquals(rootContextNode.getXri(), XDIConstants.XRI_S_ROOT);
		assertEquals(abcContextNode.getXri(), new XRI3Segment("=abc"));
		assertEquals(abcPassportContextNode.getXri(), new XRI3Segment("=abc+passport"));
		assertEquals(abcPassportVContextNode.getXri(), new XRI3Segment("=abc+passport$v"));

		assertTrue(rootContextNode.containsContextNode(new XRI3SubSegment("=abc")));
		assertTrue(abcContextNode.containsContextNode(new XRI3SubSegment("+passport")));
		assertTrue(abcPassportContextNode.containsContextNode(new XRI3SubSegment("!1")));
		assertTrue(abcPassportContextNode.containsContextNode(new XRI3SubSegment("!2")));
		assertTrue(abcPassportContextNode.containsContextNode(new XRI3SubSegment("$v")));
		assertTrue(abcPassportContextNode.containsContextNode(new XRI3SubSegment("($)")));
		assertTrue(abcPassportVContextNode.containsContextNode(new XRI3SubSegment("!1")));
		assertTrue(abcPassportVContextNode.containsContextNode(new XRI3SubSegment("!2")));
		assertTrue(abcPassportV2ContextNode.containsRelation(new XRI3Segment("$"), new XRI3Segment("=abc+passport")));
		assertTrue(abcPassport1ContextNode.containsContextNode(new XRI3SubSegment("+number")));
		assertTrue(abcPassport1ContextNode.containsContextNode(new XRI3SubSegment("+country")));
		assertTrue(abcPassport1ContextNode.containsContextNode(new XRI3SubSegment("$d")));
		assertTrue(abcPassport2ContextNode.containsContextNode(new XRI3SubSegment("+number")));
		assertTrue(abcPassport2ContextNode.containsContextNode(new XRI3SubSegment("+country")));
		assertTrue(abcPassport2ContextNode.containsContextNode(new XRI3SubSegment("$d")));
		assertTrue(abcPassport1ContextNode.containsLiteralInContextNode(new XRI3SubSegment("+number")));
		assertTrue(abcPassport1ContextNode.containsLiteralInContextNode(new XRI3SubSegment("+country")));
		assertTrue(abcPassport1ContextNode.containsLiteralInContextNode(new XRI3SubSegment("$d")));
		assertTrue(abcPassport2ContextNode.containsLiteralInContextNode(new XRI3SubSegment("+number")));
		assertTrue(abcPassport2ContextNode.containsLiteralInContextNode(new XRI3SubSegment("+country")));
		assertTrue(abcPassport2ContextNode.containsLiteralInContextNode(new XRI3SubSegment("$d")));
		assertTrue(abcPassportCContextNode.containsContextNode(new XRI3SubSegment("!1")));
		assertTrue(abcPassportCContextNode.containsContextNode(new XRI3SubSegment("!2")));
		assertTrue(abcPassportC1ContextNode.containsContextNode(new XRI3SubSegment("$d")));
		assertTrue(abcPassportC2ContextNode.containsContextNode(new XRI3SubSegment("$d")));
		assertTrue(abcPassportC1ContextNode.containsLiteralInContextNode(new XRI3SubSegment("$d")));
		assertTrue(abcPassportC2ContextNode.containsLiteralInContextNode(new XRI3SubSegment("$d")));
		assertTrue(abcPassportContextNode.containsRelation(new XRI3Segment("$v"), new XRI3Segment("=abc+passport$v!2")));
		assertTrue(abcPassportContextNode.containsRelation(new XRI3Segment("*1"), new XRI3Segment("=abc+passport!1")));
		assertTrue(abcPassportContextNode.containsRelation(new XRI3Segment("*2"), new XRI3Segment("=abc+passport!2")));
		assertTrue(abcContextNode.containsRelation(new XRI3Segment("+rel"), new XRI3Segment("=abc+passport!1")));
		assertTrue(abcContextNode.containsRelation(new XRI3Segment("+rel"), new XRI3Segment("=abc+passport!2")));

		ContextNode contextNodesArray[] = new ContextNode [] {
				graph.findContextNode(new XRI3Segment("()"), false),
				graph.findContextNode(new XRI3Segment("=abc"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!1"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!2"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport$v"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport($)"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport$v!1"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport$v!2"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport($)!1"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport($)!2"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!1+number"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!1+country"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!1$d"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!2+number"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!2+country"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!2$d"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport($)!1$d"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport($)!2$d"), false)
		};

		XRI3SubSegment[][] contextNodeArcXrisArray = new XRI3SubSegment [][] {
				new XRI3SubSegment[] { new XRI3SubSegment("=abc") },
				new XRI3SubSegment[] { new XRI3SubSegment("+passport") },
				new XRI3SubSegment[] { new XRI3SubSegment("!1"), new XRI3SubSegment("!2"), new XRI3SubSegment("$v"), new XRI3SubSegment("($)") },
				new XRI3SubSegment[] { new XRI3SubSegment("+number"), new XRI3SubSegment("+country"), new XRI3SubSegment("$d") },
				new XRI3SubSegment[] { new XRI3SubSegment("+number"), new XRI3SubSegment("+country"), new XRI3SubSegment("$d") },
				new XRI3SubSegment[] { new XRI3SubSegment("!1"), new XRI3SubSegment("!2") },
				new XRI3SubSegment[] { new XRI3SubSegment("!1"), new XRI3SubSegment("!2") },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { new XRI3SubSegment("$d") },
				new XRI3SubSegment[] { new XRI3SubSegment("$d") },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { }
		};

		XRI3Segment[][] relationArcXrisArray = new XRI3Segment [][] {
				new XRI3Segment[] { },
				new XRI3Segment[] { new XRI3Segment("+rel"), new XRI3Segment("+rel") },
				new XRI3Segment[] { new XRI3Segment("$v"), new XRI3Segment("*1"), new XRI3Segment("*2") },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { new XRI3Segment("$") },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { }
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

			Set<XRI3SubSegment> arcXris = new HashSet<XRI3SubSegment> (Arrays.asList(contextNodeArcXrisArray[i]));
			assertEquals(arcXris.size(), contextNodesArray[i].getContextNodeCount());
			assertEquals(arcXris.size(), new IteratorCounter(contextNodesArray[i].getContextNodes()).count());
			for (Iterator<ContextNode> it = contextNodesArray[i].getContextNodes(); it.hasNext(); ) assertTrue(arcXris.remove(it.next().getArcXri()));
			assertTrue(arcXris.isEmpty());
		}

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(relationArcXrisArray[i]); continue; } else assertNotNull(relationArcXrisArray[i]);

			List<XRI3Segment> arcXris = new ArrayList<XRI3Segment> (Arrays.asList(relationArcXrisArray[i]));
			for (Iterator<XRI3Segment> it = arcXris.iterator(); it.hasNext(); ) assertTrue(contextNodesArray[i].getGraph().containsRelations(contextNodesArray[i].getXri(), it.next()));
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
		assertEquals(new XRI3SubSegment("=abc"), abcContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc"), abcContextNode.getXri());
		assertEquals(new XRI3SubSegment("+passport"), abcPassportContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport"), abcPassportContextNode.getXri());
		assertEquals(new XRI3SubSegment("!1"), abcPassport1ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!1"), abcPassport1ContextNode.getXri());
		assertEquals(new XRI3SubSegment("!2"), abcPassport2ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!2"), abcPassport2ContextNode.getXri());
		assertEquals(new XRI3SubSegment("$v"), abcPassportVContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v"), abcPassportVContextNode.getXri());
		assertEquals(new XRI3SubSegment("($)"), abcPassportCContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport($)"), abcPassportCContextNode.getXri());
		assertEquals(new XRI3SubSegment("!1"), abcPassportV1ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v!1"), abcPassportV1ContextNode.getXri());
		assertEquals(new XRI3SubSegment("!2"), abcPassportV2ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v!2"), abcPassportV2ContextNode.getXri());
		assertEquals(new XRI3SubSegment("$"), abcPassportV2RelationDollar.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport"), abcPassportV2RelationDollar.getTargetContextNodeXri());
		assertEquals(new XRI3SubSegment("+number"), abcPassport1NumberLiteral.getContextNode().getArcXri());
		assertEquals("987654321", abcPassport1NumberLiteral.getLiteralData());
		assertEquals(new XRI3SubSegment("+country"), abcPassport1CountryLiteral.getContextNode().getArcXri());
		assertEquals("Canada", abcPassport1CountryLiteral.getLiteralData());
		assertEquals(new XRI3SubSegment("$d"), abcPassport1DLiteral.getContextNode().getArcXri());
		assertEquals("2005-01-01T00:00:00Z", abcPassport1DLiteral.getLiteralData());
		assertEquals(new XRI3SubSegment("+number"), abcPassport2NumberLiteral.getContextNode().getArcXri());
		assertEquals("123456789", abcPassport2NumberLiteral.getLiteralData());
		assertEquals(new XRI3SubSegment("+country"), abcPassport2CountryLiteral.getContextNode().getArcXri());
		assertEquals("New Zealand", abcPassport2CountryLiteral.getLiteralData());
		assertEquals(new XRI3SubSegment("$d"), abcPassport2DLiteral.getContextNode().getArcXri());
		assertEquals("2010-10-01T00:00:00Z", abcPassport2DLiteral.getLiteralData());
		assertEquals(new XRI3SubSegment("!1"), abcPassportC1ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport($)!1"), abcPassportC1ContextNode.getXri());
		assertEquals(new XRI3SubSegment("!2"), abcPassportC2ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport($)!2"), abcPassportC2ContextNode.getXri());
		assertEquals(new XRI3SubSegment("$d"), abcPassportC1DLiteral.getContextNode().getArcXri());
		assertEquals("2010-11-11T11:11:11Z", abcPassportC1DLiteral.getLiteralData());
		assertEquals(new XRI3SubSegment("$d"), abcPassportC2DLiteral.getContextNode().getArcXri());
		assertEquals("2010-12-22T22:22:22Z", abcPassportC2DLiteral.getLiteralData());
		assertEquals(new XRI3SubSegment("$v"), abcPassportRelationV.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v!2"), abcPassportRelationV.getTargetContextNodeXri());
		assertEquals(new XRI3SubSegment("*1"), abcPassportRelation1.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!1"), abcPassportRelation1.getTargetContextNodeXri());
		assertEquals(new XRI3SubSegment("*2"), abcPassportRelation2.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!2"), abcPassportRelation2.getTargetContextNodeXri());
		assertEquals(new XRI3SubSegment("+rel"), abcRelation1.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!1"), abcRelation1.getTargetContextNodeXri());
		assertEquals(new XRI3SubSegment("+rel"), abcRelation2.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!2"), abcRelation2.getTargetContextNodeXri());

		assertEquals(rootContextNode.getAllContextNodeCount(), 18);
		assertEquals(rootContextNode.getAllRelationCount(), 6);
		assertEquals(rootContextNode.getAllLiteralCount(), 8);
	}

	@SuppressWarnings("unused")
	private static void manipulateGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.getContextNode(new XRI3SubSegment("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.getContextNode(new XRI3SubSegment("+passport"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("$v"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("($)"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.getContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.getContextNode(new XRI3SubSegment("!2"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.getRelation(new XRI3Segment("$"));
		Literal abcPassport1LiteralNumber = abcPassport1ContextNode.getLiteralInContextNode(new XRI3SubSegment("+number"));
		Literal abcPassport1LiteralCountry = abcPassport1ContextNode.getLiteralInContextNode(new XRI3SubSegment("+country"));
		Literal abcPassport1LiteralD = abcPassport1ContextNode.getLiteralInContextNode(new XRI3SubSegment("$d"));
		Literal abcPassport2LiteralNumber = abcPassport2ContextNode.getLiteralInContextNode(new XRI3SubSegment("+number"));
		Literal abcPassport2LiteralCountry = abcPassport2ContextNode.getLiteralInContextNode(new XRI3SubSegment("+country"));
		Literal abcPassport2LiteralD = abcPassport2ContextNode.getLiteralInContextNode(new XRI3SubSegment("$d"));
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.getContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.getContextNode(new XRI3SubSegment("!2"));
		Literal abcPassportC1LiteralD = abcPassportC1ContextNode.getLiteralInContextNode(new XRI3SubSegment("$d"));
		Literal abcPassportC2LiteralD = abcPassportC2ContextNode.getLiteralInContextNode(new XRI3SubSegment("$d"));
		Relation abcPassportRelationV = abcPassportContextNode.getRelation(new XRI3Segment("$v"));
		Relation abcPassportRelation1 = abcPassportContextNode.getRelation(new XRI3Segment("*1"));
		Relation abcPassportRelation2 = abcPassportContextNode.getRelation(new XRI3Segment("*2"));
		Relation abcRelation1 = abcContextNode.getRelation(new XRI3Segment("+rel"), new XRI3Segment("=abc+passport!1"));
		Relation abcRelation2 = abcContextNode.getRelation(new XRI3Segment("+rel"), new XRI3Segment("=abc+passport!2"));

		abcPassport2ContextNode.delete();
		abcPassportC1LiteralD.setLiteralData("2010-03-03T03:03:03Z");
		abcPassportC2LiteralD.getContextNode().delete();
	}

	private static void testManipulatedGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.getContextNode(new XRI3SubSegment("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.getContextNode(new XRI3SubSegment("+passport"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("$v"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("($)"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.getContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.getContextNode(new XRI3SubSegment("!2"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.getRelation(new XRI3Segment("$"));
		Literal abcPassport1LiteralNumber = abcPassport1ContextNode.getLiteralInContextNode(new XRI3SubSegment("+number"));
		Literal abcPassport1LiteralCountry = abcPassport1ContextNode.getLiteralInContextNode(new XRI3SubSegment("+country"));
		Literal abcPassport1LiteralD = abcPassport1ContextNode.getLiteralInContextNode(new XRI3SubSegment("$d"));
		Literal abcPassport2LiteralNumber = null;
		Literal abcPassport2LiteralCountry = null;
		Literal abcPassport2LiteralD = null;
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.getContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.getContextNode(new XRI3SubSegment("!2"));
		Literal abcPassportC1LiteralD = abcPassportC1ContextNode.getLiteralInContextNode(new XRI3SubSegment("$d"));
		Literal abcPassportC2LiteralD = abcPassportC2ContextNode.getLiteralInContextNode(new XRI3SubSegment("$d"));
		Relation abcPassportRelationV = abcPassportContextNode.getRelation(new XRI3Segment("$v"));
		Relation abcPassportRelation1 = abcPassportContextNode.getRelation(new XRI3Segment("*1"));
		Relation abcPassportRelation2 = abcPassportContextNode.getRelation(new XRI3Segment("*2"));
		Relation abcRelation1 = abcContextNode.getRelation(new XRI3Segment("+rel"), new XRI3Segment("=abc+passport!1"));
		Relation abcRelation2 = abcContextNode.getRelation(new XRI3Segment("+rel"), new XRI3Segment("=abc+passport!2"));

		assertTrue(rootContextNode.containsContextNode(new XRI3SubSegment("=abc")));
		assertTrue(abcContextNode.containsContextNode(new XRI3SubSegment("+passport")));
		assertTrue(abcPassportContextNode.containsContextNode(new XRI3SubSegment("!1")));
		assertFalse(abcPassportContextNode.containsContextNode(new XRI3SubSegment("!2")));	// MANIPULATED
		assertTrue(abcPassportContextNode.containsContextNode(new XRI3SubSegment("$v")));
		assertTrue(abcPassportContextNode.containsContextNode(new XRI3SubSegment("($)")));
		assertTrue(abcPassportVContextNode.containsContextNode(new XRI3SubSegment("!1")));
		assertTrue(abcPassportVContextNode.containsContextNode(new XRI3SubSegment("!2")));
		assertTrue(abcPassportV2ContextNode.containsRelation(new XRI3Segment("$"), new XRI3Segment("=abc+passport")));
		assertTrue(abcPassport1ContextNode.containsContextNode(new XRI3SubSegment("+number")));
		assertTrue(abcPassport1ContextNode.containsContextNode(new XRI3SubSegment("+country")));
		assertTrue(abcPassport1ContextNode.containsContextNode(new XRI3SubSegment("$d")));
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertTrue(abcPassport1ContextNode.containsLiteralInContextNode(new XRI3SubSegment("+number")));
		assertTrue(abcPassport1ContextNode.containsLiteralInContextNode(new XRI3SubSegment("+country")));
		assertTrue(abcPassport1ContextNode.containsLiteralInContextNode(new XRI3SubSegment("$d")));
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertTrue(abcPassportCContextNode.containsContextNode(new XRI3SubSegment("!1")));
		assertTrue(abcPassportCContextNode.containsContextNode(new XRI3SubSegment("!2")));
		assertTrue(abcPassportC1ContextNode.containsContextNode(new XRI3SubSegment("$d")));
		assertFalse(abcPassportC2ContextNode.containsContextNode(new XRI3SubSegment("$d")));	// MANIPULATED
		assertTrue(abcPassportC1ContextNode.containsLiteralInContextNode(new XRI3SubSegment("$d")));
		assertNull(abcPassportC2LiteralD);	// MANIPULATED
		assertTrue(abcPassportContextNode.containsRelation(new XRI3Segment("$v"), new XRI3Segment("=abc+passport$v!2")));
		assertTrue(abcPassportContextNode.containsRelation(new XRI3Segment("*1"), new XRI3Segment("=abc+passport!1")));
		assertFalse(abcPassportContextNode.containsRelation(new XRI3Segment("*2"), new XRI3Segment("=abc+passport!2")));		// MANIPULATED
		assertTrue(abcContextNode.containsRelation(new XRI3Segment("+rel"), new XRI3Segment("=abc+passport!1")));
		assertFalse(abcContextNode.containsRelation(new XRI3Segment("+rel"), new XRI3Segment("=abc+passport!2")));		// MANIPULATED

		ContextNode contextNodesArray[] = new ContextNode [] {
				graph.findContextNode(new XRI3Segment("()"), false),
				graph.findContextNode(new XRI3Segment("=abc"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!1"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!2"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport$v"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport($)"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport$v!1"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport$v!2"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport($)!1"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport($)!2"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!1+number"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!1+country"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!1$d"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!2+number"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!2+country"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport!2$d"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport($)!1$d"), false),
				graph.findContextNode(new XRI3Segment("=abc+passport($)!2$d"), false)
		};

		XRI3SubSegment[][] contextNodeArcXrisArray = new XRI3SubSegment [][] {
				new XRI3SubSegment[] { new XRI3SubSegment("=abc") },
				new XRI3SubSegment[] { new XRI3SubSegment("+passport") },
				new XRI3SubSegment[] { new XRI3SubSegment("!1"), new XRI3SubSegment("$v"), new XRI3SubSegment("($)") },	// MANIPULATED
				new XRI3SubSegment[] { new XRI3SubSegment("+number"), new XRI3SubSegment("+country"), new XRI3SubSegment("$d") },
				null,	// MANIPULATED
				new XRI3SubSegment[] { new XRI3SubSegment("!1"), new XRI3SubSegment("!2") },
				new XRI3SubSegment[] { new XRI3SubSegment("!1"), new XRI3SubSegment("!2") },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { new XRI3SubSegment("$d") },
				new XRI3SubSegment[] { },	// MANIPULATED
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				null,	// MANIPULATED
				null,	// MANIPULATED
				null,	// MANIPULATED
				new XRI3SubSegment[] { },
				null	// MANIPULATED
		};

		XRI3Segment[][] relationArcXrisArray = new XRI3Segment [][] {
				new XRI3Segment[] { },
				new XRI3Segment[] { new XRI3Segment("+rel") },	// MANIPULATED
				new XRI3Segment[] { new XRI3Segment("$v"), new XRI3Segment("*1") },	// MANIPULATED
				new XRI3Segment[] { },
				null,	// MANIPULATED
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { new XRI3Segment("$") },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				new XRI3Segment[] { },
				null,	// MANIPULATED
				null,	// MANIPULATED
				null,	// MANIPULATED
				new XRI3Segment[] { },
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

			Set<XRI3SubSegment> arcXris = new HashSet<XRI3SubSegment> (Arrays.asList(contextNodeArcXrisArray[i]));
			assertEquals(arcXris.size(), contextNodesArray[i].getContextNodeCount());
			assertEquals(arcXris.size(), new IteratorCounter(contextNodesArray[i].getContextNodes()).count());
			for (Iterator<ContextNode> it = contextNodesArray[i].getContextNodes(); it.hasNext(); ) assertTrue(arcXris.remove(it.next().getArcXri()));
			assertTrue(arcXris.isEmpty());
		}

		for (int i=0; i<contextNodesArray.length; i++) {

			if (contextNodesArray[i] == null) { assertNull(relationArcXrisArray[i]); continue; } else assertNotNull(relationArcXrisArray[i]);

			List<XRI3Segment> arcXris = new ArrayList<XRI3Segment> (Arrays.asList(relationArcXrisArray[i]));
			for (Iterator<XRI3Segment> it = arcXris.iterator(); it.hasNext(); ) assertTrue(contextNodesArray[i].getGraph().containsRelations(contextNodesArray[i].getXri(), it.next()));
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
		assertEquals(new XRI3SubSegment("=abc"), abcContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc"), abcContextNode.getXri());
		assertEquals(new XRI3SubSegment("+passport"), abcPassportContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport"), abcPassportContextNode.getXri());
		assertEquals(new XRI3SubSegment("!1"), abcPassport1ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!1"), abcPassport1ContextNode.getXri());
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertNull(abcPassport2ContextNode);	// MANIPULATED
		assertEquals(new XRI3SubSegment("$v"), abcPassportVContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v"), abcPassportVContextNode.getXri());
		assertEquals(new XRI3SubSegment("($)"), abcPassportCContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport($)"), abcPassportCContextNode.getXri());
		assertEquals(new XRI3SubSegment("!1"), abcPassportV1ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v!1"), abcPassportV1ContextNode.getXri());
		assertEquals(new XRI3SubSegment("!2"), abcPassportV2ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v!2"), abcPassportV2ContextNode.getXri());
		assertEquals(new XRI3SubSegment("$"), abcPassportV2RelationDollar.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport"), abcPassportV2RelationDollar.getTargetContextNodeXri());
		assertEquals(new XRI3SubSegment("+number"), abcPassport1LiteralNumber.getContextNode().getArcXri());
		assertEquals("987654321", abcPassport1LiteralNumber.getLiteralData());
		assertEquals(new XRI3SubSegment("+country"), abcPassport1LiteralCountry.getContextNode().getArcXri());
		assertEquals("Canada", abcPassport1LiteralCountry.getLiteralData());
		assertEquals(new XRI3SubSegment("$d"), abcPassport1LiteralD.getContextNode().getArcXri());
		assertEquals("2005-01-01T00:00:00Z", abcPassport1LiteralD.getLiteralData());
		assertNull(abcPassport2LiteralNumber);	// MANIPULATED
		assertNull(abcPassport2LiteralNumber);	// MANIPULATED
		assertNull(abcPassport2LiteralCountry);	// MANIPULATED
		assertNull(abcPassport2LiteralCountry);	// MANIPULATED
		assertNull(abcPassport2LiteralD);	// MANIPULATED
		assertNull(abcPassport2LiteralD);	// MANIPULATED
		assertEquals(new XRI3SubSegment("!1"), abcPassportC1ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport($)!1"), abcPassportC1ContextNode.getXri());
		assertEquals(new XRI3SubSegment("!2"), abcPassportC2ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport($)!2"), abcPassportC2ContextNode.getXri());
		assertEquals(new XRI3SubSegment("$d"), abcPassportC1LiteralD.getContextNode().getArcXri());
		assertEquals("2010-03-03T03:03:03Z", abcPassportC1LiteralD.getLiteralData());	// MANIPULATED
		assertNull(abcPassportC2LiteralD);	// MANIPULATED
		assertNull(abcPassportC2LiteralD);	// MANIPULATED
		assertEquals(new XRI3SubSegment("$v"), abcPassportRelationV.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v!2"), abcPassportRelationV.getTargetContextNodeXri());
		assertEquals(new XRI3SubSegment("*1"), abcPassportRelation1.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!1"), abcPassportRelation1.getTargetContextNodeXri());	// MANIPULATED
		assertNull(abcPassportRelation2);	// MANIPULATED
		assertNull(abcPassportRelation2);	// MANIPULATED
		assertEquals(new XRI3SubSegment("+rel"), abcRelation1.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!1"), abcRelation1.getTargetContextNodeXri());
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
