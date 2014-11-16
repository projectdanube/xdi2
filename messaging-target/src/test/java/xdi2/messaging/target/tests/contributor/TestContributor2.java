package xdi2.messaging.target.tests.contributor;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.contributor.ContributorResult;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;

@ContributorMount(contributorAddresses={"<#email>"})
public class TestContributor2 extends AbstractContributor {

	private String value = "val";

	@Override
	public ContributorResult executeGetOnAddress(
			XDIAddress[] contributorAddresses,
			XDIAddress contributorsAddress,
			XDIAddress relativeTargetAddress,
			GetOperation operation,
			Graph resultGraph,
			ExecutionContext executionContext) throws Xdi2MessagingException {

		resultGraph.setStatement(XDIStatement.fromLiteralComponents(
				XDIAddress.create("" + contributorsAddress + "&"),
				this.value));

		return ContributorResult.DEFAULT;
	}
}