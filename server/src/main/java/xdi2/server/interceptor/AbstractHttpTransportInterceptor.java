package xdi2.server.interceptor;

import java.io.IOException;

import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.transport.HttpRequest;
import xdi2.server.transport.HttpResponse;
import xdi2.server.transport.HttpTransport;

public abstract class AbstractHttpTransportInterceptor extends AbstractInterceptor implements HttpTransportInterceptor {

	@Override
	public void init(HttpTransport httpTransport) throws Xdi2ServerException {

	}

	@Override
	public void destroy(HttpTransport httpTransport) {

	}

	@Override
	public boolean processGetRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException {

		return false;
	}

	@Override
	public boolean processPostRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException {

		return false;
	}

	@Override
	public boolean processPutRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException {

		return false;
	}

	@Override
	public boolean processDeleteRequest(HttpTransport httpTransport, HttpRequest request, HttpResponse response, MessagingTarget messagingTarget) throws Xdi2ServerException, IOException {

		return false;
	}
}
