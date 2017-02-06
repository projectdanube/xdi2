package xdi2.messaging.container.interceptor.impl.security.secrettoken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.features.secrettokens.SecretTokens;
import xdi2.core.syntax.XDIAddress;

/**
 * A SecretTokenAuthenticator that can authenticate an XDI message using a "secret token graph",
 * which contains sender addresses and secret tokens in digest form.
 */
public class GraphSecretTokenValidator extends DigestSecretTokenValidator {

	private static Logger log = LoggerFactory.getLogger(GraphSecretTokenValidator.class.getName());

	private Graph secretTokenGraph;

	public GraphSecretTokenValidator(String globalSalt, Graph secretTokenGraph) {

		super(globalSalt);

		this.secretTokenGraph = secretTokenGraph;
	}

	public GraphSecretTokenValidator() {

		this(null, null);
	}

	/*
	 * Instance methods
	 */

	@Override
	public String getLocalSaltAndDigestSecretToken(XDIAddress senderXDIAddress) {

		// sender peer root

		XdiRoot senderXdiPeerRoot = XdiCommonRoot.findCommonRoot(this.getSecretTokenGraph()).getPeerRoot(senderXDIAddress, false);
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