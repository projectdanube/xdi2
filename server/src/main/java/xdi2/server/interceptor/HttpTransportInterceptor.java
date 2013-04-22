package xdi2.server.interceptor;

import java.io.IOException;

import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.transport.HttpRequest;
import xdi2.server.transport.HttpResponse;
import xdi2.server.transport.HttpTransport;

/**
 * Interceptor that is executed when the HTTP transport is initialized, destroyed, or when it receives an incoming HTTP request.
 * 
 * @author markus
 */
public interface HttpTransportInterceptor extends Interceptor {

	/**
	 * This method gets called when the HTTP transport is initialized.
	 */
	public void init(HttpTransport httpTransport) throws Xdi2ServerException;

	/**
	 * This method gets called when the HTTP transport is destroyed.
	 */
	public void destroy(HttpTransport httpTransport);

	/**
	 * Run when the HTTP transport receives a GET request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processGetRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException;

	/**
	 * Run when the HTTP transport receives a POST request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processPostRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException;

	/**
	 * Run when the HTTP transport receives a PUT request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processPutRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException;

	/**
	 * Run when the HTTP transport receives a DELETE request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processDeleteRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException;
}
