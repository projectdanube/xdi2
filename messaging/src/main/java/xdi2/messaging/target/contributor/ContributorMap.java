package xdi2.messaging.target.contributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XDI3Util;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;

public class ContributorMap extends LinkedHashMap<XDI3Segment, List<Contributor>> implements Iterable<Contributor>, Prototype<ContributorMap> {

	private static final long serialVersionUID = 1645889897751813459L;

	private static final Logger log = LoggerFactory.getLogger(ContributorMap.class);

	public ContributorMap() {

		super();
	}

	public ContributorMap(int initialCapacity, float loadFactor, boolean accessOrder) {

		super(initialCapacity, loadFactor, accessOrder);
	}

	public ContributorMap(int initialCapacity, float loadFactor) {

		super(initialCapacity, loadFactor);
	}

	public ContributorMap(int initialCapacity) {

		super(initialCapacity);
	}

	public ContributorMap(Map<? extends XDI3Segment, ? extends List<Contributor>> m) {

		super(m);
	}

	public void addContributor(XDI3Segment contributorXri, Contributor contributor) {

		if (log.isDebugEnabled()) log.debug("Adding contributor " + contributor.getClass().getSimpleName() + " under " + contributorXri);

		List<Contributor> contributors = this.get(contributorXri);

		if (contributors == null) {

			contributors = new ArrayList<Contributor> ();
			this.put(contributorXri, contributors);
		}

		contributors.add(contributor);
	}

	public void addContributor(Contributor contributor) {

		String[] addresses = contributor.getAddresses();

		for (String address : addresses) {

			this.addContributor(XDI3Segment.create(address), contributor);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Contributor> T getContributor(Class<T> clazz) {

		for (Contributor contributor : this) {

			if (clazz.isAssignableFrom(contributor.getClass())) return (T) contributor;
		}

		return null;
	}

	public void removeContributor(XDI3Segment contributorXri, Contributor contributor) {

		if (log.isDebugEnabled()) log.debug("Removing contributor " + contributor.getClass().getSimpleName() + " from " + contributorXri);

		List<Contributor> contributors = this.get(contributorXri);
		if (contributors == null) return;

		contributors.remove(contributor);

		if (contributors.isEmpty()) {

			this.remove(contributorXri);
		}
	}

	public void removeContributor(Contributor contributor) {

		for (Iterator<Map.Entry<XDI3Segment, List<Contributor>>> entries = this.entrySet().iterator(); entries.hasNext(); ) {

			Map.Entry<XDI3Segment, List<Contributor>> entry = entries.next();

			if (entry.getValue().contains(contributor)) entry.getValue().remove(contributor);
			if (entry.getValue().isEmpty()) entries.remove();
		}
	}

	@Override
	public Iterator<Contributor> iterator() {

		return new DescendingIterator<List<Contributor>, Contributor> (this.values().iterator()) {

			@Override
			public Iterator<Contributor> descend(List<Contributor> item) {

				return item.iterator();
			}
		};
	}

	public String stringList() {

		StringBuffer buffer = new StringBuffer();

		for (Contributor contributor : this) {

			if (buffer.length() > 0) buffer.append(",");
			buffer.append(contributor.getClass().getSimpleName());
		}

		return buffer.toString();
	}

	/*
	 * Methods for executing contributors
	 */

	public void initContributors(MessagingTarget messagingTarget) throws Exception {

		for (Iterator<Contributor> contributors = this.iterator(); contributors.hasNext(); ) {

			Contributor contributor = contributors.next();

			if (! contributor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled contributor:" + contributor.getClass().getSimpleName() + " (init).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing contributor " + contributor.getClass().getSimpleName() + " (init).");

			contributor.init(messagingTarget);
		}
	}

	public void shutdownContributors(MessagingTarget messagingTarget) throws Exception {

		for (Iterator<Contributor> contributors = this.iterator(); contributors.hasNext(); ) {

			Contributor contributor = contributors.next();

			if (! contributor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled contributor: " + contributor.getClass().getSimpleName() + " (shutdown).");
				continue;
			}

			if (log.isDebugEnabled()) log.debug("Executing contributor " + contributor.getClass().getSimpleName() + " (shutdown).");

			contributor.shutdown(messagingTarget);
		}
	}

	public boolean executeContributorsAddress(XDI3Segment[] contributorChainXris, XDI3Segment relativeTargetAddress, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// find an address with contributors

		XDI3Segment relativeContextNodeXri = relativeTargetAddress;

		List<ContributorFound> contributorFounds = new ArrayList<ContributorFound> ();
		contributorFounds.addAll(this.findHigherContributors(relativeContextNodeXri));
		contributorFounds.addAll(this.findLowerContributors(relativeContextNodeXri));
		if (contributorFounds.size() == 0) return false;

		if (log.isDebugEnabled()) log.debug("For relative target address: " + relativeTargetAddress + " found contributors: " + contributorFounds);

		boolean result = false;

		for (ContributorFound contributorFound : contributorFounds) {

			XDI3Segment contributorXri = contributorFound.getContributorXri();
			Contributor contributor = contributorFound.getContributor();

			if (! contributor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled contributor " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on contributorXri " + contributorXri + " and relative target address " + relativeTargetAddress + ".");
				continue;
			}

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

					if (contributor.getContributors().executeContributorsAddress(nextContributorChainXris, nextRelativeTargetAddress, operation, operationMessageResult, executionContext)) {

						return true;
					}
				}

				// execute contributor (address)

				MessageResult tempMessageResult = new MessageResult();

				boolean handled = contributor.executeOnAddress(nextContributorChainXris, nextContributorChainXri, nextRelativeTargetAddress, operation, tempMessageResult, executionContext);

				XDI3Segment tempContextNodeXri = XDI3Util.concatXris(nextContributorChainXri, nextRelativeContextNodeXri);
				ContextNode tempContextNode = tempMessageResult.getGraph().getDeepContextNode(tempContextNodeXri);

				if (tempContextNode != null) CopyUtil.copyContextNode(tempContextNode, operationMessageResult.getGraph(), null);

				if (handled) {

					if (log.isDebugEnabled()) log.debug("Address has been fully handled by contributor " + contributor.getClass().getSimpleName() + ".");
					result = true;
				}
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popContributor();
			}
		}

		// done

		return result;
	}

