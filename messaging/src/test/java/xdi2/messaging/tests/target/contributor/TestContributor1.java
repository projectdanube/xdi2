package xdi2.messaging.tests.target.contributor;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorXri;

@ContributorXri(addresses={"(+con)"})
public class TestContributor1 extends AbstractContributor {

	private String value = "val";

	public TestContributor1() {
		
		super();
		
		this.getContributors().addContributor(new TestContributor2());
	}
	
	@Override
	public boolean getContext(
			XDI3Segment[] contributorXris,
			XDI3Segment contributorsXri,
			XDI3Segment contextNodeXri,
			GetOperation operation,
			MessageResult messageResult,
			ExecutionContext executionContext) throws Xdi2MessagingException {

		messageResult.getGraph().createStatement(XDI3Statement.fromLiteralComponents(
				XDI3Segment.create("" + contributorsXri + "=a<+b>&"),
				this.value));

		messageResult.getGraph().createStatement(XDI3Statement.fromRelationComponents(
				XDI3Segment.create("" + contributorsXri + "=x*y"),
				XDI3Segment.create("" + "+c"),
				XDI3Segment.create("" + contributorsXri + "=d*e")));

		return false;
	}
}
