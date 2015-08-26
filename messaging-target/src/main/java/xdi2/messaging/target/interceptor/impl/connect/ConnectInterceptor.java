package xdi2.messaging.target.interceptor.impl.connect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import xdi2.agent.XDIAgent;
import xdi2.agent.impl.XDIBasicAgent;
import xdi2.client.manipulator.Manipulator;
import xdi2.client.manipulator.impl.SetLinkContractMessageManipulator;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.LinkContractTemplates;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.linkcontracts.instance.PublicLinkContract;
import xdi2.core.features.linkcontracts.instantiation.LinkContractInstantiation;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.GraphAware;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.messaging.operations.ConnectOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;

/**
 * This interceptor can process {$do} operations.
 */
public class ConnectInterceptor extends AbstractInterceptor<MessagingTarget> implements GraphAware, OperationInterceptor, Prototype<ConnectInterceptor> {

	private Graph targetGraph;
	private XDIAgent xdiAgent;
	private Collection<Manipulator> manipulators;

	public ConnectInterceptor(Graph targetGraph, XDIAgent xdiAgent, Collection<Manipulator> manipulators) {

		this.targetGraph = targetGraph;
		this.xdiAgent = xdiAgent;
		this.manipulators = manipulators;
	}

	public ConnectInterceptor() {

		this(null, new XDIBasicAgent(), null);
	}

	/*
	 * Prototype
	 */

	@Override
	public ConnectInterceptor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new contributor

		ConnectInterceptor contributor = new ConnectInterceptor();

		// set the graph

		contributor.setTargetGraph(this.getTargetGraph());

		// set the agent

		contributor.setXdiAgent(this.getXdiAgent());

		// done

