package xdi2.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Properties;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.CopyUtil;

/**
 * If you extend this class, you only have to implement write(Graph, Writer, Properties)
 * 
 * @author markus
 */
public abstract class AbstractXDIWriter implements XDIWriter {

	private static final long serialVersionUID = -4120729667091454408L;

	public static final String DEFAULT_CHARSET_NAME = "UTF-8";

	protected Properties parameters;

	public AbstractXDIWriter(Properties parameters) {

		this.parameters = parameters != null ? parameters : new Properties();

		this.init();
	}

	protected abstract void init();

	@Override
	public OutputStream write(Graph graph, OutputStream stream) throws IOException {

		this.write(graph, new OutputStreamWriter(stream, DEFAULT_CHARSET_NAME));
		stream.flush();

		return stream;
	}

	@Override
	public OutputStream write(ContextNode contextNode, OutputStream stream) throws IOException {
		
		Graph subGraph = MemoryGraphFactory.getInstance().openGraph();
		CopyUtil.copyContextNode(contextNode, subGraph, null);
		
		return this.write(subGraph, stream);
	}

	@Override
	public Writer write(ContextNode contextNode, Writer writer) throws IOException {
		
		Graph subGraph = MemoryGraphFactory.getInstance().openGraph();
		CopyUtil.copyContextNode(contextNode, subGraph, null);
		
		return this.write(subGraph, writer);
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
	public final MimeType getMimeType() {

		MimeType mimeType = (MimeType) getFieldValue("MIME_TYPE");

		return mimeType;
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

		return this.getMimeType().equals(mimeType.mimeTypeWithoutParameters());
	}
}
