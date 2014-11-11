package xdi2.transport;

import java.io.IOException;

import xdi2.messaging.context.ExecutionContext;
import xdi2.transport.exceptions.Xdi2TransportException;

public interface Transport <REQUEST extends TransportRequest, RESPONSE extends TransportResponse> {

	public void init() throws Exception;
	public void shutdown() throws Exception;

	public void execute(REQUEST request, RESPONSE response) throws Xdi2TransportException, IOException;

	public ExecutionContext createExecutionContext(REQUEST request, RESPONSE response);
}
