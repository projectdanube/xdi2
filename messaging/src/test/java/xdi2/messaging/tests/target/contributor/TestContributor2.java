package xdi2.messaging.tests.target.contributor;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.contributor.ContributorResult;

@ContributorMount(contributorXDIAddresses={"<#email>"})
public class TestContributor2 extends AbstractContributor {

	private String value = "val";

	@Override
	public ContributorResult executeGetOnAddress(
			XDIAddress[] contributorAddresses,
			XDIAddress contributorsAddress,
			XDIAddress relativeTargetAddress,
			GetOperation operation,
			MessageResult messageResult,
			ExecutionContext executionContext) throws Xdi2MessagingException {

		messageResult.getGraph().setStatement(XDIStatement.fromLiteralComponents(
				XDIAddress.create("" + contributorsAddress + "&"),
				this.value));

		return ContributorResult.DEFAULT;
	}
}
