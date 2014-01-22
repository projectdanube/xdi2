package xdi2.messaging.target.contributor.impl.proxy.manipulator.impl.signing;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
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

		XDI3Segment senderXri = message.getSenderXri();
		if (senderXri == null) return null;

		// sender peer root

		XdiRoot senderXdiPeerRoot = XdiLocalRoot.findLocalRoot(this.getPrivateKeyGraph()).findPeerRoot(senderXri, false);
		senderXdiPeerRoot = senderXdiPeerRoot == null ? null : senderXdiPeerRoot.dereference();

		if (log.isDebugEnabled()) log.debug("Sender peer root: " + senderXdiPeerRoot);

		// look for private key in the graph

		XdiAttribute signaturePrivateKeyXdiAttribute = senderXdiPeerRoot == null ? null : XdiAttributeSingleton.fromContextNode(senderXdiPeerRoot.getContextNode().getDeepContextNode(XDIAuthenticationConstants.XRI_S_MSG_SIG_KEYPAIR_PRIVATE_KEY));
		signaturePrivateKeyXdiAttribute = signaturePrivateKeyXdiAttribute == null ? null : signaturePrivateKeyXdiAttribute.dereference();

		XdiValue signaturePrivateKeyXdiValue = signaturePrivateKeyXdiAttribute == null ? null : signaturePrivateKeyXdiAttribute.getXdiValue(false);
		signaturePrivateKeyXdiValue = signaturePrivateKeyXdiValue == null ? null : signaturePrivateKeyXdiValue.dereference();
		
		Literal privateKeyLiteral = signaturePrivateKeyXdiValue == null ? null : signaturePrivateKeyXdiValue.getContextNode().getLiteral();

		String privateKeyString = privateKeyLiteral == null ? null : privateKeyLiteral.getLiteralDataString();
		if (privateKeyString == null) return null;

		PrivateKey privateKey;

		try {

			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(privateKeyString));
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
