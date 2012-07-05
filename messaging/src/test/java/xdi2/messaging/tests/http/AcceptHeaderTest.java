package xdi2.messaging.tests.http;

import junit.framework.TestCase;
import xdi2.core.io.readers.XDIJSONReader;
import xdi2.core.io.readers.XDIStatementsReader;
import xdi2.messaging.http.AcceptHeader;

public class AcceptHeaderTest extends TestCase {

	public void testAcceptHeader() throws Exception {

		AcceptHeader acceptHeader;

		acceptHeader = AcceptHeader.create(XDIJSONReader.MIME_TYPES[0]);
		assertEquals(acceptHeader.toString(), "application/xdi+json;q=1,application/xdi+json;contexts=0;q=0.5,application/xdi+json;contexts=1;q=0.5,text/xdi;contexts=0;q=0.5,text/xdi;contexts=1;q=0.5,text/xdi;q=0.5");
		assertEquals(acceptHeader.bestMimeType(), XDIJSONReader.MIME_TYPES[0]);

		acceptHeader = AcceptHeader.create(XDIStatementsReader.MIME_TYPES[0]);
		assertEquals(acceptHeader.toString(), "text/xdi;q=1,application/xdi+json;contexts=0;q=0.5,application/xdi+json;contexts=1;q=0.5,application/xdi+json;q=0.5,text/xdi;contexts=0;q=0.5,text/xdi;contexts=1;q=0.5");
		assertEquals(acceptHeader.bestMimeType(), XDIStatementsReader.MIME_TYPES[0]);

		acceptHeader = AcceptHeader.create(null);
		assertEquals(acceptHeader.toString(), "application/xdi+json;contexts=0;q=0.5,application/xdi+json;contexts=1;q=0.5,application/xdi+json;q=0.5,text/xdi;contexts=0;q=0.5,text/xdi;contexts=1;q=0.5,text/xdi;q=0.5");
	}
}
