package xdi2.messaging.target.interceptor.impl.push;

import java.util.List;
import java.util.Map;
import java.util.Set;

import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.operations.Operation;

public interface PushGateway {

	public void executePush(LinkContract linkContract, Set<Operation> pushOperations, Map<Operation, XDIAddress> pushXDIAddressMap, Map<Operation, List<XDIStatement>> pushXDIStatementMap) throws Xdi2AgentException, Xdi2ClientException;
}
