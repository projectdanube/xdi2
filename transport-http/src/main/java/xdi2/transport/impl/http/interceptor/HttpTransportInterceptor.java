package xdi2.transport.impl.http.interceptor;

import java.io.IOException;

import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.transport.Transport;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.HttpRequest;
import xdi2.transport.impl.http.HttpResponse;
import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.impl.http.registry.MessagingTargetMount;

/**
 * Interceptor that is executed when it receives an incoming HTTP request.
 * 
 * @author markus
 */
public interface HttpTransportInterceptor extends Interceptor<Transport<?, ?>> {

	/**
	 * Run when the HTTP transport receives a GET request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processGetRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException;

	/**
	 * Run when the HTTP transport receives a POST request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processPostRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException;

	/**
	 * Run when the HTTP transport receives a PUT request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processPutRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException;

	/**
	 * Run when the HTTP transport receives a DELETE request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processDeleteRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException;
}
