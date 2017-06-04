package xdi2.core.security.signature.create;

import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;

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
 * This is an AESSecretKeySignatureCreator that create an XDI AESSignature by
 * obtaining secret keys using an XDI agent.
 */
public class AESAgentSecretKeySignatureCreator extends AESSecretKeySignatureCreator {

	private static Logger log = LoggerFactory.getLogger(AESAgentSecretKeySignatureCreator.class.getName());

	private XDIAgent xdiAgent;

	public AESAgentSecretKeySignatureCreator(XDIAgent xdiAgent) {

		super();

		this.xdiAgent = xdiAgent;
	}

	public AESAgentSecretKeySignatureCreator() {

		this(null);
	}

	@Override
	public SecretKey getSecretKey(XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// retrieve the key

		SecretKey secretKey = null;

		try {

			XDIAddress secretKeyXDIAddress = XDIAddressUtil.concatXDIAddresses(signerXDIAddress, XDISecurityConstants.XDI_ADD_SECRET_KEY);

			ContextNode contextNode = this.getXdiAgent().get(secretKeyXDIAddress, new SetLinkContractMessageManipulator(PublicLinkContract.class));
			if (contextNode == null) return null;

			String secretKeyString = contextNode.getLiteralDataString();
			if (secretKeyString == null) return null;

			secretKey = aesSecretKeyFromSecretKeyString(secretKeyString);
		} catch (Xdi2ClientException ex) {

			if (log.isWarnEnabled()) log.warn("Cannot retrieve secret key for " + signerXDIAddress + ": " + ex.getMessage(), ex);
			return null;
		}

		// done

		return secretKey;
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
