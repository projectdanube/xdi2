package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import java.util.Map;

import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;

/**
 * A SecretTokenAuthenticator that can authenticate an XDI message using a
 * statically configured list of sender addresses and secret tokens in digest form.
 */
public class StaticSecretTokenAuthenticator extends DigestSecretTokenAuthenticator {

	private Map<XDI3Segment, String> localSaltAndDigestSecretTokens;

	public StaticSecretTokenAuthenticator(String globalSalt, Map<XDI3Segment, String> localSaltAndDigestSecretTokens) {

		super(globalSalt);

		this.localSaltAndDigestSecretTokens = localSaltAndDigestSecretTokens;
	}

	public StaticSecretTokenAuthenticator() {

		super();
	}

	@Override
	public String getLocalSaltAndDigestSecretToken(Message message) {

		XDI3Segment senderXri = message.getSenderXri();
		if (senderXri == null) return null;

		// look for static local salt and digest secret token

		String localSaltAndDigestSecretToken = this.getLocalSaltAndDigestSecretTokens().get(senderXri);
		if (localSaltAndDigestSecretToken == null) return null;

		// done

		return localSaltAndDigestSecretToken;
	}

	public Map<XDI3Segment, String> getLocalSaltAndDigestSecretTokens() {

		return this.localSaltAndDigestSecretTokens;
	}

	public void setLocalSaltAndDigestSecretTokens(Map<XDI3Segment, String> localSaltAndDigestSecretTokens) {

		this.localSaltAndDigestSecretTokens = localSaltAndDigestSecretTokens;
	}
}