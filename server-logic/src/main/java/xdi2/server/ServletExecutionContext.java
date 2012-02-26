package xdi2.server;

import java.util.Map;

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

		Map<String, Object> messageEnvelopeAttributes = executionContext.getMessageEnvelopeAttributes();

		return (EndpointServlet) messageEnvelopeAttributes.get(EXECUTIONCONTEXT_KEY_ENDPOINTSERVLET);
	}

	public static void setEndpointServlet(ExecutionContext executionContext, EndpointServlet endpointServlet) {

		Map<String, Object> messageEnvelopeAttributes = executionContext.getMessageEnvelopeAttributes();

		messageEnvelopeAttributes.put(EXECUTIONCONTEXT_KEY_ENDPOINTSERVLET, endpointServlet);
	}	

	public static HttpServletRequest getHttpServletRequest(ExecutionContext executionContext) {

		Map<String, Object> messageEnvelopeAttributes = executionContext.getMessageEnvelopeAttributes();

		return (HttpServletRequest) messageEnvelopeAttributes.get(EXECUTIONCONTEXT_KEY_HTTPSERVLETREQUEST);
	}

	public static void setHttpServletRequest(ExecutionContext executionContext, HttpServletRequest request) {

		Map<String, Object> messageEnvelopeAttributes = executionContext.getMessageEnvelopeAttributes();

		messageEnvelopeAttributes.put(EXECUTIONCONTEXT_KEY_HTTPSERVLETREQUEST, request);
	}	

	public static HttpServletResponse getHttpServletResponse(ExecutionContext executionContext) {

		Map<String, Object> messageEnvelopeAttributes = executionContext.getMessageEnvelopeAttributes();

		return (HttpServletResponse) messageEnvelopeAttributes.get(EXECUTIONCONTEXT_KEY_HTTPSERVLETRESPONSE);
	}

	public static void setHttpServletResponse(ExecutionContext executionContext, HttpServletResponse response) {

		Map<String, Object> messageEnvelopeAttributes = executionContext.getMessageEnvelopeAttributes();

		messageEnvelopeAttributes.put(EXECUTIONCONTEXT_KEY_HTTPSERVLETRESPONSE, response);
	}	
}
