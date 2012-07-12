package xdi2.xri2xdi.resolution;

import junit.framework.TestCase;

public class XriResolverTest extends TestCase {

	public void testXriResolver() throws Exception {

		XriResolver xriResolver = new XriResolver();

		XriResolutionResult resolutionResult = xriResolver.resolve("=markus");
		
		assertEquals("=!91F2.8153.F600.AE24", resolutionResult.getInumber());
		assertEquals("https://xdi.fullxri.com/=!91F2.8153.F600.AE24/", resolutionResult.getXdiUri());
	}
}
