package xdi2.messaging.tests.target.contributor;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.contributor.ContributorResult;

@ContributorMount(contributorAddresses={"(#con)"})
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
			Graph resultGraph,
			ExecutionContext executionContext) throws Xdi2MessagingException {

		resultGraph.setStatement(XDIStatement.fromLiteralComponents(
				XDIAddress.create("" + contributorsAddress + "=a<#b>&"),
				this.value));

		resultGraph.setStatement(XDIStatement.fromRelationComponents(
				XDIAddress.create("" + contributorsAddress + "=x*y"),
				XDIAddress.create("" + "#c"),
				XDIAddress.create("" + contributorsAddress + "=d*e")));

		return ContributorResult.DEFAULT;
	}
}
