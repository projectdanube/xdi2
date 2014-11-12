package xdi2.core.keys.source;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import xdi2.core.Graph;
import xdi2.core.keys.Keys;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphUtil;

/**
 * A Signer that can authenticate an XDI message using a "private key graph",
 * which contains sender addresses and private keys.
 */
public class GraphSignaturePrivateKeySource extends AbstractKeySource<PrivateKey> implements KeySource<PrivateKey> {

	private Graph privateKeyGraph;

	public GraphSignaturePrivateKeySource(Graph privateKeyGraph) {

		super();

		this.privateKeyGraph = privateKeyGraph;
	}

	public GraphSignaturePrivateKeySource() {

		super();

		this.privateKeyGraph = null;
	}

	@Override
	protected PrivateKey getKeyInternal() throws GeneralSecurityException {

		// find graph owner

		XDIAddress XDIaddress = GraphUtil.getOwnerXDIAddress(this.getPrivateKeyGraph());

		// find private key

		PrivateKey privateKey = Keys.getSignaturePrivateKey(this.getPrivateKeyGraph(), XDIaddress);

		// done

		return privateKey;
	}

	@Override
	protected PrivateKey getKeyInternal(XDIAddress XDIaddress) throws GeneralSecurityException {

		// find private key

		PrivateKey privateKey = Keys.getSignaturePrivateKey(this.getPrivateKeyGraph(), XDIaddress);

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
