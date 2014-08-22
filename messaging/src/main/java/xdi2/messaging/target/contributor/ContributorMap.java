package xdi2.messaging.target.contributor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.Prototype;

public class ContributorMap  implements Iterable<Contributor>, Prototype<ContributorMap>, Serializable {

	private static final long serialVersionUID = 1645889897751813459L;

	private static final Logger log = LoggerFactory.getLogger(ContributorMap.class);

	private Map<XDIAddress, List<Contributor>> contributors;

	public ContributorMap(Map<XDIAddress, List<Contributor>> contributors) {

		this.contributors = new LinkedHashMap<XDIAddress, List<Contributor>> (contributors);
	}

	public ContributorMap(ContributorMap contributorMap) {

		this.contributors = new LinkedHashMap<XDIAddress, List<Contributor>> (contributorMap.contributors);
	}

	public ContributorMap() {

		this.contributors = new LinkedHashMap<XDIAddress, List<Contributor>> ();
	}

	public void addContributor(XDIAddress contextNodeAddress, Contributor contributor) {

		if (log.isDebugEnabled()) log.debug("Adding contributor " + contributor.getClass().getSimpleName() + " under " + contextNodeAddress);

		List<Contributor> contributors = this.contributors.get(contextNodeAddress);

		if (contributors == null) {

			contributors = new ArrayList<Contributor> ();
			this.contributors.put(contextNodeAddress, contributors);
		}

		contributors.add(contributor);
	}

	public void addContributor(Contributor contributor) {

		String[] contributorAddresss = contributor.getContributorMount().contributorAddresss();

		for (String contributorAddress : contributorAddresss) {

			this.addContributor(XDIAddress.create(contributorAddress), contributor);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Contributor> T getContributor(Class<T> clazz) {

		for (Contributor contributor : this) {

			if (clazz.isAssignableFrom(contributor.getClass())) return (T) contributor;
		}

		return null;
	}

	public void removeContributor(XDIAddress contributorAddress, Contributor contributor) {

		if (log.isDebugEnabled()) log.debug("Removing contributor " + contributor.getClass().getSimpleName() + " from " + contributorAddress);

		List<Contributor> contributors = this.contributors.get(contributorAddress);
		if (contributors == null) return;

		contributors.remove(contributor);

		if (contributors.isEmpty()) {

			this.contributors.remove(contributorAddress);
		}
	}

	public void removeContributor(Contributor contributor) {

		for (Iterator<Map.Entry<XDIAddress, List<Contributor>>> entries = this.contributors.entrySet().iterator(); entries.hasNext(); ) {

			Map.Entry<XDIAddress, List<Contributor>> entry = entries.next();

			if (entry.getValue().contains(contributor)) entry.getValue().remove(contributor);
			if (entry.getValue().isEmpty()) entries.remove();
		}
	}

	public boolean isEmpty() {

		return this.contributors.isEmpty();
	}

	public int size() {

		return (int) new IteratorCounter(this.iterator()).count();
	}

	public Set<Entry<XDIAddress, List<Contributor>>> entrySet() {

		return this.contributors.entrySet();
	}

	@Override
	public ReadOnlyIterator<Contributor> iterator() {

		return new DescendingIterator<List<Contributor>, Contributor> (this.contributors.values().iterator()) {

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

	public void clearDisabledForOperation(Operation operation) {

		for (Contributor contributor : this.iterator()) {

			contributor.clearDisabledForOperation(operation);
		}
	}

	public void clearDisabledForMessage(Message message) {

		for (Contributor contributor : this.iterator()) {

			contributor.clearDisabledForMessage(message);
		}
	}

	public void clearDisabledForMessageEnvelope(MessageEnvelope messageEnvelope) {

		for (Contributor contributor : this.iterator()) {

			contributor.clearDisabledForMessageEnvelope(messageEnvelope);
		}
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

		for (Map.Entry<XDIAddress, List<Contributor>> entry : this.contributors.entrySet()) {

			XDIAddress contributorAddress = entry.getKey();
			List<Contributor> contributors = entry.getValue();

			for (Contributor contributor : contributors) {

				if (! (contributor instanceof Prototype<?>)) {

					throw new Xdi2MessagingException("Cannot use contributor " + contributor.getClass().getSimpleName() + " as prototype.", null, null);
				}

				try {

					Prototype<? extends Contributor> contributorPrototype = (Prototype<? extends Contributor>) contributor;
					Contributor prototypedContributor = prototypingContext.instanceFor(contributorPrototype);

					contributorMap.addContributor(contributorAddress, prototypedContributor);
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

	public static class ContributorFound {

		private XDIAddress contributorAddress;
		private Contributor contributor;

		public ContributorFound(XDIAddress contributorAddress, Contributor contributor) {

			this.contributorAddress = contributorAddress;
			this.contributor = contributor;
		}

		public XDIAddress getContributorAddress() {

			return this.contributorAddress;
		}

		public Contributor getContributor() {

			return this.contributor;
		}

		@Override
		public String toString() {

			return this.contributorAddress.toString() + ":" + this.contributor.getClass().getSimpleName();
		}
	}
}
