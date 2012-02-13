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

import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;

public interface OperationInterceptor {

	/**
	 * Run before an operation is executed.
	 * @param operation The operation to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return true, if the operation has been fully handled.
	 */
	public boolean before(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run after an operation is executed.
	 * @param operation The operation to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return true, if the operation has been fully handled.
	 */
	public boolean after(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
