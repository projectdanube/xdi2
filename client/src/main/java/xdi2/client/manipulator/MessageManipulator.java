package xdi2.client.manipulator;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.ManipulationContext;
import xdi2.client.impl.XDIAbstractClient;
import xdi2.messaging.Message;

/**
 * This is used to manipulate messages e.g. before sending them or after receiving them.
 * @see XDIAbstractClient
 * @see ManipulatingInterceptor
 * 
 * @author markus
 */
public interface MessageManipulator extends Manipulator {

	/**
	 * Manipulate a message.
	 * @param message The message to manipulate.
	 * @param manipulationContext The current manipulation context.
	 */
	public void manipulate(Message message, ManipulationContext manipulationContext) throws Xdi2ClientException;
}
