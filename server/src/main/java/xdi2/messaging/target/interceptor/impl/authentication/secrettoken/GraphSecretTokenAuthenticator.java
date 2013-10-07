package xdi2.messaging.target.interceptor.impl.authentication.secrettoken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.xri3.XDI3Segment;
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