package xdi2.tests;

import xdi2.tests.basic.BDBBasicTest;
import xdi2.tests.basic.MapBasicTest;
import xdi2.tests.basic.MemoryBasicTest;
import xdi2.tests.basic.PropertiesBasicTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(BDBBasicTest.class);
		suite.addTestSuite(MapBasicTest.class);
		suite.addTestSuite(MemoryBasicTest.class);
		suite.addTestSuite(PropertiesBasicTest.class);
		//$JUnit-END$
		return suite;
	}

}
