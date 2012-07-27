package xdi2.server.interceptor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.messaging.target.MessagingTarget;
import xdi2.server.EndpointServlet;

public abstract class AbstractEndpointServletInterceptor implements EndpointServletInterceptor {

	@Override
	public void init(EndpointServlet endpointServlet) throws ServletException {

	}

	@Override
	public void destroy(EndpointServlet endpointServlet) {

	}

	@Override
	public boolean processGetRequest(HttpServletRequest request, HttpServletResponse response, String path, MessagingTarget messagingTarget) throws ServletException, IOException {

		return false;
	}

	@Override
	public boolean processPostRequest(HttpServletRequest request, HttpServletResponse response, String path, MessagingTarget messagingTarget) throws ServletException, IOException {

		return false;
	}

	@Override
	public boolean processPutRequest(HttpServletRequest request, HttpServletResponse response, String path, MessagingTarget messagingTarget) throws ServletException, IOException {

		return false;
	}

	@Override
	public boolean processDeleteRequest(HttpServletRequest request, HttpServletResponse response, String path, MessagingTarget messagingTarget) throws ServletException, IOException {

		return false;
	}
}
