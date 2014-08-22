package xdi2.messaging.target.interceptor.impl.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Literal;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.transport.Request;
import xdi2.transport.impl.AbstractTransport;
import xdi2.transport.impl.http.HttpRequest;

/**
 * This interceptor looks for certain features associated with the HTTP transport,
 * e.g. IP address.
 */
public class HttpTransportDataInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageInterceptor, Prototype<HttpTransportDataInterceptor> {

	private static Logger log = LoggerFactory.getLogger(HttpTransportDataInterceptor.class.getName());

	public static final XDIAddress XDI_ADD_IP = XDIAddress.create("<$ip>");

	/*
	 * Prototype
	 */

	@Override
	public HttpTransportDataInterceptor instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// done

		return this;
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// look for HttpTransport, HttpRequest, HttpResponse

		Request request = AbstractTransport.getRequest(executionContext);
		if (! (request instanceof HttpRequest)) return InterceptorResult.DEFAULT;

		HttpRequest httpRequest = (HttpRequest) request;

		// add <$ip>

		String remoteAddr = httpRequest.getRemoteAddr();

		XdiAttribute ipXdiAttribute = XdiAttributeSingleton.fromContextNode(message.getContextNode().setDeepContextNode(XDI_ADD_IP));
		XdiValue ipXdiValue = ipXdiAttribute.getXdiValue(true);
		Literal ipLiteral = ipXdiValue.getContextNode().setLiteralString(remoteAddr);

		if (log.isDebugEnabled()) log.debug("IP: " + ipLiteral.getStatement());

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}
}
