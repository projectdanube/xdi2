package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.Message;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * A SecretTokenAuthenticator that can authenticate an XDI message using a "secret token graph",
 * which contains sender addresses and secret tokens in digest form.
 */
public class GraphSecretTokenAuthenticator extends DigestSecretTokenAuthenticator {

	private static Logger log = LoggerFactory.getLogger(GraphSecretTokenAuthenticator.class.getName());

	private Graph secretTokenGraph;

	public GraphSecretTokenAuthenticator(String globalHash, Graph secretTokenGraph) {

		super(globalHash);

		this.secretTokenGraph = secretTokenGraph;
	}

	public GraphSecretTokenAuthenticator() {

		super();
	}

	@Override
	public SecretTokenAuthenticator instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new secret token authenticator

		GraphSecretTokenAuthenticator authenticator = new GraphSecretTokenAuthenticator();

		// set the global hash

		authenticator.setGlobalSalt(this.getGlobalSalt());

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

		XDI3Segment sender = message.getSender();
		if (sender == null) return false;

		// sender address

		XDI3SubSegment senderAddress = XdiPeerRoot.createPeerRootArcXri(sender);

		if (log.isDebugEnabled()) log.debug("Sender address: " + senderAddress);

		// look for local salt and digest secret token in the graph

		ContextNode senderAddressContextNode = this.getSecretTokenGraph().getRootContextNode().getContextNode(senderAddress);
		Literal localSaltAndDigestSecretTokenLiteral = senderAddressContextNode == null ? null : senderAddressContextNode.getDeepLiteral(XDIAuthenticationConstants.XRI_S_DIGEST_SECRET_TOKEN);

		String localSaltAndDigestSecretToken = localSaltAndDigestSecretTokenLiteral == null ? null : localSaltAndDigestSecretTokenLiteral.getLiteralData();
		if (localSaltAndDigestSecretToken == null) return false;

		// authenticate

		return super.authenticate(localSaltAndDigestSecretToken, secretToken);
	}

	public Graph getSecretTokenGraph() {

		return this.secretTokenGraph;
	}

	public void setSecretTokenGraph(Graph secretTokenGraph) {

		this.secretTokenGraph = secretTokenGraph;
	}
}