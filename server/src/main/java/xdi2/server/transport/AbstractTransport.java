package xdi2.server.transport;

import xdi2.messaging.transport.Request;
import xdi2.messaging.transport.Response;
import xdi2.messaging.transport.Transport;

public abstract class AbstractTransport <REQUEST extends Request, RESPONSE extends Response> implements Transport<REQUEST, RESPONSE> {

}
