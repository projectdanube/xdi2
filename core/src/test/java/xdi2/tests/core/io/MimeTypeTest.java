package xdi2.tests.core.io;

import java.util.Properties;

import junit.framework.TestCase;
import xdi2.core.io.MimeType;

public class MimeTypeTest extends TestCase {

	public void testMimeTypes() throws Exception {

		Properties parameters3 = new Properties();
		parameters3.setProperty("contexts", "0");
		parameters3.setProperty("q", "0.5");

		Properties parameters4 = new Properties();
		parameters4.setProperty("q", "0.5");

		Properties parameters5 = new Properties();
		parameters5.setProperty("contexts", "0");
		parameters5.setProperty("q", "0.5");

		MimeType[] mimeTypes = new MimeType[] {
				new MimeType("application/xdi+json;contexts=0;q=0.5", null),
				new MimeType("application/xdi+json;q=0.5;contexts=0", null),
				new MimeType("application/xdi+json", parameters3),
				new MimeType("application/xdi+json;contexts=0", parameters4),
				new MimeType("application/xdi+json;contexts=1", parameters5)
		};

		for (int i=0; i<mimeTypes.length; i++) { 

			assertEquals(mimeTypes[i], mimeTypes[i+1<mimeTypes.length ? i+1 : 0]);
			assertEquals(mimeTypes[i].getMimeType(), "application/xdi+json");
			assertEquals(mimeTypes[i].getParameterValue("contexts"), "0");
			assertEquals(mimeTypes[i].getParameterValue("q"), "0.5");
		}

		MimeType otherMimeType1 = new MimeType("application/xdi+json;q=1;contexts=0", null);
		MimeType otherMimeType2 = new MimeType("application/xdi+json;contexts=1;q=0.5", null);
		MimeType otherMimeType3 = new MimeType("application/xdi+json", null);

		assertNull(otherMimeType1.mimeTypeWithoutQuality().getParameterValue("q"));
		assertNotNull(otherMimeType1.mimeTypeWithoutQuality().getParameterValue("contexts"));
		assertNull(otherMimeType2.mimeTypeWithoutQuality().getParameterValue("q"));
		assertNotNull(otherMimeType2.mimeTypeWithoutQuality().getParameterValue("contexts"));
		
		for (int i=0; i<mimeTypes.length; i++) { 

			assertFalse(mimeTypes[i].equals(otherMimeType1));
			assertFalse(mimeTypes[i].equals(otherMimeType2));
			assertFalse(mimeTypes[i].equals(otherMimeType3));
			assertTrue(mimeTypes[i].mimeTypeWithoutParameters().equals(otherMimeType1.mimeTypeWithoutParameters()));
			assertTrue(mimeTypes[i].mimeTypeWithoutParameters().equals(otherMimeType2.mimeTypeWithoutParameters()));
			assertTrue(mimeTypes[i].mimeTypeWithoutParameters().equals(otherMimeType3.mimeTypeWithoutParameters()));
		}
	}
}
