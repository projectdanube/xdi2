package xdi2.core.security.signature.validate;

import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.keys.Keys;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphUtil;

/**
 * This is an AESSecretKeySignatureValidator that can validate an XDI AESSignature by
 * obtaining secret keys from a "secret key graph".
 */
public class AESGraphSecretKeySignatureValidator extends AESSecretKeySignatureValidator {

	private static Logger log = LoggerFactory.getLogger(AESGraphSecretKeySignatureValidator.class.getName());

	private Graph secretKeyGraph;

	public AESGraphSecretKeySignatureValidator(Graph secretKeyGraph) {

		super();

		this.secretKeyGraph = secretKeyGraph;
	}

	public AESGraphSecretKeySignatureValidator() {

		super();

		this.secretKeyGraph = null;
	}

	@Override
	public SecretKey getSecretKey(XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// signer address

		if (signerXDIAddress == null) {

			signerXDIAddress = GraphUtil.getOwnerXDIAddress(this.getSecretKeyGraph());
		}

		// signer entity

		XdiEntity signerXdiEntity = XdiCommonRoot.findCommonRoot(this.getSecretKeyGraph()).getXdiEntity(signerXDIAddress, false);
		signerXdiEntity = signerXdiEntity == null ? null : signerXdiEntity.dereference();

		if (log.isDebugEnabled()) log.debug("Signer entity: " + signerXdiEntity + " in graph " + GraphUtil.getOwnerPeerRootXDIArc(this.getSecretKeyGraph()));
		if (signerXdiEntity == null) return null;

		// find secret key

		SecretKey secretKey = Keys.getSignatureSecretKey(signerXdiEntity);

		// done

		return secretKey;
	}

	/*
	 * Getters and setters
	 */

	public Graph getSecretKeyGraph() {

		return this.secretKeyGraph;
	}

	public void setSecretKeyGraph(Graph secretKeyGraph) {

		this.secretKeyGraph = secretKeyGraph;
	}
}
