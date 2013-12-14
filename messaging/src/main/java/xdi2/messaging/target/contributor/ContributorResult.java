package xdi2.messaging.target.contributor;

import java.io.Serializable;

public class ContributorResult implements Serializable {

	private static final long serialVersionUID = -4112352311435192349L;

	public static final ContributorResult DEFAULT = new ContributorResult(false, false, false);

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

	public void setSkipParentContributors(boolean skipParentContributors) {

		this.skipParentContributors = skipParentContributors;
	}

	public boolean isSkipSiblingContributors() {

		return this.skipSiblingContributors;
	}

	public void setSkipSiblingContributors(boolean skipSiblingContributors) {

		this.skipSiblingContributors = skipSiblingContributors;
	}

	public boolean isSkipMessagingTarget() {

		return this.skipMessagingTarget;
	}

	public void setSkipMessagingTarget(boolean skipMessagingTarget) {

		this.skipMessagingTarget = skipMessagingTarget;
	}

	public void or(ContributorResult contributorResult) {

		this.skipParentContributors = this.skipParentContributors || contributorResult.skipParentContributors;
		this.skipSiblingContributors = this.skipSiblingContributors || contributorResult.skipSiblingContributors;
		this.skipMessagingTarget = this.skipMessagingTarget || contributorResult.skipMessagingTarget;
	}

	@Override
	public String toString() {

		return "[skipParentContributors:" + this.isSkipParentContributors() + ",skipSiblingContributors:" + this.isSkipSiblingContributors() + ",skipMessagingTarget:" + this.isSkipMessagingTarget() + "]";
	}
}