	public boolean executeContributorsStatement(XDI3Segment contributorChainXris[], XDI3Statement relativeTargetStatement, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// find an address with contributors

		XDI3Segment relativeContextNodeXri = relativeTargetStatement == null ? null : relativeTargetStatement.getContextNodeXri();

		List<ContributorFound> contributorFounds = new ArrayList<ContributorFound> ();
		contributorFounds.addAll(this.findHigherContributors(relativeContextNodeXri));
		if (contributorFounds.size() == 0) return false;

		if (log.isDebugEnabled()) log.debug("For relative target statement: " + relativeTargetStatement + " found contributors: " + contributorFounds);

		boolean result = false;

		for (ContributorFound contributorFound : contributorFounds) {

			XDI3Segment contributorXri = contributorFound.getContributorXri();
			Contributor contributor = contributorFound.getContributor();

			if (! contributor.isEnabled()) {

				if (log.isDebugEnabled()) log.debug("Skipping disabled contributor " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on contributorXri " + contributorXri + " and relative target statement " + relativeTargetStatement + ".");
				continue;
			}

			XDI3Statement nextRelativeTargetStatement = relativeTargetStatement == null ? null : StatementUtil.removeStartXriStatement(relativeTargetStatement, contributorXri, false, false, true);
			XDI3Segment nextRelativeContextNodeXri = nextRelativeTargetStatement == null ? null : nextRelativeTargetStatement.getContextNodeXri();

			XDI3Segment[] nextContributorChainXris = Arrays.copyOf(contributorChainXris, contributorChainXris.length + 1);
			nextContributorChainXris[nextContributorChainXris.length - 1] = contributorXri;

			XDI3Segment nextContributorChainXri = XDI3Util.concatXris(nextContributorChainXris);

			// execute the contributors

			if (log.isDebugEnabled()) log.debug("Executing contributor " + contributor.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on contributor XRI " + contributorXri + " and relative target statement " + relativeTargetStatement + "." + " Next contributor chain XRI is " + nextContributorChainXri + ", and next relative target statement is " + nextRelativeTargetStatement + ".");

			try {

				executionContext.pushContributor(contributor, "Contributor: statement: " + nextRelativeTargetStatement + " [" + nextContributorChainXri + "]");

				// execute sub-contributors (statement)

				if (! contributor.getContributors().isEmpty()) {

					if (contributor.getContributors().executeContributorsStatement(nextContributorChainXris, nextRelativeTargetStatement, operation, operationMessageResult, executionContext)) {

						return true;
					}
				}

				// execute contributor (statement)

				MessageResult tempMessageResult = new MessageResult();

				boolean handled = contributor.executeOnStatement(nextContributorChainXris, nextContributorChainXri, nextRelativeTargetStatement, operation, operationMessageResult, executionContext);

				XDI3Segment tempContextNodeXri = XDI3Util.concatXris(nextContributorChainXri, nextRelativeContextNodeXri);
				ContextNode tempContextNode = tempMessageResult.getGraph().getDeepContextNode(tempContextNodeXri);

				if (tempContextNode != null) CopyUtil.copyContextNode(tempContextNode, operationMessageResult.getGraph(), null);

				if (handled) {

					if (log.isDebugEnabled()) log.debug("Statement has been fully handled by contributor " + contributor.getClass().getSimpleName() + ".");
					result = true;
				}
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popContributor();
			}
		}

		// done

		return result;
	}

	/*
	 * Methods for finding contributors
	 */

	public List<ContributorFound> findHigherContributors(XDI3Segment contextNodeXri) {

		if (this.isEmpty()) return new ArrayList<ContributorFound> ();

		List<ContributorFound> higherContributors = new ArrayList<ContributorFound> ();

		if (contextNodeXri == null) {

		} else {

			for (Map.Entry<XDI3Segment, List<Contributor>> contributorEntry : this.entrySet()) {

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

	public List<ContributorFound> findLowerContributors(XDI3Segment contextNodeXri) {

		if (this.isEmpty()) return new ArrayList<ContributorFound> ();

		List<ContributorFound> lowerContributors = new ArrayList<ContributorFound> ();

		if (contextNodeXri == null) {

			for (Map.Entry<XDI3Segment, List<Contributor>> contributorEntry : this.entrySet()) {

				XDI3Segment contributorXri = contributorEntry.getKey();

				List<Contributor> contributors = contributorEntry.getValue();
				for (Contributor contributor : contributors) lowerContributors.add(new ContributorFound(contributorXri, contributor));
			}
		} else {

			for (Map.Entry<XDI3Segment, List<Contributor>> contributorEntry : this.entrySet()) {

				XDI3Segment contributorXri = contributorEntry.getKey();
				XDI3Segment startXri = XDI3Util.startsWith(contributorXri, contextNodeXri, false, true);
				if (startXri == null) continue;

				if (startXri.equals(contributorXri)) continue;

				List<Contributor> contributors = contributorEntry.getValue();
				for (Contributor contributor : contributors) lowerContributors.add(new ContributorFound(contributorXri, contributor));
			}
		}

		if (lowerContributors.isEmpty()) {

			if (log.isDebugEnabled()) log.debug("Finding lower contributors for " + contextNodeXri + ": No matches.");
		} else {

			if (log.isDebugEnabled()) log.debug("Finding lower contributors for " + contextNodeXri + ": Matches at " + lowerContributors);
		}

		return lowerContributors;
	}

	/*
	 * Prototype
	 */

	@SuppressWarnings("unchecked")
	@Override
	public ContributorMap instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new contributor map

		ContributorMap contributorMap = new ContributorMap();

		// add contributors

		for (Map.Entry<XDI3Segment, List<Contributor>> entry : this.entrySet()) {

			XDI3Segment contributorXri = entry.getKey();
			List<Contributor> contributors = entry.getValue();

			for (Contributor contributor : contributors) {

				if (! (contributor instanceof Prototype<?>)) {

					throw new Xdi2MessagingException("Cannot use contributor " + contributor.getClass().getSimpleName() + " as prototype.", null, null);
				}

				try {

					Prototype<? extends Contributor> contributorPrototype = (Prototype<? extends Contributor>) contributor;
					Contributor prototypedContributor = prototypingContext.instanceFor(contributorPrototype);

					contributorMap.addContributor(contributorXri, prototypedContributor);
				} catch (Xdi2MessagingException ex) {

					throw new Xdi2MessagingException("Cannot instantiate interceptor for prototype " + contributor.getClass().getSimpleName() + ": " + ex.getMessage(), ex, null);
				}
			}
		}

		// done

		return contributorMap;
	}

	/*
	 * Helper classes
	 */

	private static class ContributorFound {

		private XDI3Segment contributorXri;
		private Contributor contributor;

		public ContributorFound(XDI3Segment contributorXri, Contributor contributor) {

			this.contributorXri = contributorXri;
			this.contributor = contributor;
		}

		public XDI3Segment getContributorXri() {

			return this.contributorXri;
		}

		public Contributor getContributor() {

			return this.contributor;
		}

		@Override
		public String toString() {

			return this.contributorXri.toString() + ":" + this.contributor.getClass().getSimpleName();
		}
	}
}
