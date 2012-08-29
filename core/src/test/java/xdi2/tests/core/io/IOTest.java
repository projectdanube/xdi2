package xdi2.tests.core.io;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import org.json.JSONObject;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.readers.XDIJSONReader;
import xdi2.core.io.writers.XDIJSONWriter;

public class IOTest extends TestCase {

	public void testDefaults() throws Exception {

		assertNotNull(XDIReaderRegistry.getDefault());
		assertNotNull(XDIWriterRegistry.getDefault());
	}

	public void testReaders() throws Exception {

		String[] formats = new String[] { "XDI/JSON", "XDI DISPLAY" };
		String[] fileExtensions = new String[] { "json", "xdi" };
		MimeType[] mimeTypes = new MimeType[] { new MimeType("application/xdi+json"), new MimeType("application/xdi+json;contexts=0"), new MimeType("application/xdi+json;contexts=1"), new MimeType("text/xdi"), new MimeType("text/xdi;contexts=0"), new MimeType("text/xdi;contexts=1") };

		for (String format : formats) assertTrue(XDIReaderRegistry.forFormat(format, null).supportsFormat(format));
		for (String fileExtension : fileExtensions) assertTrue(XDIReaderRegistry.forFileExtension(fileExtension, null).supportsFileExtension(fileExtension));
		for (MimeType mimeType : mimeTypes) assertTrue(XDIReaderRegistry.forMimeType(mimeType).supportsMimeType(mimeType));
	}

	public void testWriters() throws Exception {

		String[] formats = new String[] { "XDI/JSON", "XDI DISPLAY", "KEYVALUE" };
		String[] fileExtensions = new String[] { "json", "xdi" };
		MimeType[] mimeTypes = new MimeType[] { new MimeType("application/xdi+json"), new MimeType("application/xdi+json;contexts=0"), new MimeType("application/xdi+json;contexts=1"), new MimeType("text/xdi"), new MimeType("text/xdi;contexts=0"), new MimeType("text/xdi;contexts=1") };

		for (String format : formats) assertTrue(XDIWriterRegistry.forFormat(format, null).supportsFormat(format));
		for (String fileExtension : fileExtensions) assertTrue(XDIWriterRegistry.forFileExtension(fileExtension, null).supportsFileExtension(fileExtension));
		for (MimeType mimeType : mimeTypes) assertTrue(XDIWriterRegistry.forMimeType(mimeType).supportsMimeType(mimeType));
	}
	
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
		
		System.out.println("Input JSON:\n" + new JSONObject(jsonString).toString(2));
		
		System.out.println("Output XDI:\n" + serializedXDI);
	 	
		assertEquals(xdiString, serializedXDI);
	}
	
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
		
		Properties properties = new Properties();
		properties.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "2");
		XDIJSONWriter xdiJSONWriter = new XDIJSONWriter(properties);
		Graph graph = (new MemoryGraphFactory()).parseGraph(xdiString);
		StringWriter writer = new StringWriter();
		Writer out = xdiJSONWriter.write(graph, writer);
		
		String serializedJSON = out.toString();
		
		System.out.println("Input XDI:\n" + xdiString);
		
		System.out.println("Output JSON:\n" + serializedJSON);
		
		assertEquals(jsonString, serializedJSON.replaceAll("[ \n]", ""));
	}
}
