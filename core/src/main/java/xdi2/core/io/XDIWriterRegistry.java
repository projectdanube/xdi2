package xdi2.core.io;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.io.writers.XDIDisplayWriter;
import xdi2.core.io.writers.XDIHTMLWriter;
import xdi2.core.io.writers.XDIJSONPARSEWriter;
import xdi2.core.io.writers.XDIJSONQuadWriter;
import xdi2.core.io.writers.XDIJSONTREEWriter;
import xdi2.core.io.writers.XDIJSONTripleWriter;
import xdi2.core.io.writers.XDIJSONWriter;
import xdi2.core.io.writers.XDIJXDWriter;
import xdi2.core.io.writers.XDIKeyValueWriter;

/**
 * Provides an appropriate XDIWriter for a given type.
 *
 * @author markus
 */
public final class XDIWriterRegistry {

	private static final Logger log = LoggerFactory.getLogger(XDIWriterRegistry.class);

	public static final String PARAMETER_IMPLIED = "implied";
	public static final String PARAMETER_ORDERED = "ordered";
	public static final String PARAMETER_PRETTY = "pretty";
	public static final String PARAMETER_HTML = "html";
	public static final String DEFAULT_IMPLIED = "0";
	public static final String DEFAULT_ORDERED = "0";
	public static final String DEFAULT_PRETTY = "0";
	public static final String DEFAULT_HTML = "0";

	public static final Properties PROPERTIES_IMPLIED;
	public static final Properties PROPERTIES_ORDERED;
	public static final Properties PROPERTIES_PRETTY;
	public static final Properties PROPERTIES_HTML;

	private static String writerClassNames[] = {

		XDIJSONQuadWriter.class.getName(),// first one in the array is the default
		XDIDisplayWriter.class.getName(),
		XDIJXDWriter.class.getName(),
		XDIJSONWriter.class.getName(),
		XDIJSONTREEWriter.class.getName(),
		XDIJSONPARSEWriter.class.getName(),
		XDIJSONTripleWriter.class.getName(),
		XDIKeyValueWriter.class.getName(),
		XDIHTMLWriter.class.getName()
	};

	private static List<Class<XDIWriter>> writerClasses;

	private static Class<XDIWriter> defaultWriterClass;

	private static Map<String, Class<? extends XDIWriter>> writerClassesByFormat;
	private static Map<String, Class<? extends XDIWriter>> writerClassesByFileExtension;
	private static Map<MimeType, Class<? extends XDIWriter>> writerClassesByMimeType;

	static {

		PROPERTIES_IMPLIED = new Properties(); PROPERTIES_IMPLIED.setProperty(PARAMETER_IMPLIED, "1");
		PROPERTIES_ORDERED = new Properties(); PROPERTIES_ORDERED.setProperty(PARAMETER_ORDERED, "1");
		PROPERTIES_PRETTY = new Properties(); PROPERTIES_PRETTY.setProperty(PARAMETER_PRETTY, "1");
		PROPERTIES_HTML = new Properties(); PROPERTIES_HTML.setProperty(PARAMETER_HTML, "1");

		writerClasses = new ArrayList<Class<XDIWriter>>();
		writerClassesByFormat = new HashMap<String, Class<? extends XDIWriter>>();
		writerClassesByFileExtension = new HashMap<String, Class<? extends XDIWriter>>();
		writerClassesByMimeType = new HashMap<MimeType, Class<? extends XDIWriter>>();

		for (String writerClassName : writerClassNames) {

			try {

				Class<XDIWriter> writerClass = forName(writerClassName);
				writerClasses.add(writerClass);
			} catch (ClassNotFoundException ex) {

				throw new Xdi2RuntimeException("Cannot instantiate XDI Writer " + writerClassName + ": " + ex.getMessage(), ex);
			}
		}

		if (writerClasses.isEmpty()) throw new RuntimeException("No XDI Writers could be registered.");

		for (Class<XDIWriter> writerClass : writerClasses) {

			addWriter(writerClass);
		}

		defaultWriterClass = writerClasses.get(0);
	}

	public static void addWriter(Class<? extends XDIWriter> writerClass) {

		XDIWriter writer;

		try {

			Constructor<? extends XDIWriter> constructor = writerClass.getConstructor(Properties.class);
			writer = constructor.newInstance((Properties) null);
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}

		String format = writer.getFormat();
		String fileExtension = writer.getFileExtension();
		MimeType mimeType = writer.getMimeType();

		if (format != null) writerClassesByFormat.put(format, writerClass);
		if (fileExtension != null) writerClassesByFileExtension.put(fileExtension, writerClass);
		if (mimeType != null) writerClassesByMimeType.put(mimeType, writerClass);
	}

