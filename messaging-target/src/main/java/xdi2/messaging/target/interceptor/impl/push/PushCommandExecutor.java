package xdi2.messaging.target.interceptor.impl.push;

import java.util.List;
import java.util.Map;
import java.util.Set;

import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.features.push.PushCommand;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.operations.Operation;

public interface PushCommandExecutor {

	public void executePush(PushCommand pushCommand, Set<Operation> pushCommandOperations, Map<Operation, XDIAddress> pushCommandXDIAddressMap, Map<Operation, List<XDIStatement>> pushCommandXDIStatementMap) throws Xdi2AgentException, Xdi2ClientException;
}
