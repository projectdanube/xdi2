package xdi2.tests.core.io.readers;

import java.io.StringReader;

import junit.framework.TestCase;

import org.json.JSONObject;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.readers.XDIJSONReader;

public class XDIJSONReaderTest extends TestCase {

	public void testXDIJSONReader() throws Exception {

		StringBuilder sbJSON = new StringBuilder();
		sbJSON.append("{\"@!9999!8888$($msg)$(!1234)$!($t)/!\":[\"2011-04-10T22:22:22Z\"],");
		sbJSON.append("\"@!9999!8888$($msg)$(!1234)$do/$add\":[{");
		sbJSON.append("\"=!1111!2222!3$!(+age)/$is+\":[\"+$json$number!\"],");
		sbJSON.append("\"=!1111!2222!3$!(+age)/!\":[\"33\"],");
		sbJSON.append("\"=!1111!2222!3$*(+tel)$!1/!\":[\"+1.206.555.1111\"],");
		sbJSON.append("\"=!1111!2222!3$*(+tel)/+home\":[\"=!1111!2222!3$*(+tel)$!1\"],");
		sbJSON.append("\"=!1111!2222!3$*(+tel)/+work+fax\":[\"=!1111!2222!3$*(+tel)$!2\"]}],");
		sbJSON.append("\"@!9999!8888$($msg)$(!1234)/$do\":[\"=!1111!2222!3$do\"],");
		sbJSON.append("\"(@!9999!8888)/$add\":[\"@!9999!8888$($msg)$(!1234)\"]}");
		String jsonString = sbJSON.toString();

		StringBuilder sbXDI = new StringBuilder();
		sbXDI.append("@!9999!8888$($msg)$(!1234)$!($t)/!/(data:,2011-04-10T22:22:22Z)\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$*(+tel)$!1/!/(data:,+1.206.555.1111))\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$!(+age)/!/(data:,33))\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$*(+tel)/+work+fax/=!1111!2222!3$*(+tel)$!2)\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$*(+tel)/+home/=!1111!2222!3$*(+tel)$!1)\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$!(+age)/$is+/+$json$number!)\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)/$do/=!1111!2222!3$do\n");
		sbXDI.append("(@!9999!8888)/$add/@!9999!8888$($msg)$(!1234)");
		String xdiString = sbXDI.toString();

		XDIJSONReader xdiJSONReader = new XDIJSONReader(null);
		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		StringReader reader = new StringReader(jsonString);
		xdiJSONReader.read(graph, reader);

		String serializedXDI = graph.toString(new MimeType("text/xdi")).trim();

		assertEquals(xdiString, serializedXDI);
	}
}
