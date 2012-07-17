package xdi2.core.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class MimeType implements Serializable, Comparable<MimeType> {

	private static final long serialVersionUID = 7537512421170282104L;

	private String mimeType;
	private Properties parameters;

	public MimeType(String string, Properties parameters) {

		this(string);

		if (parameters != null) this.parameters.putAll(parameters);
	}

	public MimeType(String string) {

		String[] parts = string.split(";");

		String mimeType = parts[0];
		Properties parameters = new Properties();

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
		Properties parameters = new Properties(this.getParameters());
		parameters.remove("q");

		return new MimeType(mimeType, parameters);
	}

	public String getMimeType() {

		return this.mimeType;
	}

	public Properties getParameters() {

		return this.parameters;
	}

	public String getParameterValue(String key) {

		return this.parameters.getProperty(key);
	}

	public boolean containsParameter(String key) {
		
		return this.parameters.containsKey(key);
	}
	
	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		StringBuilder buffer = new StringBuilder();

		buffer.append(this.getMimeType());

		List<String> list = new ArrayList<String> (this.getParameters().stringPropertyNames());
		Collections.sort(list);
		
		for (String key : list) {

			buffer.append(";" + key + "=" + this.getParameterValue(key));
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

	@Override
	public int compareTo(MimeType other) {

		if (other == null || other == this) return 0;

		return this.toString().compareTo(other.toString());
	}
}
