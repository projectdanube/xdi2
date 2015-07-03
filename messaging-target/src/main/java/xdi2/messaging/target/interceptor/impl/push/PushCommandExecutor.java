package xdi2.messaging.target.interceptor.impl.push;

import java.util.List;
import java.util.Map;

import xdi2.core.features.push.PushCommand;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.operations.Operation;

public interface PushCommandExecutor {

	public void executePush(PushCommand pushCommand, Map<Operation, XDIAddress> pushCommandXDIAddressMap, Map<Operation, List<XDIStatement>> pushCommandXDIStatementMap);
}
