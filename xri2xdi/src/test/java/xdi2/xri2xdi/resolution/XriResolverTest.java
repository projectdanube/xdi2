package xdi2.xri2xdi.resolution;

import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

public class XriResolverTest extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(XriResolverTest.class);

	public void testXriResolver() throws Exception {

		XriResolver xriResolver = new XriResolver();

		XriResolutionResult resolutionResult = null;

		try {

			resolutionResult = xriResolver.resolve("=markus");
		} catch (XriResolutionException ex) {

			if (ex.getCause() instanceof UnknownHostException) {

				log.warn("unknown host: " + ex.getCause().getMessage());
				return;
			}
		}

		if (resolutionResult == null) throw new NullPointerException();

		assertEquals("=!91F2.8153.F600.AE24", resolutionResult.getInumber());
		assertEquals("https://xdi.fullxri.com/=!91F2.8153.F600.AE24/", resolutionResult.getXdiUri());
	}
}
