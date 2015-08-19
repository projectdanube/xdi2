package xdi2.core.security.sign;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.keys.Keys;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphAware;
import xdi2.core.util.GraphUtil;

/**
 * This is an RSAPrivateKeySignatureCreator that create an XDI RSASignature by
 * obtaining private keys from a "private key graph".
 */
public class RSAGraphPrivateKeySignatureCreator extends RSAPrivateKeySignatureCreator implements GraphAware {

	private static Logger log = LoggerFactory.getLogger(RSAGraphPrivateKeySignatureCreator.class.getName());

	private Graph privateKeyGraph;

	public RSAGraphPrivateKeySignatureCreator(Graph privateKeyGraph) {

		super();

		this.privateKeyGraph = privateKeyGraph;
	}

	public RSAGraphPrivateKeySignatureCreator() {

		super();

		this.privateKeyGraph = null;
	}

	@Override
	public PrivateKey getPrivateKey(XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// signer address

		if (signerXDIAddress == null) {

			signerXDIAddress = GraphUtil.getOwnerXDIAddress(this.getPrivateKeyGraph());
		}

		// signer entity

		XdiEntity signerXdiEntity = XdiCommonRoot.findCommonRoot(this.getPrivateKeyGraph()).getXdiEntity(signerXDIAddress, false);
		signerXdiEntity = signerXdiEntity == null ? null : signerXdiEntity.dereference();

		if (log.isDebugEnabled()) log.debug("Signer entity: " + signerXdiEntity);

		if (signerXdiEntity == null) return null;

		// find private key

		PrivateKey privateKey = Keys.getSignaturePrivateKey(signerXdiEntity);

		// done

		return privateKey;
	}

	/*
	 * GraphAware
	 */

	@Override
	public void setGraph(Graph graph) {

		if (this.getPrivateKeyGraph() == null) this.setPrivateKeyGraph(graph);
	}

	/*
	 * Getters and setters
	 */

	public Graph getPrivateKeyGraph() {

		return this.privateKeyGraph;
	}

	public void setPrivateKeyGraph(Graph privateKeyGraph) {

		this.privateKeyGraph = privateKeyGraph;
	}
}
