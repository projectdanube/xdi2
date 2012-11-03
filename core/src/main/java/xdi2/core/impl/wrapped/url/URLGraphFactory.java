package xdi2.core.impl.wrapped.url;

import java.io.IOException;
import java.net.URL;

import xdi2.core.GraphFactory;
import xdi2.core.impl.wrapped.GraphWrapper;
import xdi2.core.impl.wrapped.WrappedGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;

/**
 * GraphFactory that creates URL-based graphs.
 * 
 * @author markus
 */
public class URLGraphFactory extends WrappedGraphFactory implements GraphFactory {

	public static final URL DEFAULT_URL = null;
	public static final String DEFAULT_MIMETYPE = XDIWriterRegistry.getDefault().getMimeType().toString();

	private URL url;
	private String mimeType;

	public URLGraphFactory() { 

		super();

		this.url = DEFAULT_URL;
		this.mimeType = DEFAULT_MIMETYPE;
	}

	@Override
	public GraphWrapper openWrapper(String identifier) throws IOException {

		// initialize graph

		XDIReader xdiReader = XDIReaderRegistry.forMimeType(this.mimeType == null ? null : new MimeType(this.mimeType));
		XDIWriter xdiWriter = XDIWriterRegistry.forMimeType(this.mimeType == null ? null : new MimeType(this.mimeType));

		return new URLGraphWrapper(this.url, this.mimeType, xdiReader, xdiWriter);
	}

	public URL getUrl() {
	
		return this.url;
	}

	public void setUrl(URL url) {
	
		this.url = url;
	}

	public String getMimeType() {

		return this.mimeType;
	}

	public void setMimeType(String mimeType) {

		this.mimeType = mimeType;
	}
}
