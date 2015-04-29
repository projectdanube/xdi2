package xdi2.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiAttributeInstanceOrdered;
import xdi2.core.features.nodetypes.XdiAttributeInstanceUnordered;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiCommonVariable;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityInstanceOrdered;
import xdi2.core.features.nodetypes.XdiEntityInstanceUnordered;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
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

		if (XdiPeerRoot.Variable.class.equals(xdiVariable.getClass()) && XdiPeerRoot.class.isAssignableFrom(xdiContext.getClass())) return true;
		if (XdiInnerRoot.Variable.class.equals(xdiVariable.getClass()) && XdiInnerRoot.class.isAssignableFrom(xdiContext.getClass())) return true;

		if (XdiEntityCollection.Variable.class.equals(xdiVariable.getClass()) && XdiEntityCollection.class.isAssignableFrom(xdiContext.getClass())) return true;
		if (XdiAttributeCollection.Variable.class.equals(xdiVariable.getClass()) && XdiAttributeCollection.class.isAssignableFrom(xdiContext.getClass())) return true;

		if (XdiEntityInstanceOrdered.Variable.class.isAssignableFrom(xdiVariable.getClass()) && XdiEntityInstanceOrdered.class.isAssignableFrom(xdiContext.getClass())) return true;
		if (XdiEntityInstanceUnordered.Variable.class.isAssignableFrom(xdiVariable.getClass()) && XdiEntityInstanceUnordered.class.isAssignableFrom(xdiContext.getClass())) return true;
		if (XdiAttributeInstanceOrdered.Variable.class.isAssignableFrom(xdiVariable.getClass()) && XdiAttributeInstanceOrdered.class.isAssignableFrom(xdiContext.getClass())) return true;
		if (XdiAttributeInstanceUnordered.Variable.class.isAssignableFrom(xdiVariable.getClass()) && XdiAttributeInstanceUnordered.class.isAssignableFrom(xdiContext.getClass())) return true;

		if (XdiEntitySingleton.Variable.class.isAssignableFrom(xdiVariable.getClass()) && XdiEntitySingleton.class.isAssignableFrom(xdiContext.getClass())) return true;
		if (XdiAttributeSingleton.Variable.class.isAssignableFrom(xdiVariable.getClass()) && XdiAttributeSingleton.class.isAssignableFrom(xdiContext.getClass())) return true;

		return false;
	}

	public static boolean matches(XDIAddress XDIvariableAddress, XDIArc XDIaddress) {

		return XDIConstants.XDI_ADD_COMMON_VARIABLE.equals(XDIvariableAddress);
	}
}
