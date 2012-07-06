package xdi2.core.io;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class MimeType implements Serializable, Comparable<MimeType> {

	private static final long serialVersionUID = 7537512421170282104L;

	private String mimeType;
	private Map<String, String> parameters;

	public MimeType(String string, Map<String, String> parameters) {

		this(string);

		this.parameters.putAll(parameters);
	}

	public MimeType(String string) {

		String[] parts = string.split(";");

		String mimeType = parts[0];
		Map<String, String> parameters = new TreeMap<String, String> ();

		for (int i=1; i<parts.length; i++) {

			String[] keyValue = parts[i].split("=");
			if (keyValue.length != 2) throw new RuntimeException("Invalid parameter: " + parts[i]);

			parameters.put(keyValue[0], keyValue[1]);
		}

		this.mimeType = mimeType;
		this.parameters = parameters;
	}

	public MimeType mimeTypeWithoutQuality() {

		String mimeType = this.getMimeType();
		Map<String, String> parameters = new TreeMap<String, String> (this.getParameters());
		parameters.remove("q");

		return new MimeType(mimeType, parameters);
	}

	public String getMimeType() {

		return this.mimeType;
	}

	public Map<String, String> getParameters() {

		return this.parameters;
	}

	public String getParameterValue(String key) {

		return this.parameters.get(key);
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		StringBuffer buffer = new StringBuffer();

		buffer.append(this.getMimeType());

		for (Map.Entry<String, String> parameter : this.getParameters().entrySet()) {

			buffer.append(";" + parameter.getKey() + "=" + parameter.getValue());
		}

		return buffer.toString();
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

	public int compareTo(MimeType other) {

		if (other == null || other == this) return 0;

		return this.toString().compareTo(other.toString());
	}
}
