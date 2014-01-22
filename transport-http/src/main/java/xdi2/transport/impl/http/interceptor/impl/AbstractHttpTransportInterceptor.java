package xdi2.transport.impl.http.interceptor.impl;

import java.io.IOException;

import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.transport.Transport;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.HttpRequest;
import xdi2.transport.impl.http.HttpResponse;
import xdi2.transport.impl.http.HttpTransport;
import xdi2.transport.impl.http.interceptor.HttpTransportInterceptor;
import xdi2.transport.impl.http.registry.MessagingTargetMount;

public abstract class AbstractHttpTransportInterceptor extends AbstractInterceptor<Transport<?, ?>> implements HttpTransportInterceptor {

	@Override
	public boolean processGetRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		return false;
	}

	@Override
	public boolean processPostRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		return false;
	}

	@Override
	public boolean processPutRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		return false;
	}

	@Override
	public boolean processDeleteRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		return false;
	}
}
