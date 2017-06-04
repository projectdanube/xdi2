package xdi2.core.security.signature.create;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.XDIAgent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.manipulator.impl.SetLinkContractMessageManipulator;
import xdi2.core.ContextNode;
import xdi2.core.constants.XDISecurityConstants;
import xdi2.core.features.linkcontracts.instance.PublicLinkContract;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.XDIAddressUtil;

/**
 * This is an RSAPrivateKeySignatureCreator that create an XDI RSASignature by
 * obtaining private keys using an XDI agent.
 */
public class RSAAgentPrivateKeySignatureCreator extends RSAPrivateKeySignatureCreator {

	private static Logger log = LoggerFactory.getLogger(RSAAgentPrivateKeySignatureCreator.class.getName());

	private XDIAgent xdiAgent;

	public RSAAgentPrivateKeySignatureCreator(XDIAgent xdiAgent) {

		super();

		this.xdiAgent = xdiAgent;
	}

	public RSAAgentPrivateKeySignatureCreator() {

		this(null);
	}

	@Override
	public RSAPrivateKey getPrivateKey(XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// retrieve the key

		RSAPrivateKey privateKey = null;

		try {

			XDIAddress privateKeyXDIAddress = XDIAddressUtil.concatXDIAddresses(signerXDIAddress, XDISecurityConstants.XDI_ADD_MSG_SIG_KEYPAIR_PRIVATE_KEY);

			ContextNode contextNode = this.getXdiAgent().get(privateKeyXDIAddress, new SetLinkContractMessageManipulator(PublicLinkContract.class));
			if (contextNode == null) return null;

			String privateKeyString = contextNode.getLiteralDataString();
			if (privateKeyString == null) return null;

			privateKey = rsaPrivateKeyFromPrivateKeyString(privateKeyString);
		} catch (Xdi2ClientException ex) {

			if (log.isWarnEnabled()) log.warn("Cannot retrieve private key for " + signerXDIAddress + ": " + ex.getMessage(), ex);
			return null;
		}

		// done

		return privateKey;
	}

	/*
	 * Getters and setters
	 */

	public XDIAgent getXdiAgent() {

		return this.xdiAgent;
	}

	public void setXdiAgent(XDIAgent xdiAgent) {

		this.xdiAgent = xdiAgent;
	}
}
