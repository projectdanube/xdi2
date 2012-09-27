package xdi2.core.features.datatypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.multiplicity.XdiAttributeMember;
import xdi2.core.features.multiplicity.XdiAttributeSingleton;
import xdi2.core.features.multiplicity.XdiCollection;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.impl.XRI3Constants;
import xdi2.core.xri3.impl.XRI3Segment;

/**
 * A helper class to work with data types, i.e. get or set them. Supported data
 * types are taken from XML Schema (XSD), JSON, and MIME.
 * 
 * @author markus
 */
public class DataTypes {

	private DataTypes() {
	}

	public static final XRI3Segment XRI_XSD_DATATYPE = new XRI3Segment(""
			+ XRI3Constants.GCS_PLUS + XRI3Constants.GCS_DOLLAR + "xsd");
	public static final XRI3Segment XRI_JSON_DATATYPE = new XRI3Segment(""
			+ XRI3Constants.GCS_PLUS + XRI3Constants.GCS_DOLLAR + "json");
	public static final XRI3Segment XRI_MIME_DATATYPE = new XRI3Segment(""
			+ XRI3Constants.GCS_PLUS + XRI3Constants.GCS_DOLLAR + "mime");
	private static final String xriBoolean = "+$binary!";

	/*
	 * Methods for data type XRIs
	 */

	/**
	 * Returns XRI3Segment for a xsd datatype string.
	 * 
	 * @param xsdType
	 * @return a xsd datatype XRI3Segment
	 */
	public static XRI3Segment dataTypeXriFromXsdType(String xsdType) {

		return new XRI3Segment("" + XRI_XSD_DATATYPE + XRI3Constants.GCS_DOLLAR
				+ xsdType + XRI3Constants.LCS_BANG);
	}

	/**
	 * Returns datatype of literal in xsd format for a given XRI segment.
	 * 
	 * @param dataTypeXri
	 * @return a string of xsd datatype
	 */
	public static String xsdTypeFromDataTypeXri(XRI3Segment dataTypeXri) {

		String dataTypeXriString = dataTypeXri.toString();

		return getDataTypeFromXRISegment(dataTypeXriString, true);
	}

	/**
	 * Returns XRI3Segment for a json datatype string.
	 * 
	 * @param jsonType
	 * @return a json datatype XRI3Segment
	 */
	public static XRI3Segment dataTypeXriFromJsonType(String jsonType) {

		return new XRI3Segment("" + XRI_JSON_DATATYPE
				+ XRI3Constants.GCS_DOLLAR + jsonType + XRI3Constants.LCS_BANG);
	}

	/**
	 * Returns datatype of literal in json format for a given XRI segment.
	 * 
	 * @param dataTypeXri
	 * @return a string of json datatype
	 */
	public static String jsonTypeFromDataTypeXri(XRI3Segment dataTypeXri) {

		String dataTypeJSONString = dataTypeXri.toString();

		return getDataTypeFromXRISegment(dataTypeJSONString, false);
	}

	/**
	 * Returns XRI3Segment for a mime datatype string.
	 * 
	 * @param mimeType
	 * @return a mime datatype XRI3Segment.
	 */
	public static XRI3Segment dataTypeXriFromMimeType(String mimeType) {

		// TODO: maybe use the MimeType class from the io package?
		// TODO: maybe somehow use enums?
		String[] parts;
		XRI3Segment xriSeg = null;
		try {
			parts = mimeType.split("/");
			xriSeg = new XRI3Segment("" + XRI_MIME_DATATYPE
					+ XRI3Constants.GCS_DOLLAR + parts[0]
					+ XRI3Constants.GCS_DOLLAR + parts[1]
					+ XRI3Constants.LCS_BANG);
		} catch (Exception ex) {
			throw new Xdi2RuntimeException("Invalid MIME Type ", ex);
		}
		return xriSeg = (xriSeg != null) ? xriSeg : new XRI3Segment("");
	}

	/**
	 * Returns datatype of literal in mime format for a given XRI segment.
	 * 
	 * @param dataTypeXri
	 * @return a string of mime datatype
	 */
	public static String mimeTypeFromDataTypeXri(XRI3Segment dataTypeXri) {

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
			XRI3Segment dataTypeXri) {

		ContextNode contextNode = literal.getContextNode();

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_IS_TYPE,
				dataTypeXri);
	}

	/**
	 * Get all datatypes associated with a literal
	 * 
	 * @param literal
	 * @return list of datatypes as XRI3Segment
	 */
	public static List<XRI3Segment> getLiteralDataType(Literal literal) {

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
				new XRI3Segment(xriBoolean));

	}

	/**
	 * Check if a literal has binary datatype
	 * 
	 * @param literal
	 * @return boolean
	 */
	public static boolean isLiteralBinary(Literal literal) {

		List<XRI3Segment> lstDatatypesXRI = getLiteralDatatypes(literal);
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
			XRI3Segment dataType) {

	}

	public static XRI3Segment getLiteralDataType(XdiCollection xdiCollection) {

		return null;
	}

	public static void setLiteralDataType(
			XdiAttributeMember xdiAttributeMember, XRI3Segment dataType) {

	}

	public static XRI3Segment getLiteralDataType(
			XdiAttributeMember xdiAttributeMember) {

		return null;
	}

	public static void setLiteralDataType(
			XdiAttributeSingleton xdiAttributeSingleton, XRI3Segment dataType) {

	}

	public static XRI3Segment getLiteralDataType(
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
			throw new Xdi2RuntimeException("Invalid XRI3Segment ", ex);
		}

		return sb.toString();
	}

	private static List<XRI3Segment> getLiteralDatatypes(Literal literal) {
		List<XRI3Segment> dataTypes;
		try {
			Graph graph = literal.getGraph();

			ReadOnlyIterator<Relation> relations = 
					literal.getContextNode().getRelations(XDIDictionaryConstants.XRI_S_IS_TYPE);
			
			dataTypes = new ArrayList<XRI3Segment>();

			while (relations.hasNext()) {

				Relation relation = relations.next();
				dataTypes.add(relation.getStatement().getObject());
			}
		} catch (Exception ex) {
			throw new Xdi2RuntimeException("Invalid XRI3Segment ", ex);
		}
		
		return dataTypes;

	}

}
