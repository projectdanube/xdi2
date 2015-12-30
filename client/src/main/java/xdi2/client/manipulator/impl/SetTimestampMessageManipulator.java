package xdi2.client.manipulator.impl;

import java.util.Date;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.ManipulationContext;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.messaging.Message;

public class SetTimestampMessageManipulator extends AbstractMessageManipulator implements MessageManipulator {

	private Date timestamp;

	public SetTimestampMessageManipulator(Date timestamp) {

		this.timestamp = timestamp;
	}

	public SetTimestampMessageManipulator() {

		this.timestamp = null;
	}

	@Override
	public void manipulate(Message message, ManipulationContext manipulationContext) throws Xdi2ClientException {

		if (this.getTimestamp() != null) {

			message.setTimestamp(this.getTimestamp());
		} else {

			message.setTimestamp(new Date());
		}
	}

	/*
	 * Getters and setters
	 */

	public Date getTimestamp() {

		return this.timestamp;
	}

	public void setSecretToken(Date timestamp) {

		this.timestamp = timestamp;
	}
}
