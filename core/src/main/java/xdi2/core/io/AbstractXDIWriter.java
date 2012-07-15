package xdi2.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.Properties;

import xdi2.core.Graph;

/**
 * If you extend this class, you only have to implement write(Graph, Writer, Properties)
 * 
 * @author markus
 */
public abstract class AbstractXDIWriter implements XDIWriter {

	private static final long serialVersionUID = -4120729667091454408L;

	protected Properties parameters;

	public AbstractXDIWriter(Properties parameters) {

		this.parameters = parameters;
	}

	@Override
	public OutputStream write(Graph graph, OutputStream stream) throws IOException {

		this.write(graph, new OutputStreamWriter(stream));
		stream.flush();

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
