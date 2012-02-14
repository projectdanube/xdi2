package xdi2.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import xdi2.tests.graph.BDBGraphTest;
import xdi2.tests.graph.MapGraphTest;
import xdi2.tests.graph.MemoryGraphTest;
import xdi2.tests.graph.PropertiesGraphTest;
import xdi2.tests.util.XDIUtilTest;
import xdi2.tests.variables.VariablesUtilTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(MemoryGraphTest.class);
		suite.addTestSuite(MapGraphTest.class);
		suite.addTestSuite(PropertiesGraphTest.class);
		suite.addTestSuite(BDBGraphTest.class);
		suite.addTestSuite(VariablesUtilTest.class);
		suite.addTestSuite(XDIUtilTest.class);
		//$JUnit-END$
		return suite;
	}
}
