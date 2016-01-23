package xdi2.transport.impl.http.interceptor.impl;

import java.io.IOException;

import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.impl.http.HttpTransportRequest;
import xdi2.transport.impl.http.HttpTransportResponse;
import xdi2.transport.registry.impl.uri.UriMessagingTargetMount;

/**
 * This interceptor redirects to a specified URL in case of a GET request without path.
 * 
 * @author markus
 */
public class RedirectHttpTransportInterceptor extends AbstractHttpTransportInterceptor {

	public static final int DEFAULT_STATUS = HttpTransportResponse.SC_FOUND;
	public static final String DEFAULT_LOCATION = null;

	private int status;
	private String location;

	public RedirectHttpTransportInterceptor() {

		this.status = DEFAULT_STATUS;
		this.location = DEFAULT_LOCATION;
	}

	@Override
	public boolean processGetRequest(HttpTransport httpTransport, HttpTransportRequest request, HttpTransportResponse response, UriMessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		if (! request.getRequestPath().equals("/")) return false;

		if (this.location == null) throw new IllegalArgumentException("Property 'location' is not set. Cannot redirect.");

		// redirect

		response.sendRedirect(this.getStatus(), this.getLocation());

		// done

		return true;
	}

	/*
	 * Getters and setters
	 */

	public int getStatus() {

		return this.status;
	}

	public void setStatus(int status) {

		this.status = status;
	}

	public String getLocation() {

		return this.location;
	}

	public void setLocation(String location) {

		this.location = location;
	}
}
