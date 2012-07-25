package xdi2.server.interceptor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.Interceptor;

public interface EndpointServletInterceptor extends Interceptor {

	/**
	 * Run when the endpoint servlet receives a GET request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processGetRequest(HttpServletRequest request, HttpServletResponse response, String path, MessagingTarget messagingTarget) throws ServletException, IOException;

	/**
	 * Run when the endpoint servlet receives a POST request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processPostRequest(HttpServletRequest request, HttpServletResponse response, String path, MessagingTarget messagingTarget) throws ServletException, IOException;

	/**
	 * Run when the endpoint servlet receives a PUT request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processPutRequest(HttpServletRequest request, HttpServletResponse response, String path, MessagingTarget messagingTarget) throws ServletException, IOException;

	/**
	 * Run when the endpoint servlet receives a DELETE request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processDeleteRequest(HttpServletRequest request, HttpServletResponse response, String path, MessagingTarget messagingTarget) throws ServletException, IOException;
}
