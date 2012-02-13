package xdi2.core.io;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides an appropriate XDIReader for a given type.
 *
 * @author markus
 */
public final class XDIReaderRegistry {

	private static final Logger log = LoggerFactory.getLogger(XDIReaderRegistry.class);

	private static String readerClassNames[] = {

		XDIJSONReader.class.getName(),
		XDIStatementsReader.class.getName()
	};

	private static List<Class<XDIReader> > readerClasses;

	private static Map<String, Class<XDIReader> > readerClassesByFormat;
	private static Map<String, Class<XDIReader> > readerClassesByMimeType;
	private static Map<String, Class<XDIReader> > readerClassesByDefaultFileExtension;

	static {

		readerClasses = new ArrayList<Class<XDIReader> > ();
		readerClassesByFormat = new HashMap<String, Class<XDIReader> >();
		readerClassesByMimeType = new HashMap<String, Class<XDIReader> >();
		readerClassesByDefaultFileExtension = new HashMap<String, Class<XDIReader> >();

		for (String readerClassName : readerClassNames) {

			try {

				Class<XDIReader> readerClass = forName(readerClassName);
				readerClasses.add(readerClass);
			} catch (Throwable ex) {

				log.warn("Cannot instantiate XDI Reader " + readerClassName + ": " + ex.getMessage());
				continue;
			}
		}

		if (readerClasses.isEmpty()) throw new RuntimeException("No XDI Readers could be registered.");

		for (Class<XDIReader> readerClass : readerClasses) {

			XDIReader reader;

			try {

				reader = readerClass.newInstance();
			} catch (Exception ex) {

				throw new RuntimeException(ex);
			}

			if (reader.getFormat() != null) readerClassesByFormat.put(reader.getFormat(), readerClass);
			for (String mimeType : reader.getMimeTypes()) readerClassesByMimeType.put(mimeType, readerClass);
			if (reader.getDefaultFileExtension() != null) readerClassesByDefaultFileExtension.put(reader.getDefaultFileExtension(), readerClass);
		}
	}

	@SuppressWarnings("unchecked")
	private static Class<XDIReader> forName(String readerClassName) throws ClassNotFoundException {

		return (Class<XDIReader>) Class.forName(readerClassName);
	}

	private XDIReaderRegistry() { }

	/**
	 * Returns an XDIReader for the specified format.
	 * @param format The desired format.
	 * @return An XDIReader, or null if no appropriate implementation could be found.
	 */
	public static XDIReader forFormat(String format) {

		if (AutoReader.FORMAT_TYPE.equalsIgnoreCase(format)) return getAuto();

		Class<XDIReader> readerClass = readerClassesByFormat.get(format);
		if (readerClass == null) return null;

		try {

			return readerClass.newInstance();
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns an XDIReader for the specified mime type, e.g.
	 * <ul>
	 * <li>text/xdi+x3</li>
	 * <li>text/plain</li>
	 * <li>application/xdi+xml</li>
	 * </ul>
	 * @param mimeType The desired mime type.
	 * @return An XDIReader, or null if no appropriate implementation could be found.
	 */
	public static XDIReader forMimeType(String mimeType) {

		Class<XDIReader> readerClass = readerClassesByMimeType.get(mimeType);
		if (readerClass == null) return null;

		try {

			return readerClass.newInstance();
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns an XDIReader for the specified file extension, e.g.
	 * <ul>
	 * <li>.xml</li>
	 * <li>.x3</li>
	 * <li>.txt</li>
	 * </ul>
	 * @param fileExtension The desired file extension.
	 * @return An XDIReader, or null if no appropriate implementation could be found.
	 */
	public static XDIReader forFileExtension(String fileExtension) {

		Class<XDIReader> readerClass = readerClassesByDefaultFileExtension.get(fileExtension);
		if (readerClass == null) return null;

		try {

			return readerClass.newInstance();
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns a list of all available XDIReaders.
	 * @return All XDIReaders this factory knows of.
	 */
	public static XDIReader[] getReaders() {

		XDIReader[] readers = new XDIReader[readerClasses.size()];

		for (int i=0; i<readerClasses.size(); i++) {

			try {

				readers[i] = readerClasses.get(i).newInstance();
			} catch (Exception ex) {

				throw new RuntimeException(ex);
			}
		}

		return readers;
	}

	/**
	 * Returns an XDIReader for the default format.
	 * @return An XDIReader.
	 */
	public static XDIReader getDefault() {

		return new AutoReader();
	}

	/**
	 * Returns an XDIReader for auto-detecting the format.
	 * @return An XDIReader.
	 */
	public static AutoReader getAuto() {

		return new AutoReader();
	}

	/**
	 * Returns all formats for which XDIReader implementations exist.
	 * @return A string array of formats.
	 */
	public static String[] getFormats() {

		return readerClassesByFormat.keySet().toArray(new String[readerClassesByFormat.size()]);
	}

	/**
	 * Returns all mime types for which XDIReader implementations exist.
	 * @return A string array of mime types.
	 */
	public static String[] getMimeTypes() {

		return readerClassesByMimeType.keySet().toArray(new String[readerClassesByMimeType.size()]);
	}
}
