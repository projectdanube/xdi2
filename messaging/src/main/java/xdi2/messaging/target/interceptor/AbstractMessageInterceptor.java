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
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.target.ExecutionContext;

public abstract class AbstractMessageInterceptor implements MessageInterceptor {

	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
