package xdi2.server.registry;

import xdi2.messaging.target.MessagingTarget;

public class MessagingTargetMount {

	private String messagingTargetPath;
	private MessagingTarget messagingTarget;

	public MessagingTargetMount(String messagingTargetPath, MessagingTarget messagingTarget) {

		this.messagingTargetPath = messagingTargetPath;
		this.messagingTarget = messagingTarget;
	}

	public MessagingTargetMount() {

	}

	public String getMessagingTargetPath() {

		return this.messagingTargetPath;
	}

	public void setMessagingTargetPath(String messagingTargetPath) {

		this.messagingTargetPath = messagingTargetPath;
	}

	public MessagingTarget getMessagingTarget() {

		return this.messagingTarget;
	}

	public void setMessagingTarget(MessagingTarget messagingTarget) {

		this.messagingTarget = messagingTarget;
	}

	@Override
	public String toString() {

		return this.messagingTargetPath + " --> " + this.messagingTarget.getClass().getSimpleName();
	}
}
