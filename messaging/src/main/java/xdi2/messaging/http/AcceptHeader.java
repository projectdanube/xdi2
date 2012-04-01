package xdi2.messaging.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xdi2.core.io.XDIReaderRegistry;

/**
 * A helper class that can parse and produce HTTP Accept: headers.
 * 
 * @author markus
 */
public class AcceptHeader {

	protected static final String ENTRY_SEPARATOR = ",";

	protected List<AcceptEntry> entries = new ArrayList<AcceptEntry>();

	public AcceptHeader() {

	}

	public AcceptHeader(String header) {

		if (header != null) this.parse(header);
	}

	/**
	 * Creates an Accept: header with all the mime types we understand.
	 * and optionally with a "preferred" one.
	 * @param preferredMimeType The preferred mime type.
	 * @return The Accept: header
	 */
	public static AcceptHeader create(String preferredMimeType) {

		AcceptHeader acceptHeader = new AcceptHeader();

		for (String mimeType : XDIReaderRegistry.getMimeTypes()) {

			if (mimeType.equals(preferredMimeType)) continue;

			acceptHeader.addEntry(new AcceptEntry(0.5f, mimeType));
		}

		if (preferredMimeType != null) {

			acceptHeader.addEntry(new AcceptEntry(1, preferredMimeType));
		}

		return acceptHeader;
	}

	public void addEntry(AcceptEntry entry) {

		this.entries.add(entry);
		this.sort();
	}

	public boolean containsMimeType(String mimeType) {

		for (AcceptEntry entry : this.entries) {

			if (entry.getMimeType().equals(mimeType)) return true;
		}

		return false;
	}

	protected void parse(String header) {

		this.entries.clear();

		String[] strEntries = header.split(ENTRY_SEPARATOR);

		for (String strEntry : strEntries) {

			AcceptEntry entry;

			try {

				entry = new AcceptEntry(strEntry);
			} catch(Exception ex) {

				continue;
			}

			this.entries.add(entry);
		}

		this.sort();
	}

	protected void sort() {

		Collections.sort(this.entries);
		Collections.reverse(this.entries);
	}

	public List<AcceptEntry> getEntries() {

		return this.entries;
	}

	@Override
	public String toString() {

		StringBuffer header = new StringBuffer();

		for (Iterator<AcceptEntry> entries = this.entries.iterator(); entries.hasNext(); ) {

			AcceptEntry entry = entries.next();

			header.append(entry.toString());
			if (entries.hasNext()) header.append(ENTRY_SEPARATOR);
		}

		return header.toString();
	}

	/**
	 * A class representing one entry in an HTTP accept header.
	 * 
	 * @author markus
	 */
	public static class AcceptEntry implements Comparable<AcceptEntry> {

		protected static final Float DEFAULT_Q = new Float(1);
		protected static final Pattern PATTERN_ACCEPT_Q = Pattern.compile("^\\s*(.+)\\s*;\\s*[qQ]=(.+)\\s*$");
		protected static final Pattern PATTERN_ACCEPT = Pattern.compile("^\\s*(.+)\\s*$");
		protected static final String ENTRY_SEPARATOR = ";";

		private Float q;
		private String mimeType;

		public AcceptEntry(float q, String mimeType) {

			this.q = new Float(q);
			this.mimeType = mimeType;
		}

		private AcceptEntry(String entry) {

			Matcher matcher;

			if ((matcher = PATTERN_ACCEPT_Q.matcher(entry)).matches()) {

				this.q = new Float(matcher.group(2));
				this.mimeType = matcher.group(1);
			} else if ((matcher = PATTERN_ACCEPT.matcher(entry)).matches()) {

				this.q = DEFAULT_Q;
				this.mimeType = matcher.group(1);
			} else {

				throw new IllegalArgumentException();
			}
		}

		public float getQ() {

			return(this.q.floatValue());
		}

		public String getMimeType() {

			return(this.mimeType);
		}

		@Override
		public String toString() {

			return(this.mimeType + ENTRY_SEPARATOR + "q=" + this.q);
		}

		public int compareTo(AcceptEntry other) {

			return(this.q.compareTo(other.q));
		}
	}
}
