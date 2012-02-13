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
package xdi2.messaging.target.interceptor;

import xdi2.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.target.ExecutionContext;

public interface MessageEnvelopeInterceptor {

	/**
	 * Run before a message envelope is executed.
	 * @param messageEnvelope The message envelope to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return true, if the message envelope has been fully handled.
	 */
	public boolean before(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run after a message envelope is executed.
	 * @param messageEnvelope The message envelope to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return true, if the message envelope has been fully handled.
	 */
	public boolean after(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run if an exception occurs while a message envelope is executed.
	 */
	public void exception(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex);
}
