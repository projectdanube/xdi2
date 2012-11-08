package xdi2.core.impl.wrapped.file;

import java.io.IOException;

import xdi2.core.GraphFactory;
import xdi2.core.impl.wrapped.WrappedGraphFactory;
import xdi2.core.impl.wrapped.GraphWrapper;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;

/**
 * GraphFactory that creates file-based graphs.
 * 
 * @author markus
 */
public class FileGraphFactory extends WrappedGraphFactory implements GraphFactory {

	public static final String DEFAULT_PATH = "xdi2-graph.xdi";
	public static final String DEFAULT_MIMETYPE = XDIWriterRegistry.getDefault().getMimeType().toString();

	private String path;
	private String mimeType;

	public FileGraphFactory() { 

		super();

		this.path = DEFAULT_PATH;
		this.mimeType = DEFAULT_MIMETYPE;
	}

	@Override
	public GraphWrapper openWrapper(String identifier) throws IOException {

		// check identifier

		if (identifier != null) {

			this.setPath("xdi2-graph." + identifier + ".xdi");
		}

		// initialize graph

		XDIReader xdiReader = XDIReaderRegistry.forMimeType(this.mimeType == null ? null : new MimeType(this.mimeType));
		XDIWriter xdiWriter = XDIWriterRegistry.forMimeType(this.mimeType == null ? null : new MimeType(this.mimeType));

		return new FileGraphWrapper(this.path, this.mimeType, xdiReader, xdiWriter);
	}

	public String getPath() {

		return this.path;
	}

	public void setPath(String path) {

		this.path = path;
	}

	public String getMimeType() {

		return this.mimeType;
	}

	public void setMimeType(String mimeType) {

		this.mimeType = mimeType;
	}
}
