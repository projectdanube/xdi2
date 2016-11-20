package xdi2.messaging.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import xdi2.messaging.tests.basic.BasicTest;
import xdi2.messaging.tests.http.AcceptHeaderTest;
import xdi2.messaging.tests.instantiation.MessageInstantiationTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(BasicTest.class);
		suite.addTestSuite(AcceptHeaderTest.class);
		suite.addTestSuite(MessageInstantiationTest.class);
		//$JUnit-END$
		return suite;
	}

}
