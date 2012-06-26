package xdi2.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.messaging.target.ExecutionContext;

/**
 * Methods for storing state related to the XDI endpoint servlet.
 */
public class ServletExecutionContext {

	private static final String EXECUTIONCONTEXT_KEY_ENDPOINTSERVLET = ServletExecutionContext.class.getCanonicalName() + "#endpointservlet";
	private static final String EXECUTIONCONTEXT_KEY_HTTPSERVLETREQUEST = ServletExecutionContext.class.getCanonicalName() + "#httpservletrequest";
	private static final String EXECUTIONCONTEXT_KEY_HTTPSERVLETRESPONSE = ServletExecutionContext.class.getCanonicalName() + "#httpservletresponse";

	private ServletExecutionContext() { }

	public static EndpointServlet getEndpointServlet(ExecutionContext executionContext) {

		return (EndpointServlet) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_ENDPOINTSERVLET);
	}

	public static void setEndpointServlet(ExecutionContext executionContext, EndpointServlet endpointServlet) {

		executionContext.setMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_ENDPOINTSERVLET, endpointServlet);
	}	

	public static HttpServletRequest getHttpServletRequest(ExecutionContext executionContext) {

		return (HttpServletRequest) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_HTTPSERVLETREQUEST);
	}

	public static void setHttpServletRequest(ExecutionContext executionContext, HttpServletRequest request) {

		executionContext.setMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_HTTPSERVLETREQUEST, request);
	}	

	public static HttpServletResponse getHttpServletResponse(ExecutionContext executionContext) {

		return (HttpServletResponse) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_HTTPSERVLETRESPONSE);
	}

	public static void setHttpServletResponse(ExecutionContext executionContext, HttpServletResponse response) {

		executionContext.setMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_HTTPSERVLETRESPONSE, response);
	}	
}
