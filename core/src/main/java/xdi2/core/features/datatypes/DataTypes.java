package xdi2.core.features.datatypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.multiplicity.XdiAttributeMember;
import xdi2.core.features.multiplicity.XdiAttributeSingleton;
import xdi2.core.features.multiplicity.XdiCollection;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XRI3Constants;

/**
 * A helper class to work with data types, i.e. get or set them. Supported data
 * types are taken from XML Schema (XSD), JSON, and MIME.
 * 
 * @author markus
 */
public class DataTypes {

	private DataTypes() {
	}

	public static final XDI3Segment XRI_DATATYPE_XSD = XDI3Segment.create(""
			+ XRI3Constants.GCS_PLUS + XRI3Constants.GCS_DOLLAR + "xsd");
	public static final XDI3Segment XRI_DATATYPE_JSON = XDI3Segment.create(""
			+ XRI3Constants.GCS_PLUS + XRI3Constants.GCS_DOLLAR + "json");
	public static final XDI3Segment XRI_DATATYPE_MIME = XDI3Segment.create(""
			+ XRI3Constants.GCS_PLUS + XRI3Constants.GCS_DOLLAR + "mime");
	private static final String xriBoolean = "+$binary!";

	/*
	 * Methods for data type XRIs
	 */

	/**
	 * Returns XDI3Segment for a xsd datatype string.
	 * 
	 * @param xsdType
	 * @return a xsd datatype XDI3Segment
	 */
	public static XDI3Segment dataTypeXriFromXsdType(String xsdType) {

		return XDI3Segment.create("" + XRI_DATATYPE_XSD + XRI3Constants.GCS_DOLLAR
				+ xsdType + XRI3Constants.LCS_BANG);
	}

	/**
	 * Returns datatype of literal in xsd format for a given XRI segment.
	 * 
	 * @param dataTypeXri
	 * @return a string of xsd datatype
	 */
	public static String xsdTypeFromDataTypeXri(XDI3Segment dataTypeXri) {

		String dataTypeXriString = dataTypeXri.toString();

		return getDataTypeFromXRISegment(dataTypeXriString, true);
	}

	/**
	 * Returns XDI3Segment for a json datatype string.
	 * 
	 * @param jsonType
	 * @return a json datatype XDI3Segment
	 */
	public static XDI3Segment dataTypeXriFromJsonType(String jsonType) {

		return XDI3Segment.create("" + XRI_DATATYPE_JSON
				+ XRI3Constants.GCS_DOLLAR + jsonType + XRI3Constants.LCS_BANG);
	}

	/**
	 * Returns datatype of literal in json format for a given XRI segment.
	 * 
	 * @param dataTypeXri
	 * @return a string of json datatype
	 */
	public static String jsonTypeFromDataTypeXri(XDI3Segment dataTypeXri) {

		String dataTypeJSONString = dataTypeXri.toString();

		return getDataTypeFromXRISegment(dataTypeJSONString, false);
	}

	/**
	 * Returns XDI3Segment for a mime datatype string.
	 * 
	 * @param mimeType
	 * @return a mime datatype XDI3Segment.
	 */
	public static XDI3Segment dataTypeXriFromMimeType(String mimeType) {

		// TODO: maybe use the MimeType class from the io package?
		// TODO: maybe somehow use enums?
		String[] parts;
		XDI3Segment xriSeg = null;
		try {
			parts = mimeType.split("/");
			xriSeg = XDI3Segment.create("" + XRI_DATATYPE_MIME
					+ XRI3Constants.GCS_DOLLAR + parts[0]
							+ XRI3Constants.GCS_DOLLAR + parts[1]
									+ XRI3Constants.LCS_BANG);
		} catch (Exception ex) {
			throw new Xdi2RuntimeException("Invalid MIME Type ", ex);
		}
		return xriSeg = (xriSeg != null) ? xriSeg : XDI3Segment.create("");
	}

	/**
	 * Returns datatype of literal in mime format for a given XRI segment.
	 * 
	 * @param dataTypeXri
	 * @return a string of mime datatype
	 */
	public static String mimeTypeFromDataTypeXri(XDI3Segment dataTypeXri) {

		String dataTypeMIMEString = dataTypeXri.toString();

		return getDataTypeFromXRISegment(dataTypeMIMEString, false);
	}

