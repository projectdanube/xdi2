package xdi2.core.features.datatypes;

import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.AddressUtil;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeAddressIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;

/**
 * A helper class to work with data types, i.e. get or set them. Supported data
 * types are taken from XML Schema (XSD), JSON, and MIME.
 * 
 * @author markus
 */
public class DataTypes {

	private DataTypes() { }

	public static final XDIAddress XDI_ADD_DATATYPE_XSD = XDIAddress.create("" + XDIConstants.CS_CLASS_RESERVED + "xsd");
	public static final XDIAddress XDI_ADD_DATATYPE_JSON = XDIAddress.create("" + XDIConstants.CS_CLASS_RESERVED + "json");
	public static final XDIAddress XDI_ADD_DATATYPE_MIME = XDIAddress.create("" + XDIConstants.CS_CLASS_RESERVED + "mime");

	/*
	 * Methods for booleans
	 */

	public static XDIAddress booleanToAddress(Boolean b) {

		if (Boolean.TRUE.equals(b)) return XDIConstants.XDI_ADD_TRUE;
		if (Boolean.FALSE.equals(b)) return XDIConstants.XDI_ADD_FALSE;

		return null;
	}

	public static Boolean addressToBoolean(XDIAddress address) {

		if (XDIConstants.XDI_ADD_TRUE.equals(address)) return Boolean.TRUE;
		if (XDIConstants.XDI_ADD_FALSE.equals(address)) return Boolean.FALSE;

		return null;
	}

	/*
	 * Methods for data types of context nodes
	 */

	/**
	 * Set a $is# datatype associated with a context node
	 * 
	 * @param contextNode
	 * @param dataTypeAddress
	 */
	public static void setDataType(ContextNode contextNode, XDIAddress dataTypeAddress) {

		contextNode.setRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE, dataTypeAddress);
	}

	/**
	 * Get all $is# datatypes associated with a context node
	 * 
	 * @param contextNode
	 * @return list of datatypes
	 */
	public static List<XDIAddress> getDataTypes(ContextNode contextNode) {

		ReadOnlyIterator<Relation> relations = contextNode.getRelations(XDIDictionaryConstants.XDI_ADD_IS_TYPE);

		return new IteratorListMaker<XDIAddress> (new MappingRelationTargetContextNodeAddressIterator(relations)).list();
	}

	/**
	 * Get a $is# datatype associated with a context node
	 * 
	 * @param contextNode
	 * @return datatype
	 */
	public static XDIAddress getDataType(ContextNode contextNode) {

		Relation relation = contextNode.getRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE);

		return relation == null ? null : relation.getTargetContextNodeAddress();
	}

	/*
	 * Methods for data type addresses
	 */

	/**
	 * Returns an XDI address for an XSD data type.
	 */
	public static XDIAddress dataTypeAddressFromXsdType(String xsdType) {

		XDIAddress xsdTypeAddress = XDIAddress.create("" + XDIConstants.CS_CLASS_RESERVED + xsdType.replace(":", XDIConstants.CS_CLASS_RESERVED.toString()));

		return AddressUtil.concatAddresses(XDI_ADD_DATATYPE_XSD, xsdTypeAddress);
	}

	/**
	 * Returns an XDI address for a JSON data type.
	 */
	public static XDIAddress dataTypeAddressFromJsonType(String jsonType) {

		XDIAddress jsonTypeAddress = XDIAddress.create("" + XDIConstants.CS_CLASS_RESERVED + jsonType);

		return AddressUtil.concatAddresses(XDI_ADD_DATATYPE_JSON, jsonTypeAddress);
	}

	/**
	 * Returns an XDI address for a MIME data type.
	 */
	public static XDIAddress dataTypeAddressFromMimeType(String mimeType) {

		XDIAddress mimeTypeAddress = XDIAddress.create("" + XDIConstants.CS_CLASS_RESERVED + mimeType.replace("/", XDIConstants.CS_CLASS_RESERVED.toString()));

		return AddressUtil.concatAddresses(XDI_ADD_DATATYPE_MIME, mimeTypeAddress);
	}

	/**
	 * Returns an XSD data type for an XDI address.
	 */
	public static String xsdTypeFromDataTypeAddress(XDIAddress dataTypeAddress) {

		if (AddressUtil.startsWith(dataTypeAddress, XDI_ADD_DATATYPE_XSD) == null) throw new Xdi2RuntimeException("Invalid XSD data type address: " + dataTypeAddress);

		XDIAddress xsdTypeAddress = AddressUtil.localAddress(dataTypeAddress, - XDI_ADD_DATATYPE_XSD.getNumArcs());

		return xsdTypeAddress.toString().substring(1).replace(XDIConstants.CS_CLASS_RESERVED.toString(), ":");
	}

	/**
	 * Returns an XSD data type for a JSON data type.
	 */
	public static String jsonTypeFromDataTypeAddress(XDIAddress dataTypeAddress) {

		if (AddressUtil.startsWith(dataTypeAddress, XDI_ADD_DATATYPE_JSON) == null) throw new Xdi2RuntimeException("Invalid JSON data type address: " + dataTypeAddress);

		XDIAddress jsonTypeAddress = AddressUtil.localAddress(dataTypeAddress, - XDI_ADD_DATATYPE_JSON.getNumArcs());

		return jsonTypeAddress.toString().substring(1);
	}

	/**
	 * Returns a MIME data type for an XDI address.
	 */
	public static String mimeTypeFromDataTypeAddress(XDIAddress dataTypeAddress) {

		if (AddressUtil.startsWith(dataTypeAddress, XDI_ADD_DATATYPE_MIME) == null) throw new Xdi2RuntimeException("Invalid MIME data type address: " + dataTypeAddress);
		
		XDIAddress mimeTypeAddress = AddressUtil.localAddress(dataTypeAddress, - XDI_ADD_DATATYPE_MIME.getNumArcs());

		return mimeTypeAddress.toString().substring(1).replace(XDIConstants.CS_CLASS_RESERVED.toString(), "/");
	}
}
