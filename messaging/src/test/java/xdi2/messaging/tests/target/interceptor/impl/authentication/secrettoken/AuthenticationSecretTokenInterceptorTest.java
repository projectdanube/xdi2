package xdi2.messaging.tests.target.interceptor.impl.authentication.secrettoken;

import junit.framework.TestCase;
import xdi2.messaging.target.interceptor.impl.authentication.secrettoken.DigestSecretTokenAuthenticator;
import xdi2.messaging.target.interceptor.impl.authentication.secrettoken.StaticSecretTokenAuthenticator;

public class AuthenticationSecretTokenInterceptorTest extends TestCase {

	public static String SECRET_TOKEN = "s3cr3t";
	public static String GLOBAL_SALT = "3b97782d-b130-4906-b41d-f83b6968765f";
	public static String LOCAL_SALT = "08d72268-b1b7-4883-aaa7-0e2254a905cb";

	public static String LOCAL_SALT_AND_DIGEST_SECRET_TOKEN = "xdi2-digest:08d72268-b1b7-4883-aaa7-0e2254a905cb:06c730a7e52d267f6462ea9363051553d14d844587962936ff219fb23458f4a9f77a915cd723e52e10bcf4e252cdca24d69096d046d9d6ad248bcfbf2f24467a";
	public static String DIGEST_SECRET_TOKEN = "06c730a7e52d267f6462ea9363051553d14d844587962936ff219fb23458f4a9f77a915cd723e52e10bcf4e252cdca24d69096d046d9d6ad248bcfbf2f24467a";

	public static String[] VALID_SALTS = new String[] { GLOBAL_SALT, LOCAL_SALT };
	public static String[] INVALID_SALTS = new String[] { "3b97782d:b130:4906:b41d:f83b6968765f", "0-0-0-0-0" };
	
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

		assertTrue(staticSecretTokenAuthenticator.authenticate(LOCAL_SALT_AND_DIGEST_SECRET_TOKEN, SECRET_TOKEN));
	}
}
