package xdi2.messaging.target.interceptor.impl.authentication.signature;

import java.security.PublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.syntax.XDIAddress;
import xdi2.discovery.XDIDiscoveryClient;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.Message;

/**
 * A SignatureAuthenticator that can authenticate an XDI message by obtaining
 * public keys using XDI discovery.
 */
public class DiscoverySignatureAuthenticator extends PublicKeySignatureAuthenticator {

	private static Logger log = LoggerFactory.getLogger(DiscoverySignatureAuthenticator.class.getName());

	public static final XDIDiscoveryClient DEFAULT_DISCOVERY_CLIENT = XDIDiscoveryClient.DEFAULT_DISCOVERY_CLIENT;

	private XDIDiscoveryClient xdiDiscoveryClient;

	public DiscoverySignatureAuthenticator(XDIDiscoveryClient xdiDiscoveryClient) {

		super();

		this.xdiDiscoveryClient = xdiDiscoveryClient;
	}

	public DiscoverySignatureAuthenticator() {

		this(DEFAULT_DISCOVERY_CLIENT);
	}

	@Override
	public PublicKey getPublicKey(Message message) {

		XDIAddress senderXDIAddress = message.getSenderXDIAddress();
		if (senderXDIAddress == null) return null;

		// perform discovery

		PublicKey publicKey = null;

		try {

			XDIDiscoveryResult xdiDiscoveryResult = this.getXdiDiscoveryClient().discover(senderXDIAddress);

			if (xdiDiscoveryResult != null) publicKey = xdiDiscoveryResult.getSignaturePublicKey();
		} catch (Xdi2ClientException ex) {

			if (log.isWarnEnabled()) log.warn("Cannot discover public key for " + senderXDIAddress + ": " + ex.getMessage(), ex);

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
