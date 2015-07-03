package xdi2.transport.impl.http.interceptor.impl;

import java.io.IOException;

import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.impl.http.HttpTransportRequest;
import xdi2.transport.impl.http.HttpTransportResponse;
import xdi2.transport.impl.http.interceptor.HttpTransportInterceptor;
import xdi2.transport.impl.http.registry.HttpMessagingTargetMount;

/**
 * This interceptor simply returns a plain HTTP status and empty body.
 * 
 * @author markus
 */
public class StatusHttpTransportInterceptor extends AbstractHttpTransportInterceptor implements HttpTransportInterceptor {

	public static final String DEFAULT_PATH = "/status";
	public static final int DEFAULT_STATUS = 200;

	private String path;
	private int status;

	public StatusHttpTransportInterceptor() {

		this.path = DEFAULT_PATH;
		this.status = DEFAULT_STATUS;
	}

	/*
	 * HttpTransportInterceptor
	 */

	@Override
	public boolean processGetRequest(HttpTransport httpTransport, HttpTransportRequest request, HttpTransportResponse response, HttpMessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		if (! request.getRequestPath().equals(this.getPath())) return false;

		// send response

		response.setStatus(this.getStatus());

		// done

		return true;
	}

	/*
	 * Getters and setters
	 */

	public String getPath() {

		return this.path;
	}

	public void setPath(String path) {

		this.path = path;
	}

	public int getStatus() {

		return this.status;
	}

	public void setStatus(int status) {

		this.status = status;
	}
}
