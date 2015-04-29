package xdi2.messaging.tests.http;

import junit.framework.TestCase;
import xdi2.core.io.MimeType;
import xdi2.core.io.readers.XDIDisplayReader;
import xdi2.core.io.readers.XDIJSONQuadReader;
import xdi2.messaging.http.AcceptHeader;

public class AcceptHeaderTest extends TestCase {

	public void testCreateAcceptHeader() throws Exception {

		AcceptHeader acceptHeader;

		acceptHeader = AcceptHeader.create(XDIJSONQuadReader.MIME_TYPE);
		assertEquals(acceptHeader.toString(), "application/xdi+json;q=1,text/xdi;q=0.5");
		assertEquals(acceptHeader.bestMimeType(true, false), XDIJSONQuadReader.MIME_TYPE);

		acceptHeader = AcceptHeader.create(XDIDisplayReader.MIME_TYPE);
		assertEquals(acceptHeader.toString(), "text/xdi;q=1,application/xdi+json;q=0.5");
		assertEquals(acceptHeader.bestMimeType(true, false), XDIDisplayReader.MIME_TYPE);

		acceptHeader = AcceptHeader.create(null);
		assertEquals(acceptHeader.toString(), "application/xdi+json;q=0.5,text/xdi;q=0.5");
	}

	public void testParseAcceptHeader() throws Exception {

		AcceptHeader acceptHeader;

		acceptHeader = AcceptHeader.parse("application/xdi+json;q=1,application/xdi+json;contexts=0;q=0.5,application/xdi+json;contexts=1;q=0.5,text/xdi;contexts=0;q=0.5,text/xdi;contexts=1;q=0.5,text/xdi;q=0.5");
		assertEquals(acceptHeader.getMimeTypes().size(), 6);
		assertEquals(acceptHeader.bestMimeType(true, false), XDIJSONQuadReader.MIME_TYPE);

		acceptHeader = AcceptHeader.parse("text/xdi;q=1,application/xdi+json;contexts=0;q=0.5,application/xdi+json;contexts=1;q=0.5,application/xdi+json;q=0.5,text/xdi;contexts=0;q=0.5,text/xdi;contexts=1;q=0.5");
		assertEquals(acceptHeader.getMimeTypes().size(), 6);
		assertEquals(acceptHeader.bestMimeType(true, false), XDIDisplayReader.MIME_TYPE);

		acceptHeader = AcceptHeader.parse("application/xml;q=1.1,text/html,text/xdi;q=0.7,*/*;q=0.8");
		assertEquals(acceptHeader.getMimeTypes().size(), 4);
		assertEquals(acceptHeader.bestMimeType(false, false), new MimeType("application/xml"));
		assertEquals(acceptHeader.bestMimeType(true, false), new MimeType("text/xdi"));
		assertEquals(acceptHeader.bestMimeType(false, true), new MimeType("text/html"));
	}

	public void testAcceptHeaderParameters() throws Exception {

		AcceptHeader acceptHeader;

		acceptHeader = AcceptHeader.parse("application/xdi+json;contexts=1;ordered=0");
		assertNotNull(acceptHeader.bestMimeType(true, true));
		assertNotNull(acceptHeader.bestMimeType(true, true));
		assertEquals(acceptHeader.bestMimeType(true, true).getParameterValue("contexts"), "1");
		assertEquals(acceptHeader.bestMimeType(true, true).getParameterValue("ordered"), "0");
	}
}
