package xdi2.xri2xdi;

import junit.framework.Test;
import junit.framework.TestSuite;
import xdi2.xri2xdi.resolution.XriResolverTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(XriResolverTest.class);
		//$JUnit-END$
		return suite;
	}
}
