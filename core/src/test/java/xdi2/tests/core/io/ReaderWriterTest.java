package xdi2.tests.core.io;

import java.io.StringReader;

import junit.framework.TestCase;

import org.junit.Test;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.readers.XDIDisplayReader;
import xdi2.core.io.readers.XDIJSONReader;

public class ReaderWriterTest extends TestCase {

	@Test
	public void testXDIDisplayReader() throws Exception {

		StringBuilder sbXdiDisplay = new StringBuilder();
		sbXdiDisplay.append("(@!9999!8888)/$add/@!9999!8888$($msg)$(!1234)\n");
		sbXdiDisplay.append("@!9999!8888$($msg)$(!1234)$!($t)/!/(data:,2011-04-10T22:22:22Z)\n");
		sbXdiDisplay.append("@!9999!8888$($msg)$(!1234)/$do/=!1111!2222!3$do\n");
		sbXdiDisplay.append("@!9999!8888$($msg)$(!1234)$do/$add/=markus\n");
		sbXdiDisplay.append("@!9999!8888$($msg)$(!1234)$do/$add/(http://example.com)\n");
		sbXdiDisplay.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$*(+tel)$!1/!/(data:,+1.206.555.1111))\n");
		sbXdiDisplay.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$*(+tel)/+home/=!1111!2222!3$*(+tel)$!1)\n");
		sbXdiDisplay.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$*(+tel)/+work+fax/=!1111!2222!3$*(+tel)$!2)\n");
		sbXdiDisplay.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$!(+age)/!/(data:,33))\n");
		sbXdiDisplay.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$!(+age)/$is+/+$json$number!)");
		String xdiDisplayString = sbXdiDisplay.toString();

		StringBuilder sbXdiJson = new StringBuilder();
		sbXdiJson.append("{\"@!9999!8888$($msg)$(!1234)$!($t)/!\":[\"2011-04-10T22:22:22Z\"],");
		sbXdiJson.append("\"@!9999!8888$($msg)$(!1234)$do/$add\":[");
		sbXdiJson.append("\"=markus\",");
		sbXdiJson.append("\"(http://example.com)\",{");
		sbXdiJson.append("\"=!1111!2222!3$!(+age)/$is+\":[\"+$json$number!\"],");
		sbXdiJson.append("\"=!1111!2222!3$!(+age)/!\":[\"33\"],");
		sbXdiJson.append("\"=!1111!2222!3$*(+tel)$!1/!\":[\"+1.206.555.1111\"],");
		sbXdiJson.append("\"=!1111!2222!3$*(+tel)/+home\":[\"=!1111!2222!3$*(+tel)$!1\"],");
		sbXdiJson.append("\"=!1111!2222!3$*(+tel)/+work+fax\":[\"=!1111!2222!3$*(+tel)$!2\"]}],");
		sbXdiJson.append("\"@!9999!8888$($msg)$(!1234)/$do\":[\"=!1111!2222!3$do\"],");
		sbXdiJson.append("\"(@!9999!8888)/$add\":[\"@!9999!8888$($msg)$(!1234)\"]}");
		String xdiJsonString = sbXdiJson.toString();

		Graph graph1 = MemoryGraphFactory.getInstance().openGraph();
		XDIDisplayReader xdiDisplayReader = new XDIDisplayReader(null);
		xdiDisplayReader.read(graph1, new StringReader(xdiDisplayString));

		Graph graph2 = MemoryGraphFactory.getInstance().openGraph();
		XDIJSONReader xdiJsonReader = new XDIJSONReader(null);
		xdiJsonReader.read(graph2, new StringReader(xdiJsonString));

		MimeType xdiDisplayMimeType = new MimeType("text/xdi;contexts=1;ordered=1");
		MimeType xdiJsonMimeType = new MimeType("application/xdi+json");

		assertEquals(graph1, graph2);
		assertEquals(graph1.toString(xdiDisplayMimeType), graph2.toString(xdiDisplayMimeType));
		assertEquals(graph1.toString(xdiJsonMimeType), graph2.toString(xdiJsonMimeType));
	}
}
