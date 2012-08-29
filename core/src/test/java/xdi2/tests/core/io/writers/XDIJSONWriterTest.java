package xdi2.tests.core.io.writers;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.writers.XDIJSONWriter;

public class XDIJSONWriterTest extends TestCase {
	
	public void testXDIJSONWriter() throws Exception {
		
		StringBuilder sbXDI = new StringBuilder();
		sbXDI.append("(@!9999!8888)/$add/@!9999!8888$($msg)$(!1234)\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)$!($t)/!/(data:,2011-04-10T22:22:22Z)\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)/$do/=!1111!2222!3$do\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)$do/$add/=markus\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)$do/$add/(http://example.com)\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$*(+tel)$!1/!/(data:,+1.206.555.1111))\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$*(+tel)/+home/=!1111!2222!3$*(+tel)$!1)\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$*(+tel)/+work+fax/=!1111!2222!3$*(+tel)$!2)\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$!(+age)/!/(data:,33))\n");
		sbXDI.append("@!9999!8888$($msg)$(!1234)$do/$add/(=!1111!2222!3$!(+age)/$is+/+$json$number!)");
		String xdiString = sbXDI.toString();

		StringBuilder sbJSON = new StringBuilder();
		sbJSON.append("{\"(@!9999!8888)/$add\":[\"@!9999!8888$($msg)$(!1234)\"],");
		sbJSON.append("\"@!9999!8888$($msg)$(!1234)$!($t)/!\":[\"2011-04-10T22:22:22Z\"],");
		sbJSON.append("\"@!9999!8888$($msg)$(!1234)$do/$add\":[");
		sbJSON.append("\"=markus\",");
		sbJSON.append("\"(http://example.com)\",{");
		sbJSON.append("\"=!1111!2222!3$!(+age)/!\":[\"33\"],");
		sbJSON.append("\"=!1111!2222!3$!(+age)/$is+\":[\"+$json$number!\"],");
		sbJSON.append("\"=!1111!2222!3$*(+tel)$!1/!\":[\"+1.206.555.1111\"],");
		sbJSON.append("\"=!1111!2222!3$*(+tel)/+home\":[\"=!1111!2222!3$*(+tel)$!1\"],");
		sbJSON.append("\"=!1111!2222!3$*(+tel)/+work+fax\":[\"=!1111!2222!3$*(+tel)$!2\"]}],");
		sbJSON.append("\"@!9999!8888$($msg)$(!1234)/$do\":[\"=!1111!2222!3$do\"]}");
		String jsonString = sbJSON.toString();
		
		Properties params = new Properties();
		params.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "2");
		XDIJSONWriter xdiJSONWriter = new XDIJSONWriter(params);
		Graph graph = (new MemoryGraphFactory()).parseGraph(xdiString);
		StringWriter writer = new StringWriter();
		Writer out = xdiJSONWriter.write(graph, writer);
		
		String serializedJSON = out.toString();
		
		System.out.println("Input XDI:\n" + xdiString);
		
		System.out.println("Output JSON:\n" + serializedJSON);
		
		assertEquals(jsonString, serializedJSON.replaceAll("[ \n]", ""));
	}
}
