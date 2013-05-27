package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * A SecretTokenAuthenticator that can authenticate a secret token against
 * a stored digested secret token, using both a global salt and local salts
 * for producing the digest.
 */
public abstract class DigestSecretTokenAuthenticator implements SecretTokenAuthenticator {

	private String globalSalt;

	public DigestSecretTokenAuthenticator(String globalSalt) {

		this.globalSalt = globalSalt;
	}

	public DigestSecretTokenAuthenticator() {

	}

	public boolean authenticate(String localSaltAndDigestSecretToken, String secretToken) {

		String[] parts = localSaltAndDigestSecretToken.split(":");
		if (parts.length != 3) return false;

		String localSalt = parts[1];
		String digestSecretToken = parts[2];

		return digestSecretToken.equals(digestSecretToken(digestSecretToken, this.getGlobalSalt(), localSalt));
	}

	public static String localSaltAndDigestSecretToken(String secretToken, String globalSalt) {

		String localSalt = UUID.randomUUID().toString();

		return localSaltAndDigestSecretToken(secretToken, globalSalt, localSalt);
	}

	public static String localSaltAndDigestSecretToken(String secretToken, String globalSalt, String localSalt) {

		String digestSecretToken = digestSecretToken(secretToken, globalSalt, localSalt);

		return "xdi2:" + localSalt + ":" + digestSecretToken;
	}

	public static String digestSecretToken(String secretToken, String globalSalt, String localSalt) {

		try {

			return DigestUtils.sha512Hex(globalSalt + ":" + localSalt + DigestUtils.sha512Hex(globalSalt + ":" + Base64.encodeBase64String(secretToken.getBytes("UTF-8"))));
		} catch (UnsupportedEncodingException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public String getGlobalSalt() {

		return this.globalSalt;
	}

	public void setGlobalSalt(String globalSalt) {

		this.globalSalt = globalSalt;
	}
}
