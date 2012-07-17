package xdi2.messaging.tests.http;

import junit.framework.TestCase;
import xdi2.core.io.MimeType;
import xdi2.core.io.readers.XDIJSONReader;
import xdi2.core.io.readers.XDIStatementsReader;
import xdi2.messaging.http.AcceptHeader;

public class AcceptHeaderTest extends TestCase {

	public void testCreateAcceptHeader() throws Exception {

		AcceptHeader acceptHeader;

		acceptHeader = AcceptHeader.create(XDIJSONReader.MIME_TYPES[0]);
		assertEquals(acceptHeader.toString(), "application/xdi+json;q=1,application/xdi+json;contexts=0;q=0.5,application/xdi+json;contexts=1;q=0.5,text/xdi;contexts=0;q=0.5,text/xdi;contexts=1;q=0.5,text/xdi;q=0.5");
		assertEquals(acceptHeader.bestMimeType(true, false), XDIJSONReader.MIME_TYPES[0]);

		acceptHeader = AcceptHeader.create(XDIStatementsReader.MIME_TYPES[0]);
		assertEquals(acceptHeader.toString(), "text/xdi;q=1,application/xdi+json;contexts=0;q=0.5,application/xdi+json;contexts=1;q=0.5,application/xdi+json;q=0.5,text/xdi;contexts=0;q=0.5,text/xdi;contexts=1;q=0.5");
		assertEquals(acceptHeader.bestMimeType(true, false), XDIStatementsReader.MIME_TYPES[0]);

		acceptHeader = AcceptHeader.create(null);
		assertEquals(acceptHeader.toString(), "application/xdi+json;contexts=0;q=0.5,application/xdi+json;contexts=1;q=0.5,application/xdi+json;q=0.5,text/xdi;contexts=0;q=0.5,text/xdi;contexts=1;q=0.5,text/xdi;q=0.5");
	}

	public void testParseAcceptHeader() throws Exception {

		AcceptHeader acceptHeader;

		acceptHeader = AcceptHeader.parse("application/xdi+json;q=1,application/xdi+json;contexts=0;q=0.5,application/xdi+json;contexts=1;q=0.5,text/xdi;contexts=0;q=0.5,text/xdi;contexts=1;q=0.5,text/xdi;q=0.5");
		assertEquals(acceptHeader.bestMimeType(true, false), XDIJSONReader.MIME_TYPES[0]);

		acceptHeader = AcceptHeader.parse("text/xdi;q=1,application/xdi+json;contexts=0;q=0.5,application/xdi+json;contexts=1;q=0.5,application/xdi+json;q=0.5,text/xdi;contexts=0;q=0.5,text/xdi;contexts=1;q=0.5");
		assertEquals(acceptHeader.bestMimeType(true, false), XDIStatementsReader.MIME_TYPES[0]);

		acceptHeader = AcceptHeader.parse("application/xml;q=1.1,text/html,text/xdi;q=0.7,*/*;q=0.8");
		assertEquals(acceptHeader.bestMimeType(false, false), new MimeType("application/xml"));
		assertEquals(acceptHeader.bestMimeType(true, false), new MimeType("text/xdi"));
		assertEquals(acceptHeader.bestMimeType(false, true), new MimeType("text/html"));
	}
}
