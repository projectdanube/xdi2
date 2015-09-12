package xdi2.messaging.target.interceptor.impl.security.secrettoken;

import java.util.Map;

import xdi2.core.syntax.XDIAddress;

/**
 * A SecretTokenAuthenticator that can authenticate an XDI message using a
 * statically configured list of sender addresses and secret tokens in digest form.
 */
public class StaticSecretTokenValidator extends DigestSecretTokenValidator {

	private Map<XDIAddress, String> localSaltAndDigestSecretTokens;

	public StaticSecretTokenValidator(String globalSalt, Map<XDIAddress, String> localSaltAndDigestSecretTokens) {

		super(globalSalt);

		this.localSaltAndDigestSecretTokens = localSaltAndDigestSecretTokens;
	}

	public StaticSecretTokenValidator() {

		super();
	}

	@Override
	public String getLocalSaltAndDigestSecretToken(XDIAddress senderXDIAddress) {

		// look for static local salt and digest secret token

		String localSaltAndDigestSecretToken = this.getLocalSaltAndDigestSecretTokens().get(senderXDIAddress);
		if (localSaltAndDigestSecretToken == null) return null;

		// done

		return localSaltAndDigestSecretToken;
	}

	public Map<XDIAddress, String> getLocalSaltAndDigestSecretTokens() {

		return this.localSaltAndDigestSecretTokens;
	}

	public void setLocalSaltAndDigestSecretTokens(Map<XDIAddress, String> localSaltAndDigestSecretTokens) {

		this.localSaltAndDigestSecretTokens = localSaltAndDigestSecretTokens;
	}
}