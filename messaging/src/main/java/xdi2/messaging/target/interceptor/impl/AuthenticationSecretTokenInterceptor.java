package xdi2.messaging.target.interceptor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.MessageInterceptor;

public class AuthenticationSecretTokenInterceptor implements MessageInterceptor, Prototype<AuthenticationSecretTokenInterceptor> {

	private static Logger log = LoggerFactory.getLogger(AuthenticationSecretTokenInterceptor.class.getName());

	private SecretTokenAuthenticator secretTokenAuthenticator;

	/*
	 * Prototype
	 */

	@Override
	public AuthenticationSecretTokenInterceptor instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new interceptor

		AuthenticationSecretTokenInterceptor interceptor = new AuthenticationSecretTokenInterceptor();

		interceptor.setSecretTokenAuthenticator(this.getSecretTokenAuthenticator().instanceFor(prototypingContext));

		// done

		return interceptor;
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// look for secret token on the message

		Literal secretTokenLiteral = message.getContextNode().getDeepLiteral(XDIPolicyConstants.XRI_S_SECRET_TOKEN);
		if (secretTokenLiteral == null) return false;

		String secretToken = secretTokenLiteral.getLiteralData();

		// authenticate

		boolean authenticated = this.getSecretTokenAuthenticator().authenticate(message, secretToken);
		log.debug("" + message.getSender() + " authenticated: " + authenticated);

		// done

		return false;
	}

	@Override
	public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Getters and setters
	 */

	public SecretTokenAuthenticator getSecretTokenAuthenticator() {

		return this.secretTokenAuthenticator;
	}

	public void setSecretTokenAuthenticator(SecretTokenAuthenticator secretTokenAuthenticator) {

		this.secretTokenAuthenticator = secretTokenAuthenticator;
	}

	/*
	 * Helper classes
	 */

	public interface SecretTokenAuthenticator extends Prototype<SecretTokenAuthenticator> {

		public boolean authenticate(Message message, String secretToken);
	}

	public static class GraphSecretTokenAuthenticator implements SecretTokenAuthenticator {

		private Graph secretTokenGraph;

		@Override
		public SecretTokenAuthenticator instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

			// create new secret token authenticator

			GraphSecretTokenAuthenticator authenticator = new GraphSecretTokenAuthenticator();

			// set the secret token graph

			if (this.getSecretTokenGraph() == null) {

				if (prototypingContext.getMessagingTarget() instanceof GraphMessagingTarget) {

					authenticator.setSecretTokenGraph(((GraphMessagingTarget) prototypingContext.getMessagingTarget()).getGraph());
				} else {

					throw new Xdi2RuntimeException("No secret token graph.");
				}
			} else {

				authenticator.setSecretTokenGraph(this.getSecretTokenGraph());
			}

			// done

			return authenticator;
		}

		@Override
		public boolean authenticate(Message message, String secretToken) {

			XDI3Segment fromAddress = message.getFromAddress();
			if (fromAddress == null) return false;
			
			Equivalence.get
			
			return false;
		}

		public Graph getSecretTokenGraph() {

			return this.secretTokenGraph;
		}

		public void setSecretTokenGraph(Graph secretTokenGraph) {

			this.secretTokenGraph = secretTokenGraph;
		}
	}
}
