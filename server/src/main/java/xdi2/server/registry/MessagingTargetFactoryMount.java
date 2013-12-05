package xdi2.server.registry;

import xdi2.server.factory.MessagingTargetFactory;

public class MessagingTargetFactoryMount {

	private String messagingTargetFactoryPath;
	private MessagingTargetFactory messagingTargetFactory;

	public MessagingTargetFactoryMount(String messagingTargetFactoryPath, MessagingTargetFactory messagingTargetFactory) {

		this.messagingTargetFactoryPath = messagingTargetFactoryPath;
		this.messagingTargetFactory = messagingTargetFactory;
	}

	public MessagingTargetFactoryMount() {

	}

	public String getMessagingTargetFactoryPath() {

		return this.messagingTargetFactoryPath;
	}

	public void setMessagingTargetFactoryPath(String messagingTargetFactoryPath) {

		this.messagingTargetFactoryPath = messagingTargetFactoryPath;
	}

	public MessagingTargetFactory getMessagingTargetFactory() {

		return this.messagingTargetFactory;
	}

	public void setMessagingTargetFactory(MessagingTargetFactory messagingTargetFactory) {

		this.messagingTargetFactory = messagingTargetFactory;
	}

	@Override
	public String toString() {

		return this.messagingTargetFactoryPath + " --> " + this.messagingTargetFactory.getClass().getSimpleName();
	}
}
