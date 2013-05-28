package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import java.util.Map;

import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.exceptions.Xdi2MessagingException;

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
	public SecretTokenAuthenticator instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// done

		return this;
	}

	@Override
	public boolean authenticate(Message message, String secretToken) {

		XDI3Segment sender = message.getSender();
		if (sender == null) return false;

		// look for static local salt and digest secret token

		String localSaltAndDigestSecretToken = this.getLocalSaltAndDigestSecretTokens().get(sender);
		if (localSaltAndDigestSecretToken == null) return false;

		// authenticate

		return super.authenticate(localSaltAndDigestSecretToken, secretToken);
	}

	public Map<XDI3Segment, String> getLocalSaltAndDigestSecretTokens() {

		return this.localSaltAndDigestSecretTokens;
	}

	public void setLocalSaltAndDigestSecretTokens(Map<XDI3Segment, String> localSaltAndDigestSecretTokens) {

		this.localSaltAndDigestSecretTokens = localSaltAndDigestSecretTokens;
	}
}