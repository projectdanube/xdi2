package xdi2.messaging.target.interceptor.impl;

import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;

public class LinkContractAuthenticationInterceptor extends AbstractInterceptor implements MessageInterceptor {	
	@Override
	public boolean before(Message message, MessageResult messageResult,
			ExecutionContext executionContext) throws Xdi2MessagingException {

		//done
		//for simple shared secret authentication, there's nothing to be done here. However, for more sophisticated authentication mechanisms, this interceptor or it's subclasses should be helpful. 
		return false;
	}

	@Override
	public boolean after(Message message, MessageResult messageResult,
			ExecutionContext executionContext) throws Xdi2MessagingException {
		// done
		return false;
	}	
}

