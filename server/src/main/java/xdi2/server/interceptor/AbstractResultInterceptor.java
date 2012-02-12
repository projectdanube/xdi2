package xdi2.server.interceptor;

import xdi2.Statement;
import xdi2.exceptions.Xdi2MessagingException;
import xdi2.server.ExecutionContext;

public abstract class AbstractResultInterceptor implements ResultInterceptor {

	public boolean exclude(Statement statement, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
