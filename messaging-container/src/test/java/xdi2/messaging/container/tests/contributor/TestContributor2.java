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

@ContributorMount(contributorXDIAddresses={"<#email>"})
public class TestContributor2 extends AbstractContributor {

	private String value = "val";

	@Override
	public ContributorResult executeGetOnAddress(
			XDIAddress[] contributorAddresses,
			XDIAddress contributorsAddress,
			XDIAddress relativeTargetAddress,
			GetOperation operation,
			Graph operationResultGraph,
			ExecutionContext executionContext) throws Xdi2MessagingException {

		operationResultGraph.setStatement(XDIStatement.fromLiteralComponents(
				XDIAddress.create("" + contributorsAddress),
				this.value));

		return ContributorResult.DEFAULT;
	}
}
