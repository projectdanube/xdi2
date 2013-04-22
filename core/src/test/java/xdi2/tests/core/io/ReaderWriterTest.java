package xdi2.tests.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

import junit.framework.TestCase;

import org.junit.Test;

import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.readers.XDIDisplayReader;
import xdi2.core.io.readers.XDIJSONReader;
import xdi2.core.xri3.XDI3Statement;

public class ReaderWriterTest extends TestCase {

	private static String readFromFile(String filename) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(ReaderWriterTest.class.getResourceAsStream(filename), "UTF-8"));
		StringWriter writer = new StringWriter();
		String line;

		while ((line = reader.readLine()) != null) writer.write(line + "\n");

		return writer.getBuffer().toString();
	}

	private static void assertEqualsGraphs(Graph graph1, Graph graph2) throws Exception {

		assertEquals(graph1.getRootContextNode().getAllStatementCount(), graph2.getRootContextNode().getAllStatementCount());
		assertEquals(graph1.getRootContextNode().getAllContextNodeCount(), graph2.getRootContextNode().getAllContextNodeCount());
		assertEquals(graph1.getRootContextNode().getAllRelationCount(), graph2.getRootContextNode().getAllRelationCount());
		assertEquals(graph1.getRootContextNode().getAllLiteralCount(), graph2.getRootContextNode().getAllLiteralCount());

		Iterator<Statement> s1 = graph1.getRootContextNode().getAllStatements();
		Iterator<Statement> s2 = graph2.getRootContextNode().getAllStatements();

		while (s1.hasNext()) { XDI3Statement s = s1.next().getXri(); assertTrue(s.toString(), graph2.containsStatement(s)); }
		while (s2.hasNext()) { XDI3Statement s = s2.next().getXri(); assertTrue(s.toString(), graph1.containsStatement(s)); }

		assertEquals(graph1, graph2);
	}

	@Test
	public void testXDIDisplayReader() throws Exception {

		String xdiDisplayString = readFromFile("readerwriter.xdi");
		String xdiJsonString = readFromFile("readerwriter.json");

		Graph graph1 = MemoryGraphFactory.getInstance().openGraph();
		XDIDisplayReader xdiDisplayReader = new XDIDisplayReader(null);
		xdiDisplayReader.read(graph1, new StringReader(xdiDisplayString));

		Graph graph2 = MemoryGraphFactory.getInstance().openGraph();
		XDIJSONReader xdiJsonReader = new XDIJSONReader(null);
		xdiJsonReader.read(graph2, new StringReader(xdiJsonString));
		
		assertEqualsGraphs(graph1, graph2);

		MimeType[] mimeTypes = new MimeType[] {

				new MimeType("text/xdi;ordered=1"),
				new MimeType("text/xdi;ordered=1;implied=1"),
				new MimeType("text/xdi;ordered=1;inner=1"),
				new MimeType("text/xdi;ordered=1;implied=0;inner=0"),
				new MimeType("text/xdi;ordered=1;implied=0;inner=1"),
				new MimeType("text/xdi;ordered=1;implied=1;inner=0"),
				new MimeType("text/xdi;ordered=1;implied=1;inner=1"),
				new MimeType("application/xdi+json;ordered=1"),
				new MimeType("application/xdi+json;ordered=1;implied=1"),
				new MimeType("application/xdi+json;ordered=1;inner=1"),
				new MimeType("application/xdi+json;ordered=1;implied=0;inner=0"),
				new MimeType("application/xdi+json;ordered=1;implied=0;inner=1"),
				new MimeType("application/xdi+json;ordered=1;implied=1;inner=0"),
				new MimeType("application/xdi+json;ordered=1;implied=1;inner=1")
		};

		for (int i=0; i<mimeTypes.length; i++) {

			assertEquals(graph1.toString(mimeTypes[i]), graph2.toString(mimeTypes[i]));

			for (int ii=0; ii<mimeTypes.length; ii++) {

				Graph graph1a = MemoryGraphFactory.getInstance().openGraph();
				XDIReaderRegistry.forMimeType(mimeTypes[i]).read(graph1a, new StringReader(graph1.toString(mimeTypes[i])));

				Graph graph2a = MemoryGraphFactory.getInstance().openGraph();
				XDIReaderRegistry.forMimeType(mimeTypes[ii]).read(graph2a, new StringReader(graph2.toString(mimeTypes[ii])));

				assertEqualsGraphs(graph1a, graph2a);
				assertEqualsGraphs(graph1a, graph1);
				assertEqualsGraphs(graph2a, graph2);
			}
		}
	}
}
