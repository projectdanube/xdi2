package xdi2.messaging.container.contributor;

import java.io.Serializable;

public class ContributorResult implements Serializable {

	private static final long serialVersionUID = -4112352311435192349L;

	public static final ContributorResult DEFAULT = new ContributorResult(false, false, false);
	public static final ContributorResult SKIP_PARENT_CONTRIBUTORS = new ContributorResult(true, false, false);
	public static final ContributorResult SKIP_SIBLING_CONTRIBUTORS = new ContributorResult(false, true, false);
	public static final ContributorResult SKIP_MESSAGING_CONTAINER = new ContributorResult(false, false, true);
	public static final ContributorResult SKIP_PARENT_CONTRIBUTORS_AND_MESSAGING_CONTAINER = new ContributorResult(true, false, true);
	public static final ContributorResult SKIP_SIBLING_CONTRIBUTORS_AND_MESSAGING_CONTAINER = new ContributorResult(false, true, true);
	public static final ContributorResult SKIP_PARENT_CONTRIBUTORS_AND_SIBLING_CONTRIBUTORS_AND_MESSAGING_CONTAINER = new ContributorResult(true, true, true);

	private boolean skipParentContributors;
	private boolean skipSiblingContributors;
	private boolean skipMessagingContainer;

	public ContributorResult(boolean skipParentContributors, boolean skipSiblingContributors, boolean skipMessagingContainer) {

		this.skipParentContributors = skipParentContributors;
		this.skipSiblingContributors = skipSiblingContributors;
		this.skipMessagingContainer = skipMessagingContainer;
	}

	public boolean isSkipParentContributors() {

		return this.skipParentContributors;
	}

	public boolean isSkipSiblingContributors() {

		return this.skipSiblingContributors;
	}

	public boolean isSkipMessagingContainer() {

		return this.skipMessagingContainer;
	}

	public ContributorResult or(ContributorResult contributorResult) {

		boolean skipParentContributors = this.skipParentContributors || contributorResult.skipParentContributors;
		boolean skipSiblingContributors = this.skipSiblingContributors || contributorResult.skipSiblingContributors;
		boolean skipMessagingContainer = this.skipMessagingContainer || contributorResult.skipMessagingContainer;

		return new ContributorResult(skipParentContributors, skipSiblingContributors, skipMessagingContainer);
	}

	@Override
	public String toString() {

		return "[skipParentContributors:" + this.skipParentContributors + ",skipSiblingContributors:" + this.skipSiblingContributors + ",skipMessagingContainer:" + this.skipMessagingContainer + "]";
	}
}
