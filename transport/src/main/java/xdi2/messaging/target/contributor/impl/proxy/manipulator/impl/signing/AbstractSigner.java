package xdi2.messaging.target.contributor.impl.proxy.manipulator.impl.signing;

import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractSigner implements Signer {

	@Override
	public void init(MessagingTarget messagingTarget, SigningProxyManipulator signatureMessageEnvelopeManipulator) throws Exception {

	}

	@Override
	public void shutdown(MessagingTarget messagingTarget, SigningProxyManipulator signatureMessageEnvelopeManipulator) throws Exception {

	}
}
