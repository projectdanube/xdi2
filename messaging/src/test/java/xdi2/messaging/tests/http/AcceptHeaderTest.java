package xdi2.messaging.tests.http;

import junit.framework.TestCase;
import xdi2.core.io.XDIJSONReader;
import xdi2.core.io.XDIStatementsReader;
import xdi2.messaging.http.AcceptHeader;

public class AcceptHeaderTest extends TestCase {

	public void testAcceptHeader() throws Exception {

		AcceptHeader acceptHeader;

		acceptHeader = AcceptHeader.create(XDIJSONReader.MIME_TYPE);
		assertEquals(acceptHeader.toString(), "application/xdi+json;q=1.0,text/plain;q=0.5");

		acceptHeader = AcceptHeader.create(XDIStatementsReader.MIME_TYPE);
		assertEquals(acceptHeader.toString(), "text/plain;q=1.0,application/xdi+json;q=0.5");

		acceptHeader = AcceptHeader.create(null);
		assertEquals(acceptHeader.toString(), "text/plain;q=0.5,application/xdi+json;q=0.5");
	}
}
