package xdi2.core.security.signature.validate;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.keys.Keys;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphUtil;

/**
 * This is an RSAPublicKeySignatureValidator that can validate an XDI RSASignature by
 * obtaining public keys from a "public key graph".
 */
public class RSAGraphPublicKeySignatureValidator extends RSAPublicKeySignatureValidator {

	private static Logger log = LoggerFactory.getLogger(RSAGraphPublicKeySignatureValidator.class.getName());

	private Graph publicKeyGraph;

	public RSAGraphPublicKeySignatureValidator(Graph publicKeyGraph) {

		super();

		this.publicKeyGraph = publicKeyGraph;
	}

	public RSAGraphPublicKeySignatureValidator() {

		super();

		this.publicKeyGraph = null;
	}

	@Override
	public PublicKey getPublicKey(XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// signer address

		if (signerXDIAddress == null) {

			signerXDIAddress = GraphUtil.getOwnerXDIAddress(this.getPublicKeyGraph());
		}

		// signer entity

		XdiEntity signerXdiEntity = XdiCommonRoot.findCommonRoot(this.getPublicKeyGraph()).getXdiEntity(signerXDIAddress, false);
		signerXdiEntity = signerXdiEntity == null ? null : signerXdiEntity.dereference();

		if (log.isDebugEnabled()) log.debug("Signer entity: " + signerXdiEntity + " in graph " + GraphUtil.getOwnerPeerRootXDIArc(this.getPublicKeyGraph()));
		if (signerXdiEntity == null) return null;

		// find public key

		PublicKey publicKey = Keys.getSignaturePublicKey(signerXdiEntity);

		// done

		return publicKey;
	}

	/*
	 * Getters and setters
	 */

	public Graph getPublicKeyGraph() {

		return this.publicKeyGraph;
	}

	public void setPublicKeyGraph(Graph publicKeyGraph) {

		this.publicKeyGraph = publicKeyGraph;
	}
}
