package xdi2.core.security.signature.validate;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;

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
 * This is an RSAPublicKeySignatureValidator that validate an XDI RSASignature by
 * obtaining public keys using an XDI agent.
 */
public class RSAAgentPublicKeySignatureValidator extends RSAPublicKeySignatureValidator {

	private static Logger log = LoggerFactory.getLogger(RSAAgentPublicKeySignatureValidator.class.getName());

	private XDIAgent xdiAgent;

	public RSAAgentPublicKeySignatureValidator(XDIAgent xdiAgent) {

		super();

		this.xdiAgent = xdiAgent;
	}

	public RSAAgentPublicKeySignatureValidator() {

		this(null);
	}

	@Override
	public RSAPublicKey getPublicKey(XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// retrieve the key

		RSAPublicKey publicKey = null;

		try {

			XDIAddress publicKeyXDIAddress = XDIAddressUtil.concatXDIAddresses(signerXDIAddress, XDISecurityConstants.XDI_ADD_MSG_SIG_KEYPAIR_PUBLIC_KEY);

			ContextNode contextNode = this.getXdiAgent().get(publicKeyXDIAddress, new SetLinkContractMessageManipulator(PublicLinkContract.class));
			if (contextNode == null) return null;

			String publicKeyString = contextNode.getLiteralDataString();
			if (publicKeyString == null) return null;

			publicKey = rsaPublicKeyFromPublicKeyString(publicKeyString);
		} catch (Xdi2ClientException ex) {

			if (log.isWarnEnabled()) log.warn("Cannot retrieve public key for " + signerXDIAddress + ": " + ex.getMessage(), ex);
			return null;
		}

		// done

		return publicKey;
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
