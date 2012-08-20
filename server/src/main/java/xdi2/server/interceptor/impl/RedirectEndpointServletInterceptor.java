package xdi2.server.interceptor.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.messaging.target.MessagingTarget;
import xdi2.server.EndpointServlet;
import xdi2.server.RequestInfo;
import xdi2.server.interceptor.AbstractEndpointServletInterceptor;

/**
 * This interceptor redirects to a specified URL in case of a GET request without path.
 * 
 * @author markus
 */
public class RedirectEndpointServletInterceptor extends AbstractEndpointServletInterceptor {

	private String location;

	@Override
	public boolean processGetRequest(EndpointServlet endpointServlet, HttpServletRequest request, HttpServletResponse response, RequestInfo requestInfo, MessagingTarget messagingTarget) throws ServletException, IOException {

		if (! requestInfo.getRequestPath().equals("/")) return false;

		// redirect

		response.sendRedirect(this.getLocation());

		// done

		return true;
	}

	public String getLocation() {

		return this.location;
	}

	public void setLocation(String location) {

		this.location = location;
	}
}
