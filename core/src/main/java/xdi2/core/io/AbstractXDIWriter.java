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

	public synchronized OutputStream write(Graph graph, OutputStream stream, Properties parameters) throws IOException {

		this.write(graph, new OutputStreamWriter(stream), parameters);
		stream.flush();

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
