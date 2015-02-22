package xdi2.messaging.target.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.XDIStatementUtil;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.contributor.Contributor;
import xdi2.messaging.target.contributor.ContributorMap;
import xdi2.messaging.target.contributor.ContributorMap.ContributorFound;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.contributor.ContributorResult;

public class ContributorExecutor {

	private static final Logger log = LoggerFactory.getLogger(ContributorExecutor.class);

	private ContributorExecutor() {

	}

	/*
	 * Methods for executing contributors
	 */

	public static ContributorResult executeContributorsAddress(ContributorMap contributorMap, XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetXDIAddress, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContributorResult contributorResultXDIAddress = ContributorResult.DEFAULT;

		// find an address with contributors

		XDIAddress relativeContextNodeXDIAddress = relativeTargetXDIAddress;

		List<ContributorFound> contributorFounds = new ArrayList<ContributorFound> ();
		contributorFounds.addAll(findHigherContributors(contributorMap, contributorsAddress, relativeContextNodeXDIAddress));
		contributorFounds.addAll(findLowerContributors(contributorMap, contributorsAddress, relativeContextNodeXDIAddress));
		if (contributorFounds.size() == 0) return ContributorResult.DEFAULT;

		if (log.isDebugEnabled()) log.debug("For relative target address: " + relativeTargetXDIAddress + " found contributors: " + contributorFounds);

		for (ContributorFound contributorFound : contributorFounds) {

			XDIAddress contributorAddress = contributorFound.getContributorXDIAddress();
			Contributor contributor = contributorFound.getContributor();

			// check mount

			ContributorMount contributorMount = contributorFound.getContributor().getContributorMount();

			if (contributorMount != null &&
					contributorMount.operationXDIAddresses().length > 0 && 
					! Arrays.asList(contributorMount.operationXDIAddresses()).contains(operation.getOperationXDIAddress())) {

				if (log.isDebugEnabled()) log.debug("Skipping contributor (doesn't like operation) " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributor address " + contributorAddress + " and relative target address " + relativeTargetXDIAddress + ".");
				continue;
			}

			// skip the contributor?

			if (contributor.skip(executionContext)) {

				if (log.isDebugEnabled()) log.debug("Skipping contributor (disabled) " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributor address " + contributorAddress + " and relative target address " + relativeTargetXDIAddress + ".");
				continue;
			}

			// calculate next addresses

			XDIAddress nextRelativeTargetXDIAddress = relativeTargetXDIAddress == null ? null : XDIAddressUtil.removeStartXDIAddress(relativeTargetXDIAddress, contributorAddress);
			XDIAddress nextRelativeContextNodeXDIAddress = nextRelativeTargetXDIAddress;

			XDIAddress[] nextContributorAddresses = Arrays.copyOf(contributorAddresses, contributorAddresses.length + 1);
			nextContributorAddresses[nextContributorAddresses.length - 1] = contributorAddress;

			XDIAddress nextContributorsAddress = XDIAddressUtil.concatXDIAddresses(nextContributorAddresses);

			// execute the contributor

			if (log.isDebugEnabled()) log.debug("Executing contributor " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributor address " + contributorAddress + " and relative target address " + relativeTargetXDIAddress + "." + " Next contributor chain address is " + nextContributorsAddress + ", and next relative target address is " + nextRelativeTargetXDIAddress + ".");

			try {

				executionContext.pushContributor(contributor, "Contributor: address: " + nextRelativeTargetXDIAddress + " / " + nextContributorsAddress);

				// execute sub-contributors (address)

				if (! contributor.getContributors().isEmpty()) {

					ContributorResult contributorResult = executeContributorsAddress(contributor.getContributors(), nextContributorAddresses, nextContributorsAddress, nextRelativeTargetXDIAddress, operation, operationMessageResult, executionContext);
					contributorResultXDIAddress = contributorResultXDIAddress.or(contributorResult);

					if (contributorResult.isSkipParentContributors()) {

						if (log.isDebugEnabled()) log.debug("Skipping parent contributors according to sub-contributors (address).");
						return contributorResultXDIAddress;
					}
				}

				// execute contributor (address)

				MessageResult tempMessageResult = new MessageResult();

				ContributorResult contributorResult = contributor.executeOnAddress(nextContributorAddresses, nextContributorsAddress, nextRelativeTargetXDIAddress, operation, tempMessageResult, executionContext);
				contributorResultXDIAddress = contributorResultXDIAddress.or(contributorResult);

				XDIAddress tempContextNodeXDIAddress = XDIAddressUtil.concatXDIAddresses(nextContributorsAddress, nextRelativeContextNodeXDIAddress);
				ContextNode tempContextNode = tempMessageResult.getGraph().getDeepContextNode(tempContextNodeXDIAddress, true);

				if (tempContextNode != null) CopyUtil.copyContextNode(tempContextNode, operationMessageResult.getGraph(), null);

				if (contributorResult.isSkipSiblingContributors()) {

					if (log.isDebugEnabled()) log.debug("Skipping sibling contributors (address) according to " + contributor.getClass().getSimpleName() + ".");
					return contributorResultXDIAddress;
				}
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popContributor();
			}
		}

		// done

		return contributorResultXDIAddress;
	}

	public static ContributorResult executeContributorsStatement(ContributorMap contributorMap, XDIAddress contributorAddresses[], XDIAddress contributorsAddress, XDIStatement relativeTargetXDIStatement, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContributorResult contributorResultXDIStatement = ContributorResult.DEFAULT;

		// find an address with contributors

		XDIAddress relativeContextNodeXDIAddress = relativeTargetXDIStatement == null ? null : relativeTargetXDIStatement.getContextNodeXDIAddress();

		List<ContributorFound> contributorFounds = new ArrayList<ContributorFound> ();
		contributorFounds.addAll(findHigherContributors(contributorMap, contributorsAddress, relativeContextNodeXDIAddress));
		if (contributorFounds.size() == 0) return ContributorResult.DEFAULT;

		if (log.isDebugEnabled()) log.debug("For relative target statement: " + relativeTargetXDIStatement + " found contributors: " + contributorFounds);

		for (ContributorFound contributorFound : contributorFounds) {

			XDIAddress contributorAddress = contributorFound.getContributorXDIAddress();
			Contributor contributor = contributorFound.getContributor();

			// check mount

			ContributorMount contributorMount = contributorFound.getContributor().getContributorMount();

			if (contributorMount != null &&
					contributorMount.operationXDIAddresses().length > 0 && 
					! Arrays.asList(contributorMount.operationXDIAddresses()).contains(operation.getOperationXDIAddress())) {

				if (log.isDebugEnabled()) log.debug("Skipping contributor (doesn't like operation) " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributor address " + contributorAddress + " and relative target statement " + relativeTargetXDIStatement + ".");
				continue;
			}

			if (relativeTargetXDIStatement.isContextNodeStatement()) {

				if (contributorMount != null &&
						contributorMount.contextNodeXDIArcs().length > 0 && 
						! Arrays.asList(contributorMount.contextNodeXDIArcs()).contains(relativeTargetXDIStatement.getContextNodeXDIArc())) {

					if (log.isDebugEnabled()) log.debug("Skipping contributor (doesn't like context node arc " + relativeTargetXDIStatement.getContextNodeXDIArc() + ") " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributor address " + contributorAddress + " and relative target statement " + relativeTargetXDIStatement + ".");
					continue;
				}

				if (contributorMount != null &&
						contributorMount.targetContextNodeXDIAddresses().length > 0 && 
						! Arrays.asList(contributorMount.targetContextNodeXDIAddresses()).contains(relativeTargetXDIStatement.getTargetContextNodeXDIAddress())) {

					if (log.isDebugEnabled()) log.debug("Skipping contributor (doesn't like target context node address " + relativeTargetXDIStatement.getTargetContextNodeXDIAddress() + ") " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributor address " + contributorAddress + " and relative target statement " + relativeTargetXDIStatement + ".");
					continue;
				}
			}

			if (relativeTargetXDIStatement.isRelationStatement()) {

				if (contributorMount != null &&
						contributorMount.relationXDIAddresses().length > 0 && 
						! Arrays.asList(contributorMount.relationXDIAddresses()).contains(relativeTargetXDIStatement.getRelationXDIAddress())) {

					if (log.isDebugEnabled()) log.debug("Skipping contributor (doesn't like relation arc " + relativeTargetXDIStatement.getRelationXDIAddress() + ") " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributor address " + contributorAddress + " and relative target statement " + relativeTargetXDIStatement + ".");
					continue;
				}

				if (contributorMount != null &&
						contributorMount.targetContextNodeXDIAddresses().length > 0 && 
						! Arrays.asList(contributorMount.targetContextNodeXDIAddresses()).contains(relativeTargetXDIStatement.getTargetContextNodeXDIAddress())) {

					if (log.isDebugEnabled()) log.debug("Skipping contributor (doesn't like target context node address " + relativeTargetXDIStatement.getTargetContextNodeXDIAddress() + ") " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributor address " + contributorAddress + " and relative target statement " + relativeTargetXDIStatement + ".");
					continue;
				}
			}

			if (relativeTargetXDIStatement.isLiteralStatement()) {

			}

			// skip contributor?

			if (contributor.skip(executionContext)) {

				if (log.isDebugEnabled()) log.debug("Skipping contributor (disabled) " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributor address " + contributorAddress + " and relative target statement " + relativeTargetXDIStatement + ".");
				continue;
			}

			// calculate next addresses

			XDIStatement nextRelativeTargetXDIStatement = relativeTargetXDIStatement == null ? null : XDIStatementUtil.removeStartXDIStatement(relativeTargetXDIStatement, contributorAddress);
			XDIAddress nextRelativecontextNodeXDIAddress = nextRelativeTargetXDIStatement == null ? null : nextRelativeTargetXDIStatement.getContextNodeXDIAddress();

			XDIAddress[] nextContributorAddresses = Arrays.copyOf(contributorAddresses, contributorAddresses.length + 1);
			nextContributorAddresses[nextContributorAddresses.length - 1] = contributorAddress;

			XDIAddress nextContributorsAddress = XDIAddressUtil.concatXDIAddresses(nextContributorAddresses);

			// execute the contributor

			if (log.isDebugEnabled()) log.debug("Executing contributor " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributor address " + contributorAddress + " and relative target statement " + relativeTargetXDIStatement + "." + " Next contributor chain address is " + nextContributorsAddress + ", and next relative target statement is " + nextRelativeTargetXDIStatement + ".");

			try {

				executionContext.pushContributor(contributor, "Contributor: statement: " + nextRelativeTargetXDIStatement + " / " + nextContributorsAddress);

				// execute sub-contributors (statement)

				if (! contributor.getContributors().isEmpty()) {

					ContributorResult contributorResult = executeContributorsStatement(contributor.getContributors(), nextContributorAddresses, nextContributorsAddress, nextRelativeTargetXDIStatement, operation, operationMessageResult, executionContext);
					contributorResultXDIStatement = contributorResultXDIStatement.or(contributorResult);

					if (contributorResult.isSkipParentContributors()) {

						if (log.isDebugEnabled()) log.debug("Skipping parent contributors according to sub-contributors (statement).");
						return contributorResultXDIStatement;
					}
				}

				// execute contributor (statement)

				MessageResult tempMessageResult = new MessageResult();

				ContributorResult contributorResult = contributor.executeOnStatement(nextContributorAddresses, nextContributorsAddress, nextRelativeTargetXDIStatement, operation, operationMessageResult, executionContext);
				contributorResultXDIStatement = contributorResultXDIStatement.or(contributorResult);

				XDIAddress tempContextNodeXDIAddress = XDIAddressUtil.concatXDIAddresses(nextContributorsAddress, nextRelativecontextNodeXDIAddress);
				ContextNode tempContextNode = tempMessageResult.getGraph().getDeepContextNode(tempContextNodeXDIAddress, true);

				if (tempContextNode != null) CopyUtil.copyContextNode(tempContextNode, operationMessageResult.getGraph(), null);

				if (contributorResult.isSkipSiblingContributors()) {

					if (log.isDebugEnabled()) log.debug("Skipping sibling contributors (statement) according to " + contributor.getClass().getSimpleName() + ".");
					return contributorResultXDIStatement;
				}
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popContributor();
			}
		}

		// done

		return contributorResultXDIStatement;
	}

	/*
	 * Methods for finding contributors
	 */

	public static List<ContributorFound> findHigherContributors(ContributorMap contributorMap, XDIAddress contributorsAddress, XDIAddress relativeContextNodeXDIAddress) {

		if (contributorMap.isEmpty()) return new ArrayList<ContributorFound> ();

		List<ContributorFound> higherContributors = new ArrayList<ContributorFound> ();

		if (relativeContextNodeXDIAddress == null) {

		} else {

			for (Map.Entry<XDIAddress, List<Contributor>> contributorEntry : contributorMap.entrySet()) {

				XDIAddress nextContributorAddress = contributorEntry.getKey();
				XDIAddress startXDIAddress = XDIAddressUtil.startsWithXDIAddress(XDIAddressUtil.concatXDIAddresses(contributorsAddress, relativeContextNodeXDIAddress), XDIAddressUtil.concatXDIAddresses(contributorsAddress, nextContributorAddress), false, true);
				if (startXDIAddress == null) continue;

				startXDIAddress = XDIAddressUtil.localXDIAddress(startXDIAddress, - contributorsAddress.getNumXDIArcs());

				nextContributorAddress = startXDIAddress;

				List<Contributor> contributors = contributorEntry.getValue();
				for (Contributor contributor : contributors) higherContributors.add(new ContributorFound(nextContributorAddress, contributor));
			}
		}

		if (higherContributors.isEmpty()) {

			if (log.isDebugEnabled()) log.debug("Finding higher contributors for " + relativeContextNodeXDIAddress + ": No matches.");
		} else {

			if (log.isDebugEnabled()) log.debug("Finding higher contributors for " + relativeContextNodeXDIAddress + ": Matches at " + higherContributors);
		}

		return higherContributors;
	}

	public static List<ContributorFound> findLowerContributors(ContributorMap contributorMap, XDIAddress contributorsAddress, XDIAddress relativeContextNodeXDIAddress) {

		if (contributorMap.isEmpty()) return new ArrayList<ContributorFound> ();

		List<ContributorFound> lowerContributors = new ArrayList<ContributorFound> ();

		if (relativeContextNodeXDIAddress == null) {

			for (Map.Entry<XDIAddress, List<Contributor>> contributorEntry : contributorMap.entrySet()) {

				XDIAddress nextContributorAddress = contributorEntry.getKey();

				List<Contributor> contributors = contributorEntry.getValue();
				for (Contributor contributor : contributors) lowerContributors.add(new ContributorFound(nextContributorAddress, contributor));
			}
		} else {

			for (Map.Entry<XDIAddress, List<Contributor>> contributorEntry : contributorMap.entrySet()) {

				XDIAddress nextContributorAddress = contributorEntry.getKey();
				XDIAddress startXDIAddress = XDIAddressUtil.startsWithXDIAddress(XDIAddressUtil.concatXDIAddresses(contributorsAddress, nextContributorAddress), XDIAddressUtil.concatXDIAddresses(contributorsAddress, relativeContextNodeXDIAddress), false, true);
				if (startXDIAddress == null) continue;

				startXDIAddress = XDIAddressUtil.localXDIAddress(startXDIAddress, - contributorsAddress.getNumXDIArcs());

				if (startXDIAddress.equals(nextContributorAddress)) continue;

				List<Contributor> contributors = contributorEntry.getValue();
				for (Contributor contributor : contributors) lowerContributors.add(new ContributorFound(nextContributorAddress, contributor));
			}
		}

		if (lowerContributors.isEmpty()) {

			if (log.isDebugEnabled()) log.debug("Finding lower contributors for " + relativeContextNodeXDIAddress + ": No matches.");
		} else {

			if (log.isDebugEnabled()) log.debug("Finding lower contributors for " + relativeContextNodeXDIAddress + ": Matches at " + lowerContributors);
		}

		return lowerContributors;
	}
}
