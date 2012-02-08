package xdi2.tests;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;
import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.Relation;
import xdi2.constants.XDIConstants;
import xdi2.io.XDIReader;
import xdi2.io.XDIReaderRegistry;
import xdi2.io.XDIWriter;
import xdi2.io.XDIWriterRegistry;
import xdi2.xri3.impl.XRI3Segment;
import xdi2.xri3.impl.XRI3SubSegment;

public abstract class BasicTest extends TestCase {

	protected abstract Graph openNewGraph(String id) throws IOException;

	public void testMakeGraph() throws Exception {

		Graph graph1 = this.openNewGraph("1");
		
		makeGraph(graph1);
		testGraph(graph1);
	}

	public void testReadJson() throws Exception {

		Graph graph2 = this.openNewGraph("2");

		XDIReader reader = XDIReaderRegistry.forFormat("XDI/JSON");

		reader.read(graph2, new FileReader("test.json"), null);
		testGraph(graph2);
	}

	public void testWriteJson() throws Exception {

		Graph graph3 = this.openNewGraph("3");
		Graph graph4 = this.openNewGraph("4");

		XDIReader reader = XDIReaderRegistry.forFormat("XDI/JSON");
		XDIWriter writer = XDIWriterRegistry.forFormat("XDI/JSON");
		StringWriter buffer = new StringWriter();

		makeGraph(graph3);
		writer.write(graph3, buffer, null);
		reader.read(graph4, new StringReader(buffer.getBuffer().toString()), null);

		testGraph(graph4);
		testGraphsEqual(graph3, graph4);
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
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.createRelation(new XRI3SubSegment("$"), new XRI3Segment("=abc+passport"));
		Literal abcPassport1LiteralNumber = abcPassport1ContextNode.createLiteral(new XRI3SubSegment("+number"), "987654321");
		Literal abcPassport1LiteralCountry = abcPassport1ContextNode.createLiteral(new XRI3SubSegment("+country"), "Canada");
		Literal abcPassport1LiteralD = abcPassport1ContextNode.createLiteral(new XRI3SubSegment("$d"), "2005-01-01T00:00:00Z");
		Literal abcPassport2LiteralNumber = abcPassport2ContextNode.createLiteral(new XRI3SubSegment("+number"), "123456789");
		Literal abcPassport2LiteralCountry = abcPassport2ContextNode.createLiteral(new XRI3SubSegment("+country"), "New Zealand");
		Literal abcPassport2LiteralD = abcPassport2ContextNode.createLiteral(new XRI3SubSegment("$d"), "2010-10-01T00:00:00Z");
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.createContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.createContextNode(new XRI3SubSegment("!2"));
		Literal abcPassportC1LiteralD = abcPassportC1ContextNode.createLiteral(new XRI3SubSegment("$d"), "2010-11-11T11:11:11Z");
		Literal abcPassportC2LiteralD = abcPassportC2ContextNode.createLiteral(new XRI3SubSegment("$d"), "2010-12-22T22:22:22Z");
		Relation abcPassportRelationV = abcPassportContextNode.createRelation(new XRI3SubSegment("$v"), new XRI3Segment("=abc+passport$v!2"));
		Relation abcPassportRelation1 = abcPassportContextNode.createRelation(new XRI3SubSegment("*1"), new XRI3Segment("=abc+passport!1"));
		Relation abcPassportRelation2 = abcPassportContextNode.createRelation(new XRI3SubSegment("*2"), new XRI3Segment("=abc+passport!2"));
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
		Relation abcPassportV2RelationDollar = abcPassportV2ContextNode.getRelation(new XRI3SubSegment("$"));
		Literal abcPassport1LiteralNumber = abcPassport1ContextNode.getLiteral(new XRI3SubSegment("+number"));
		Literal abcPassport1LiteralCountry = abcPassport1ContextNode.getLiteral(new XRI3SubSegment("+country"));
		Literal abcPassport1LiteralD = abcPassport1ContextNode.getLiteral(new XRI3SubSegment("$d"));
		Literal abcPassport2LiteralNumber = abcPassport2ContextNode.getLiteral(new XRI3SubSegment("+number"));
		Literal abcPassport2LiteralCountry = abcPassport2ContextNode.getLiteral(new XRI3SubSegment("+country"));
		Literal abcPassport2LiteralD = abcPassport2ContextNode.getLiteral(new XRI3SubSegment("$d"));
		ContextNode abcPassportC1ContextNode = abcPassportCContextNode.getContextNode(new XRI3SubSegment("!1"));
		ContextNode abcPassportC2ContextNode = abcPassportCContextNode.getContextNode(new XRI3SubSegment("!2"));
		Literal abcPassportC1LiteralD = abcPassportC1ContextNode.getLiteral(new XRI3SubSegment("$d"));
		Literal abcPassportC2LiteralD = abcPassportC2ContextNode.getLiteral(new XRI3SubSegment("$d"));
		Relation abcPassportRelationV = abcPassportContextNode.getRelation(new XRI3SubSegment("$v"));
		Relation abcPassportRelation1 = abcPassportContextNode.getRelation(new XRI3SubSegment("*1"));
		Relation abcPassportRelation2 = abcPassportContextNode.getRelation(new XRI3SubSegment("*2"));

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
		assertEquals(new XRI3SubSegment("+number"), abcPassport1LiteralNumber.getArcXri());
		assertEquals("987654321", abcPassport1LiteralNumber.getLiteralData());
		assertEquals(new XRI3SubSegment("+country"), abcPassport1LiteralCountry.getArcXri());
		assertEquals("Canada", abcPassport1LiteralCountry.getLiteralData());
		assertEquals(new XRI3SubSegment("$d"), abcPassport1LiteralD.getArcXri());
		assertEquals("2005-01-01T00:00:00Z", abcPassport1LiteralD.getLiteralData());
		assertEquals(new XRI3SubSegment("+number"), abcPassport2LiteralNumber.getArcXri());
		assertEquals("123456789", abcPassport2LiteralNumber.getLiteralData());
		assertEquals(new XRI3SubSegment("+country"), abcPassport2LiteralCountry.getArcXri());
		assertEquals("New Zealand", abcPassport2LiteralCountry.getLiteralData());
		assertEquals(new XRI3SubSegment("$d"), abcPassport2LiteralD.getArcXri());
		assertEquals("2010-10-01T00:00:00Z", abcPassport2LiteralD.getLiteralData());
		assertEquals(new XRI3SubSegment("!1"), abcPassportC1ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport()!1"), abcPassportC1ContextNode.getXri());
		assertEquals(new XRI3SubSegment("!2"), abcPassportC2ContextNode.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport()!2"), abcPassportC2ContextNode.getXri());
		assertEquals(new XRI3SubSegment("$d"), abcPassportC1LiteralD.getArcXri());
		assertEquals("2010-11-11T11:11:11Z", abcPassportC1LiteralD.getLiteralData());
		assertEquals(new XRI3SubSegment("$d"), abcPassportC2LiteralD.getArcXri());
		assertEquals("2010-12-22T22:22:22Z", abcPassportC2LiteralD.getLiteralData());
		assertEquals(new XRI3SubSegment("$v"), abcPassportRelationV.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport$v!2"), abcPassportRelationV.getRelationXri());
		assertEquals(new XRI3SubSegment("*1"), abcPassportRelation1.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!1"), abcPassportRelation1.getRelationXri());
		assertEquals(new XRI3SubSegment("*2"), abcPassportRelation2.getArcXri());
		assertEquals(new XRI3Segment("=abc+passport!2"), abcPassportRelation2.getRelationXri());

		assertEquals(rootContextNode.getAllContextNodeCount(), 10);
		assertEquals(rootContextNode.getAllRelationCount(), 4);
		assertEquals(rootContextNode.getAllLiteralCount(), 8);
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
