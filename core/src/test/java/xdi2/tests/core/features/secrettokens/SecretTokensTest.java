package xdi2.tests.core.features.secrettokens;

import junit.framework.TestCase;
import xdi2.core.features.secrettokens.SecretTokens;
import xdi2.core.syntax.XDIAddress;

public class SecretTokensTest extends TestCase {

	public static String SECRET_TOKEN = "s3cr3t";
	public static String GLOBAL_SALT = "3b97782d-b130-4906-b41d-f83b6968765f";
	public static String LOCAL_SALT = "08d72268-b1b7-4883-aaa7-0e2254a905cb";

	public static String LOCAL_SALT_AND_DIGEST_SECRET_TOKEN = "xdi2-digest:08d72268-b1b7-4883-aaa7-0e2254a905cb:79967d0c9845c536a004e638c1913a8a3917f73c7d4801beb9f8488f6603bcbcfb5a5502e75648737e0039dd401bd771d23a123c2f862e2743aa246250c897e6";
	public static String DIGEST_SECRET_TOKEN = "79967d0c9845c536a004e638c1913a8a3917f73c7d4801beb9f8488f6603bcbcfb5a5502e75648737e0039dd401bd771d23a123c2f862e2743aa246250c897e6";

	public static String[] VALID_SALTS = new String[] { GLOBAL_SALT, LOCAL_SALT };
	public static String[] INVALID_SALTS = new String[] { "3b97782d:b130:4906:b41d:f83b6968765f", "0-0-0-0-0" };

	public void testDigestSecretTokenWithChosenLocalSalt() throws Exception {

		String localSaltAndDigestSecretToken = SecretTokens.localSaltAndDigestSecretToken(SECRET_TOKEN, GLOBAL_SALT, LOCAL_SALT);
		assertEquals(localSaltAndDigestSecretToken, LOCAL_SALT_AND_DIGEST_SECRET_TOKEN);

		String digestSecretToken = SecretTokens.digestSecretToken(SECRET_TOKEN, GLOBAL_SALT, LOCAL_SALT);
		assertEquals(digestSecretToken, DIGEST_SECRET_TOKEN);

		String[] parts = localSaltAndDigestSecretToken.split(":");
		assertEquals(parts.length, 3);
		assertEquals(parts[0], SecretTokens.PREFIX_XDI2_DIGEST);
		assertEquals(parts[1], LOCAL_SALT);
		assertEquals(parts[2], DIGEST_SECRET_TOKEN);
	}

	public void testDigestSecretTokenWithRandomLocalSalt() throws Exception {

		String localSaltAndDigestSecretToken = SecretTokens.localSaltAndDigestSecretToken(SECRET_TOKEN, GLOBAL_SALT);

		String[] parts = localSaltAndDigestSecretToken.split(":");
		assertEquals(parts.length, 3);
		assertEquals(parts[0], SecretTokens.PREFIX_XDI2_DIGEST);

		String localSalt = parts[1];
		String digestSecretToken = parts[2];

		assertEquals(localSaltAndDigestSecretToken, SecretTokens.localSaltAndDigestSecretToken(SECRET_TOKEN, GLOBAL_SALT, localSalt));
		assertEquals(digestSecretToken, SecretTokens.digestSecretToken(SECRET_TOKEN, GLOBAL_SALT, localSalt)); 
	}

	public void testValidSalts() throws Exception {

		for (String salt : VALID_SALTS) assertTrue(SecretTokens.isValidSalt(salt));
		for (String salt : INVALID_SALTS) assertFalse(SecretTokens.isValidSalt(salt));
	}
}
