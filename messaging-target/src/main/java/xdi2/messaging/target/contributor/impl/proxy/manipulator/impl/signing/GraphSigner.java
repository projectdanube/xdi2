package xdi2.messaging.target.contributor.impl.proxy.manipulator.impl.signing;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * A Signer that can authenticate an XDI message using a "private key graph",
 * which contains sender addresses and private keys.
 */
public class GraphSigner extends PrivateKeySigner {

	private static Logger log = LoggerFactory.getLogger(GraphSigner.class.getName());

	private Graph privateKeyGraph;

	public GraphSigner(Graph privateKeyGraph) {

		super();

		this.privateKeyGraph = privateKeyGraph;
	}

	public GraphSigner() {

		super();

		this.privateKeyGraph = null;
	}

	@Override
	public void init(MessagingTarget messagingTarget, SigningProxyManipulator signatureMessageEnvelopeManipulator) throws Exception {

		super.init(messagingTarget, signatureMessageEnvelopeManipulator);

		if (this.getPrivateKeyGraph() == null && messagingTarget instanceof GraphMessagingTarget) this.setPrivateKeyGraph(((GraphMessagingTarget) messagingTarget).getGraph());
		if (this.getPrivateKeyGraph() == null) throw new Xdi2MessagingException("No private key graph.", null, null);
	}

	@Override
	public PrivateKey getPrivateKey(Message message) {

		XDIAddress senderXDIAddress = message.getSenderXDIAddress();
		if (senderXDIAddress == null) return null;

		// sender peer root

		XdiRoot senderXdiPeerRoot = XdiCommonRoot.findCommonRoot(this.getPrivateKeyGraph()).getPeerRoot(senderXDIAddress, false);
		senderXdiPeerRoot = senderXdiPeerRoot == null ? null : senderXdiPeerRoot.dereference();

		if (log.isDebugEnabled()) log.debug("Sender peer root: " + senderXdiPeerRoot);

		// look for private key in the graph

		ContextNode signaturePrivateKeyContextNode = senderXdiPeerRoot.getContextNode().getDeepContextNode(XDIAuthenticationConstants.XDI_ADD_MSG_SIG_KEYPAIR_PRIVATE_KEY, true);
		XdiAttribute signaturePrivateKeyXdiAttribute = signaturePrivateKeyContextNode == null ? null : XdiAttributeSingleton.fromContextNode(signaturePrivateKeyContextNode);
		signaturePrivateKeyXdiAttribute = signaturePrivateKeyXdiAttribute == null ? null : signaturePrivateKeyXdiAttribute.dereference();

		LiteralNode privateKeyLiteral = signaturePrivateKeyXdiAttribute == null ? null : signaturePrivateKeyXdiAttribute.getLiteralNode();

		String privateKeyString = privateKeyLiteral == null ? null : privateKeyLiteral.getLiteralDataString();
		if (privateKeyString == null) return null;

		PrivateKey privateKey;

		try {

			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(privateKeyString.getBytes(Charset.forName("UTF-8"))));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			privateKey = keyFactory.generatePrivate(keySpec);
		} catch (GeneralSecurityException ex) {

			if (log.isWarnEnabled()) log.warn("Invalid RSA private key " + privateKeyString + ": " + ex.getMessage(), ex);

			return null;
		}

		// done

		return privateKey;
	}

	/*
	 * Getters and setters
	 */

	public Graph getPrivateKeyGraph() {
	
		return this.privateKeyGraph;
	}

	public void setPrivateKeyGraph(Graph publicKeyGraph) {
	
		this.privateKeyGraph = publicKeyGraph;
	}
}
