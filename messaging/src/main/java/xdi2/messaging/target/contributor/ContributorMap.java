package xdi2.messaging.target.contributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.util.StatementUtil;
import xdi2.core.util.XDI3Util;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.Prototype;

public class ContributorMap extends LinkedHashMap<XDI3Segment, List<Contributor>> implements Iterable<Contributor>, Prototype<ContributorMap> {

	private static final long serialVersionUID = 1645889897751813459L;

	private static final Logger log = LoggerFactory.getLogger(ContributorMap.class);

	public ContributorMap() {

		super();
	}

	public void addContributor(XDI3Segment contributorXri, Contributor contributor) {

		log.debug("Adding contributor " + contributor.getClass().getSimpleName() + " under " + contributorXri);

		List<Contributor> contributors = this.get(contributorXri);

		if (contributors == null) {

			contributors = new ArrayList<Contributor> ();
			this.put(contributorXri, contributors);
		}

		contributors.add(contributor);
	}

	public void addContributor(Contributor contributor) {

		ContributorXri contributorCall = contributor.getClass().getAnnotation(ContributorXri.class);

		for (String address : contributorCall.addresses()) {

			this.addContributor(XDI3Segment.create(address), contributor);
		}
	}

	public void removeContributor(XDI3Segment contributorXri, Contributor contributor) {

		log.debug("Removing contributor " + contributor.getClass().getSimpleName() + " from " + contributorXri);

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

	public boolean executeContributorsAddress(XDI3Segment[] contributorXris, XDI3Segment relativeTargetAddress, XDI3Segment targetAddress, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetAddress == null) return false;

		// find an address with contributors

		XDI3Segment relativeContextNodeXri = relativeTargetAddress;

		XDI3Segment nextContributorXri = this.findHigherContributorXri(relativeContextNodeXri);
		if (nextContributorXri == null) return false;

		XDI3Segment nextRelativeTargetAddress = XDI3Util.reduceXri(relativeTargetAddress, nextContributorXri, false, true);
		XDI3Segment nextRelativeContextNodeXri = nextRelativeTargetAddress;

		XDI3Segment[] nextContributorXris = Arrays.copyOf(contributorXris, contributorXris.length + 1);
		nextContributorXris[nextContributorXris.length - 1] = nextRelativeContextNodeXri == null ? relativeContextNodeXri : XDI3Util.parentXri(relativeContextNodeXri, - nextRelativeContextNodeXri.getNumSubSegments());

		if (log.isDebugEnabled()) log.debug("Next contributor XRIs: " + Arrays.asList(nextContributorXris) + ", next relative target address: " + nextRelativeTargetAddress + ", target address: " + targetAddress);

		// execute the contributors

		for (Iterator<Contributor> contributors = this.get(nextContributorXri).iterator(); contributors.hasNext(); ) {

			Contributor contributor = contributors.next();

			if (log.isDebugEnabled()) log.debug("Executing contributor " + contributor.getClass().getSimpleName() + " (address).");

			try {

				executionContext.pushContributor(contributor, "Contributor: address");

				if (contributor.executeOnAddress(nextContributorXris, nextRelativeTargetAddress, targetAddress, operation, operationMessageResult, executionContext)) {

					if (log.isDebugEnabled()) log.debug("Address has been fully handled by contributor " + contributor.getClass().getSimpleName() + ".");
					return true;
				}
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popContributor();
			}
		}

		// done

		return false;
	}

	public boolean executeContributorsStatement(XDI3Segment contributorXris[], XDI3Statement relativeTargetStatement, XDI3Statement targetStatement, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (relativeTargetStatement == null) return false;
		
		// find an address with contributors

		XDI3Segment relativeContextNodeXri = relativeTargetStatement.getContextNodeXri();

		XDI3Segment nextContributorXri = this.findHigherContributorXri(relativeContextNodeXri);
		if (nextContributorXri == null) return false;

		XDI3Statement nextRelativeTargetStatement = StatementUtil.reduceStatement(relativeTargetStatement, nextContributorXri, false, true);
		XDI3Segment nextRelativeContextNodeXri = nextRelativeTargetStatement == null ? null : nextRelativeTargetStatement.getContextNodeXri();

		XDI3Segment[] nextContributorXris = Arrays.copyOf(contributorXris, contributorXris.length + 1);
		nextContributorXris[nextContributorXris.length - 1] = nextRelativeContextNodeXri == null ? relativeContextNodeXri : XDI3Util.parentXri(relativeContextNodeXri, - nextRelativeContextNodeXri.getNumSubSegments());

		if (log.isDebugEnabled()) log.debug("Next contributor XRIs: " + Arrays.asList(nextContributorXris) + ", next relative target statement: " + nextRelativeTargetStatement + ", target statement: " + targetStatement);

		// execute the contributors

		for (Iterator<Contributor> contributors = this.get(nextContributorXri).iterator(); contributors.hasNext(); ) {

			Contributor contributor = contributors.next();

			if (log.isDebugEnabled()) log.debug("Executing contributor " + contributor.getClass().getSimpleName() + " (statement).");

			try {

				executionContext.pushContributor(contributor, "Contributor: statement");

				if (contributor.executeOnStatement(nextContributorXris, nextRelativeTargetStatement, targetStatement, operation, operationMessageResult, executionContext)) {

					if (log.isDebugEnabled()) log.debug("Statement has been fully handled by contributor " + contributor.getClass().getSimpleName() + ".");
					return true;
				}
			} catch (Exception ex) {

				throw executionContext.processException(ex);
			} finally {

				executionContext.popContributor();
			}
		}

		// done

		return false;
	}

	/*
	 * Methods for finding contributors
	 */

	public XDI3Segment findMatchingContributorXri(XDI3Segment contextNodeXri) {

		return this.containsKey(contextNodeXri) ? contextNodeXri : null;
	}

	public XDI3Segment findHigherContributorXri(XDI3Segment contextNodeXri) {

		for (XDI3Segment contributorXri : this.keySet()) {

			if (XDI3Util.startsWith(contextNodeXri, contributorXri, false, true)) {

				if (log.isDebugEnabled()) log.debug("Finding higher contributor XRI for " + contextNodeXri + ": Match at " + contributorXri + ": " + this.get(contributorXri));

				return contributorXri;
			}
		}

		if (log.isDebugEnabled()) log.debug("Finding higher contributor XRI for " + contextNodeXri + ": No match.");

		return null;
	}

	public XDI3Segment findLowerContributorXri(XDI3Segment contextNodeXri) {

		for (XDI3Segment contributorXri : this.keySet()) {

			if (log.isDebugEnabled()) log.debug("Finding lower contributor XRI for " + contextNodeXri + ": Match at " + contributorXri + ": " + this.get(contributorXri));

			if (XDI3Util.startsWith(contributorXri, contextNodeXri, true, false)) return contributorXri;
		}

		if (log.isDebugEnabled()) log.debug("Finding lower contributor XRI for " + contextNodeXri + ": No match.");

		return null;
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
}
