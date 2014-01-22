package xdi2.transport;

import xdi2.messaging.context.ExecutionContext;

public interface Transport <REQUEST extends Request, RESPONSE extends Response> {

	public void init() throws Exception;
	public void shutdown() throws Exception;

	public ExecutionContext createExecutionContext(REQUEST request, RESPONSE response);
}
