package xdi2.core.impl.wrapped.url;

import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.memory.MemoryGraph;
import xdi2.core.impl.wrapped.GraphWrapper;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIWriter;

public class URLGraphWrapper implements GraphWrapper {

	private static final Logger log = LoggerFactory.getLogger(URLGraphWrapper.class);

	private URL url;
	private String mimeType;
	private XDIReader xdiReader;
	private XDIWriter xdiWriter;

	public URLGraphWrapper(URL url , String mimeType, XDIReader xdiReader, XDIWriter xdiWriter) {

		this.url = url;
		this.mimeType = mimeType;
		this.xdiReader = xdiReader;
		this.xdiWriter = xdiWriter;
	}

	@Override
	public void load(MemoryGraph memoryGraph) {

		memoryGraph.clear();

		try {

			if (log.isDebugEnabled()) log.debug("Loading URL " + this.url);

			InputStream stream = this.url.openStream();

			this.xdiReader.read(memoryGraph, stream);
			stream.close();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot load URL at " + this.url, ex);
		}
	}

	@Override
	public void save(MemoryGraph memoryGraph) {

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

	public XDIReader getXdiReader() {

		return this.xdiReader;
	}

	public void setXdiReader(XDIReader xdiReader) {

		this.xdiReader = xdiReader;
	}

	public XDIWriter getXdiWriter() {

		return this.xdiWriter;
	}

	public void setXdiWriter(XDIWriter xdiWriter) {

		this.xdiWriter = xdiWriter;
	}
}
