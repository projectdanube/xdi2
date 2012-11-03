package xdi2.core.impl.wrapped.classpath;

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
 * GraphFactory that creates classpath-based graphs.
 * 
 * @author markus
 */
public class ClasspathGraphFactory extends WrappedGraphFactory implements GraphFactory {

	public static final String DEFAULT_CLASSPATH = null;
	public static final String DEFAULT_MIMETYPE = XDIWriterRegistry.getDefault().getMimeType().toString();

	private String classpath;
	private String mimeType;

	public ClasspathGraphFactory() { 

		super();

		this.classpath = DEFAULT_CLASSPATH;
		this.mimeType = DEFAULT_MIMETYPE;
	}

	@Override
	public GraphWrapper openWrapper(String identifier) throws IOException {

		// initialize graph

		XDIReader xdiReader = XDIReaderRegistry.forMimeType(this.mimeType == null ? null : new MimeType(this.mimeType));
		XDIWriter xdiWriter = XDIWriterRegistry.forMimeType(this.mimeType == null ? null : new MimeType(this.mimeType));

		return new ClasspathGraphWrapper(this.classpath, this.mimeType, xdiReader, xdiWriter);
	}

	public String getClasspath() {

		return this.classpath;
	}

	public void setClasspath(String classpath) {

		this.classpath = classpath;
	}

	public String getMimeType() {

		return this.mimeType;
	}

	public void setMimeType(String mimeType) {

		this.mimeType = mimeType;
	}
}
