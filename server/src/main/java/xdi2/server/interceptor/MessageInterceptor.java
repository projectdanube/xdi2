/*******************************************************************************
 * Copyright (c) 2010 Markus Sabadello
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Sabadello - Initial API and implementation
 *******************************************************************************/
package xdi2.server.interceptor;

import xdi2.exceptions.Xdi2MessagingException;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.server.ExecutionContext;

public interface MessageInterceptor {

	/**
	 * Run before a message is executed.
	 * @param message The message to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return true, if the message has been fully handled.
	 */
	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run after a message is executed.
	 * @param message The message to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return true, if the message has been fully handled.
	 */
	public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
