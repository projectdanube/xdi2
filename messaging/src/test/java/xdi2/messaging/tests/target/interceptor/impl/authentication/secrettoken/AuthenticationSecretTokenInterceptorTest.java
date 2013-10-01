package xdi2.messaging.tests.target.interceptor.impl.authentication.secrettoken;

import java.util.Collections;

import junit.framework.TestCase;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.target.interceptor.impl.authentication.secrettoken.DigestSecretTokenAuthenticator;
import xdi2.messaging.target.interceptor.impl.authentication.secrettoken.StaticSecretTokenAuthenticator;

public class AuthenticationSecretTokenInterceptorTest extends TestCase {

	public static String SECRET_TOKEN = "s3cr3t";
	public static String GLOBAL_SALT = "3b97782d-b130-4906-b41d-f83b6968765f";
	public static String LOCAL_SALT = "08d72268-b1b7-4883-aaa7-0e2254a905cb";

	public static String LOCAL_SALT_AND_DIGEST_SECRET_TOKEN = "xdi2-digest:08d72268-b1b7-4883-aaa7-0e2254a905cb:79967d0c9845c536a004e638c1913a8a3917f73c7d4801beb9f8488f6603bcbcfb5a5502e75648737e0039dd401bd771d23a123c2f862e2743aa246250c897e6";
	public static String DIGEST_SECRET_TOKEN = "79967d0c9845c536a004e638c1913a8a3917f73c7d4801beb9f8488f6603bcbcfb5a5502e75648737e0039dd401bd771d23a123c2f862e2743aa246250c897e6";

	public static String[] VALID_SALTS = new String[] { GLOBAL_SALT, LOCAL_SALT };
	public static String[] INVALID_SALTS = new String[] { "3b97782d:b130:4906:b41d:f83b6968765f", "0-0-0-0-0" };

	public static XDI3Segment SENDER_XRI = XDI3Segment.create("=sender");

	public void testDigestSecretTokenAuthenticatorWithChosenLocalSalt() throws Exception {

		String localSaltAndDigestSecretToken = DigestSecretTokenAuthenticator.localSaltAndDigestSecretToken(SECRET_TOKEN, GLOBAL_SALT, LOCAL_SALT);
		assertEquals(localSaltAndDigestSecretToken, LOCAL_SALT_AND_DIGEST_SECRET_TOKEN);

		String digestSecretToken = DigestSecretTokenAuthenticator.digestSecretToken(SECRET_TOKEN, GLOBAL_SALT, LOCAL_SALT);
		assertEquals(digestSecretToken, DIGEST_SECRET_TOKEN);

		String[] parts = localSaltAndDigestSecretToken.split(":");
		assertEquals(parts.length, 3);
		assertEquals(parts[0], "xdi2-digest");
		assertEquals(parts[1], LOCAL_SALT);
		assertEquals(parts[2], DIGEST_SECRET_TOKEN);
	}

	public void testDigestSecretTokenAuthenticatorWithRandomLocalSalt() throws Exception {

		String localSaltAndDigestSecretToken = DigestSecretTokenAuthenticator.localSaltAndDigestSecretToken(SECRET_TOKEN, GLOBAL_SALT);

		String[] parts = localSaltAndDigestSecretToken.split(":");
		assertEquals(parts.length, 3);
		assertEquals(parts[0], "xdi2-digest");

		String localSalt = parts[1];
		String digestSecretToken = parts[2];

		assertEquals(localSaltAndDigestSecretToken, DigestSecretTokenAuthenticator.localSaltAndDigestSecretToken(SECRET_TOKEN, GLOBAL_SALT, localSalt));
		assertEquals(digestSecretToken, DigestSecretTokenAuthenticator.digestSecretToken(SECRET_TOKEN, GLOBAL_SALT, localSalt)); 
	}

	public void testValidSalts() throws Exception {

		for (String salt : VALID_SALTS) assertTrue(DigestSecretTokenAuthenticator.isValidSalt(salt));
		for (String salt : INVALID_SALTS) assertFalse(DigestSecretTokenAuthenticator.isValidSalt(salt));
	}

	public void testStaticSecretTokenAuthenticator() throws Exception {

		StaticSecretTokenAuthenticator staticSecretTokenAuthenticator = new StaticSecretTokenAuthenticator();
		staticSecretTokenAuthenticator.setGlobalSalt(GLOBAL_SALT);
		staticSecretTokenAuthenticator.setLocalSaltAndDigestSecretTokens(Collections.singletonMap(SENDER_XRI, LOCAL_SALT_AND_DIGEST_SECRET_TOKEN));

		Message message = new MessageEnvelope().getMessage(SENDER_XRI, true);

		assertTrue(staticSecretTokenAuthenticator.authenticate(message, SECRET_TOKEN));
	}
}
