package xdi2.messaging.target.interceptor;

import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

public abstract class AbstractResultInterceptor implements ResultInterceptor {

	public boolean exclude(Statement statement, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
