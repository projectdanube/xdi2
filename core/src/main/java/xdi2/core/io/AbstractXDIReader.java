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

	@Override
	public void read(Graph graph, String string, Properties parameters) throws IOException, Xdi2ParseException {

		this.read(graph, new StringReader(string), parameters);
	}

	@Override
	public InputStream read(Graph graph, InputStream stream, Properties parameters) throws IOException, Xdi2ParseException {

		this.read(graph, new InputStreamReader(stream), parameters);

		return stream;
	}

	private final Object getFieldValue(String fieldName) {
		
		Field field;

		try {

			field = this.getClass().getField(fieldName);
			return field.get(null);
		} catch (Exception ex) {
			
			throw new RuntimeException("Class " + this.getClass().getCanonicalName() + " must define the static field '" + fieldName + "' of type String (" + ex.getMessage() + ")");
		}
	}

	@Override
	public final String getFormat() {

		String format = (String) getFieldValue("FORMAT_NAME");

		return format;
	}

	@Override
	public final String getFileExtension() {

		String FileExtension = (String) getFieldValue("FILE_EXTENSION");

		return FileExtension;
	}

	@Override
	public final MimeType[] getMimeTypes() {

		MimeType[] mimeTypes = (MimeType[]) getFieldValue("MIME_TYPES");

		return mimeTypes;
	}

	@Override
	public boolean supportsFormat(String format) {

		return this.getFormat().equals(format);
	}

	@Override
	public boolean supportsFileExtension(String fileExtension) {

		return this.getFileExtension().equals(fileExtension);
	}

	@Override
	public boolean supportsMimeType(MimeType mimeType) {

		for (MimeType thisMimeType : this.getMimeTypes()) {

			if (thisMimeType.equals(mimeType)) return true;
		}

		return false;
	}
}
