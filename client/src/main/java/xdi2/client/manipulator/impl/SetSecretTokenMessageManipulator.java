package xdi2.client.manipulator.impl;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.messaging.Message;

public class SetSecretTokenMessageManipulator extends AbstractMessageManipulator implements MessageManipulator {

	private String secretToken;

	public SetSecretTokenMessageManipulator(String secretToken) {

		this.secretToken = secretToken;
	}

	public SetSecretTokenMessageManipulator() {

		this.secretToken = null;
	}

	@Override
	public void manipulate(Message message) throws Xdi2ClientException {

		if (this.getSecretToken() != null) {

			message.setSecretToken(this.getSecretToken());
		}
	}

	/*
	 * Getters and setters
	 */

	public String getSecretToken() {

		return this.secretToken;
	}

	public void setSecretToken(String secretToken) {

		this.secretToken = secretToken;
	}
}
