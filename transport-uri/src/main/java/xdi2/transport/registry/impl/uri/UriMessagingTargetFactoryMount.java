package xdi2.transport.registry.impl.uri;

import xdi2.messaging.target.factory.impl.uri.UriMessagingTargetFactory;
import xdi2.transport.registry.MessagingTargetFactoryMount;

public class UriMessagingTargetFactoryMount implements MessagingTargetFactoryMount {

	private String messagingTargetFactoryPath;
	private UriMessagingTargetFactory messagingTargetFactory;

	public UriMessagingTargetFactoryMount(String messagingTargetFactoryPath, UriMessagingTargetFactory messagingTargetFactory) {

		this.messagingTargetFactoryPath = messagingTargetFactoryPath;
		this.messagingTargetFactory = messagingTargetFactory;
	}

	public UriMessagingTargetFactoryMount() {

	}

	public String getMessagingTargetFactoryPath() {

		return this.messagingTargetFactoryPath;
	}

	public void setMessagingTargetFactoryPath(String messagingTargetFactoryPath) {

		this.messagingTargetFactoryPath = messagingTargetFactoryPath;
	}

	@Override
	public UriMessagingTargetFactory getMessagingTargetFactory() {

		return this.messagingTargetFactory;
	}

	public void setMessagingTargetFactory(UriMessagingTargetFactory messagingTargetFactory) {

		this.messagingTargetFactory = messagingTargetFactory;
	}

	@Override
	public String toString() {

		return this.messagingTargetFactoryPath + " --> " + this.messagingTargetFactory.getClass().getSimpleName();
	}
}
