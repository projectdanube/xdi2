package xdi2.tests.basic;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.Relation;
import xdi2.io.XDIReader;
import xdi2.io.XDIReaderRegistry;
import xdi2.io.XDIWriter;
import xdi2.io.XDIWriterRegistry;
import xdi2.util.XDIConstants;
import xdi2.util.iterators.IteratorCounter;
import xdi2.xri3.impl.XRI3Segment;
import xdi2.xri3.impl.XRI3SubSegment;

public abstract class BasicTest extends TestCase {

	protected abstract Graph openNewGraph(String id) throws IOException;
	protected abstract Graph reopenGraph(Graph graph, String id) throws IOException;

	public void testMakeGraph() throws Exception {

		Graph graph1 = this.openNewGraph("1");

		makeGraph(graph1);
		testGraph(graph1);
	}

	public void testReopenGraph() throws Exception {

		Graph graph2 = this.openNewGraph("2");

		makeGraph(graph2);
		graph2 = this.reopenGraph(graph2, "2");
		testGraph(graph2);
	}

	public void testReadJson() throws Exception {

		Graph graph3 = this.openNewGraph("3");

		XDIReader reader = XDIReaderRegistry.forFormat("XDI/JSON");

		reader.read(graph3, this.getClass().getResourceAsStream("test.json"), null).close();
		testGraph(graph3);
	}

	public void testWriteJson() throws Exception {

		Graph graph4 = this.openNewGraph("4");
		Graph graph5 = this.openNewGraph("5");

		XDIReader reader = XDIReaderRegistry.forFormat("XDI/JSON");
		XDIWriter writer = XDIWriterRegistry.forFormat("XDI/JSON");

		makeGraph(graph4);
		writer.write(graph4, new FileWriter(new File("test.json.out")), null).close();
		reader.read(graph5, new FileReader(new File("test.json.out")), null).close();

		testGraph(graph5);
		testGraphsEqual(graph4, graph5);
	}

	public void testWriteStatements() throws Exception {

		Graph graph6 = this.openNewGraph("6");
//		Graph graph7 = this.openNewGraph("7");

//		XDIReader reader = XDIReaderRegistry.forFormat("STATEMENTS");
		XDIWriter writer = XDIWriterRegistry.forFormat("STATEMENTS");

		makeGraph(graph6);
		writer.write(graph6, new FileWriter(new File("test.statements.out")), null).close();
//		reader.read(graph7, new FileReader(new File("test.statements.out")), null).close();

//		testGraph(graph7);
//		testGraphsEqual(graph6, graph7);
	}

	public void testManipulate() throws Exception {

		Graph graph8 = this.openNewGraph("8");

		makeGraph(graph8);
		manipulateGraph(graph8);
		testManipulatedGraph(graph8);
	}

	public void testManipulateAndReopenGraph() throws Exception {

		Graph graph9 = this.openNewGraph("8");

		makeGraph(graph9);
		graph9 = this.reopenGraph(graph9, "8");
		manipulateGraph(graph9);
		graph9 = this.reopenGraph(graph9, "8");
		testManipulatedGraph(graph9);
	}

