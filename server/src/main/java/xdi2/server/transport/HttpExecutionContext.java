package xdi2.server.transport;

import xdi2.messaging.target.ExecutionContext;

/**
 * Methods for storing state related to the XDI HTTP transport.
 */
public class HttpExecutionContext {

	private static final String EXECUTIONCONTEXT_KEY_HTTPTRANSPORT = HttpExecutionContext.class.getCanonicalName() + "#httptransport";
	private static final String EXECUTIONCONTEXT_KEY_HTTPREQUEST = HttpExecutionContext.class.getCanonicalName() + "#httprequest";
	private static final String EXECUTIONCONTEXT_KEY_HTTPRESPONSE = HttpExecutionContext.class.getCanonicalName() + "#httpresponse";

	private HttpExecutionContext() { }

	public static HttpTransport getHttpTransport(ExecutionContext executionContext) {

		return (HttpTransport) executionContext.getExecutionContextAttribute(EXECUTIONCONTEXT_KEY_HTTPTRANSPORT);
	}

	public static void putHttpTransport(ExecutionContext executionContext, HttpTransport httpTransport) {

		executionContext.putExecutionContextAttribute(EXECUTIONCONTEXT_KEY_HTTPTRANSPORT, httpTransport);
	}	

	public static HttpRequest getHttpRequest(ExecutionContext executionContext) {

		return (HttpRequest) executionContext.getExecutionContextAttribute(EXECUTIONCONTEXT_KEY_HTTPREQUEST);
	}

	public static void putHttpRequest(ExecutionContext executionContext, HttpRequest request) {

		executionContext.putExecutionContextAttribute(EXECUTIONCONTEXT_KEY_HTTPREQUEST, request);
	}	

	public static HttpResponse getHttpResponse(ExecutionContext executionContext) {

		return (HttpResponse) executionContext.getExecutionContextAttribute(EXECUTIONCONTEXT_KEY_HTTPRESPONSE);
	}

	public static void putHttpResponse(ExecutionContext executionContext, HttpResponse response) {

		executionContext.putExecutionContextAttribute(EXECUTIONCONTEXT_KEY_HTTPRESPONSE, response);
	}	
}
