package xdi2.messaging.container.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import xdi2.messaging.container.tests.contributor.ContributorTest;
import xdi2.messaging.container.tests.impl.graph.BDBJSONGraphMessagingContainerTest;
import xdi2.messaging.container.tests.impl.graph.BDBKeyValueGraphMessagingContainerTest;
import xdi2.messaging.container.tests.impl.graph.FileJSONGraphMessagingContainerTest;
import xdi2.messaging.container.tests.impl.graph.FileWrapperGraphMessagingContainerTest;
import xdi2.messaging.container.tests.impl.graph.MapGraphMessagingContainerTest;
import xdi2.messaging.container.tests.impl.graph.MemoryGraphMessagingContainerTest;
import xdi2.messaging.container.tests.impl.graph.MemoryJSONGraphMessagingContainerTest;
import xdi2.messaging.container.tests.impl.graph.PropertiesKeyValueGraphMessagingContainerTest;
import xdi2.messaging.container.tests.interceptor.impl.authentication.secrettoken.AuthenticationSecretTokenInterceptorTest;
import xdi2.messaging.container.tests.interceptor.impl.linkcontract.LinkContractInterceptorTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(MemoryGraphMessagingContainerTest.class);
		suite.addTestSuite(MapGraphMessagingContainerTest.class);
		suite.addTestSuite(PropertiesKeyValueGraphMessagingContainerTest.class);
		suite.addTestSuite(BDBKeyValueGraphMessagingContainerTest.class);
		suite.addTestSuite(MemoryJSONGraphMessagingContainerTest.class);
		suite.addTestSuite(FileJSONGraphMessagingContainerTest.class);
		suite.addTestSuite(BDBJSONGraphMessagingContainerTest.class);
		suite.addTestSuite(FileWrapperGraphMessagingContainerTest.class);
		suite.addTestSuite(LinkContractInterceptorTest.class);
		suite.addTestSuite(ContributorTest.class);
		suite.addTestSuite(AuthenticationSecretTokenInterceptorTest.class);
		//$JUnit-END$
		return suite;
	}

}
