package xdi2.messaging.target.contributor;

import java.io.Serializable;

public class ContributorResult implements Serializable {

	private static final long serialVersionUID = -4112352311435192349L;

	public static final ContributorResult DEFAULT = new ContributorResult(false, false, false);
	public static final ContributorResult SKIP_PARENT_CONTRIBUTORS = new ContributorResult(true, false, false);
	public static final ContributorResult SKIP_SIBLING_CONTRIBUTORS = new ContributorResult(false, true, false);
	public static final ContributorResult SKIP_MESSAGING_TARGET = new ContributorResult(false, false, true);

	private boolean skipParentContributors;
	private boolean skipSiblingContributors;
	private boolean skipMessagingTarget;

	public ContributorResult(boolean skipParentContributors, boolean skipSiblingContributors, boolean skipMessagingTarget) {

		this.skipParentContributors = skipParentContributors;
		this.skipSiblingContributors = skipSiblingContributors;
		this.skipMessagingTarget = skipMessagingTarget;
	}

	public boolean isSkipParentContributors() {

		return this.skipParentContributors;
	}

	public boolean isSkipSiblingContributors() {

		return this.skipSiblingContributors;
	}

	public boolean isSkipMessagingTarget() {

		return this.skipMessagingTarget;
	}

	public ContributorResult or(ContributorResult contributorResult) {

		boolean skipParentContributors = this.skipParentContributors || contributorResult.skipParentContributors;
		boolean skipSiblingContributors = this.skipSiblingContributors || contributorResult.skipSiblingContributors;
		boolean skipMessagingTarget = this.skipMessagingTarget || contributorResult.skipMessagingTarget;

		return new ContributorResult(skipParentContributors, skipSiblingContributors, skipMessagingTarget);
	}

	@Override
	public String toString() {

		return "[skipParentContributors:" + this.skipParentContributors + ",skipSiblingContributors:" + this.skipSiblingContributors + ",skipMessagingTarget:" + this.skipMessagingTarget + "]";
	}
}
