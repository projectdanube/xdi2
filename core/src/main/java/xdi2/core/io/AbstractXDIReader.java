package xdi2.core.io;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Properties;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2ParseException;

/**
 * If you extend this class, you only have to implement read(Graph, Reader, Properties)
 * 
 * @author markus
 */
public abstract class AbstractXDIReader implements XDIReader {

	private static final long serialVersionUID = -3924954880534200486L;

	public void read(Graph graph, String string, Properties parameters) throws IOException, Xdi2ParseException {

		this.read(graph, new StringReader(string), parameters);
	}

	public InputStream read(Graph graph, InputStream stream, Properties parameters) throws IOException, Xdi2ParseException {

		this.read(graph, new InputStreamReader(stream), parameters);

		return stream;
	}

	private final String getFieldValue(String fieldName) {
		
		Field field;

		try {

			field = this.getClass().getField(fieldName);
			return (String) field.get(null);
		} catch (Exception ex) {
			
			throw new RuntimeException("Class " + this.getClass().getCanonicalName() + " must define the static field '" + fieldName + "' of type String (" + ex.getMessage() + ")");
		}
	}
	
	public final String getFormat() {

		return getFieldValue("FORMAT_NAME");
	}

	public final String getMimeType() {

		return getFieldValue("MIME_TYPE");
	}

	public final String getDefaultFileExtension() {

		return getFieldValue("DEFAULT_FILE_EXTENSION");
	}
}
