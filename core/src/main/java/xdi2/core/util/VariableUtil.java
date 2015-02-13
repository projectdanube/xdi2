package xdi2.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiCommonVariable;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiVariable;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

/**
 * Utility methods for working with variables.
 * 
 * @author markus
 */
// TODO: FIX THESE for new variable syntax
public final class VariableUtil {

	private static final Logger log = LoggerFactory.getLogger(VariableUtil.class);

	private VariableUtil() { }

	/**
	 * Checks if a variable can match multiple contexts.
	 * @param xdiVariable The variable.
	 * @return True, if the variable can match multiple contexts.
	 */
	public static boolean isMultiple(XdiVariable<?> xdiVariable) {

		return true;
	}

	public static boolean isMultiple(XDIAddress XDIaddress) {

		return true;
	}

	/**
	 * Checks if a variables matches an arc.
	 * @param xdiVariable The variable.
	 * @param arc The arc to match the variable against.
	 * @return True, if the variable matches the arc.
	 */
	@Deprecated
	// TODO: this is just a start
	public static boolean matches(XdiVariable<?> xdiVariable, XdiContext<?> xdiContext) {

		if (log.isTraceEnabled()) log.trace("Matching variable " + xdiVariable + " (" + xdiVariable.getClass().getCanonicalName() + ") against context " + xdiContext + " (" + xdiContext.getClass().getCanonicalName() + ")");

		if (XdiCommonVariable.class.isAssignableFrom(xdiVariable.getClass())) return true;

		if (XdiPeerRoot.class.isAssignableFrom(xdiVariable.getClass()) && XdiPeerRoot.class.isAssignableFrom(xdiContext.getClass())) return true;
		if (XdiInnerRoot.class.isAssignableFrom(xdiVariable.getClass()) && XdiInnerRoot.class.isAssignableFrom(xdiContext.getClass())) return true;
		if (XdiEntityCollection.class.isAssignableFrom(xdiVariable.getClass()) && XdiEntityCollection.class.isAssignableFrom(xdiContext.getClass())) return true;
		if (XdiAttributeCollection.class.isAssignableFrom(xdiVariable.getClass()) && XdiAttributeCollection.class.isAssignableFrom(xdiContext.getClass())) return true;
		if (XdiEntity.class.isAssignableFrom(xdiVariable.getClass()) && XdiEntity.class.isAssignableFrom(xdiContext.getClass())) return true;
		if (XdiAttribute.class.isAssignableFrom(xdiVariable.getClass()) && XdiAttribute.class.isAssignableFrom(xdiContext.getClass())) return true;

		return false;
	}

	public static boolean matches(XDIAddress XDIvariableAddress, XDIArc XDIaddress) {

		return XDIConstants.XDI_ADD_COMMON_VARIABLE.equals(XDIvariableAddress);
	}
}
