package xdi2.client.manipulator;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.messaging.Message;

/**
 * This is used to manipulate messages before they are sent
 * by an XDIClient
 * 
 * @author markus
 */
public interface MessageManipulator extends Manipulator {

	/**
	 * Manipulate a message.
	 * @param message The message to manipulate.
	 */
	public void manipulate(Message message) throws Xdi2ClientException;
}
