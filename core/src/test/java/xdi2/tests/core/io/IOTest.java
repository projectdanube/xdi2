package xdi2.tests.core.io;

import junit.framework.TestCase;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriterRegistry;

public class IOTest extends TestCase {

	public void testReaders() throws Exception {

		String[] formats = new String[] { "XDI/JSON", "STATEMENTS" };
		String[] mimeTypes = new String[] { "application/xdi+json", "text/plain" };

		for (String format : formats) assertEquals(XDIReaderRegistry.forFormat(format).getFormat(), format);
		for (String mimeType : mimeTypes) assertEquals(XDIReaderRegistry.forMimeType(mimeType).getMimeType(), mimeType);
	}

	public void testWriters() throws Exception {

		String[] formats = new String[] { "XDI/JSON", "STATEMENTS" };
		String[] mimeTypes = new String[] { "application/xdi+json", "text/plain" };

		for (String format : formats) assertEquals(XDIWriterRegistry.forFormat(format).getFormat(), format);
		for (String mimeType : mimeTypes) assertEquals(XDIWriterRegistry.forMimeType(mimeType).getMimeType(), mimeType);
	}
}
