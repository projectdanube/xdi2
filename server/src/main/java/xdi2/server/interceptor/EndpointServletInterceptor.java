package xdi2.server.interceptor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.server.EndpointServlet;
import xdi2.server.RequestInfo;

/**
 * Interceptor that is executed when the EndpointServlet is initialized, destroyed, or when it receives an incoming HTTP request.
 * 
 * @author markus
 */
public interface EndpointServletInterceptor extends Interceptor {

	/**
	 * This method gets called when the endpoint servlet is initialized.
	 */
	public void init(EndpointServlet endpointServlet) throws ServletException;

	/**
	 * This method gets called when the endpoint servlet is destroyed.
	 */
	public void destroy(EndpointServlet endpointServlet);

	/**
	 * Run when the endpoint servlet receives a GET request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processGetRequest(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException;

	/**
	 * Run when the endpoint servlet receives a POST request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processPostRequest(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException;

	/**
	 * Run when the endpoint servlet receives a PUT request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processPutRequest(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException;

	/**
	 * Run when the endpoint servlet receives a DELETE request.
	 * @return True, if the request has been fully handled.
	 */
	public boolean processDeleteRequest(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException;
}
