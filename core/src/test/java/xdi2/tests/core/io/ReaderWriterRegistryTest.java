package xdi2.tests.core.io;

import junit.framework.TestCase;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriterRegistry;

public class ReaderWriterRegistryTest extends TestCase {

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
}
