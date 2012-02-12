package xdi2.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import xdi2.tests.basic.BDBBasicTest;
import xdi2.tests.basic.MapBasicTest;
import xdi2.tests.basic.MemoryBasicTest;
import xdi2.tests.basic.PropertiesBasicTest;
import xdi2.tests.util.XDIUtilTest;
import xdi2.tests.variables.VariablesUtilTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(BDBBasicTest.class);
		suite.addTestSuite(MapBasicTest.class);
		suite.addTestSuite(MemoryBasicTest.class);
		suite.addTestSuite(PropertiesBasicTest.class);
		suite.addTestSuite(VariablesUtilTest.class);
		suite.addTestSuite(XDIUtilTest.class);
		//$JUnit-END$
		return suite;
	}
}
