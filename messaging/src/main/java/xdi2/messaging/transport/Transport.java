package xdi2.messaging.transport;

import xdi2.messaging.context.ExecutionContext;

public interface Transport <REQUEST extends Request, RESPONSE extends Response> {

	public ExecutionContext createExecutionContext(REQUEST request, RESPONSE response);
}
