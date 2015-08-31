package xdi2.messaging.target.interceptor.impl.push;

import java.util.List;
import java.util.Map;
import java.util.Set;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.MessagingTarget;

public interface PushGateway {

	public void executePush(MessagingTarget messagingTarget, GenericLinkContract pushLinkContract, Set<Operation> pushLinkContractOperations, Map<Operation, Graph> pushLinkContractOperationResultGraphs, Map<Operation, XDIAddress> pushLinkContractXDIAddressMap, Map<Operation, List<XDIStatement>> pushLinkContractXDIStatementMap) throws Xdi2ClientException;
}
