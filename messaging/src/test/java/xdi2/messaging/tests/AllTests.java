package xdi2.messaging.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import xdi2.messaging.tests.basic.BasicTest;
import xdi2.messaging.tests.http.AcceptHeaderTest;
import xdi2.messaging.tests.linkcontracts.LinkContractsTest;
import xdi2.messaging.tests.messagingtarget.BDBGraphMessagingTargetTest;
import xdi2.messaging.tests.messagingtarget.FileGraphMessagingTargetTest;
import xdi2.messaging.tests.messagingtarget.MapGraphMessagingTargetTest;
import xdi2.messaging.tests.messagingtarget.MemoryGraphMessagingTargetTest;
import xdi2.messaging.tests.messagingtarget.PropertiesGraphMessagingTargetTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(BasicTest.class);
		suite.addTestSuite(AcceptHeaderTest.class);
		suite.addTestSuite(MemoryGraphMessagingTargetTest.class);
		suite.addTestSuite(MapGraphMessagingTargetTest.class);
		suite.addTestSuite(PropertiesGraphMessagingTargetTest.class);
		suite.addTestSuite(BDBGraphMessagingTargetTest.class);
		suite.addTestSuite(FileGraphMessagingTargetTest.class);
		suite.addTestSuite(LinkContractsTest.class);
		//$JUnit-END$
		return suite;
	}

}
