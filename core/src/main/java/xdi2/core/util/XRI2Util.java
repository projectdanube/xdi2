package xdi2.core.util;

import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

/**
 * Various utility methods for working with XRI 2.0 syntax.
 * 
 * @author markus
 */
public final class XRI2Util {

	private static final Logger log = LoggerFactory.getLogger(XRI2Util.class);

	private XRI2Util() { }

	/**
	 * Maps an XRI 2.0 I-Number to a Cloud Number.
	 */
	public static CloudNumber iNumberToCloudNumber(String iNumber) {

		if (log.isTraceEnabled()) log.trace("iNumberToCloudNumber(" + iNumber + ")");

		if (iNumber.startsWith("xri://")) iNumber = iNumber.substring("xri://".length());

		Character cs = Character.valueOf(iNumber.charAt(0));
		if ((! XDIConstants.CS_AUTHORITY_PERSONAL.equals(cs)) && (! XDIConstants.CS_AUTHORITY_LEGAL.equals(cs))) return null;

		iNumber = iNumber.substring(2).toLowerCase();

		String[] parts = iNumber.split("\\.");
		if (parts.length != 4) return null;

		for (int i=0; i<parts.length; i++) {

			if (parts[i].length() > 4) return null;
			while (parts[i].length() < 4) parts[i] = "0" + parts[i];
		}

		StringBuilder builder = new StringBuilder();

		builder.append("[" + cs + "]" + XDIConstants.CS_MEMBER_UNORDERED + ":uuid:");
		builder.append(parts[0]);
		builder.append(parts[1]);
		builder.append("-");
		builder.append(parts[2]);
		builder.append("-");
		builder.append(parts[3]);
		builder.append("-");
		builder.append(parts[0]);
		builder.append("-");
		builder.append(parts[1]);
		builder.append(parts[2]);
		builder.append(parts[3]);

		return CloudNumber.create(builder.toString());
	}

	/**
	 * Maps a Cloud Number to an XRI 2.0 I-Number.
	 */
	public static String cloudNumberToINumber(CloudNumber cloudNumber) {

		if (log.isTraceEnabled()) log.trace("cloudNumberToINumber(" + cloudNumber + ")");

		XDIAddress XDIaddress = cloudNumber.getXDIAddress();

		if (XDIaddress.getNumXDIArcs() != 2) return null;

		if (! XDIaddress.getFirstXDIArc().isClassXs()) return null;
		if (XDIaddress.getFirstXDIArc().hasLiteralNode()) return null;
		if (XDIaddress.getFirstXDIArc().hasXRef()) return null;

		char cs = XDIaddress.getFirstXDIArc().getCs().charValue();

		if (! XDIConstants.CS_MEMBER_UNORDERED.equals(XDIaddress.getLastXDIArc().getCs())) return null;
		if (! XDIaddress.getLastXDIArc().hasLiteralNode()) return null;
		if (XDIaddress.getLastXDIArc().hasXRef()) return null;
		if (! XDIaddress.getLastXDIArc().getLiteralNode().startsWith(":uuid")) return null;
		if (XDIaddress.getLastXDIArc().getLiteralNode().length() != 42) return null;

		String[] parts = new String[4];
		parts[0] = XDIaddress.getLastXDIArc().getLiteralNode().substring(6, 10);
		parts[1] = XDIaddress.getLastXDIArc().getLiteralNode().substring(10, 14);
		parts[2] = XDIaddress.getLastXDIArc().getLiteralNode().substring(15, 19);
		parts[3] = XDIaddress.getLastXDIArc().getLiteralNode().substring(20, 24);

		if (! parts[0].equals(XDIaddress.getLastXDIArc().getLiteralNode().substring(25, 29))) return null;
		if (! parts[1].equals(XDIaddress.getLastXDIArc().getLiteralNode().substring(30, 34))) return null;
		if (! parts[2].equals(XDIaddress.getLastXDIArc().getLiteralNode().substring(34, 38))) return null;
		if (! parts[3].equals(XDIaddress.getLastXDIArc().getLiteralNode().substring(38, 42))) return null;

		for (int i=0; i<parts.length; i++) {

			if (parts[i].length() > 4) return null;
			if (parts[i].indexOf('-') != -1) return null;
			parts[i] = parts[i].toUpperCase();
			while (parts[i].charAt(0) == '0') parts[i] = parts[i].substring(1);
		}

		StringBuilder builder = new StringBuilder();

		builder.append("" + cs + XDIConstants.CS_MEMBER_UNORDERED);
		builder.append(parts[0]);
		builder.append(".");
		builder.append(parts[1]);
		builder.append(".");
		builder.append(parts[2]);
		builder.append(".");
		builder.append(parts[3]);

		return builder.toString();
	}

	/**
	 * Maps an XRI 2.0 service type to an XDI arc.
	 */
	public static XDIArc typeToXDIArc(String type) {

		if (log.isTraceEnabled()) log.trace("typeToXdiarc(" + type + ")");

		if (type.startsWith("xri://")) type = type.substring(6);
		type = type.replace('+' , '#');

		XDIArc XDIarc = null;

		try { XDIarc = XDIArc.create(type); } catch (Exception ex) { XDIarc = null; }
		if (XDIarc == null) try { XDIarc = XDIArc.create("#(" + type + ")"); } catch (Exception ex) { XDIarc = null; }
		if (XDIarc == null) try { XDIarc = XDIArc.create("#(" + URLEncoder.encode(type, "UTF-8") + ")"); } catch (Exception ex) { XDIarc = null; }

		if (XDIarc == null) return null;

		if (! XdiAttributeSingleton.isValidXDIArc(XDIarc)) XDIarc = XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIarc);

		return XDIarc;
	}
}
