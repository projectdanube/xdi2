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

	public static ContributorResult executeContributorsAddress(ContributorMap contributorMap, XDIAddress[] contributorChainAddresses, XDIAddress relativeTargetAddress, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContributorResult contributorResultAddress = ContributorResult.DEFAULT;

		// find an address with contributors

		XDIAddress relativecontextNodeXDIAddress = relativeTargetAddress;

		List<ContributorFound> contributorFounds = new ArrayList<ContributorFound> ();
		contributorFounds.addAll(findHigherContributors(contributorMap, relativecontextNodeXDIAddress));
		contributorFounds.addAll(findLowerContributors(contributorMap, relativecontextNodeXDIAddress));
		if (contributorFounds.size() == 0) return ContributorResult.DEFAULT;

		if (log.isDebugEnabled()) log.debug("For relative target address: " + relativeTargetAddress + " found contributors: " + contributorFounds);

		for (ContributorFound contributorFound : contributorFounds) {

			XDIAddress contributorAddress = contributorFound.getContributorAddress();
			Contributor contributor = contributorFound.getContributor();

			// check mount

			ContributorMount contributorMount = contributorFound.getContributor().getContributorMount();

			if (contributorMount.operationAddresses().length > 0 && ! Arrays.asList(contributorMount.operationAddresses()).contains(operation.getOperationXDIAddress())) {

				if (log.isDebugEnabled()) log.debug("Skipping contributor (doesn't like operation) " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributorAddress " + contributorAddress + " and relative target address " + relativeTargetAddress + ".");
				continue;
			}

			// skip the contributor?

			if (contributor.skip(executionContext)) {

				if (log.isDebugEnabled()) log.debug("Skipping contributor (disabled) " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributorAddress " + contributorAddress + " and relative target address " + relativeTargetAddress + ".");
				continue;
			}

			// calculate next addresses

			XDIAddress nextRelativeTargetAddress = relativeTargetAddress == null ? null : XDIAddressUtil.removeStartXDIAddress(relativeTargetAddress, contributorAddress);
			XDIAddress nextRelativecontextNodeXDIAddress = nextRelativeTargetAddress;

			XDIAddress[] nextContributorChainAddresses = Arrays.copyOf(contributorChainAddresses, contributorChainAddresses.length + 1);
			nextContributorChainAddresses[nextContributorChainAddresses.length - 1] = contributorAddress;

			XDIAddress nextContributorChainAddress = XDIAddressUtil.concatXDIAddresses(nextContributorChainAddresses);

			// execute the contributor

			if (log.isDebugEnabled()) log.debug("Executing contributor " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributor XRI " + contributorAddress + " and relative target address " + relativeTargetAddress + "." + " Next contributor chain XRI is " + nextContributorChainAddress + ", and next relative target address is " + nextRelativeTargetAddress + ".");

			try {

				executionContext.pushContributor(contributor, "Contributor: address: " + nextRelativeTargetAddress + " / " + nextContributorChainAddress);

				// execute sub-contributors (address)

				if (! contributor.getContributors().isEmpty()) {

					ContributorResult contributorResult = executeContributorsAddress(contributor.getContributors(), nextContributorChainAddresses, nextRelativeTargetAddress, operation, operationMessageResult, executionContext);
					contributorResultAddress = contributorResultAddress.or(contributorResult);

					if (contributorResult.isSkipParentContributors()) {

						if (log.isDebugEnabled()) log.debug("Skipping parent contributors according to sub-contributors (address).");
						return contributorResultAddress;
					}
				}

				// execute contributor (address)

				MessageResult tempMessageResult = new MessageResult();

				ContributorResult contributorResult = contributor.executeOnAddress(nextContributorChainAddresses, nextContributorChainAddress, nextRelativeTargetAddress, operation, tempMessageResult, executionContext);
				contributorResultAddress = contributorResultAddress.or(contributorResult);

				XDIAddress tempcontextNodeXDIAddress = XDIAddressUtil.concatXDIAddresses(nextContributorChainAddress, nextRelativecontextNodeXDIAddress);
				ContextNode tempContextNode = tempMessageResult.getGraph().getDeepContextNode(tempcontextNodeXDIAddress, true);

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

	public static ContributorResult executeContributorsStatement(ContributorMap contributorMap, XDIAddress contributorChainAddresses[], XDIStatement relativeTargetStatement, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContributorResult contributorResultStatement = ContributorResult.DEFAULT;

		// find an address with contributors

		XDIAddress relativecontextNodeXDIAddress = relativeTargetStatement == null ? null : relativeTargetStatement.getContextNodeXDIAddress();

		List<ContributorFound> contributorFounds = new ArrayList<ContributorFound> ();
		contributorFounds.addAll(findHigherContributors(contributorMap, relativecontextNodeXDIAddress));
		if (contributorFounds.size() == 0) return ContributorResult.DEFAULT;

		if (log.isDebugEnabled()) log.debug("For relative target statement: " + relativeTargetStatement + " found contributors: " + contributorFounds);

		for (ContributorFound contributorFound : contributorFounds) {

			XDIAddress contributorAddress = contributorFound.getContributorAddress();
			Contributor contributor = contributorFound.getContributor();

			// check mount

			ContributorMount contributorMount = contributorFound.getContributor().getContributorMount();

			if (contributorMount.operationAddresses().length > 0 && ! Arrays.asList(contributorMount.operationAddresses()).contains(operation.getOperationXDIAddress())) {

				if (log.isDebugEnabled()) log.debug("Skipping contributor (doesn't like operation) " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributorAddress " + contributorAddress + " and relative target statement " + relativeTargetStatement + ".");
				continue;
			}

			if (relativeTargetStatement.isContextNodeStatement()) {

				if (contributorMount.contextNodeArcs().length > 0 && ! Arrays.asList(contributorMount.contextNodeArcs()).contains(relativeTargetStatement.getContextNodeXDIArc())) {

					if (log.isDebugEnabled()) log.debug("Skipping contributor (doesn't like context node arc " + relativeTargetStatement.getContextNodeXDIArc() + ") " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributorAddress " + contributorAddress + " and relative target statement " + relativeTargetStatement + ".");
					continue;
				}

				if (contributorMount.targetContextNodeXDIAddresses().length > 0 && ! Arrays.asList(contributorMount.targetContextNodeXDIAddresses()).contains(relativeTargetStatement.getTargetContextNodeXDIAddress())) {

					if (log.isDebugEnabled()) log.debug("Skipping contributor (doesn't like target context node XRI " + relativeTargetStatement.getTargetContextNodeXDIAddress() + ") " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributorAddress " + contributorAddress + " and relative target statement " + relativeTargetStatement + ".");
					continue;
				}
			}

			if (relativeTargetStatement.isRelationStatement()) {

				if (contributorMount.relationAddresses().length > 0 && ! Arrays.asList(contributorMount.relationAddresses()).contains(relativeTargetStatement.getRelationXDIAddress())) {

					if (log.isDebugEnabled()) log.debug("Skipping contributor (doesn't like relation arc " + relativeTargetStatement.getRelationXDIAddress() + ") " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributorAddress " + contributorAddress + " and relative target statement " + relativeTargetStatement + ".");
					continue;
				}

				if (contributorMount.targetContextNodeXDIAddresses().length > 0 && ! Arrays.asList(contributorMount.targetContextNodeXDIAddresses()).contains(relativeTargetStatement.getTargetContextNodeXDIAddress())) {

					if (log.isDebugEnabled()) log.debug("Skipping contributor (doesn't like target context node XRI " + relativeTargetStatement.getTargetContextNodeXDIAddress() + ") " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributorAddress " + contributorAddress + " and relative target statement " + relativeTargetStatement + ".");
					continue;
				}
			}

			if (relativeTargetStatement.isLiteralStatement()) {

			}

			// skip contributor?

			if (contributor.skip(executionContext)) {

				if (log.isDebugEnabled()) log.debug("Skipping contributor (disabled) " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributorAddress " + contributorAddress + " and relative target statement " + relativeTargetStatement + ".");
				continue;
			}

			// calculate next addresses

			XDIStatement nextRelativeTargetStatement = relativeTargetStatement == null ? null : XDIStatementUtil.removeStartXDIStatement(relativeTargetStatement, contributorAddress);
			XDIAddress nextRelativecontextNodeXDIAddress = nextRelativeTargetStatement == null ? null : nextRelativeTargetStatement.getContextNodeXDIAddress();

			XDIAddress[] nextContributorChainAddresses = Arrays.copyOf(contributorChainAddresses, contributorChainAddresses.length + 1);
			nextContributorChainAddresses[nextContributorChainAddresses.length - 1] = contributorAddress;

			XDIAddress nextContributorChainAddress = XDIAddressUtil.concatXDIAddresses(nextContributorChainAddresses);

			// execute the contributor

			if (log.isDebugEnabled()) log.debug("Executing contributor " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXDIAddress() + " on contributor XRI " + contributorAddress + " and relative target statement " + relativeTargetStatement + "." + " Next contributor chain XRI is " + nextContributorChainAddress + ", and next relative target statement is " + nextRelativeTargetStatement + ".");

			try {

				executionContext.pushContributor(contributor, "Contributor: statement: " + nextRelativeTargetStatement + " / " + nextContributorChainAddress);

				// execute sub-contributors (statement)

				if (! contributor.getContributors().isEmpty()) {

					ContributorResult contributorResult = executeContributorsStatement(contributor.getContributors(), nextContributorChainAddresses, nextRelativeTargetStatement, operation, operationMessageResult, executionContext);
					contributorResultStatement = contributorResultStatement.or(contributorResult);

					if (contributorResult.isSkipParentContributors()) {

						if (log.isDebugEnabled()) log.debug("Skipping parent contributors according to sub-contributors (statement).");
						return contributorResultStatement;
					}
				}

				// execute contributor (statement)

				MessageResult tempMessageResult = new MessageResult();

				ContributorResult contributorResult = contributor.executeOnStatement(nextContributorChainAddresses, nextContributorChainAddress, nextRelativeTargetStatement, operation, operationMessageResult, executionContext);
				contributorResultStatement = contributorResultStatement.or(contributorResult);

				XDIAddress tempcontextNodeXDIAddress = XDIAddressUtil.concatXDIAddresses(nextContributorChainAddress, nextRelativecontextNodeXDIAddress);
				ContextNode tempContextNode = tempMessageResult.getGraph().getDeepContextNode(tempcontextNodeXDIAddress, true);

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

	public static List<ContributorFound> findHigherContributors(ContributorMap contributorMap, XDIAddress contextNodeXDIAddress) {

		if (contributorMap.isEmpty()) return new ArrayList<ContributorFound> ();

		List<ContributorFound> higherContributors = new ArrayList<ContributorFound> ();

		if (contextNodeXDIAddress == null) {

		} else {

			for (Map.Entry<XDIAddress, List<Contributor>> contributorEntry : contributorMap.entrySet()) {

				XDIAddress contributorAddress = contributorEntry.getKey();
				XDIAddress startAddress = XDIAddressUtil.startsWithXDIAddress(contextNodeXDIAddress, contributorAddress, false, true);
				if (startAddress == null) continue;

				contributorAddress = startAddress;

				List<Contributor> contributors = contributorEntry.getValue();
				for (Contributor contributor : contributors) higherContributors.add(new ContributorFound(contributorAddress, contributor));
			}
		}

		if (higherContributors.isEmpty()) {

			if (log.isDebugEnabled()) log.debug("Finding higher contributors for " + contextNodeXDIAddress + ": No matches.");
		} else {

			if (log.isDebugEnabled()) log.debug("Finding higher contributors for " + contextNodeXDIAddress + ": Matches at " + higherContributors);
		}

		return higherContributors;
	}

	public static List<ContributorFound> findLowerContributors(ContributorMap contributorMap, XDIAddress contextNodeXDIAddress) {

		if (contributorMap.isEmpty()) return new ArrayList<ContributorFound> ();

		List<ContributorFound> lowerContributors = new ArrayList<ContributorFound> ();

		if (contextNodeXDIAddress == null) {

			for (Map.Entry<XDIAddress, List<Contributor>> contributorEntry : contributorMap.entrySet()) {

				XDIAddress contributorAddress = contributorEntry.getKey();

				List<Contributor> contributors = contributorEntry.getValue();
				for (Contributor contributor : contributors) lowerContributors.add(new ContributorFound (contributorAddress, contributor));
			}
		} else {

			for (Map.Entry<XDIAddress, List<Contributor>> contributorEntry : contributorMap.entrySet()) {

				XDIAddress contributorAddress = contributorEntry.getKey();
				XDIAddress startAddress = XDIAddressUtil.startsWithXDIAddress(contributorAddress, contextNodeXDIAddress, false, true);
				if (startAddress == null) continue;

				if (startAddress.equals(contributorAddress)) continue;

				List<Contributor> contributors = contributorEntry.getValue();
				for (Contributor contributor : contributors) lowerContributors.add(new ContributorFound (contributorAddress, contributor));
			}
		}

		if (lowerContributors.isEmpty()) {

			if (log.isDebugEnabled()) log.debug("Finding lower contributors for " + contextNodeXDIAddress + ": No matches.");
		} else {

			if (log.isDebugEnabled()) log.debug("Finding lower contributors for " + contextNodeXDIAddress + ": Matches at " + lowerContributors);
		}

		return lowerContributors;
	}
}
