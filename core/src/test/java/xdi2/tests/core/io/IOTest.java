package xdi2.tests.core.io;

import junit.framework.TestCase;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriterRegistry;

public class IOTest extends TestCase {

	public void testReaders() throws Exception {

		String[] formats = new String[] { "XDI/JSON", "STATEMENTS" };
		String[] fileExtensions = new String[] { "json", "xdi" };
		MimeType[] mimeTypes = new MimeType[] { new MimeType("application/xdi+json"), new MimeType("application/xdi+json;contexts=0"), new MimeType("application/xdi+json;contexts=1"), new MimeType("application/xdi+json"), new MimeType("application/xdi+json;contexts=0"), new MimeType("application/xdi+json;contexts=1") };

		for (String format : formats) assertTrue(XDIReaderRegistry.forFormat(format).supportsFormat(format));
		for (String fileExtension : fileExtensions) assertTrue(XDIReaderRegistry.forFileExtension(fileExtension).supportsFileExtension(fileExtension));
		for (MimeType mimeType : mimeTypes) assertTrue(XDIReaderRegistry.forMimeType(mimeType).supportsMimeType(mimeType));
	}

	public void testWriters() throws Exception {

		String[] formats = new String[] { "XDI/JSON", "XDI/JSON_WITH_CONTEXT_STATEMENTS", "STATEMENTS", "STATEMENTS_WITH_CONTEXT_STATEMENTS", "STATEMENTS_HTML", "KEYVALUE" };
		String[] fileExtensions = new String[] { "json", "xdi", "html" };
		MimeType[] mimeTypes = new MimeType[] { new MimeType("application/xdi+json"), new MimeType("application/xdi+json;contexts=0"), new MimeType("application/xdi+json;contexts=1"), new MimeType("application/xdi+json"), new MimeType("application/xdi+json;contexts=0"), new MimeType("application/xdi+json;contexts=1"), new MimeType("text/html") };

		for (String format : formats) assertTrue(XDIWriterRegistry.forFormat(format).supportsFormat(format));
		for (String fileExtension : fileExtensions) assertTrue(XDIWriterRegistry.forFileExtension(fileExtension).supportsFileExtension(fileExtension));
		for (MimeType mimeType : mimeTypes) assertTrue(XDIWriterRegistry.forMimeType(mimeType).supportsMimeType(mimeType));
	}
}
