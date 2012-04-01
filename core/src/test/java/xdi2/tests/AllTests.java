package xdi2.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import xdi2.tests.core.graph.BDBGraphTest;
import xdi2.tests.core.graph.MapGraphTest;
import xdi2.tests.core.graph.MemoryGraphTest;
import xdi2.tests.core.graph.PropertiesGraphTest;
import xdi2.tests.core.impl.keyvalue.BDBKeyValueTest;
import xdi2.tests.core.impl.keyvalue.MapKeyValueTest;
import xdi2.tests.core.impl.keyvalue.PropertiesKeyValueTest;
import xdi2.tests.core.io.IOTest;
import xdi2.tests.core.util.XDIUtilTest;
import xdi2.tests.core.variables.VariablesUtilTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(MemoryGraphTest.class);
		suite.addTestSuite(MapGraphTest.class);
		suite.addTestSuite(PropertiesGraphTest.class);
		suite.addTestSuite(BDBGraphTest.class);
		suite.addTestSuite(MapKeyValueTest.class);
		suite.addTestSuite(PropertiesKeyValueTest.class);
		suite.addTestSuite(BDBKeyValueTest.class);
		suite.addTestSuite(IOTest.class);
		suite.addTestSuite(VariablesUtilTest.class);
		suite.addTestSuite(XDIUtilTest.class);
		//$JUnit-END$
		return suite;
	}
}