	/*
	 * Methods for data types of literals
	 */

	// TODO: Use the XDIDictionaryConstants.XRI_S_IS_TYPE predicate to express
	// type statements

	/**
	 * Create a "$is" relation for a literal and datatype XRI segment
	 * 
	 * @param literal
	 * @param dataTypeXri
	 */
	public static void setLiteralDataType(Literal literal,
			XDI3Segment dataTypeXri) {

		ContextNode contextNode = literal.getContextNode();

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_IS_TYPE,
				dataTypeXri);
	}

	/**
	 * Get all datatypes associated with a literal
	 * 
	 * @param literal
	 * @return list of datatypes as XDI3Segment
	 */
	public static List<XDI3Segment> getLiteralDataType(Literal literal) {

		return getLiteralDatatypes(literal);
	}

	/**
	 * Create a "$is" relation for a literal and binary XRI segment
	 * 
	 * @param literal
	 * @param binary
	 */
	public static void setLiteralBinary(Literal literal, boolean binary) {

		ContextNode contextNode = literal.getContextNode();

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_IS_TYPE,
				XDI3Segment.create(xriBoolean));

	}

	/**
	 * Check if a literal has binary datatype
	 * 
	 * @param literal
	 * @return boolean
	 */
	public static boolean isLiteralBinary(Literal literal) {

		List<XDI3Segment> lstDatatypesXRI = getLiteralDatatypes(literal);
		boolean isBinary = false;

		Iterator<?> itrDatatypes = lstDatatypesXRI.iterator();

		while (itrDatatypes.hasNext()) {
			if (itrDatatypes.next().toString().contains(xriBoolean)) {
				isBinary = true;
				break;
			}
		}

		return isBinary;
	}

	/*
	 * Methods for data types of multiplicity contexts TODO: we'll think about
	 * that a bit later
	 */

	public static void setLiteralDataType(XdiCollection xdiCollection,
			XDI3Segment dataType) {

	}

	public static XDI3Segment getLiteralDataType(XdiCollection xdiCollection) {

		return null;
	}

	public static void setLiteralDataType(
			XdiAttributeMember xdiAttributeMember, XDI3Segment dataType) {

	}

	public static XDI3Segment getLiteralDataType(
			XdiAttributeMember xdiAttributeMember) {

		return null;
	}

	public static void setLiteralDataType(
			XdiAttributeSingleton xdiAttributeSingleton, XDI3Segment dataType) {

	}

	public static XDI3Segment getLiteralDataType(
			XdiAttributeSingleton xdiAttributeSingleton) {

		return null;
	}

	/**
	 * Generic method to get datatype from a string of xri segment.
	 * 
	 * @param xriSegment
	 * @param isXSDType
	 * @return a string of datatype
	 */
	private static String getDataTypeFromXRISegment(String xriSegment,
			boolean isXSDType) {
		StringBuilder sb = new StringBuilder();
		try {
			String[] split = xriSegment.substring(xriSegment.indexOf("$") + 1,
					xriSegment.indexOf("!")).split("[$]");

			for (int i = 0; i < split.length; i++) {

				if (!isXSDType && i == 0)
					continue;

				sb.append(split[i]);

				if (i != split.length - 1) {

					if (!isXSDType)
						sb.append("/");
					else
						sb.append(":");
				}
			}
		} catch (Exception ex) {
			throw new Xdi2RuntimeException("Invalid XDI3Segment ", ex);
		}

		return sb.toString();
	}

	private static List<XDI3Segment> getLiteralDatatypes(Literal literal) {

		List<XDI3Segment> dataTypes;

		try {

			ReadOnlyIterator<Relation> relations = literal.getContextNode().getRelations(XDIDictionaryConstants.XRI_S_IS_TYPE);

			dataTypes = new ArrayList<XDI3Segment>();

			while (relations.hasNext()) {

				Relation relation = relations.next();
				dataTypes.add(relation.getStatement().getObject());
			}
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Invalid XDI3Segment ", ex);
		}

		return dataTypes;
	}
}
