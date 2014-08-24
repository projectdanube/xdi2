package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.features.secrettokens.SecretTokens;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * A SecretTokenAuthenticator that can authenticate an XDI message using a "secret token graph",
 * which contains sender addresses and secret tokens in digest form.
 */
public class GraphSecretTokenAuthenticator extends DigestSecretTokenAuthenticator {

	private static Logger log = LoggerFactory.getLogger(GraphSecretTokenAuthenticator.class.getName());

	private Graph secretTokenGraph;

	public GraphSecretTokenAuthenticator(String globalSalt, Graph secretTokenGraph) {

		super(globalSalt);

		this.secretTokenGraph = secretTokenGraph;
	}

	public GraphSecretTokenAuthenticator() {

		super();

		this.secretTokenGraph = null;
	}

	@Override
	public void init(MessagingTarget messagingTarget, AuthenticationSecretTokenInterceptor authenticationSecretTokenInterceptor) throws Exception {

		super.init(messagingTarget, authenticationSecretTokenInterceptor);

		if (this.getSecretTokenGraph() == null && messagingTarget instanceof GraphMessagingTarget) this.setSecretTokenGraph(((GraphMessagingTarget) messagingTarget).getGraph());
		if (this.getSecretTokenGraph() == null) throw new Xdi2MessagingException("No secret token graph.", null, null);
	}

	@Override
	public String getLocalSaltAndDigestSecretToken(Message message) {

		// sender

		XDIAddress senderAddress = message.getSenderAddress();
		if (senderAddress == null) return null;

		// sender peer root

		XdiRoot senderXdiPeerRoot = XdiLocalRoot.findLocalRoot(this.getSecretTokenGraph()).getPeerRoot(senderAddress, false);
		senderXdiPeerRoot = senderXdiPeerRoot == null ? null : senderXdiPeerRoot.dereference();

		if (log.isDebugEnabled()) log.debug("Sender peer root: " + senderXdiPeerRoot);

		if (senderXdiPeerRoot == null) return null;

		// find local salt and digest secret token

		String localSaltAndDigestSecretToken = senderXdiPeerRoot == null ? null : SecretTokens.getLocalSaltAndDigestSecretToken(senderXdiPeerRoot);

		// done

		return localSaltAndDigestSecretToken;
	}

	/*
	 * Getters and setters
	 */

	public Graph getSecretTokenGraph() {

		return this.secretTokenGraph;
	}

	public void setSecretTokenGraph(Graph secretTokenGraph) {

		this.secretTokenGraph = secretTokenGraph;
	}
}