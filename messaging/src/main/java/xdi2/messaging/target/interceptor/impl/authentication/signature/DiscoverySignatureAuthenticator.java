package xdi2.messaging.target.interceptor.impl.authentication.signature;

import java.security.PublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * A SignatureAuthenticator that can authenticate an XDI message by obtaining
 * public keys using XDI discovery.
 */
public class DiscoverySignatureAuthenticator extends PublicKeySignatureAuthenticator {

	private static Logger log = LoggerFactory.getLogger(DiscoverySignatureAuthenticator.class.getName());

	private XDIDiscoveryClient xdiDiscoveryClient;

	public DiscoverySignatureAuthenticator(XDIDiscoveryClient xdiDiscovery) {

		super();

		this.xdiDiscoveryClient = xdiDiscovery;
	}

	public DiscoverySignatureAuthenticator() {

		super();
	}

	@Override
	public DiscoverySignatureAuthenticator instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new public key authenticator

		DiscoverySignatureAuthenticator authenticator = new DiscoverySignatureAuthenticator();

		// set the discovery client

		if (this.getPublicKeyGraph() == null) {

			if (prototypingContext.getMessagingTarget() instanceof GraphMessagingTarget) {

				authenticator.setPublicKeyGraph(((GraphMessagingTarget) prototypingContext.getMessagingTarget()).getGraph());
			} else {

				throw new Xdi2RuntimeException("No public key graph.");
			}
		} else {

			authenticator.setPublicKeyGraph(this.getPublicKeyGraph());
		}

		// done

		return authenticator;
	}

	@Override
	public PublicKey getPublicKey(Message message) {

		XDI3Segment senderXri = message.getSenderXri();
		if (senderXri == null) return null;

		// perform discovery


		// done

		return null;
	}

	public XDIDiscoveryClient getXdiDiscoveryClient() {

		return xdiDiscoveryClient;
	}

	public void setXdiDiscoveryClient(XDIDiscoveryClient xdiDiscoveryClient) {

		this.xdiDiscoveryClient = xdiDiscoveryClient;
	}
}