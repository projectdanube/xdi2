package xdi2.transport.registry.impl.uri;

import xdi2.messaging.container.MessagingContainer;
import xdi2.transport.registry.MessagingContainerMount;

public class UriMessagingContainerMount implements MessagingContainerMount {

	private String messagingContainerPath;
	private MessagingContainer messagingContainer;

	public UriMessagingContainerMount(String messagingContainerPath, MessagingContainer messagingContainer) {

		this.messagingContainerPath = messagingContainerPath;
		this.messagingContainer = messagingContainer;
	}

	public UriMessagingContainerMount() {

	}

	public String getMessagingContainerPath() {

		return this.messagingContainerPath;
	}

	public void setMessagingContainerPath(String messagingContainerPath) {

		this.messagingContainerPath = messagingContainerPath;
	}

	@Override
	public MessagingContainer getMessagingContainer() {

		return this.messagingContainer;
	}

	public void setMessagingContainer(MessagingContainer messagingContainer) {

		this.messagingContainer = messagingContainer;
	}

	@Override
	public String toString() {

		return this.messagingContainerPath + " --> " + this.messagingContainer.getClass().getSimpleName();
	}
}
