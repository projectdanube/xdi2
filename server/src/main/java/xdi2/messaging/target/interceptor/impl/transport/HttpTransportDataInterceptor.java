package xdi2.messaging.target.interceptor.impl.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Literal;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.transport.Request;
import xdi2.server.transport.HttpRequest;

/**
 * This interceptor looks for certain features associated with the HTTP transport,
 * e.g. IP address.
 */
public class HttpTransportDataInterceptor extends AbstractInterceptor implements MessageInterceptor, Prototype<HttpTransportDataInterceptor> {

	private static Logger log = LoggerFactory.getLogger(HttpTransportDataInterceptor.class.getName());

	public static final XDI3Segment XRI_S_IP = XDI3Segment.create("<$ip>");

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

		Request request = executionContext.getRequest();
		if (! (request instanceof HttpRequest)) return InterceptorResult.DEFAULT;

		HttpRequest httpRequest = (HttpRequest) request;

		// add <$ip>

		String ip = httpRequest.getRemoteAddr();

		XdiAttribute ipXdiAttribute = XdiAttributeSingleton.fromContextNode(message.getContextNode().setDeepContextNode(XRI_S_IP));
		XdiValue ipXdiValue = ipXdiAttribute.getXdiValue(true);
		Literal ipLiteral = ipXdiValue.getContextNode().setLiteralString(ip);

		if (log.isDebugEnabled()) log.debug("IP: " + ipLiteral.getStatement());

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}
}
