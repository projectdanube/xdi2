package xdi2.messaging.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriterRegistry;

/**
 * A helper class that can parse and produce HTTP Accept: headers.
 * 
 * @author markus
 */
public class AcceptHeader implements Serializable, Comparable<AcceptHeader> {

	private static final long serialVersionUID = 400811467881181917L;

	protected List<MimeType> mimeTypes = new ArrayList<MimeType>();

	public AcceptHeader() {

	}

	public AcceptHeader(String header) {

		String[] strings = header.split(",");

		for (String string : strings) if (! string.trim().isEmpty()) this.mimeTypes.add(new MimeType(string));
	}

	/**
	 * Creates an Accept: header with all the mime types we understand.
	 * and optionally with a "preferred" one.
	 * @param preferredMimeType The preferred mime type.
	 * @return The Accept: header
	 */
	public static AcceptHeader create(MimeType preferredMimeType) {

		AcceptHeader acceptHeader = new AcceptHeader();

		for (MimeType mimeType : XDIReaderRegistry.getMimeTypes()) {

			if (mimeType.equals(preferredMimeType)) continue;

			acceptHeader.addMimeType(new MimeType(mimeType + ";q=0.5"));
		}

		if (preferredMimeType != null) {

			acceptHeader.addMimeType(new MimeType(preferredMimeType + ";q=1"));
		}

		return acceptHeader;
	}

	/**
	 * Returns a MimeType that we can satisfy for this Accept header. 
	 * @return A MimeType, or null if no appropriate implementation could be found.
	 */
	public MimeType bestMimeType() {

		for (MimeType mimeType : this.getMimeTypesSortedByQuality()) {

			MimeType mimeTypeWithoutQuality = mimeType.mimeTypeWithoutQuality();

			if ((XDIWriterRegistry.forMimeType(mimeTypeWithoutQuality)) != null) return mimeTypeWithoutQuality;
		}

		return null;
	}

	public List<MimeType> getMimeTypes() {

		return this.mimeTypes;
	}

	public List<MimeType> getMimeTypesSortedByQuality() {

		List<MimeType> sortedMimeTypes = new ArrayList<MimeType> (this.mimeTypes);
		Collections.sort(sortedMimeTypes, qualityComparator);

		return sortedMimeTypes;
	}

	public void addMimeType(MimeType mimeType) {

		this.mimeTypes.add(mimeType);
	}

	public boolean containsMimeType(MimeType mimeType) {

		return this.mimeTypes.contains(mimeType);
	}


	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		StringBuffer header = new StringBuffer();

		for (Iterator<MimeType> mimeTypes = this.getMimeTypesSortedByQuality().iterator(); mimeTypes.hasNext(); ) {

			MimeType mimeType = mimeTypes.next();

			header.append(mimeType.toString());
			if (mimeTypes.hasNext()) header.append(",");
		}

		return header.toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof MimeType)) return false;
		if (object == this) return true;

		MimeType other = (MimeType) object;

		return this.toString().equals(other.toString());
	}

	@Override
	public int hashCode() {

		return this.toString().hashCode();
	}

	public int compareTo(AcceptHeader other) {

		if (other == null || other == this) return 0;

		return this.toString().compareTo(other.toString());
	}

	private static Comparator<? super MimeType> qualityComparator = new Comparator<MimeType>() {

		@Override
		public int compare(MimeType mimeType1, MimeType mimeType2) {

			float q1 = Float.parseFloat(mimeType1.getParameterValue("q"));
			float q2 = Float.parseFloat(mimeType2.getParameterValue("q"));

			if (q1 > q2) return -1;
			if (q1 < q2) return 1;
			return mimeType1.compareTo(mimeType2);
		}
	};
}
