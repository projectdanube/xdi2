package xdi2.server.tests;


import junit.framework.Test;
import junit.framework.TestSuite;
import xdi2.messaging.tests.target.interceptor.impl.authentication.secrettoken.AuthenticationSecretTokenInterceptorTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(AuthenticationSecretTokenInterceptorTest.class);
		//$JUnit-END$
		return suite;
	}
}
