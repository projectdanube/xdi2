package xdi2.core.security.signature.validate;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.syntax.XDIAddress;
import xdi2.discovery.XDIDiscoveryClient;
import xdi2.discovery.XDIDiscoveryResult;

/**
 * This is an RSAPublicKeySignatureValidator that validate an XDI RSASignature by
 * obtaining public keys using XDI discovery.
 */
public class RSADiscoveryPublicKeySignatureValidator extends RSAPublicKeySignatureValidator {

	private static Logger log = LoggerFactory.getLogger(RSADiscoveryPublicKeySignatureValidator.class.getName());

	public static final XDIDiscoveryClient DEFAULT_DISCOVERY_CLIENT = XDIDiscoveryClient.DEFAULT_DISCOVERY_CLIENT;

	private XDIDiscoveryClient xdiDiscoveryClient;

	public RSADiscoveryPublicKeySignatureValidator(XDIDiscoveryClient xdiDiscoveryClient) {

		super();

		this.xdiDiscoveryClient = xdiDiscoveryClient;
	}

	public RSADiscoveryPublicKeySignatureValidator() {

		this(DEFAULT_DISCOVERY_CLIENT);
	}

	@Override
	public RSAPublicKey getPublicKey(XDIAddress signerXDIAddress) throws GeneralSecurityException {

		// perform discovery

		RSAPublicKey publicKey = null;

		try {

			XDIDiscoveryResult xdiDiscoveryResult = this.getXdiDiscoveryClient().discover(signerXDIAddress);

			String publicKeyString = xdiDiscoveryResult.getSignaturePublicKey();
			if (publicKeyString == null) return null;

			publicKey = rsaPublicKeyFromPublicKeyString(publicKeyString);
		} catch (Xdi2ClientException ex) {

			if (log.isWarnEnabled()) log.warn("Cannot discover public key for " + signerXDIAddress + ": " + ex.getMessage(), ex);

			return null;
		}

		// done

		return publicKey;
	}

	/*
	 * Getters and setters
	 */

	public XDIDiscoveryClient getXdiDiscoveryClient() {

		return this.xdiDiscoveryClient;
	}

	public void setXdiDiscoveryClient(XDIDiscoveryClient xdiDiscoveryClient) {

		this.xdiDiscoveryClient = xdiDiscoveryClient;
	}
}
