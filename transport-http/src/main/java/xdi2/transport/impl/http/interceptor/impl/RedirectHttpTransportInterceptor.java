package xdi2.transport.impl.http.interceptor.impl;

import java.io.IOException;

import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.HttpRequest;
import xdi2.transport.impl.http.HttpResponse;
import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.impl.http.registry.MessagingTargetMount;

/**
 * This interceptor redirects to a specified URL in case of a GET request without path.
 * 
 * @author markus
 */
public class RedirectHttpTransportInterceptor extends AbstractHttpTransportInterceptor {

	private String location;

	@Override
	public boolean processGetRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

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
