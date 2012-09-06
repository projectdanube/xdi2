package xdi2.core.features.datatypes;

import xdi2.core.Literal;
import xdi2.core.features.multiplicity.XdiAttributeMember;
import xdi2.core.features.multiplicity.XdiAttributeSingleton;
import xdi2.core.features.multiplicity.XdiCollection;
import xdi2.core.xri3.impl.XRI3Constants;
import xdi2.core.xri3.impl.XRI3Segment;

/**
 * A helper class to work with data types, i.e. get or set them.
 * Supported data types are taken from XML Schema (XSD), JSON, and MIME.

 * @author markus
 */
public class DataTypes {

	private DataTypes() { }

	public static final XRI3Segment XRI_XSD_DATATYPE = new XRI3Segment("" + XRI3Constants.GCS_PLUS + XRI3Constants.GCS_DOLLAR + "xsd");
	public static final XRI3Segment XRI_JSON_DATATYPE = new XRI3Segment("" + XRI3Constants.GCS_PLUS + XRI3Constants.GCS_DOLLAR + "json");
	public static final XRI3Segment XRI_MIME_DATATYPE = new XRI3Segment("" + XRI3Constants.GCS_PLUS + XRI3Constants.GCS_DOLLAR + "mime");

	/*
	 * Methods for data type XRIs
	 */

	public static XRI3Segment dataTypeXriFromXsdType(String xsdType) {

		return new XRI3Segment("" + XRI_XSD_DATATYPE + XRI3Constants.GCS_DOLLAR + xsdType + XRI3Constants.LCS_BANG);
	}

	public static String xsdTypeFromDataTypeXri(XRI3Segment dataTypeXri) {

		return null;
	}

	public static XRI3Segment dataTypeXriFromJsonType(String jsonType) {

		return new XRI3Segment("" + XRI_JSON_DATATYPE + XRI3Constants.GCS_DOLLAR + jsonType + XRI3Constants.LCS_BANG);
	}

	public static String jsonTypeFromDataTypeXri(XRI3Segment dataTypeXri) {

		return null;
	}

	public static XRI3Segment dataTypeXriFromMimeType(String mimeType) {

		// TODO: maybe use the MimeType class from the io package?
		// TODO: maybe somehow use enums?
		
		return null;
	}

	public static String mimeTypeFromDataTypeXri(XRI3Segment dataTypeXri) {

		return null;
	}

	/*
	 * Methods for data types of literals
	 */

	// TODO: Use the XDIDictionaryConstants.XRI_S_IS_TYPE predicate to express type statements

	public static void setLiteralDataType(Literal literal, XRI3Segment dataTypeXri) {

	}

	public static XRI3Segment getLiteralDataType(Literal literal) {

		return null;
	}

	public static void setLiteralBinary(Literal literal, boolean binary) {

	}

	public static boolean isLiteralBinary(Literal literal) {

		return false;
	}

	/*
	 * Methods for data types of multiplicity contexts
	 * TODO: we'll think about that a bit later
	 */
	
	public static void setLiteralDataType(XdiCollection xdiCollection, XRI3Segment dataType) {

	}

	public static XRI3Segment getLiteralDataType(XdiCollection xdiCollection) {

		return null;
	}

	public static void setLiteralDataType(XdiAttributeMember xdiAttributeMember, XRI3Segment dataType) {

	}

	public static XRI3Segment getLiteralDataType(XdiAttributeMember xdiAttributeMember) {

		return null;
	}

	public static void setLiteralDataType(XdiAttributeSingleton xdiAttributeSingleton, XRI3Segment dataType) {

	}

	public static XRI3Segment getLiteralDataType(XdiAttributeSingleton xdiAttributeSingleton) {

		return null;
	}
}
