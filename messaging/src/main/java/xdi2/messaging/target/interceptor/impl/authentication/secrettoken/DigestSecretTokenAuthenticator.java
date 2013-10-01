package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.messaging.Message;

/**
 * A SecretTokenAuthenticator that can authenticate a secret token against
 * a stored digested secret token, using both a global salt and local salts
 * for producing the digest.
 */
public abstract class DigestSecretTokenAuthenticator extends AbstractSecretTokenAuthenticator implements SecretTokenAuthenticator {

	public static String PREFIX_XDI2_DIGEST = "xdi2-digest";

	private static Logger log = LoggerFactory.getLogger(DigestSecretTokenAuthenticator.class.getName());

	private String globalSalt;

	public DigestSecretTokenAuthenticator(String globalSalt) {

		this.globalSalt = globalSalt;
	}

	public DigestSecretTokenAuthenticator() {

	}

	@Override
	public final boolean authenticate(Message message, String secretToken) {

		String localSaltAndDigestSecretToken = this.getLocalSaltAndDigestSecretToken(message);

		if (localSaltAndDigestSecretToken == null) {

			if (log.isDebugEnabled()) log.debug("No local salt and digest secret token found for sender: " + message.getSenderXri());

			return false;
		}

		if (log.isDebugEnabled()) log.debug("Local salt and digest secret token found for sender " + message.getSenderXri() + ": " + localSaltAndDigestSecretToken);

		// prepare authentication

		String[] parts = localSaltAndDigestSecretToken.split(":");

		if (parts.length != 3) {

			if (log.isWarnEnabled()) log.warn("Invalid digest format (not 3 parts).");

			return false;
		}

		if (! PREFIX_XDI2_DIGEST.equals(parts[0])) {

			if (log.isWarnEnabled()) log.warn("Invalid digest format (prefix '" + parts[0] + "' doesn't match expected '" + PREFIX_XDI2_DIGEST + "')");

			return false;
		}

		String localSalt = parts[1];
		String digestSecretToken = parts[2];

		// authenticate

		boolean authenticated = digestSecretToken.equals(digestSecretToken(secretToken, this.getGlobalSalt(), localSalt));

		return authenticated;
	}

	protected abstract String getLocalSaltAndDigestSecretToken(Message message);

	/*
	 * Getters and setters
	 */

	public String getGlobalSalt() {

		return this.globalSalt;
	}

	public void setGlobalSalt(String globalSalt) {

		this.globalSalt = globalSalt;
	}

	/*
	 * Helper methods
	 */

	public static String localSaltAndDigestSecretToken(String secretToken, String globalSalt) {

		String localSalt = randomSalt();

		return localSaltAndDigestSecretToken(secretToken, globalSalt, localSalt);
	}

	public static String localSaltAndDigestSecretToken(String secretToken, String globalSalt, String localSalt) {

		String digestSecretToken = digestSecretToken(secretToken, globalSalt, localSalt);

		return PREFIX_XDI2_DIGEST + ":" + localSalt + ":" + digestSecretToken;
	}

	public static String digestSecretToken(String secretToken, String globalSalt, String localSalt) {

		if (! isValidSalt(globalSalt)) throw new Xdi2RuntimeException("Invalid global salt.");
		if (! isValidSalt(localSalt)) throw new Xdi2RuntimeException("Invalid local salt.");

		try {

			return DigestUtils.sha512Hex(globalSalt + ":" + localSalt + ":" + DigestUtils.sha512Hex(globalSalt + ":" + Base64.encodeBase64String(secretToken.getBytes("UTF-8"))));
		} catch (UnsupportedEncodingException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public static String randomSalt() {

		return UUID.randomUUID().toString();
	}

	public static boolean isValidSalt(String salt) {

		try {

			UUID.fromString(salt);
		} catch (IllegalArgumentException ex) {

			return false;
		}

		if (salt.length() != 36) return false;

		return true;
	}
}
