package xdi2.transport.registry.impl.uri;

import xdi2.messaging.container.factory.impl.uri.UriMessagingContainerFactory;
import xdi2.transport.registry.MessagingContainerFactoryMount;

public class UriMessagingContainerFactoryMount implements MessagingContainerFactoryMount {

	private String messagingContainerFactoryPath;
	private UriMessagingContainerFactory messagingContainerFactory;

	public UriMessagingContainerFactoryMount(String messagingContainerFactoryPath, UriMessagingContainerFactory messagingContainerFactory) {

		this.messagingContainerFactoryPath = messagingContainerFactoryPath;
		this.messagingContainerFactory = messagingContainerFactory;
	}

	public UriMessagingContainerFactoryMount() {

	}

	public String getMessagingContainerFactoryPath() {

		return this.messagingContainerFactoryPath;
	}

	public void setMessagingContainerFactoryPath(String messagingContainerFactoryPath) {

		this.messagingContainerFactoryPath = messagingContainerFactoryPath;
	}

	@Override
	public UriMessagingContainerFactory getMessagingContainerFactory() {

		return this.messagingContainerFactory;
	}

	public void setMessagingContainerFactory(UriMessagingContainerFactory messagingContainerFactory) {

		this.messagingContainerFactory = messagingContainerFactory;
	}

	@Override
	public String toString() {

		return this.messagingContainerFactoryPath + " --> " + this.messagingContainerFactory.getClass().getSimpleName();
	}
}
