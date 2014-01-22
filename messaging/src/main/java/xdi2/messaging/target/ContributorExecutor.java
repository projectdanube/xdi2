package xdi2.messaging.target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.contributor.Contributor;
import xdi2.messaging.target.contributor.ContributorMap;
import xdi2.messaging.target.contributor.ContributorResult;
import xdi2.messaging.target.contributor.ContributorMap.ContributorFound;

public class ContributorExecutor {

	private static final Logger log = LoggerFactory.getLogger(ContributorExecutor.class);

	private ContributorExecutor() {

	}


	/*
	 * Methods for executing contributors
	 */

	public static ContributorResult executeContributorsAddress(ContributorMap contributorMap, XDI3Segment[] contributorChainXris, XDI3Segment relativeTargetAddress, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContributorResult contributorResultAddress = ContributorResult.DEFAULT;

		// find an address with contributors

		XDI3Segment relativeContextNodeXri = relativeTargetAddress;

		List<ContributorFound> contributorFounds = new ArrayList<ContributorFound> ();
		contributorFounds.addAll(findHigherContributors(contributorMap, relativeContextNodeXri));
		contributorFounds.addAll(findLowerContributors(contributorMap, relativeContextNodeXri));
		if (contributorFounds.size() == 0) return ContributorResult.DEFAULT;

		if (log.isDebugEnabled()) log.debug("For relative target address: " + relativeTargetAddress + " found contributors: " + contributorFounds);

		for (ContributorFound contributorFound : contributorFounds) {

			XDI3Segment contributorXri = contributorFound.getContributorXri();
			Contributor contributor = contributorFound.getContributor();

			// skip the contributor?

			if (! contributor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping contributor (disabled) " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on contributorXri " + contributorXri + " and relative target address " + relativeTargetAddress + ".");
				continue;
			}

			// calculate next addresses

			XDI3Segment nextRelativeTargetAddress = relativeTargetAddress == null ? null : XDI3Util.removeStartXri(relativeTargetAddress, contributorXri);
			XDI3Segment nextRelativeContextNodeXri = nextRelativeTargetAddress;

			XDI3Segment[] nextContributorChainXris = Arrays.copyOf(contributorChainXris, contributorChainXris.length + 1);
			nextContributorChainXris[nextContributorChainXris.length - 1] = contributorXri;

			XDI3Segment nextContributorChainXri = XDI3Util.concatXris(nextContributorChainXris);

			// execute the contributor

			if (log.isDebugEnabled()) log.debug("Executing contributor " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on contributor XRI " + contributorXri + " and relative target address " + relativeTargetAddress + "." + " Next contributor chain XRI is " + nextContributorChainXri + ", and next relative target address is " + nextRelativeTargetAddress + ".");

			try {

				executionContext.pushContributor(contributor, "Contributor: address: " + nextRelativeTargetAddress + " [" + nextContributorChainXri + "]");

				// execute sub-contributors (address)

				if (! contributor.getContributors().isEmpty()) {

					ContributorResult contributorResult = executeContributorsAddress(contributor.getContributors(), nextContributorChainXris, nextRelativeTargetAddress, operation, operationMessageResult, executionContext);
					contributorResultAddress = contributorResultAddress.or(contributorResult);

					if (contributorResult.isSkipParentContributors()) {

						if (log.isDebugEnabled()) log.debug("Skipping parent contributors according to sub-contributors (address).");
						return contributorResultAddress;
					}
				}

				// execute contributor (address)

				MessageResult tempMessageResult = new MessageResult();

				ContributorResult contributorResult = contributor.executeOnAddress(nextContributorChainXris, nextContributorChainXri, nextRelativeTargetAddress, operation, tempMessageResult, executionContext);
				contributorResultAddress = contributorResultAddress.or(contributorResult);

				XDI3Segment tempContextNodeXri = XDI3Util.concatXris(nextContributorChainXri, nextRelativeContextNodeXri);
				ContextNode tempContextNode = tempMessageResult.getGraph().getDeepContextNode(tempContextNodeXri);

				if (tempContextNode != null) CopyUtil.copyContextNode(tempContextNode, operationMessageResult.getGraph(), null);

				if (contributorResult.isSkipSiblingContributors()) {

					if (log.isDebugEnabled()) log.debug("Skipping sibling contributors (address) according to " + contributor.getClass().getSimpleName() + ".");
					return contributorResultAddress;
				}
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popContributor();
			}
		}

		// done

		return contributorResultAddress;
	}

