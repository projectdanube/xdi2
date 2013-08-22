package xdi2.messaging.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import xdi2.messaging.tests.basic.BasicTest;
import xdi2.messaging.tests.http.AcceptHeaderTest;
import xdi2.messaging.tests.target.contributor.ContributorTest;
import xdi2.messaging.tests.target.impl.graph.BDBGraphMessagingTargetTest;
import xdi2.messaging.tests.target.impl.graph.FileGraphMessagingTargetTest;
import xdi2.messaging.tests.target.impl.graph.FileJSONGraphMessagingTargetTest;
import xdi2.messaging.tests.target.impl.graph.MemoryJSONGraphMessagingTargetTest;
import xdi2.messaging.tests.target.impl.graph.MapGraphMessagingTargetTest;
import xdi2.messaging.tests.target.impl.graph.MemoryGraphMessagingTargetTest;
import xdi2.messaging.tests.target.impl.graph.PropertiesGraphMessagingTargetTest;
import xdi2.messaging.tests.target.interceptor.impl.authentication.secrettoken.AuthenticationSecretTokenInterceptorTest;
import xdi2.messaging.tests.target.interceptor.impl.linkcontract.LinkContractInterceptorTest;

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
		suite.addTestSuite(MemoryJSONGraphMessagingTargetTest.class);
		suite.addTestSuite(FileJSONGraphMessagingTargetTest.class);
		suite.addTestSuite(LinkContractInterceptorTest.class);
		suite.addTestSuite(AuthenticationSecretTokenInterceptorTest.class);
		suite.addTestSuite(ContributorTest.class);
		//$JUnit-END$
		return suite;
	}

}
