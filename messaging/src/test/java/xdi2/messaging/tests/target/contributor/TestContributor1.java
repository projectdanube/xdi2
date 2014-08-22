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

@ContributorMount(contributorXris={"(#con)"})
public class TestContributor1 extends AbstractContributor {

	private String value = "val";

	public TestContributor1() {
		
		super();
		
		this.getContributors().addContributor(new TestContributor2());
	}
	
	@Override
	public ContributorResult executeGetOnAddress(
			XDIAddress[] contributorXris,
			XDIAddress contributorsXri,
			XDIAddress relativeTargetAddress,
			GetOperation operation,
			MessageResult messageResult,
			ExecutionContext executionContext) throws Xdi2MessagingException {

		messageResult.getGraph().setStatement(XDIStatement.fromLiteralComponents(
				XDIAddress.create("" + contributorsXri + "=a<#b>&"),
				this.value));

		messageResult.getGraph().setStatement(XDIStatement.fromRelationComponents(
				XDIAddress.create("" + contributorsXri + "=x*y"),
				XDIAddress.create("" + "#c"),
				XDIAddress.create("" + contributorsXri + "=d*e")));

		return ContributorResult.DEFAULT;
	}
}
