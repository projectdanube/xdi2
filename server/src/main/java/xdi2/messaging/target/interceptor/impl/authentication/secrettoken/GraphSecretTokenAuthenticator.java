package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.xri3.XDI3Segment;
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
	public GraphSecretTokenAuthenticator instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

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
	public String getLocalSaltAndDigestSecretToken(Message message) {

		XDI3Segment senderXri = message.getSenderXri();
		if (senderXri == null) return null;

		// sender peer root

		XdiPeerRoot senderPeerRoot = XdiLocalRoot.findLocalRoot(this.getSecretTokenGraph()).findPeerRoot(senderXri, false);

		if (log.isDebugEnabled()) log.debug("Sender peer root: " + senderPeerRoot);

		// look for local salt and digest secret token in the graph

		XdiAttribute localSaltAndDigestSecretTokenXdiAttribute = senderPeerRoot == null ? null : XdiAttributeSingleton.fromContextNode(senderPeerRoot.getContextNode().getDeepContextNode(XDIAuthenticationConstants.XRI_S_DIGEST_SECRET_TOKEN));
		XdiValue localSaltAndDigestSecretTokenXdiValue = localSaltAndDigestSecretTokenXdiAttribute == null ? null : localSaltAndDigestSecretTokenXdiAttribute.getXdiValue(false);
		Literal localSaltAndDigestSecretTokenLiteral = localSaltAndDigestSecretTokenXdiValue == null ? null : localSaltAndDigestSecretTokenXdiValue.getContextNode().getLiteral();

		String localSaltAndDigestSecretToken = localSaltAndDigestSecretTokenLiteral == null ? null : localSaltAndDigestSecretTokenLiteral.getLiteralDataString();
		if (localSaltAndDigestSecretToken == null) return null;

		// done

		return localSaltAndDigestSecretToken;
	}

	public Graph getSecretTokenGraph() {

		return this.secretTokenGraph;
	}

	public void setSecretTokenGraph(Graph secretTokenGraph) {

		this.secretTokenGraph = secretTokenGraph;
	}
}