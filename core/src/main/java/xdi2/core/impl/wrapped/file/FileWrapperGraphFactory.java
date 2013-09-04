package xdi2.core.impl.wrapped.file;

import java.io.IOException;

import xdi2.core.GraphFactory;
import xdi2.core.impl.wrapped.WrappedGraphFactory;
import xdi2.core.impl.wrapped.WrapperStore;
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
public class FileWrapperGraphFactory extends WrappedGraphFactory implements GraphFactory {

	public static final String DEFAULT_MIMETYPE = XDIWriterRegistry.getDefault().getMimeType().toString();

	private String path;
	private String mimeType;

	public FileWrapperGraphFactory() { 

		super();

		this.mimeType = DEFAULT_MIMETYPE;
	}

	@Override
	public WrapperStore openWrapper(String identifier) throws IOException {

		// check identifier

		String path = this.getPath();

		if (path == null) {

			path = "xdi2-file-wrapper-graph." + identifier + ".xdi";
		}

		// initialize graph

		XDIReader xdiReader = XDIReaderRegistry.forMimeType(this.mimeType == null ? null : new MimeType(this.mimeType));
		XDIWriter xdiWriter = XDIWriterRegistry.forMimeType(this.mimeType == null ? null : new MimeType(this.mimeType));

		return new FileWrapperStore(path, this.mimeType, xdiReader, xdiWriter);
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
