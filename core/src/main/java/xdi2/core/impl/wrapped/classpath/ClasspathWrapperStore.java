package xdi2.core.impl.wrapped.classpath;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.memory.MemoryGraph;
import xdi2.core.impl.wrapped.WrapperStore;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIWriter;

public class ClasspathWrapperStore implements WrapperStore {

	private static final Logger log = LoggerFactory.getLogger(ClasspathWrapperStore.class);

	private String classpath;
	private String mimeType;
	private XDIReader xdiReader;
	private XDIWriter xdiWriter;

	private ClassLoader classLoader;

	public ClasspathWrapperStore(String classpath, String mimeType, XDIReader xdiReader, XDIWriter xdiWriter) {

		this.classpath = classpath;
		this.mimeType = mimeType;
		this.xdiReader = xdiReader;
		this.xdiWriter = xdiWriter;

		this.classLoader = Thread.currentThread().getContextClassLoader();
	}

	@Override
	public void load(MemoryGraph memoryGraph) {

		memoryGraph.clear();

		String classpath = this.getClasspath();
		if (classpath.startsWith("/")) classpath = classpath.substring(1);

		try {

			if (log.isDebugEnabled()) log.debug("Loading classpath " + classpath);

			InputStream stream = this.getClassLoader().getResourceAsStream(classpath);

			this.xdiReader.read(memoryGraph, stream);
			stream.close();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Cannot load classpath at " + classpath + " with classloader " + this.getClassLoader().getClass().getCanonicalName(), ex);
		}
	}

	@Override
	public void save(MemoryGraph memoryGraph) {

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

	public ClassLoader getClassLoader() {

		return this.classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {

		this.classLoader = classLoader;
	}
}
