package xdi2.transport.impl.http.registry;

import xdi2.transport.impl.http.factory.HttpMessagingTargetFactory;
import xdi2.transport.registry.MessagingTargetFactoryMount;

public class HttpMessagingTargetFactoryMount implements MessagingTargetFactoryMount {

	private String messagingTargetFactoryPath;
	private HttpMessagingTargetFactory messagingTargetFactory;

	public HttpMessagingTargetFactoryMount(String messagingTargetFactoryPath, HttpMessagingTargetFactory messagingTargetFactory) {

		this.messagingTargetFactoryPath = messagingTargetFactoryPath;
		this.messagingTargetFactory = messagingTargetFactory;
	}

	public HttpMessagingTargetFactoryMount() {

	}

	public String getMessagingTargetFactoryPath() {

		return this.messagingTargetFactoryPath;
	}

	public void setMessagingTargetFactoryPath(String messagingTargetFactoryPath) {

		this.messagingTargetFactoryPath = messagingTargetFactoryPath;
	}

	@Override
	public HttpMessagingTargetFactory getMessagingTargetFactory() {

		return this.messagingTargetFactory;
	}

	public void setMessagingTargetFactory(HttpMessagingTargetFactory messagingTargetFactory) {

		this.messagingTargetFactory = messagingTargetFactory;
	}

	@Override
	public String toString() {

		return this.messagingTargetFactoryPath + " --> " + this.messagingTargetFactory.getClass().getSimpleName();
	}
}
