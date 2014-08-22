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

@ContributorMount(contributorXris={"(#test)"})
public class TestContributor3 extends AbstractContributor {

	@Override
	public ContributorResult executeGetOnAddress(
			XDIAddress[] contributorXris,
			XDIAddress contributorsXri,
			XDIAddress relativeTargetAddress,
			GetOperation operation,
			MessageResult messageResult,
			ExecutionContext executionContext) throws Xdi2MessagingException {

		messageResult.getGraph().setStatement(XDIStatement.fromRelationComponents(
				XDIAddress.create("" + contributorsXri + "=markus"),
				XDIAddress.create("" + "#friend"),
				XDIAddress.create("" + contributorsXri + "=animesh")));

		return ContributorResult.DEFAULT;
	}
}
