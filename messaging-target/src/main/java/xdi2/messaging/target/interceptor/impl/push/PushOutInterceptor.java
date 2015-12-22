package xdi2.messaging.target.interceptor.impl.push;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.GraphAware;
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.ExecutionResultInterceptor;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;

/**
 * This interceptor executes push link contracts while a message is executed.
 */
public class PushOutInterceptor extends AbstractInterceptor<MessagingTarget> implements GraphAware, ExecutionResultInterceptor, OperationInterceptor, Prototype<PushOutInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(PushOutInterceptor.class);

	private Graph pushLinkContractsGraph;
	private PushGateway pushGateway;

	public PushOutInterceptor(Graph pushLinkContractsGraph, PushGateway pushGateway) {

		this.pushLinkContractsGraph = pushLinkContractsGraph;
		this.pushGateway = pushGateway;
	}

	public PushOutInterceptor() {

		this.pushLinkContractsGraph = null;
		this.pushGateway = new BasicPushGateway();
	}

	/*
	 * Prototype
	 */

	@Override
	public PushOutInterceptor instanceFor(PrototypingContext prototypingContext) {

		// create new interceptor

		PushOutInterceptor interceptor = new PushOutInterceptor();

		// set the graph

		interceptor.setPushLinkContractsGraph(this.getPushLinkContractsGraph());
		interceptor.setPushGateway(this.getPushGateway());

		// done

		return interceptor;
	}

	/*
	 * GraphAware
	 */

	@Override
	public void setGraph(Graph graph) {

		if (this.getPushLinkContractsGraph() == null) this.setPushLinkContractsGraph(graph);
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// only care about write operations

		if (operation.isReadOnlyOperation()) return InterceptorResult.DEFAULT;

		// add the write operation

		addWriteOperationPerMessageEnvelope(executionContext, operation);

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// done

		return InterceptorResult.DEFAULT;
	}

	/*
	 * ExecutionResultInterceptor
	 */

	@Override
	public void finish(MessagingTarget messagingTarget, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		// create the push maps

		List<Operation> writeOperations = getWriteOperationsPerMessageEnvelope(executionContext);
		if (writeOperations == null) return;

		Map<GenericLinkContract, Map<Operation, XDIAddress>> pushLinkContractsXDIAddressMap = new HashMap<GenericLinkContract, Map<Operation, XDIAddress>> ();
		Map<GenericLinkContract, Map<Operation, List<XDIStatement>>> pushLinkContractsXDIStatementMap = new HashMap<GenericLinkContract, Map<Operation, List<XDIStatement>>> ();

		for (Operation writeOperation : writeOperations) {

			XDIAddress targetXDIAddress = writeOperation.getTargetXDIAddress();
			IterableIterator<XDIStatement> targetXDIStatements = writeOperation.getTargetXDIStatements();

			// look for push link contracts for the operation's target address

			if (targetXDIAddress != null) {

				List<GenericLinkContract> pushLinkContracts = findPushLinkContracts(this.getPushLinkContractsGraph(), targetXDIAddress);
				if (pushLinkContracts == null || pushLinkContracts.isEmpty()) continue;

				for (GenericLinkContract pushLinkContract : pushLinkContracts) {

					if (log.isDebugEnabled()) log.debug("For push link contract " + pushLinkContract + " processing target address " + targetXDIAddress);

					// TODO: evaluate policy here?

					/*					if (pushLinkContract.getMessageXDIAddress() != null && ! pushLinkContract.getMessageXDIAddress().equals(writeOperation.getMessage().getContextNode().getXDIAddress())) {

						if (log.isDebugEnabled()) log.debug("Push link contract " + pushLinkContract + " is associated with message " + pushLinkContract.getMessageXDIAddress() + ", not " + writeOperation.getMessage().getContextNode().getXDIAddress());
						continue;
					}*/

					Map<Operation, XDIAddress> pushLinkContractXDIAddressMap = pushLinkContractsXDIAddressMap.get(pushLinkContract);
					if (pushLinkContractXDIAddressMap == null) { pushLinkContractXDIAddressMap = new HashMap<Operation, XDIAddress> (); pushLinkContractsXDIAddressMap.put(pushLinkContract, pushLinkContractXDIAddressMap); }

					pushLinkContractXDIAddressMap.put(writeOperation, targetXDIAddress);
				}
			}

			// look for push link contracts for the operation's target statements

			if (targetXDIStatements != null) {

				for (XDIStatement targetXDIStatement : targetXDIStatements) {

					targetXDIAddress = targetXDIAddressForTargetXDIStatement(targetXDIStatement);

					List<GenericLinkContract> pushLinkContracts = findPushLinkContracts(this.getPushLinkContractsGraph(), targetXDIAddress);
					if (pushLinkContracts == null || pushLinkContracts.isEmpty()) continue;

					for (GenericLinkContract pushLinkContract : pushLinkContracts) {

						if (log.isDebugEnabled()) log.debug("For push link contract " + pushLinkContract + " processing target statement " + targetXDIStatement);

						// TODO: evaluate policy here?

						/*						if (pushLinkContract.getMessageXDIAddress() != null && ! pushLinkContract.getMessageXDIAddress().equals(writeOperation.getMessage().getContextNode().getXDIAddress())) {

							if (log.isDebugEnabled()) log.debug("Push link contract " + pushLinkContract + " is associated with message " + pushLinkContract.getMessageXDIAddress() + ", not " + writeOperation.getMessage().getContextNode().getXDIAddress());
							continue;
						}*/

						Map<Operation, List<XDIStatement>> pushLinkContractXDIStatementMap = pushLinkContractsXDIStatementMap.get(pushLinkContract);
						if (pushLinkContractXDIStatementMap == null) { pushLinkContractXDIStatementMap = new HashMap<Operation, List<XDIStatement>> (); pushLinkContractsXDIStatementMap.put(pushLinkContract, pushLinkContractXDIStatementMap); }

						List<XDIStatement> pushLinkContractXDIStatementList = pushLinkContractXDIStatementMap.get(writeOperation);
						if (pushLinkContractXDIStatementList == null) { pushLinkContractXDIStatementList = new ArrayList<XDIStatement> (); pushLinkContractXDIStatementMap.put(writeOperation, pushLinkContractXDIStatementList); }

						pushLinkContractXDIStatementList.add(targetXDIStatement);
					}
				}
			}
		}

		// execute the push link contracts

		Set<GenericLinkContract> pushLinkContracts = new HashSet<GenericLinkContract> ();
		pushLinkContracts.addAll(pushLinkContractsXDIAddressMap.keySet());
		pushLinkContracts.addAll(pushLinkContractsXDIStatementMap.keySet());

		for (GenericLinkContract pushLinkContract : pushLinkContracts) {

			Map<Operation, XDIAddress> pushedXDIAddressMap = pushLinkContractsXDIAddressMap.get(pushLinkContract);
			Map<Operation, List<XDIStatement>> pushedXDIStatementMap = pushLinkContractsXDIStatementMap.get(pushLinkContract);

			Set<Operation> pushedOperations = new HashSet<Operation> ();
			if (pushedXDIAddressMap != null) pushedOperations.addAll(pushedXDIAddressMap.keySet());
			if (pushedXDIStatementMap != null) pushedOperations.addAll(pushedXDIStatementMap.keySet());

			Map<Operation, Graph> pushedOperationResultGraphs = new HashMap<Operation, Graph> ();

			for (Operation pushedOperation : pushedOperations) {

				// TODO maybe don't push the ENTIRE operation result graph for all operations that trigger push contract?

				Graph pushedOperationResultGraph = executionResult.getFinishedOperationResultGraph(pushedOperation);
				pushedOperationResultGraphs.put(pushedOperation, pushedOperationResultGraph);
			}

			try {

				if (log.isDebugEnabled()) log.debug("Executing push " + pushLinkContract);

				this.getPushGateway().executePush(
						messagingTarget, 
						pushLinkContract, 
						pushedOperations, 
						pushedOperationResultGraphs, 
						pushedXDIAddressMap, 
						pushedXDIStatementMap);
			} catch (Exception ex) {

				if (log.isWarnEnabled()) log.warn("Problem while executing push: " + ex.getMessage(), ex, executionContext);
			}
		}
	}

	/*
	 * Getters and setters
	 */

	public Graph getPushLinkContractsGraph() {

		return this.pushLinkContractsGraph;
	}

	public void setPushLinkContractsGraph(Graph pushLinkContractsGraph) {

		this.pushLinkContractsGraph = pushLinkContractsGraph;
	}

	public PushGateway getPushGateway() {

		return this.pushGateway;
	}

	public void setPushGateway(PushGateway pushGateway) {

		this.pushGateway = pushGateway;
	}

	/*
	 * Helper methods
	 */

	private static XDIAddress targetXDIAddressForTargetXDIStatement(XDIStatement targetXDIStatement) {

		if (targetXDIStatement.isContextNodeStatement()) {

			return targetXDIStatement.getTargetXDIAddress();
		} else {

			return targetXDIStatement.getContextNodeXDIAddress();
		}
	}

	private static List<GenericLinkContract> findPushLinkContracts(Graph pushLinkContractsGraph, XDIAddress XDIaddress) {

		List<GenericLinkContract> pushLinkContracts = new ArrayList<GenericLinkContract> ();

		while (true) {

			ContextNode contextNode = pushLinkContractsGraph.getDeepContextNode(XDIaddress);

			if (contextNode != null) {

				if (log.isDebugEnabled()) log.debug("Trying to find push link contracts for context node " + contextNode);

				for (Relation pushLinkContractRelation : contextNode.getRelations(XDILinkContractConstants.XDI_ADD_IS_PUSH)) {

					if (log.isDebugEnabled()) log.debug("Trying to find push link contract for relation " + pushLinkContractRelation);

					ContextNode pushLinkContractContextNode = pushLinkContractRelation.followContextNode();
					if (pushLinkContractContextNode == null) continue;

					XdiEntity pushLinkContractXdiEntity = XdiAbstractEntity.fromContextNode(pushLinkContractContextNode);
					if (pushLinkContractXdiEntity == null) continue;

					GenericLinkContract pushLinkContract = GenericLinkContract.fromXdiEntity(pushLinkContractXdiEntity);
					if (pushLinkContract == null) continue;

					for (XDIAddress pushTargetXDIAddress : pushLinkContract.getPermissionTargetXDIAddresses(XDILinkContractConstants.XDI_ADD_PUSH)) {

						if (log.isDebugEnabled()) log.debug("Found push link contract to address " + pushTargetXDIAddress + " for address " + XDIaddress);

						if (XDIAddressUtil.startsWithXDIAddress(XDIaddress, pushTargetXDIAddress) != null) {

							pushLinkContracts.add(pushLinkContract);
							break;
						}
					}
				}
			}

			if (XDIaddress.getNumXDIArcs() == 0) break;
			XDIaddress = XDIAddressUtil.parentXDIAddress(XDIaddress, -1);
		}

		// done

		if (log.isDebugEnabled()) log.debug("Found push link contracts for address " + XDIaddress + ": " + pushLinkContracts);

		return pushLinkContracts;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_WRITEOPERATIONS_PER_MESSAGEENVELOPE = PushOutInterceptor.class.getCanonicalName() + "#writeoperationspermessageenvelope";

	@SuppressWarnings("unchecked")
	private static List<Operation> getWriteOperationsPerMessageEnvelope(ExecutionContext executionContext) {

		return (List<Operation>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_WRITEOPERATIONS_PER_MESSAGEENVELOPE);
	}

	@SuppressWarnings("unchecked")
	private static void addWriteOperationPerMessageEnvelope(ExecutionContext executionContext, Operation operation) {

		List<Operation> writeOperations = (List<Operation>) executionContext.getMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_WRITEOPERATIONS_PER_MESSAGEENVELOPE);
		if (writeOperations == null) { writeOperations = new ArrayList<Operation> (); executionContext.putMessageEnvelopeAttribute(EXECUTIONCONTEXT_KEY_WRITEOPERATIONS_PER_MESSAGEENVELOPE, writeOperations); }

		writeOperations.add(operation);
	}
}
