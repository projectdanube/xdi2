package xdi2.messaging.target.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import xdi2.messaging.target.tests.contributor.ContributorTest;
import xdi2.messaging.target.tests.impl.graph.BDBJSONGraphMessagingTargetTest;
import xdi2.messaging.target.tests.impl.graph.BDBKeyValueGraphMessagingTargetTest;
import xdi2.messaging.target.tests.impl.graph.FileJSONGraphMessagingTargetTest;
import xdi2.messaging.target.tests.impl.graph.FileWrapperGraphMessagingTargetTest;
import xdi2.messaging.target.tests.impl.graph.MapGraphMessagingTargetTest;
import xdi2.messaging.target.tests.impl.graph.MemoryGraphMessagingTargetTest;
import xdi2.messaging.target.tests.impl.graph.MemoryJSONGraphMessagingTargetTest;
import xdi2.messaging.target.tests.impl.graph.PropertiesKeyValueGraphMessagingTargetTest;
import xdi2.messaging.target.tests.interceptor.impl.linkcontract.LinkContractInterceptorTest;
import xdi2.messaging.tests.target.interceptor.impl.authentication.secrettoken.AuthenticationSecretTokenInterceptorTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(MemoryGraphMessagingTargetTest.class);
		suite.addTestSuite(MapGraphMessagingTargetTest.class);
		suite.addTestSuite(PropertiesKeyValueGraphMessagingTargetTest.class);
		suite.addTestSuite(BDBKeyValueGraphMessagingTargetTest.class);
		suite.addTestSuite(MemoryJSONGraphMessagingTargetTest.class);
		suite.addTestSuite(FileJSONGraphMessagingTargetTest.class);
		suite.addTestSuite(BDBJSONGraphMessagingTargetTest.class);
		suite.addTestSuite(FileWrapperGraphMessagingTargetTest.class);
		suite.addTestSuite(LinkContractInterceptorTest.class);
		suite.addTestSuite(ContributorTest.class);
		suite.addTestSuite(AuthenticationSecretTokenInterceptorTest.class);
		//$JUnit-END$
		return suite;
	}

}
