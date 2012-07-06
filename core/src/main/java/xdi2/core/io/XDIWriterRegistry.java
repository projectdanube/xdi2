package xdi2.core.io;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.io.writers.XDIJSONWriter;
import xdi2.core.io.writers.XDIJSONWriterWithContextStatements;
import xdi2.core.io.writers.XDIKeyValueWriter;
import xdi2.core.io.writers.XDIStatementsWriter;
import xdi2.core.io.writers.XDIStatementsWriterHTML;
import xdi2.core.io.writers.XDIStatementsWriterWithContextStatements;

/**
 * Provides an appropriate XDIWriter for a given type.
 *
 * @author markus
 */
public final class XDIWriterRegistry {

	private static final Logger log = LoggerFactory.getLogger(XDIWriterRegistry.class);

	private static String writerClassNames[] = {

		XDIJSONWriter.class.getName(),// first one in the array is the default
		XDIJSONWriterWithContextStatements.class.getName(),
		XDIStatementsWriterWithContextStatements.class.getName(),
		XDIStatementsWriter.class.getName(),
		XDIStatementsWriterHTML.class.getName(),
		XDIKeyValueWriter.class.getName()
	};

	private static List<Class<XDIWriter>> writerClasses;

	private static Class<XDIWriter> defaultWriterClass;

	private static Map<String, Class<XDIWriter>> writerClassesByFormat;
	private static Map<String, Class<XDIWriter>> writerClassesByFileExtension;
	private static Map<MimeType, Class<XDIWriter>> writerClassesByMimeType;

	static {

		writerClasses = new ArrayList<Class<XDIWriter>>();
		writerClassesByFormat = new HashMap<String, Class<XDIWriter>>();
		writerClassesByFileExtension = new HashMap<String, Class<XDIWriter>>();
		writerClassesByMimeType = new HashMap<MimeType, Class<XDIWriter>>();

		for (String writerClassName : writerClassNames) {

			try {

				Class<XDIWriter> writerClass = forName(writerClassName);
				writerClasses.add(writerClass);
			} catch (Throwable ex) {

				log.warn("Cannot instantiate XDI Writer " + writerClassName + ": " + ex.getMessage());
				continue;
			}
		}

		if (writerClasses.isEmpty()) throw new RuntimeException("No XDI Writers could be registered.");

		for (Class<XDIWriter> writerClass : writerClasses) {

			XDIWriter writer;

			try {

				writer = writerClass.newInstance();
			} catch (Exception ex) {

				throw new RuntimeException(ex);
			}

			String format = writer.getFormat();
			String fileExtension = writer.getFileExtension();
			MimeType[] mimeTypes = writer.getMimeTypes();

			if (format != null) writerClassesByFormat.put(format, writerClass);
			if (fileExtension != null) writerClassesByFileExtension.put(fileExtension, writerClass);
			if (mimeTypes != null) for (MimeType mimeType : mimeTypes) writerClassesByMimeType.put(mimeType, writerClass);
		}

		defaultWriterClass = writerClasses.get(0);
	}

	@SuppressWarnings("unchecked")
	private static Class<XDIWriter> forName(String writerClassName) throws ClassNotFoundException {

		return (Class<XDIWriter>) Class.forName(writerClassName);
	}

	private XDIWriterRegistry() { }

	/**
	 * Returns an XDIWriter for the specified format, e.g.
	 * <ul>
	 * <li>X3</li>
	 * <li>X-TRIPLES</li>
	 * <li>XDI/XML</li>
	 * </ul>
	 * @param format The desired format.
	 * @return An XDIWriter, or null if no appropriate implementation could be found.
	 */
	public static XDIWriter forFormat(String format) {

		Class<XDIWriter> writerClass = writerClassesByFormat.get(format);
		if (writerClass == null) return null;

		try {

			return writerClass.newInstance();
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns an XDIWriter for the specified mime type, e.g.
	 * <ul>
	 * <li>text/xdi+x3</li>
	 * <li>text/plain</li>
	 * <li>application/xdi+xml</li>
	 * </ul>
	 * @param mimeType The desired mime type.
	 * @return An XDIWriter, or null if no appropriate implementation could be found.
	 */
	public static XDIWriter forMimeType(MimeType mimeType) {

		Class<XDIWriter> writerClass = writerClassesByMimeType.get(mimeType);
		if (writerClass == null) return null;

		try {

			return writerClass.newInstance();
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns an XDIWriter for the specified file extension, e.g.
	 * <ul>
	 * <li>.xml</li>
	 * <li>.x3</li>
	 * <li>.txt</li>
	 * </ul>
	 * @param fileExtension The desired file extension.
	 * @return An XDIWriter, or null if no appropriate implementation could be found.
	 */
	public static XDIWriter forFileExtension(String fileExtension) {

		Class<XDIWriter> writerClass = writerClassesByFileExtension.get(fileExtension);
		if (writerClass == null) return null;

		try {

			return writerClass.newInstance();
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns a list of all available XDIWriters.
	 * @return All XDIWriters this factory knows of.
	 */
	public static XDIWriter[] getWriters() {

		XDIWriter[] writers = new XDIWriter[writerClasses.size()];

		for (int i=0; i<writerClasses.size(); i++) {

			try {

				writers[i] = writerClasses.get(i).newInstance();
			} catch (Exception ex) {

				throw new RuntimeException(ex);
			}
		}

		return writers;
	}

	/**
	 * Returns an XDIWriter for the default format.
	 * @return An XDIWriter.
	 */
	public static XDIWriter getDefault() {

		Class<XDIWriter> writerClass = defaultWriterClass;

		try {

			return writerClass.newInstance();
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns all formats for which XDIWriter implementations exist.
	 * @return A string array of formats.
	 */
	public static String[] getFormats() {

		return writerClassesByFormat.keySet().toArray(new String[writerClassesByFormat.size()]);
	}

	/**
	 * Returns all mime types for which XDIWriter implementations exist.
	 * @return A string array of mime types.
	 */
	public static String[] getMimeTypes() {

		return writerClassesByMimeType.keySet().toArray(new String[writerClassesByMimeType.size()]);
	}
}
