package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import xdi2.messaging.Message;
import xdi2.messaging.exceptions.Xdi2MessagingException;

/**
 * A SecretTokenAuthenticator that can authenticate a secret token against
 * a statically configured digested secret token.
 */
public class StaticSecretTokenAuthenticator extends DigestSecretTokenAuthenticator {

	private String localSaltAndDigestSecretToken;

	public StaticSecretTokenAuthenticator(String globalSalt, String localSaltAndDigestSecretToken) {

		super(globalSalt);

		this.localSaltAndDigestSecretToken = localSaltAndDigestSecretToken;
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

		// authenticate
		
		return super.authenticate(this.getLocalSaltAndDigestSecretToken(), secretToken);
	}

	public String getLocalSaltAndDigestSecretToken() {

		return this.localSaltAndDigestSecretToken;
	}

	public void setLocalSaltAndDigestSecretToken(String localSaltAndDigestSecretToken) {

		this.localSaltAndDigestSecretToken = localSaltAndDigestSecretToken;
	}
}