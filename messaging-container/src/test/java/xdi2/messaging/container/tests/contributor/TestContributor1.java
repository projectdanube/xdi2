package xdi2.messaging.container.tests.contributor;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.container.contributor.ContributorMount;
import xdi2.messaging.container.contributor.ContributorResult;
import xdi2.messaging.container.contributor.impl.AbstractContributor;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.operations.GetOperation;

@ContributorMount(contributorXDIAddresses={"(#con)"})
public class TestContributor1 extends AbstractContributor {

	private String value = "val";

	public TestContributor1() {

		super();

		this.getContributors().addContributor(new TestContributor2());
	}

	@Override
	public ContributorResult executeGetOnAddress(
			XDIAddress[] contributorAddresses,
			XDIAddress contributorsAddress,
			XDIAddress relativeTargetAddress,
			GetOperation operation,
			Graph operationResultGraph,
			ExecutionContext executionContext) throws Xdi2MessagingException {

		operationResultGraph.setStatement(XDIStatement.fromLiteralComponents(
				XDIAddress.create("" + contributorsAddress + "=a<#b>"),
				this.value));

		operationResultGraph.setStatement(XDIStatement.fromRelationComponents(
				XDIAddress.create("" + contributorsAddress + "=x*y"),
				XDIAddress.create("" + "#c"),
				XDIAddress.create("" + contributorsAddress + "=d*e")));

		return ContributorResult.DEFAULT;
	}
}
