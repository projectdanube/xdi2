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

import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.Prototype;

public class ContributorMap  implements Iterable<Contributor>, Prototype<ContributorMap>, Serializable {

	private static final long serialVersionUID = 1645889897751813459L;

	private static final Logger log = LoggerFactory.getLogger(ContributorMap.class);

	private Map<XDI3Segment, List<Contributor>> contributors;

	public ContributorMap() {

		super();

		this.contributors = new LinkedHashMap<XDI3Segment, List<Contributor>> ();
	}

	public void addContributor(XDI3Segment contextNodeXri, Contributor contributor) {

		if (log.isDebugEnabled()) log.debug("Adding contributor " + contributor.getClass().getSimpleName() + " under " + contextNodeXri);

		List<Contributor> contributors = this.contributors.get(contextNodeXri);

		if (contributors == null) {

			contributors = new ArrayList<Contributor> ();
			this.contributors.put(contextNodeXri, contributors);
		}

		contributors.add(contributor);
	}

	public void addContributor(Contributor contributor) {

		String[] contributorXris = contributor.getContributorMount().contributorXris();

		for (String contributorXri : contributorXris) {

			this.addContributor(XDI3Segment.create(contributorXri), contributor);
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

		List<Contributor> contributors = this.contributors.get(contributorXri);
		if (contributors == null) return;

		contributors.remove(contributor);

		if (contributors.isEmpty()) {

			this.contributors.remove(contributorXri);
		}
	}

	public void removeContributor(Contributor contributor) {

		for (Iterator<Map.Entry<XDI3Segment, List<Contributor>>> entries = this.contributors.entrySet().iterator(); entries.hasNext(); ) {

			Map.Entry<XDI3Segment, List<Contributor>> entry = entries.next();

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

	public Set<Entry<XDI3Segment, List<Contributor>>> entrySet() {

		return this.contributors.entrySet();
	}

	@Override
	public Iterator<Contributor> iterator() {

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

	/*
	 * Prototype
	 */

	@SuppressWarnings("unchecked")
	@Override
	public ContributorMap instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new contributor map

		ContributorMap contributorMap = new ContributorMap();

		// add contributors

		for (Map.Entry<XDI3Segment, List<Contributor>> entry : this.contributors.entrySet()) {

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

	public static class ContributorFound {

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
