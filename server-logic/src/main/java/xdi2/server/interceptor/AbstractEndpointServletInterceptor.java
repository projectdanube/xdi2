package xdi2.server.interceptor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractEndpointServletInterceptor implements EndpointServletInterceptor {

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
