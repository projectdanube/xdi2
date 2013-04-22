package xdi2.server.interceptor.impl;

import java.io.IOException;

import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.interceptor.AbstractHttpTransportInterceptor;
import xdi2.server.transport.HttpRequest;
import xdi2.server.transport.HttpResponse;
import xdi2.server.transport.HttpTransport;

/**
 * This interceptor redirects to a specified URL in case of a GET request without path.
 * 
 * @author markus
 */
public class RedirectHttpTransportInterceptor extends AbstractHttpTransportInterceptor {

	private String location;

	@Override
	public boolean processGetRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException {

		if (! request.getRequestPath().equals("/")) return false;

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
