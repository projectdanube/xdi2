package xdi2.transport.impl.http.interceptor.impl;

import java.io.IOException;

import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;
import xdi2.transport.Transport;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.impl.http.HttpTransportRequest;
import xdi2.transport.impl.http.HttpTransportResponse;
import xdi2.transport.impl.http.interceptor.HttpTransportInterceptor;
import xdi2.transport.impl.http.registry.HttpMessagingTargetMount;

public abstract class AbstractHttpTransportInterceptor extends AbstractInterceptor<Transport<?, ?>> implements HttpTransportInterceptor {

	@Override
	public boolean processGetRequest(HttpTransport httpTransport, HttpTransportRequest request, HttpTransportResponse response, HttpMessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		return false;
	}

	@Override
	public boolean processPostRequest(HttpTransport httpTransport, HttpTransportRequest request, HttpTransportResponse response, HttpMessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		return false;
	}

	@Override
	public boolean processPutRequest(HttpTransport httpTransport, HttpTransportRequest request, HttpTransportResponse response, HttpMessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		return false;
	}

	@Override
	public boolean processDeleteRequest(HttpTransport httpTransport, HttpTransportRequest request, HttpTransportResponse response, HttpMessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		return false;
	}
}