	@SuppressWarnings("unchecked")
	private static Class<XDIWriter> forName(String writerClassName) throws ClassNotFoundException {

		return (Class<XDIWriter>) Class.forName(writerClassName);
	}

	private XDIWriterRegistry() { }

	/**
	 * Returns an XDIWriter for the specified format, e.g.
	 * <ul>
	 * <li>XDI/JSON</li>
	 * <li>XDI DISPLAY</li>
	 * </ul>
	 * @param format The desired format.
	 * @return An XDIWriter, or null if no appropriate implementation could be found.
	 */
	public static XDIWriter forFormat(String format, Properties parameters) {

		if (format == null) return XDIWriterRegistry.getDefault();

		Class<? extends XDIWriter> writerClass = writerClassesByFormat.get(format);
		if (writerClass == null) return null;

		try {

			Constructor<? extends XDIWriter> constructor = writerClass.getConstructor(Properties.class);
			return constructor.newInstance(parameters);
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns an XDIWriter for the specified file extension, e.g.
	 * <ul>
	 * <li>.json</li>
	 * <li>.xdi</li>
	 * </ul>
	 * @param fileExtension The desired file extension.
	 * @return An XDIWriter, or null if no appropriate implementation could be found.
	 */
	public static XDIWriter forFileExtension(String fileExtension, Properties parameters) {

		if (fileExtension == null) return XDIWriterRegistry.getDefault();

		Class<? extends XDIWriter> writerClass = writerClassesByFileExtension.get(fileExtension);
		if (writerClass == null) return null;

		try {

			Constructor<? extends XDIWriter> constructor = writerClass.getConstructor(Properties.class);
			return constructor.newInstance(parameters);
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns an XDIWriter for the specified mime type, e.g.
	 * <ul>
	 * <li>application/xdi+json</li>
	 * <li>text/xdi</li>
	 * </ul>
	 * @param mimeType The desired mime type.
	 * @return An XDIWriter, or null if no appropriate implementation could be found.
	 */
	public static XDIWriter forMimeType(MimeType mimeType) {

		if (mimeType == null) return XDIWriterRegistry.getDefault();

		Class<? extends XDIWriter> writerClass = writerClassesByMimeType.get(mimeType.mimeTypeWithoutParameters());
		if (writerClass == null) return null;

		try {

			Constructor<? extends XDIWriter> constructor = writerClass.getConstructor(Properties.class);
			return constructor.newInstance(mimeType.getParameters());
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns an XDIWriter for the default format.
	 * @return An XDIWriter.
	 */
	public static XDIWriter getDefault() {

		try {

			Constructor<XDIWriter> constructor = defaultWriterClass.getConstructor(Properties.class);
			return constructor.newInstance((Properties) null);
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
	 * Returns all file extensions for which XDIWriter implementations exist.
	 * @return A string array of file extensions.
	 */
	public static String[] getFileExtensions() {

		return writerClassesByFileExtension.keySet().toArray(new String[writerClassesByFileExtension.size()]);
	}

	/**
	 * Returns all mime types for which XDIWriter implementations exist.
	 * @return A string array of mime types.
	 */
	public static MimeType[] getMimeTypes() {

		return writerClassesByMimeType.keySet().toArray(new MimeType[writerClassesByMimeType.size()]);
	}

	/**
	 * Checks if we have an XDIWriter that supports this format.
	 * @param format The desired format.
	 * @return True, if supported.
	 */
	public static boolean supportsFormat(String format) {

		return writerClassesByMimeType.containsKey(format);
	}

	/**
	 * Checks if we have an XDIWriter that supports this file extension.
	 * @param fileExtension The desired file extension.
	 * @return True, if supported.
	 */
	public static boolean supportsFileExtension(MimeType fileExtension) {

		return writerClassesByFileExtension.containsKey(fileExtension);
	}

	/**
	 * Checks if we have an XDIWriter that supports this MIME type.
	 * @param mimeType The desired mime type.
	 * @return True, if supported.
	 */
	public static boolean supportsMimeType(MimeType mimeType) {

		return writerClassesByMimeType.containsKey(mimeType);
	}
}