	public static ContributorResult executeContributorsStatement(ContributorMap contributorMap, XDI3Segment contributorChainXris[], XDI3Statement relativeTargetStatement, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContributorResult contributorResultStatement = ContributorResult.DEFAULT;

		// find an address with contributors

		XDI3Segment relativeContextNodeXri = relativeTargetStatement == null ? null : relativeTargetStatement.getContextNodeXri();

		List<ContributorFound> contributorFounds = new ArrayList<ContributorFound> ();
		contributorFounds.addAll(findHigherContributors(contributorMap, relativeContextNodeXri));
		if (contributorFounds.size() == 0) return ContributorResult.DEFAULT;

		if (log.isDebugEnabled()) log.debug("For relative target statement: " + relativeTargetStatement + " found contributors: " + contributorFounds);

		for (ContributorFound contributorFound : contributorFounds) {

			XDI3Segment contributorXri = contributorFound.getContributorXri();
			Contributor contributor = contributorFound.getContributor();

			// skip contributor?

			if (! contributor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping contributor (disabled) " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on contributorXri " + contributorXri + " and relative target statement " + relativeTargetStatement + ".");
				continue;
			}

			// calculate next addresses

			XDI3Statement nextRelativeTargetStatement = relativeTargetStatement == null ? null : StatementUtil.removeStartXriStatement(relativeTargetStatement, contributorXri, false, false, true);
			XDI3Segment nextRelativeContextNodeXri = nextRelativeTargetStatement == null ? null : nextRelativeTargetStatement.getContextNodeXri();

			XDI3Segment[] nextContributorChainXris = Arrays.copyOf(contributorChainXris, contributorChainXris.length + 1);
			nextContributorChainXris[nextContributorChainXris.length - 1] = contributorXri;

			XDI3Segment nextContributorChainXri = XDI3Util.concatXris(nextContributorChainXris);

			// execute the contributor

			if (log.isDebugEnabled()) log.debug("Executing contributor " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on contributor XRI " + contributorXri + " and relative target statement " + relativeTargetStatement + "." + " Next contributor chain XRI is " + nextContributorChainXri + ", and next relative target statement is " + nextRelativeTargetStatement + ".");

			try {

				executionContext.pushContributor(contributor, "Contributor: statement: " + nextRelativeTargetStatement + " [" + nextContributorChainXri + "]");

				// execute sub-contributors (statement)

				if (! contributor.getContributors().isEmpty()) {

					ContributorResult contributorResult = executeContributorsStatement(contributor.getContributors(), nextContributorChainXris, nextRelativeTargetStatement, operation, operationMessageResult, executionContext);
					contributorResultStatement = contributorResultStatement.or(contributorResult);

					if (contributorResult.isSkipParentContributors()) {

						if (log.isDebugEnabled()) log.debug("Skipping parent contributors according to sub-contributors (statement).");
						return contributorResultStatement;
					}
				}

				// execute contributor (statement)

				MessageResult tempMessageResult = new MessageResult();

				ContributorResult contributorResult = contributor.executeOnStatement(nextContributorChainXris, nextContributorChainXri, nextRelativeTargetStatement, operation, operationMessageResult, executionContext);
				contributorResultStatement = contributorResultStatement.or(contributorResult);

				XDI3Segment tempContextNodeXri = XDI3Util.concatXris(nextContributorChainXri, nextRelativeContextNodeXri);
				ContextNode tempContextNode = tempMessageResult.getGraph().getDeepContextNode(tempContextNodeXri);

				if (tempContextNode != null) CopyUtil.copyContextNode(tempContextNode, operationMessageResult.getGraph(), null);

				if (contributorResult.isSkipSiblingContributors()) {

					if (log.isDebugEnabled()) log.debug("Skipping sibling contributors (statement) according to " + contributor.getClass().getSimpleName() + ".");
					return contributorResultStatement;
				}
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popContributor();
			}
		}

		// done

		return contributorResultStatement;
	}

	/*
	 * Methods for finding contributors
	 */

	public static List<ContributorFound> findHigherContributors(ContributorMap contributorMap, XDI3Segment contextNodeXri) {

		if (contributorMap.isEmpty()) return new ArrayList<ContributorFound> ();

		List<ContributorFound> higherContributors = new ArrayList<ContributorFound> ();

		if (contextNodeXri == null) {

		} else {

			for (Map.Entry<XDI3Segment, List<Contributor>> contributorEntry : contributorMap.entrySet()) {

				XDI3Segment contributorXri = contributorEntry.getKey();
				XDI3Segment startXri = XDI3Util.startsWith(contextNodeXri, contributorXri, false, true);
				if (startXri == null) continue;

				contributorXri = startXri;

				List<Contributor> contributors = contributorEntry.getValue();
				for (Contributor contributor : contributors) higherContributors.add(new ContributorFound(contributorXri, contributor));
			}
		}

		if (higherContributors.isEmpty()) {

			if (log.isDebugEnabled()) log.debug("Finding higher contributors for " + contextNodeXri + ": No matches.");
		} else {

			if (log.isDebugEnabled()) log.debug("Finding higher contributors for " + contextNodeXri + ": Matches at " + higherContributors);
		}

		return higherContributors;
	}

	public static List<ContributorFound> findLowerContributors(ContributorMap contributorMap, XDI3Segment contextNodeXri) {

		if (contributorMap.isEmpty()) return new ArrayList<ContributorFound> ();

		List<ContributorFound> lowerContributors = new ArrayList<ContributorFound> ();

		if (contextNodeXri == null) {

			for (Map.Entry<XDI3Segment, List<Contributor>> contributorEntry : contributorMap.entrySet()) {

				XDI3Segment contributorXri = contributorEntry.getKey();

				List<Contributor> contributors = contributorEntry.getValue();
				for (Contributor contributor : contributors) lowerContributors.add(new ContributorFound (contributorXri, contributor));
			}
		} else {

			for (Map.Entry<XDI3Segment, List<Contributor>> contributorEntry : contributorMap.entrySet()) {

				XDI3Segment contributorXri = contributorEntry.getKey();
				XDI3Segment startXri = XDI3Util.startsWith(contributorXri, contextNodeXri, false, true);
				if (startXri == null) continue;

				if (startXri.equals(contributorXri)) continue;

				List<Contributor> contributors = contributorEntry.getValue();
				for (Contributor contributor : contributors) lowerContributors.add(new ContributorFound (contributorXri, contributor));
			}
		}

		if (lowerContributors.isEmpty()) {

			if (log.isDebugEnabled()) log.debug("Finding lower contributors for " + contextNodeXri + ": No matches.");
		} else {

			if (log.isDebugEnabled()) log.debug("Finding lower contributors for " + contextNodeXri + ": Matches at " + lowerContributors);
		}

		return lowerContributors;
	}
}
