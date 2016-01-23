package xdi2.messaging.target.interceptor.impl.security.secrettoken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.secrettokens.SecretTokens;
import xdi2.core.syntax.XDIAddress;

/**
 * A SecretTokenAuthenticator that can authenticate a secret token against
 * a stored digest secret token, using both a global salt and local salts
 * for producing the digest.
 */
public abstract class DigestSecretTokenValidator extends AbstractSecretTokenValidator implements SecretTokenValidator {

	private static Logger log = LoggerFactory.getLogger(DigestSecretTokenValidator.class.getName());

	private String globalSalt;

	public DigestSecretTokenValidator(String globalSalt) {

		this.globalSalt = globalSalt;
	}

	public DigestSecretTokenValidator() {

		this.globalSalt = null;
	}

	@Override
	public final boolean authenticate(String secretToken, XDIAddress senderXDIAddress) {

		String localSaltAndDigestSecretToken = this.getLocalSaltAndDigestSecretToken(senderXDIAddress);

		if (localSaltAndDigestSecretToken == null) {

			if (log.isDebugEnabled()) log.debug("No local salt and digest secret token found for sender: " + senderXDIAddress);

			return false;
		}

		if (log.isDebugEnabled()) log.debug("Local salt and digest secret token found for sender " + senderXDIAddress + ": " + localSaltAndDigestSecretToken);

		// prepare authentication

		String[] parts = localSaltAndDigestSecretToken.split(":");

		if (parts.length != 3) {

			if (log.isWarnEnabled()) log.warn("Invalid digest format (not 3 parts).");

			return false;
		}

		if (! SecretTokens.PREFIX_XDI2_DIGEST.equals(parts[0])) {

			if (log.isWarnEnabled()) log.warn("Invalid digest format (prefix '" + parts[0] + "' doesn't match expected '" + SecretTokens.PREFIX_XDI2_DIGEST + "')");

			return false;
		}

		String localSalt = parts[1];
		String digestSecretToken = parts[2];

		// authenticate

		boolean authenticated = digestSecretToken.equals(SecretTokens.digestSecretToken(secretToken, this.getGlobalSalt(), localSalt));

		return authenticated;
	}

	protected abstract String getLocalSaltAndDigestSecretToken(XDIAddress senderXDIAddress);

	/*
	 * Getters and setters
	 */

	public String getGlobalSalt() {

		return this.globalSalt;
	}

	public void setGlobalSalt(String globalSalt) {

		this.globalSalt = globalSalt;
	}
}