	@SuppressWarnings("unused")
	private static void makeGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.createContextNode(new XRI3SubSegment("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.createContextNode(new XRI3SubSegment("+passport"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.createContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.createContextNode(new XRI3SubSegment("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.createContextNode(new XRI3SubSegment("$v"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.createContextNode(new XRI3SubSegment("()"));
		ContextNode abcPassportV1ContextNode = abcPassportVContextNode.createContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassportV2ContextNode = abcPassportVContextNode.createContextNode(new XRI3SubSegment("!2"));
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.createRelation(new XRI3Segment("$"), new XRI3Segment("=abc+passport"));
		Literal abcPassport1NumberLiteral = abcPassport1ContextNode.createLiteralInContextNode(new XRI3SubSegment("+number"), "987654321");
		Literal abcPassport1CountryLiteral = abcPassport1ContextNode.createLiteralInContextNode(new XRI3SubSegment("+country"), "Canada");
		Literal abcPassport1DLiteral = abcPassport1ContextNode.createLiteralInContextNode(new XRI3SubSegment("$d"), "2005-01-01T00:00:00Z");
		Literal abcPassport2NumberLiteral = abcPassport2ContextNode.createLiteralInContextNode(new XRI3SubSegment("+number"), "123456789");
		Literal abcPassport2CountryLiteral = abcPassport2ContextNode.createLiteralInContextNode(new XRI3SubSegment("+country"), "New Zealand");
		Literal abcPassport2DLiteral = abcPassport2ContextNode.createLiteralInContextNode(new XRI3SubSegment("$d"), "2010-10-01T00:00:00Z");
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.createContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.createContextNode(new XRI3SubSegment("!2"));
		Literal abcPassportC1DLiteral = abcPassportC1ContextNode.createLiteralInContextNode(new XRI3SubSegment("$d"), "2010-11-11T11:11:11Z");
		Literal abcPassportC2DLiteral = abcPassportC2ContextNode.createLiteralInContextNode(new XRI3SubSegment("$d"), "2010-12-22T22:22:22Z");
		Relation abcPassportRelationV = abcPassportContextNode.createRelation(new XRI3Segment("$v"), new XRI3Segment("=abc+passport$v!2"));
		Relation abcPassportRelation1 = abcPassportContextNode.createRelation(new XRI3Segment("*1"), new XRI3Segment("=abc+passport!1"));
		Relation abcPassportRelation2 = abcPassportContextNode.createRelation(new XRI3Segment("*2"), new XRI3Segment("=abc+passport!2"));
	}

	private static void testGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.getContextNode(new XRI3SubSegment("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.getContextNode(new XRI3SubSegment("+passport"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("$v"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("()"));
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

		assertTrue(rootContextNode.containsContextNode(new XRI3SubSegment("=abc")));
		assertTrue(abcContextNode.containsContextNode(new XRI3SubSegment("+passport")));
		assertTrue(abcPassportContextNode.containsContextNode(new XRI3SubSegment("!1")));
		assertTrue(abcPassportContextNode.containsContextNode(new XRI3SubSegment("!2")));
		assertTrue(abcPassportContextNode.containsContextNode(new XRI3SubSegment("$v")));
		assertTrue(abcPassportContextNode.containsContextNode(new XRI3SubSegment("()")));
		assertTrue(abcPassportVContextNode.containsContextNode(new XRI3SubSegment("!1")));
		assertTrue(abcPassportVContextNode.containsContextNode(new XRI3SubSegment("!2")));
		assertTrue(abcPassportV2ContextNode.containsRelation(new XRI3Segment("$")));
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
		assertTrue(abcPassportContextNode.containsRelation(new XRI3Segment("$v")));
		assertTrue(abcPassportContextNode.containsRelation(new XRI3Segment("*1")));
		assertTrue(abcPassportContextNode.containsRelation(new XRI3Segment("*2")));

		ContextNode contextNodesArray[] = new ContextNode [] {
				graph.findContextNode(new XRI3Segment("()")),
				graph.findContextNode(new XRI3Segment("=abc")),
				graph.findContextNode(new XRI3Segment("=abc+passport")),
				graph.findContextNode(new XRI3Segment("=abc+passport!1")),
				graph.findContextNode(new XRI3Segment("=abc+passport!2")),
				graph.findContextNode(new XRI3Segment("=abc+passport$v")),
				graph.findContextNode(new XRI3Segment("=abc+passport()")),
				graph.findContextNode(new XRI3Segment("=abc+passport$v!1")),
				graph.findContextNode(new XRI3Segment("=abc+passport$v!2")),
				graph.findContextNode(new XRI3Segment("=abc+passport()!1")),
				graph.findContextNode(new XRI3Segment("=abc+passport()!2")),
				graph.findContextNode(new XRI3Segment("=abc+passport!1+number")),
				graph.findContextNode(new XRI3Segment("=abc+passport!1+country")),
				graph.findContextNode(new XRI3Segment("=abc+passport!1$d")),
				graph.findContextNode(new XRI3Segment("=abc+passport!2+number")),
				graph.findContextNode(new XRI3Segment("=abc+passport!2+country")),
				graph.findContextNode(new XRI3Segment("=abc+passport!2$d")),
				graph.findContextNode(new XRI3Segment("=abc+passport()!1$d")),
				graph.findContextNode(new XRI3Segment("=abc+passport()!2$d"))
		};

		XRI3SubSegment[][] contextNodeArcXrisArray = new XRI3SubSegment [][] {
				new XRI3SubSegment[] { new XRI3SubSegment("=abc") },
				new XRI3SubSegment[] { new XRI3SubSegment("+passport") },
				new XRI3SubSegment[] { new XRI3SubSegment("!1"), new XRI3SubSegment("!2"), new XRI3SubSegment("$v"), new XRI3SubSegment("()") },
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

		XRI3SubSegment[][] relationArcXrisArray = new XRI3SubSegment [][] {
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { new XRI3SubSegment("$v"), new XRI3SubSegment("*1"), new XRI3SubSegment("*2") },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { new XRI3SubSegment("$") },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { }
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

			Set<XRI3SubSegment> arcXris = new HashSet<XRI3SubSegment> (Arrays.asList(relationArcXrisArray[i]));
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
		assertEquals(XDIConstants.XRI_CONTEXT, rootContextNode.getXri());
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
		assertEquals(new XRI3SubSegment("()"), abcPassportCContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport()"), abcPassportCContextNode.getXri());
		assertEquals(new XRI3SubSegment("!1"), abcPassportV1ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v!1"), abcPassportV1ContextNode.getXri());
		assertEquals(new XRI3SubSegment("!2"), abcPassportV2ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v!2"), abcPassportV2ContextNode.getXri());
		assertEquals(new XRI3SubSegment("$"), abcPassportV2RelationDollar.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport"), abcPassportV2RelationDollar.getRelationXri());
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
		assertEquals(new XRI3Segment("=abc+passport()!1"), abcPassportC1ContextNode.getXri());
		assertEquals(new XRI3SubSegment("!2"), abcPassportC2ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport()!2"), abcPassportC2ContextNode.getXri());
		assertEquals(new XRI3SubSegment("$d"), abcPassportC1DLiteral.getContextNode().getArcXri());
		assertEquals("2010-11-11T11:11:11Z", abcPassportC1DLiteral.getLiteralData());
		assertEquals(new XRI3SubSegment("$d"), abcPassportC2DLiteral.getContextNode().getArcXri());
		assertEquals("2010-12-22T22:22:22Z", abcPassportC2DLiteral.getLiteralData());
		assertEquals(new XRI3SubSegment("$v"), abcPassportRelationV.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v!2"), abcPassportRelationV.getRelationXri());
		assertEquals(new XRI3SubSegment("*1"), abcPassportRelation1.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!1"), abcPassportRelation1.getRelationXri());
		assertEquals(new XRI3SubSegment("*2"), abcPassportRelation2.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!2"), abcPassportRelation2.getRelationXri());

		assertEquals(rootContextNode.getAllContextNodeCount(), 18);
		assertEquals(rootContextNode.getAllRelationCount(), 4);
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
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("()"));
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

		abcPassport2ContextNode.delete();
		abcPassportC1LiteralD.setLiteralData("2010-03-03T03:03:03Z");
		abcPassportC2LiteralD.getContextNode().delete();
		abcPassportRelation1.setRelationXri(new XRI3Segment("=abc+passport!3"));
		abcPassportRelation2.delete();
	}

	private static void testManipulatedGraph(Graph graph) throws Exception {

		ContextNode rootContextNode = graph.getRootContextNode();
		ContextNode abcContextNode = rootContextNode.getContextNode(new XRI3SubSegment("=abc"));
		ContextNode abcPassportContextNode = abcContextNode.getContextNode(new XRI3SubSegment("+passport"));
		ContextNode abcPassport1ContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassport2ContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("!2"));
		ContextNode abcPassportVContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("$v"));
		ContextNode abcPassportCContextNode = abcPassportContextNode.getContextNode(new XRI3SubSegment("()"));
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

		assertTrue(rootContextNode.containsContextNode(new XRI3SubSegment("=abc")));
		assertTrue(abcContextNode.containsContextNode(new XRI3SubSegment("+passport")));
		assertTrue(abcPassportContextNode.containsContextNode(new XRI3SubSegment("!1")));
		assertFalse(abcPassportContextNode.containsContextNode(new XRI3SubSegment("!2")));	// MANIPULATED
		assertTrue(abcPassportContextNode.containsContextNode(new XRI3SubSegment("$v")));
		assertTrue(abcPassportContextNode.containsContextNode(new XRI3SubSegment("()")));
		assertTrue(abcPassportVContextNode.containsContextNode(new XRI3SubSegment("!1")));
		assertTrue(abcPassportVContextNode.containsContextNode(new XRI3SubSegment("!2")));
		assertTrue(abcPassportV2ContextNode.containsRelation(new XRI3Segment("$")));
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
		assertTrue(abcPassportContextNode.containsRelation(new XRI3Segment("$v")));
		assertTrue(abcPassportContextNode.containsRelation(new XRI3Segment("*1")));
		assertFalse(abcPassportContextNode.containsRelation(new XRI3Segment("*2")));		// MANIPULATED

		ContextNode contextNodesArray[] = new ContextNode [] {
				graph.findContextNode(new XRI3Segment("()")),
				graph.findContextNode(new XRI3Segment("=abc")),
				graph.findContextNode(new XRI3Segment("=abc+passport")),
				graph.findContextNode(new XRI3Segment("=abc+passport!1")),
				graph.findContextNode(new XRI3Segment("=abc+passport!2")),
				graph.findContextNode(new XRI3Segment("=abc+passport$v")),
				graph.findContextNode(new XRI3Segment("=abc+passport()")),
				graph.findContextNode(new XRI3Segment("=abc+passport$v!1")),
				graph.findContextNode(new XRI3Segment("=abc+passport$v!2")),
				graph.findContextNode(new XRI3Segment("=abc+passport()!1")),
				graph.findContextNode(new XRI3Segment("=abc+passport()!2")),
				graph.findContextNode(new XRI3Segment("=abc+passport!1+number")),
				graph.findContextNode(new XRI3Segment("=abc+passport!1+country")),
				graph.findContextNode(new XRI3Segment("=abc+passport!1$d")),
				graph.findContextNode(new XRI3Segment("=abc+passport!2+number")),
				graph.findContextNode(new XRI3Segment("=abc+passport!2+country")),
				graph.findContextNode(new XRI3Segment("=abc+passport!2$d")),
				graph.findContextNode(new XRI3Segment("=abc+passport()!1$d")),
				graph.findContextNode(new XRI3Segment("=abc+passport()!2$d"))
		};

		XRI3SubSegment[][] contextNodeArcXrisArray = new XRI3SubSegment [][] {
				new XRI3SubSegment[] { new XRI3SubSegment("=abc") },
				new XRI3SubSegment[] { new XRI3SubSegment("+passport") },
				new XRI3SubSegment[] { new XRI3SubSegment("!1"), new XRI3SubSegment("$v"), new XRI3SubSegment("()") },	// MANIPULATED
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

		XRI3SubSegment[][] relationArcXrisArray = new XRI3SubSegment [][] {
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { new XRI3SubSegment("$v"), new XRI3SubSegment("*1") },	// MANIPULATED
				new XRI3SubSegment[] { },
				null,	// MANIPULATED
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { new XRI3SubSegment("$") },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				new XRI3SubSegment[] { },
				null,	// MANIPULATED
				null,	// MANIPULATED
				null,	// MANIPULATED
				new XRI3SubSegment[] { },
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

			Set<XRI3SubSegment> arcXris = new HashSet<XRI3SubSegment> (Arrays.asList(relationArcXrisArray[i]));
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
		assertEquals(XDIConstants.XRI_CONTEXT, rootContextNode.getXri());
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
		assertEquals(new XRI3SubSegment("()"), abcPassportCContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport()"), abcPassportCContextNode.getXri());
		assertEquals(new XRI3SubSegment("!1"), abcPassportV1ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v!1"), abcPassportV1ContextNode.getXri());
		assertEquals(new XRI3SubSegment("!2"), abcPassportV2ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v!2"), abcPassportV2ContextNode.getXri());
		assertEquals(new XRI3SubSegment("$"), abcPassportV2RelationDollar.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport"), abcPassportV2RelationDollar.getRelationXri());
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
		assertEquals(new XRI3Segment("=abc+passport()!1"), abcPassportC1ContextNode.getXri());
		assertEquals(new XRI3SubSegment("!2"), abcPassportC2ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport()!2"), abcPassportC2ContextNode.getXri());
		assertEquals(new XRI3SubSegment("$d"), abcPassportC1LiteralD.getContextNode().getArcXri());
		assertEquals("2010-03-03T03:03:03Z", abcPassportC1LiteralD.getLiteralData());	// MANIPULATED
		assertNull(abcPassportC2LiteralD);	// MANIPULATED
		assertNull(abcPassportC2LiteralD);	// MANIPULATED
		assertEquals(new XRI3SubSegment("$v"), abcPassportRelationV.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v!2"), abcPassportRelationV.getRelationXri());
		assertEquals(new XRI3SubSegment("*1"), abcPassportRelation1.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!3"), abcPassportRelation1.getRelationXri());	// MANIPULATED
		assertNull(abcPassportRelation2);	// MANIPULATED
		assertNull(abcPassportRelation2);	// MANIPULATED

		assertEquals(rootContextNode.getAllContextNodeCount(), 13);	// MANIPULATED
		assertEquals(rootContextNode.getAllRelationCount(), 3);	// MANIPULATED
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