		return contributor;
	}

	/*
	 * GraphAware
	 */

	@Override
	public void setGraph(Graph graph) {

		if (this.getTargetGraph() == null) this.setTargetGraph(graph);
	}

	/*
	 * TargetInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check operation

		if (! (operation instanceof ConnectOperation)) return InterceptorResult.DEFAULT;

		// get link contract template(s)

		List<LinkContractTemplate> linkContractTemplates = this.getLinkContractTemplates(operation, executionContext);

		// connect

		for (LinkContractTemplate linkContractTemplate : linkContractTemplates) {

			this.connect(linkContractTemplate, operation, operationResultGraph, executionContext);
		}

		// done

		return InterceptorResult.SKIP_MESSAGING_TARGET;
	}

	@Override
	public InterceptorResult after(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// done

		return InterceptorResult.DEFAULT;
	}

	/*
	 * Helper methods
	 */

	public List<LinkContractTemplate> getLinkContractTemplates(Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		List<LinkContractTemplate> linkContractTemplates = ConnectInterceptor.getLinkContractTemplates(executionContext);
		if (linkContractTemplates != null) return linkContractTemplates;

		if (linkContractTemplates == null && operation.getTargetXDIAddress() != null) linkContractTemplates = this.linkContractTemplateFromTargetXDIAddress(operation.getTargetXDIAddress(), executionContext);
		if (linkContractTemplates == null && operation.getTargetXdiInnerRoot() != null) linkContractTemplates = this.linkContractTemplatesFromTargetXdiInnerRoot(operation.getTargetXdiInnerRoot(), executionContext);
		if (linkContractTemplates == null) throw new Xdi2MessagingException("No link contract template(s) in operation " + operation, null, executionContext);

		ConnectInterceptor.putLinkContractTemplates(executionContext, linkContractTemplates);

		return linkContractTemplates;
	}

	private List<LinkContractTemplate> linkContractTemplateFromTargetXDIAddress(XDIAddress targetXDIAddress, ExecutionContext executionContext) throws Xdi2MessagingException {

		// use agent to obtain link contract template

		XDIAddress linkContractTemplateXDIaddress = targetXDIAddress;

		ContextNode linkContractTemplateContextNode;

		try {

			Collection<Manipulator> manipulators = new ArrayList<Manipulator> ();
			manipulators.add(new SetLinkContractMessageManipulator(PublicLinkContract.class));
			manipulators.addAll(this.getManipulators());

			linkContractTemplateContextNode = this.getXdiAgent().get(linkContractTemplateXDIaddress, manipulators);
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Unable to obtain link contract template at address " + targetXDIAddress + ": " + ex.getMessage(), ex, executionContext);
		}

		if (linkContractTemplateContextNode == null) throw new Xdi2MessagingException("Cannot find link contract template at address " + targetXDIAddress, null, executionContext);

		XdiEntitySingleton.Variable linkContractTemplateXdiVariable = XdiEntitySingleton.Variable.fromContextNode(linkContractTemplateContextNode);
		if (linkContractTemplateXdiVariable == null) throw new Xdi2MessagingException("Invalid link contract template context node at address " + targetXDIAddress, null, executionContext);

		LinkContractTemplate linkContractTemplate = LinkContractTemplate.fromXdiEntitySingletonVariable(linkContractTemplateXdiVariable);
		if (linkContractTemplate == null) throw new Xdi2MessagingException("Invalid link contract template at address " + targetXDIAddress, null, executionContext);

		// done

		return Collections.singletonList(linkContractTemplate);
	}

	private List<LinkContractTemplate> linkContractTemplatesFromTargetXdiInnerRoot(XdiInnerRoot targetXdiInnerRoot, ExecutionContext executionContext) throws Xdi2MessagingException {

		// get the inner graph

		Graph innerGraph = targetXdiInnerRoot.getInnerGraph();

		// return link contract templates

		return new IteratorListMaker<LinkContractTemplate> (LinkContractTemplates.getAllLinkContractTemplates(innerGraph)).list();
	}

	private void connect(LinkContractTemplate linkContractTemplate, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// determine requesting and authorizing authorities

		XDIAddress requestingAuthority = operation.getSenderXDIAddress();
		if (requestingAuthority == null) throw new Xdi2MessagingException("No requesting authority for link contract instantiation.", null, executionContext);

		XDIAddress authorizingAuthority = operation.getMessage().getToXDIAddress();
		if (authorizingAuthority == null) throw new Xdi2MessagingException("No authorizing authority for link contract instantiation.", null, executionContext);

		// determine variable values

		Map<XDIArc, XDIAddress> variableValues = operation.getVariableValues();

		// instantiate link contract

		LinkContractInstantiation linkContractInstantiation = new LinkContractInstantiation(linkContractTemplate);
		linkContractInstantiation.setRequestingAuthority(requestingAuthority);
		linkContractInstantiation.setAuthorizingAuthority(authorizingAuthority);
		linkContractInstantiation.setVariableValues(variableValues);

		LinkContract linkContract = linkContractInstantiation.execute(false, true);

		// write link contract into operation result graph

		CopyUtil.copyGraph(linkContract.getContextNode().getGraph(), operationResultGraph, null);

		// link contract into target graph

		if (this.getTargetGraph() != null) {

			CopyUtil.copyGraph(linkContract.getContextNode().getGraph(), this.getTargetGraph(), null);
		}
	}

	/*
	 * Getters and setters
	 */

	public Graph getTargetGraph() {

		return this.targetGraph;
	}

	public void setTargetGraph(Graph targetGraph) {

		this.targetGraph = targetGraph;
	}

	public XDIAgent getXdiAgent() {

		return this.xdiAgent;
	}

	public void setXdiAgent(XDIAgent xdiAgent) {

		this.xdiAgent = xdiAgent;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_LINKCONTRACTTEMPLATES_PER_OPERATION = ConnectInterceptor.class.getCanonicalName() + "#linkcontracttemplatesperoperation";

	@SuppressWarnings("unchecked")
	public static List<LinkContractTemplate> getLinkContractTemplates(ExecutionContext executionContext) {

		return (List<LinkContractTemplate>) executionContext.getOperationAttribute(EXECUTIONCONTEXT_KEY_LINKCONTRACTTEMPLATES_PER_OPERATION);
	}

	public static void putLinkContractTemplates(ExecutionContext executionContext, List<LinkContractTemplate> linkContractTemplates) {

		executionContext.putOperationAttribute(EXECUTIONCONTEXT_KEY_LINKCONTRACTTEMPLATES_PER_OPERATION, linkContractTemplates);
	}

	public Collection<Manipulator> getManipulators() {

		return this.manipulators;
	}

	public void setManipulators(Collection<Manipulator> manipulators) {

		this.manipulators = manipulators;
	}
}
