package xdi2.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger log = LoggerFactory.getLogger(AbstractXDIWriter.class);

	public static final String DEFAULT_CHARSET_NAME = "UTF-8";

	protected Properties parameters;

	private boolean writeImplied;
	private boolean writeOrdered;
	private boolean writePretty;
	private boolean writeHtml;

	public AbstractXDIWriter(Properties parameters) {

		this.parameters = parameters != null ? parameters : new Properties();

		this.init();
	}

	protected void init() {

		// check parameters

		this.writeImplied = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_IMPLIED, XDIWriterRegistry.DEFAULT_IMPLIED));
		this.writeOrdered = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_ORDERED, XDIWriterRegistry.DEFAULT_ORDERED));
		this.writePretty = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_PRETTY, XDIWriterRegistry.DEFAULT_PRETTY));
		this.writeHtml = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_HTML, XDIWriterRegistry.DEFAULT_HTML));

		if (log.isTraceEnabled()) log.trace("Parameters: writeImplied=" + this.writeImplied + ", writeOrdered=" + this.writeOrdered + ", writePretty=" + this.writePretty + ", writeHtml=" + this.writeHtml);
	}

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

	public boolean isWriteImplied() {

		return this.writeImplied;
	}

	public boolean isWriteOrdered() {

		return this.writeOrdered;
	}

	public boolean isWritePretty() {

		return this.writePretty;
	}

	public boolean isWriteHtml() {

		return this.writeHtml;
	}
}
