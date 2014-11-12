package xdi2.core.keys.source;

import java.security.GeneralSecurityException;
import java.security.PublicKey;

import xdi2.core.Graph;
import xdi2.core.keys.Keys;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphUtil;

/**
 * A Signer that can authenticate an XDI message using a "public key graph",
 * which contains sender addresses and public keys.
 */
public class GraphSignaturePublicKeySource extends AbstractKeySource<PublicKey> implements KeySource<PublicKey> {

	private Graph publicKeyGraph;

	public GraphSignaturePublicKeySource(Graph publicKeyGraph) {

		super();

		this.publicKeyGraph = publicKeyGraph;
	}

	public GraphSignaturePublicKeySource() {

		super();

		this.publicKeyGraph = null;
	}

	@Override
	protected PublicKey getKeyInternal() throws GeneralSecurityException {

		// find graph owner

		XDIAddress XDIaddress = GraphUtil.getOwnerXDIAddress(this.getPublicKeyGraph());

		// find public key

		PublicKey publicKey = Keys.getSignaturePublicKey(this.getPublicKeyGraph(), XDIaddress);

		// done

		return publicKey;
	}

	@Override
	protected PublicKey getKeyInternal(XDIAddress XDIaddress) throws GeneralSecurityException {

		// find public key

		PublicKey publicKey = Keys.getSignaturePublicKey(this.getPublicKeyGraph(), XDIaddress);

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
