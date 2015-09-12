package xdi2.messaging.tests.target.interceptor.impl.authentication.secrettoken;

import java.util.Collections;

import junit.framework.TestCase;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.target.interceptor.impl.security.secrettoken.StaticSecretTokenValidator;

public class AuthenticationSecretTokenInterceptorTest extends TestCase {

	public static String SECRET_TOKEN = "s3cr3t";
	public static String GLOBAL_SALT = "3b97782d-b130-4906-b41d-f83b6968765f";
	public static String LOCAL_SALT = "08d72268-b1b7-4883-aaa7-0e2254a905cb";

	public static String LOCAL_SALT_AND_DIGEST_SECRET_TOKEN = "xdi2-digest:08d72268-b1b7-4883-aaa7-0e2254a905cb:79967d0c9845c536a004e638c1913a8a3917f73c7d4801beb9f8488f6603bcbcfb5a5502e75648737e0039dd401bd771d23a123c2f862e2743aa246250c897e6";
	public static String DIGEST_SECRET_TOKEN = "79967d0c9845c536a004e638c1913a8a3917f73c7d4801beb9f8488f6603bcbcfb5a5502e75648737e0039dd401bd771d23a123c2f862e2743aa246250c897e6";

	public static String[] VALID_SALTS = new String[] { GLOBAL_SALT, LOCAL_SALT };
	public static String[] INVALID_SALTS = new String[] { "3b97782d:b130:4906:b41d:f83b6968765f", "0-0-0-0-0" };

	public static XDIAddress SENDER = XDIAddress.create("=sender");

	public void testStaticSecretTokenAuthenticator() throws Exception {

		StaticSecretTokenValidator staticSecretTokenAuthenticator = new StaticSecretTokenValidator();
		staticSecretTokenAuthenticator.setGlobalSalt(GLOBAL_SALT);
		staticSecretTokenAuthenticator.setLocalSaltAndDigestSecretTokens(Collections.singletonMap(SENDER, LOCAL_SALT_AND_DIGEST_SECRET_TOKEN));

		Message message = new MessageEnvelope().createMessage(SENDER);
		message.setSecretToken(SECRET_TOKEN);

		assertTrue(staticSecretTokenAuthenticator.authenticate(message.getSecretToken(), message.getSenderXDIAddress()));
	}
}
