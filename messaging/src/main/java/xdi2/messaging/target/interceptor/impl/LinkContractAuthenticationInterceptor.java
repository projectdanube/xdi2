package xdi2.messaging.target.interceptor.impl;

import xdi2.core.Graph;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;

public class LinkContractAuthenticationInterceptor extends AbstractInterceptor implements MessageInterceptor {

	private Graph messageGraph;
	@Override
	public boolean before(Message message, MessageResult messageResult,
			ExecutionContext executionContext) throws Xdi2MessagingException {
		
		String authToken = message.getAuthenticationToken();
		if(authToken != null && !authToken.isEmpty()){
			putAuthtoken(executionContext, authToken);
		}
		
		return false;
	}

	@Override
	public boolean after(Message message, MessageResult messageResult,
			ExecutionContext executionContext) throws Xdi2MessagingException {
		// done
		return false;
	}
	
	public void setMessageGraph(Graph g){
		this.messageGraph = g;
	}
	public Graph getMessageGraph(){
		return this.messageGraph;
	}
	
	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_AUTH_TOKEN_PER_MESSAGE = LinkContractAuthenticationInterceptor.class.getCanonicalName() + "#authtokenpermessage";

	public static String getAuthToken(ExecutionContext executionContext) {

		return (String) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_AUTH_TOKEN_PER_MESSAGE);
	}

	private static void putAuthtoken(ExecutionContext executionContext, String token) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_AUTH_TOKEN_PER_MESSAGE, token);
	}

}

