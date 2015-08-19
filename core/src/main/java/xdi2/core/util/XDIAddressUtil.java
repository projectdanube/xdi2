package xdi2.core.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAbstractVariable;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.nodetypes.XdiVariable;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIXRef;

/**
 * Various utility methods for working with XDI addresses.
 * 
 * @author markus
 */
public final class XDIAddressUtil {

	private static final Logger log = LoggerFactory.getLogger(XDIAddressUtil.class);

	private XDIAddressUtil() { }

	/**
	 * Checks if an address starts with a certain other address.
	 */
	public static XDIAddress startsWithXDIAddress(final XDIAddress XDIaddress, final XDIAddress startXDIAddress, final boolean variablesinXDIAddress, final boolean variablesInStart) {

		if (XDIaddress == null) throw new NullPointerException();
		if (startXDIAddress == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			if (startXDIAddress.equals(XDIConstants.XDI_ADD_ROOT)) { result = XDIConstants.XDI_ADD_ROOT; return result; }
			if (XDIaddress.equals(XDIConstants.XDI_ADD_ROOT)) { result = null; return result; }

			MatchPosition startPosition = new MatchPosition(startXDIAddress, true);
			MatchPosition addressPosition = new MatchPosition(XDIaddress, true);

			while (true) {

				if (startPosition.done()) { result = addressPosition.result(); return result; }
				if (addressPosition.done()) { result = null; return result; }

				XdiVariable<?> xdiVariable;
				XdiContext<?> xdiContext;

				// try to match variable in address

				if (variablesinXDIAddress && (xdiVariable = addressPosition.toVariable()) != null) {

					xdiContext = startPosition.toContext();

					if (VariableUtil.matches(xdiVariable, xdiContext)) {

						startPosition.next();

						if (VariableUtil.isMultiple(xdiVariable)) {

							while (true) {

								if (startPosition.done()) break;
								xdiContext = startPosition.toContext();
								if (! VariableUtil.matches(xdiVariable, xdiContext)) break;
								if (startPosition.matchesNext(addressPosition)) break;

								startPosition.next();
							}
						}

						addressPosition.next();
						continue;
					}
				}

				// try to match variable in start

				if (variablesInStart && (xdiVariable = startPosition.toVariable()) != null) {

					xdiContext = addressPosition.toContext();

					if (VariableUtil.matches(xdiVariable, xdiContext)) {

						addressPosition.next();

						if (VariableUtil.isMultiple(xdiVariable)) {

							while (true) {

								if (addressPosition.done()) break;
								xdiContext = addressPosition.toContext();
								if (! VariableUtil.matches(xdiVariable, xdiContext)) break;
								if (addressPosition.matchesNext(startPosition)) break;

								addressPosition.next();
							}
						}

						startPosition.next();
						continue;
					}
				}

				// try to match the arc

				if (! addressPosition.matchesCurrent(startPosition)) { result = null; return result; }

				addressPosition.next();
				startPosition.next();
			}
		} finally {

			if (log.isTraceEnabled()) log.trace("startsWithXDIAddress(" + XDIaddress + "," + startXDIAddress + "," + variablesinXDIAddress + "," + variablesInStart + ") --> " + result);
		}
	}

	/**
	 * Checks if an address starts with a certain other address.
	 */
	public static XDIAddress startsWithXDIAddress(XDIAddress XDIaddress, XDIAddress startXDIAddress) {

		return startsWithXDIAddress(XDIaddress, startXDIAddress, false, false);
	}

	/**
	 * Checks if an address ends with a certain other address.
	 */
	public static XDIAddress endsWithXDIAddress(final XDIAddress XDIaddress, final XDIAddress endXDIAddress, final boolean variablesinXDIAddress, final boolean variablesInEnd) {

		if (XDIaddress == null) throw new NullPointerException();
		if (endXDIAddress == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			if (endXDIAddress.equals(XDIConstants.XDI_ADD_ROOT)) { result = XDIConstants.XDI_ADD_ROOT; return result; }
			if (XDIaddress.equals(XDIConstants.XDI_ADD_ROOT)) { result = null; return result; }

			MatchPosition addressPosition = new MatchPosition(XDIaddress, false);
			MatchPosition endPosition = new MatchPosition(endXDIAddress, false);

			while (true) {

				if (endPosition.done()) { result = addressPosition.result(); return result; }
				if (addressPosition.done()) { result = null; return result; }

				XdiVariable<?> xdiVariable;
				XdiContext<?> xdiContext;

				// try to match variable in address

				if (variablesinXDIAddress && (xdiVariable = addressPosition.toVariable()) != null) {

					xdiContext = endPosition.toContext();

					if (VariableUtil.matches(xdiVariable, xdiContext)) {

						endPosition.next();

						if (VariableUtil.isMultiple(xdiVariable)) {

							while (true) {

								if (endPosition.done()) break;
								xdiContext = endPosition.toContext();
								if (! VariableUtil.matches(xdiVariable, xdiContext)) break;
								if (endPosition.matchesNext(addressPosition)) break;

								endPosition.next();
							}
						}

						addressPosition.next();
						continue;
					} else {

						{ result = null; return result; }
					}
				}

				// try to match variable in end

				if (variablesInEnd && (xdiVariable = endPosition.toVariable()) != null) {

					xdiContext = addressPosition.toContext();

					if (VariableUtil.matches(xdiVariable, xdiContext)) {

						addressPosition.next();

						if (VariableUtil.isMultiple(xdiVariable)) {

							while (true) {

								if (addressPosition.done()) break;
								xdiContext = addressPosition.toContext();
								if (! VariableUtil.matches(xdiVariable, xdiContext)) break;
								if (addressPosition.matchesNext(endPosition)) break;

								addressPosition.next();
							}
						}

						endPosition.next();
						continue;
					} else {

						{ result = null; return result; }
					}
				}

				// try to match the arc

				if (! addressPosition.matchesCurrent(endPosition)) { result = null; return result; }

				addressPosition.next();
				endPosition.next();
			}
		} finally {

			if (log.isTraceEnabled()) log.trace("endsWithXDIAddress(" + XDIaddress + "," + endXDIAddress + "," + variablesinXDIAddress + "," + variablesInEnd + ") --> " + result);
		}
	}

	/**
	 * Checks if an address ends with a certain other address.
	 */
	public static XDIAddress endsWithXDIAddress(final XDIAddress XDIaddress, final XDIAddress endXDIAddress) {

		return endsWithXDIAddress(XDIaddress, endXDIAddress, false, false);
	}

	/**
	 * Extracts an address's parent arc(s), counting either from the start or the end.
	 * For =a*b*c*d and 1, this returns =a
	 * For =a*b*c*d and -1, this returns =a*b*c
	 */
	public static XDIAddress parentXDIAddress(final XDIAddress XDIaddress, final int numXDIArcs) {

		if (XDIaddress == null) throw new NullPointerException();
		if (XDIaddress.getNumXDIArcs() == numXDIArcs) return XDIaddress;
		if (XDIaddress.getNumXDIArcs() == - numXDIArcs) return XDIConstants.XDI_ADD_ROOT;

		XDIAddress result = null;

		try {

			List<XDIArc> XDIarcs = new ArrayList<XDIArc> ();

			if (numXDIArcs > 0) {

				for (int i = 0; i < numXDIArcs; i++) XDIarcs.add(XDIaddress.getXDIArc(i));
			} else if (numXDIArcs < 0) {

				for (int i = 0; i < XDIaddress.getNumXDIArcs() - (- numXDIArcs); i++) XDIarcs.add(XDIaddress.getXDIArc(i));
			} else {

				{ result = XDIaddress; return result; }
			}

			if (XDIarcs.size() == 0) { result = null; return result; }

			{ result = XDIAddress.fromComponents(XDIarcs); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("parentXDIAddress(" + XDIaddress + "," + numXDIArcs + ") --> " + result);
		}
	}

	/**
	 * Extracts an address's local arc(s).
	 * For =a*b*c*d and 1, this returns *d
	 * For =a*b*c*d and -1, this returns *b*c*d
	 */
	public static XDIAddress localXDIAddress(final XDIAddress XDIaddress, final int numXDIArcs) {

		if (XDIaddress == null) throw new NullPointerException();
		if (XDIaddress.getNumXDIArcs() == numXDIArcs) return XDIaddress;
		if (XDIaddress.getNumXDIArcs() == - numXDIArcs) return XDIConstants.XDI_ADD_ROOT;

		XDIAddress result = null;

		try {

			List<XDIArc> XDIarcs = new ArrayList<XDIArc> ();

			if (numXDIArcs > 0) {

				for (int i = XDIaddress.getNumXDIArcs() - numXDIArcs; i < XDIaddress.getNumXDIArcs(); i++) XDIarcs.add(XDIaddress.getXDIArc(i));
			} else if (numXDIArcs < 0) {

				for (int i = (- numXDIArcs); i < XDIaddress.getNumXDIArcs(); i++) XDIarcs.add(XDIaddress.getXDIArc(i));
			} else {

				{ result = XDIaddress; return XDIaddress; }
			}

			if (XDIarcs.size() == 0) { result = null; return result; }

			{ result = XDIAddress.fromComponents(XDIarcs); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("localXDIAddress(" + XDIaddress + "," + numXDIArcs + ") --> " + result);
		}
	}

	/**
	 * Extracts an partial address from an address.
	 */
	public static XDIAddress subXDIAddress(final XDIAddress XDIaddress, final int startIndex, final int endIndex) {

		if (XDIaddress == null) throw new NullPointerException();

		return XDIAddressUtil.localXDIAddress(XDIAddressUtil.parentXDIAddress(XDIaddress, endIndex), - startIndex);
	}

	/**
	 * Finds a part of an XDI address that matches a certain node type.
	 */
	public static <X extends XdiContext<?>> XDIAddress extractXDIAddress(XDIAddress XDIaddress, Class<? extends X>[] clazzes, boolean keepParent, boolean keepLocal) {

		try {

			ContextNode contextNode = GraphUtil.contextNodeFromComponents(XDIaddress);
			XdiContext<?> xdiContext = null;

			List<XDIArc> XDIarcs = null;
			List<XDIArc> XDIparentArcs = new ArrayList<XDIArc> ();
			List<XDIArc> XDIlocalArcs = new ArrayList<XDIArc> ();

			while (contextNode != null) {

				xdiContext = XdiAbstractContext.fromContextNode(contextNode);

				boolean found = false;

				for (Class<? extends XdiContext<?>> clazz : clazzes)
					if (clazz.isAssignableFrom(xdiContext.getClass())) found = true;

				if (found) {

					if (XDIarcs == null) XDIarcs = new ArrayList<XDIArc> ();

					if (! contextNode.isRootContextNode()) {

						XDIarcs.add(0, contextNode.getXDIArc());
					}
				} else {

					if (! contextNode.isRootContextNode()) {

						if (XDIarcs == null) {

							XDIlocalArcs.add(0, contextNode.getXDIArc());
						} else {

							XDIparentArcs.add(0, contextNode.getXDIArc());
						}
					}
				}

				contextNode = contextNode.getContextNode();
			}

			if (XDIarcs == null) return null;

			if (keepParent) XDIarcs.addAll(0, XDIparentArcs);
			if (keepLocal) XDIarcs.addAll(XDIlocalArcs);

			return XDIAddress.fromComponents(XDIarcs);
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Unexpected reflect error: " + ex.getMessage(), ex);
		}
	}

	public static <X extends XdiContext<?>> XDIAddress extractXDIAddress(XDIAddress XDIaddress, Class<X> clazz, boolean keepParent, boolean keepLocal) {

		@SuppressWarnings("unchecked")
		Class<? extends XdiContext<?>>[] clazzes = (Class<? extends XdiContext<?>>[]) Array.newInstance(clazz.getClass(), 1);
		clazzes[0] = clazz;

		return extractXDIAddress(XDIaddress, clazzes, keepParent, keepLocal);
	}

	/**
	 * Finds a part of an XDI address that does not match a certain node type.
	 */
	/*	public static XDIAddress findNotXDIAddress(XDIAddress XDIaddress, Class<? extends XdiContext<?>>[] clazzes) {

		if (XDIaddress == null) throw new NullPointerException();

		try {

			ContextNode contextNode = GraphUtil.contextNodeFromComponents(XDIaddress);
			XdiContext<?> xdiContext = null;

			while (contextNode != null) {

				xdiContext = XdiAbstractContext.fromContextNode(contextNode);

				boolean found = false;

				for (Class<? extends XdiContext<?>> clazz : clazzes)
					if (clazz.isAssignableFrom(xdiContext.getClass())) found = true;

				if (! found) return contextNode.getXDIAddress();

				contextNode = contextNode.getContextNode();
			}

			return null;
		} catch (Exception ex) {

			throw new Xdi2RuntimeException("Unexpected reflect error: " + ex.getMessage(), ex);
		}
	}

	public static <X extends XdiContext<?>> XDIAddress findNotXDIAddress(XDIAddress XDIaddress, Class<X> clazz) {

		@SuppressWarnings("unchecked")
		Class<? extends XdiContext<?>>[] clazzes = (Class<? extends XdiContext<?>>[]) Array.newInstance(clazz.getClass(), 1);
		clazzes[0] = clazz;

		return findNotXDIAddress(XDIaddress, clazzes);
	}*/

	/**
	 * Get the index of an arc inside an address.
	 * For =a*b*c*d and *b, this returns =a*b
	 * For =a*b*c*d and *c, this returns =a*b*c
	 * For =a*b*c*d and *x, this returns null
	 */
	public static int indexOfXDIArc(final XDIAddress XDIaddress, final XDIArc searchXDIArc) {

		if (XDIaddress == null) throw new NullPointerException();
		if (searchXDIArc == null) throw new NullPointerException();

		for (int i=0; i<XDIaddress.getNumXDIArcs(); i++) {

			XDIArc XDIarc = XDIaddress.getXDIArc(i);

			if (XDIarc.equals(searchXDIArc)) return i;
		}

		return -1;
	}

	/**
	 * Get the last index of an arc inside an address.
	 * For =a*b*c*d and *b, this returns *b*c*d
	 * For =a*b*c*d and *c, this returns *c*d
	 * For =a*b*c*d and *x, this returns null
	 */
	public static int lastIndexOfXDIArc(final XDIAddress XDIaddress, final XDIArc searchXDIArc) {

		if (XDIaddress == null) throw new NullPointerException();
		if (searchXDIArc == null) throw new NullPointerException();

		for (int i=XDIaddress.getNumXDIArcs()-1; i>=0; i--) {

			XDIArc XDIarc = XDIaddress.getXDIArc(i);

			if (XDIarc.equals(searchXDIArc)) return i;
		}

		return -1;
	}

	/**
	 * Removes a start address from an address.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 * E.g. for =a*b*c*d and (empty address), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns (empty address)
	 * E.g. for =a*b*c*d and =x, this returns null
	 */
	public static XDIAddress removeStartXDIAddress(final XDIAddress XDIaddress, final XDIAddress startXDIAddress, final boolean variablesinXDIAddress, final boolean variablesInStart) {

		if (XDIaddress == null) throw new NullPointerException();
		if (startXDIAddress == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			if (startXDIAddress.equals(XDIConstants.XDI_ADD_ROOT)) { result = XDIaddress; return result; }
			if (XDIaddress.equals(XDIConstants.XDI_ADD_ROOT)) { result = null; return result; }

			XDIAddress foundXDIAddress = XDIAddressUtil.startsWithXDIAddress(XDIaddress, startXDIAddress, variablesinXDIAddress, variablesInStart);
			if (foundXDIAddress == null) { result = null; return result; }

			if (XDIaddress.equals(foundXDIAddress)) { result = XDIConstants.XDI_ADD_ROOT; return result; }

			{ result = XDIAddressUtil.localXDIAddress(XDIaddress, - foundXDIAddress.getNumXDIArcs()); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("removeStartXDIAddress(" + XDIaddress + "," + startXDIAddress + "," + variablesinXDIAddress + "," + variablesInStart + ") --> " + result);
		}
	}

	/**
	 * Removes a start address from an address.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns ()
	 * E.g. for =a*b*c*d and =x, this returns null
	 */
	public static XDIAddress removeStartXDIAddress(final XDIAddress XDIaddress, final XDIAddress startXDIAddress) {

		return removeStartXDIAddress(XDIaddress, startXDIAddress, false, false);
	}

	/**
	 * Removes an end address from an address.
	 * E.g. for =a*b*c*d and *c*d, this returns =a*b
	 * E.g. for =a*b*c*d and (empty address), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns (empty address)
	 * E.g. for =a*b*c*d and *y, this returns null
	 */
	public static XDIAddress removeEndXDIAddress(final XDIAddress XDIaddress, final XDIAddress endXDIAddress, final boolean variablesinXDIAddress, final boolean variablesInEnd) {

		if (XDIaddress == null) throw new NullPointerException();
		if (endXDIAddress == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			if (endXDIAddress.equals(XDIConstants.XDI_ADD_ROOT)) { result = XDIaddress; return result; }
			if (XDIaddress.equals(XDIConstants.XDI_ADD_ROOT)) { result = null; return result; }

			XDIAddress foundXDIAddress = XDIAddressUtil.endsWithXDIAddress(XDIaddress, endXDIAddress, variablesinXDIAddress, variablesInEnd);
			if (foundXDIAddress == null) { result = null; return result; }

			if (XDIaddress.equals(foundXDIAddress)) { result = XDIConstants.XDI_ADD_ROOT; return result; }

			{ result = XDIAddressUtil.parentXDIAddress(XDIaddress, - foundXDIAddress.getNumXDIArcs()); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("removeEndXDIAddress(" + XDIaddress + "," + endXDIAddress + "," + variablesinXDIAddress + "," + variablesInEnd + ") --> " + result);
		}
	}

	/**
	 * Removes an end address from an address.
	 * E.g. for =a*b*c*d and *c*d, this returns =a*b
	 * E.g. for =a*b*c*d and (empty address), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns (empty address)
	 * E.g. for =a*b*c*d and *y, this returns null
	 */
	public static XDIAddress removeEndXDIAddress(final XDIAddress XDIaddress, final XDIAddress endXDIAddress) {

		return removeEndXDIAddress(XDIaddress, endXDIAddress, false, false);
	}

	/**
	 * Replaces all occurrences of an arc with an address.
	 */
	public static XDIAddress replaceXDIAddress(final XDIAddress XDIaddress, final XDIArc oldXDIArc, XDIAddress newXDIAddress) {

		if (XDIaddress == null) throw new NullPointerException();
		if (oldXDIArc == null) throw new NullPointerException();

		if (newXDIAddress == null) newXDIAddress = XDIConstants.XDI_ADD_ROOT;

		XDIAddress result = null;

		try {

			List<XDIArc> XDIarcs = new ArrayList<XDIArc> ();

			for (XDIArc XDIarc : XDIaddress.getXDIArcs()) {

				if (XDIarc.equals(oldXDIArc)) {

					XDIarcs.addAll(newXDIAddress.getXDIArcs());

					continue;
				}

				if (XDIarc.hasXRef() && XDIarc.getXRef().hasXDIArc()) {

					XDIArc xrefXDIArc = XDIarc.getXRef().getXDIArc();

					XDIAddress replacedXrefXDIAddress = replaceXDIAddress(xrefXDIArc, oldXDIArc, newXDIAddress);

					for (XDIArc replacedXrefXDIArc : replacedXrefXDIAddress.getXDIArcs()) {

						XDIarcs.add(XDIArc.fromComponents(XDIarc.getCs(), XDIarc.isVariable(), XDIarc.isDefinition(), XDIarc.isCollection(), XDIarc.isAttribute(), XDIarc.isImmutable(), XDIarc.isRelative(), null, XDIXRef.fromComponents(XDIarc.getXRef().getXs(), replacedXrefXDIArc, null, null, null, null)));
					}

					continue;
				}

				if (XDIarc.hasXRef() && XDIarc.getXRef().hasPartialSubjectAndPredicate()) {

					XDIAddress xrefPartialSubject = XDIarc.getXRef().getPartialSubject();
					XDIAddress xrefPartialPredicate = XDIarc.getXRef().getPartialPredicate();

					XDIAddress replacedXrefPartialSubject = replaceXDIAddress(xrefPartialSubject, oldXDIArc, newXDIAddress);
					XDIAddress replacedXrefPartialPredicate = replaceXDIAddress(xrefPartialPredicate, oldXDIArc, newXDIAddress);

					XDIarcs.add(XDIArc.fromComponents(XDIarc.getCs(), XDIarc.isVariable(), XDIarc.isDefinition(), XDIarc.isCollection(), XDIarc.isAttribute(), XDIarc.isImmutable(), XDIarc.isRelative(), null, XDIXRef.fromComponents(XDIarc.getXRef().getXs(), null, replacedXrefPartialSubject, replacedXrefPartialPredicate, null, null)));

					continue;
				}

				XDIarcs.add(XDIarc);
			}

			{ result = XDIAddress.fromComponents(XDIarcs); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("replaceAddress(" + XDIaddress + "," + oldXDIArc + "," + newXDIAddress + ") --> " + result);
		}
	}

	/**
	 * Replaces all occurrences of an arc with an address.
	 */
	public static XDIAddress replaceXDIAddress(final XDIArc XDIarc, final XDIArc oldXDIArc, XDIAddress newXDIAddress) {

		XDIAddress XDIaddress = XDIarc == null ? null : XDIAddress.fromComponent(XDIarc);

		return replaceXDIAddress(XDIaddress, oldXDIArc, newXDIAddress);
	}

	/**
	 * Replaces all occurrences of an arc with an arc.
	 */
	public static XDIAddress replaceXDIAddress(final XDIAddress XDIaddress, final XDIArc oldXDIArc, final XDIArc newXDIArc) {

		XDIAddress newXDIAddress = newXDIArc == null ? null : XDIAddress.fromComponent(newXDIArc);

		return replaceXDIAddress(XDIaddress, oldXDIArc, newXDIAddress);
	}

	/**
	 * Concats all addresses into a new address.
	 */
	public static XDIAddress concatXDIAddresses(final XDIAddress... XDIaddresses) {

		XDIAddress result = null;

		try {

			List<XDIArc> XDIarcs = new ArrayList<XDIArc> ();

			if (XDIaddresses != null) {

				for (XDIAddress XDIaddress : XDIaddresses) {

					if (XDIaddress != null) XDIarcs.addAll(XDIaddress.getXDIArcs());
				}
			}

			if (XDIarcs.size() == 0) XDIarcs.addAll(XDIConstants.XDI_ADD_ROOT.getXDIArcs());

			{ result = XDIAddress.fromComponents(XDIarcs); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("concatXDIAddresses(" + Arrays.asList(XDIaddresses) + ") --> " + result);
		}
	}

	/**
	 * Concats address(es) and arc(s) into a new address.
	 */
	public static XDIAddress concatXDIAddresses(final XDIArc... arcs) {

		XDIAddress[] addresses = new XDIAddress[arcs.length];
		for (int i=0; i<arcs.length; i++) addresses[i] = XDIAddress.fromComponent(arcs[i]);

		return concatXDIAddresses(addresses);
	}

	/**
	 * Concats address(es) and arc(s) into a new address.
	 */
	public static XDIAddress concatXDIAddresses(final XDIAddress XDIaddress, final XDIArc XDIarc) {

		return concatXDIAddresses(XDIaddress, XDIarc == null ? null : XDIAddress.fromComponent(XDIarc));
	}

	/**
	 * Concats address(es) and arc(s) into a new address.
	 */
	public static XDIAddress concatXDIAddresses(final XDIArc XDIarc, final XDIAddress XDIaddress) {

		return concatXDIAddresses(XDIarc == null ? null : XDIAddress.fromComponent(XDIarc), XDIaddress);
	}

	/*
	 * Helper classes
	 */

	private static final class MatchPosition {

		private XDIAddress XDIaddress;
		private boolean forward;

		private int position;
		private XDIAddress positionXDIAddress;
		private XdiContext<?> positionXdiContext;
		private XdiVariable<?> positionXdiVariable;

		private MatchPosition(XDIAddress XDIaddress, boolean forward) {

			this.XDIaddress = XDIaddress;
			this.forward = forward;

			this.position = forward ? 0 : XDIaddress.getNumXDIArcs() - 1;
			this.positionXDIAddress = null;
			this.positionXdiContext = null;
			this.positionXdiVariable = null;
		}

		private void next() {

			if (this.forward) this.position++; else this.position--;
			this.positionXDIAddress = null;
			this.positionXdiContext = null;
			this.positionXdiVariable = null;
		}

		private boolean done() {

			return this.forward ? this.position == this.XDIaddress.getNumXDIArcs() : this.position == -1;
		}

		private boolean matchesCurrent(MatchPosition other) {

			XDIArc myXDIArc = this.XDIaddress.getXDIArc(this.position);
			XDIArc otherXDIArc = other.XDIaddress.getXDIArc(other.position);

			return myXDIArc.equals(otherXDIArc);
		}

		private boolean matchesNext(MatchPosition other) {

			if (other.forward && other.position + 1 >= other.XDIaddress.getNumXDIArcs()) return false;
			if (! other.forward && other.position -1 < 0) return false;

			XDIArc myXDIArc = this.XDIaddress.getXDIArc(this.position);
			XDIArc otherXDIArc = other.XDIaddress.getXDIArc(other.forward ? other.position + 1 : other.position - 1);

			return myXDIArc.equals(otherXDIArc);
		}

		private XDIAddress result() {

			return this.forward ?
					XDIAddressUtil.parentXDIAddress(this.XDIaddress, this.position):
						XDIAddressUtil.localXDIAddress(this.XDIaddress, - this.position - 1);						
		}

		private XdiContext<?> toContext() {

			if (this.positionXDIAddress == null) this.positionXDIAddress = XDIAddressUtil.parentXDIAddress(this.XDIaddress, this.position + 1); 
			if (this.positionXdiContext == null) this.positionXdiContext = XdiAbstractContext.fromXDIAddress(this.positionXDIAddress);
			return this.positionXdiContext;
		}

		private XdiVariable<?> toVariable() {

			if (this.positionXDIAddress == null) this.positionXDIAddress = XDIAddressUtil.parentXDIAddress(this.XDIaddress, this.position + 1); 
			if (this.positionXdiVariable == null) this.positionXdiVariable = XdiAbstractVariable.fromXDIAddress(this.positionXDIAddress);
			return this.positionXdiVariable;
		}

		@Override
		public String toString() {

			if (this.positionXDIAddress == null) this.positionXDIAddress = XDIAddressUtil.parentXDIAddress(this.XDIaddress, this.position + 1); 
			return this.positionXDIAddress.toString();
		}
	}

	public static final Comparator<? super XDIAddress> XDIADDRESS_ASCENDING_COMPARATOR = new Comparator<XDIAddress>() {

		@Override
		public int compare(XDIAddress o1, XDIAddress o2) {

			if (o1.getNumXDIArcs() < o2.getNumXDIArcs()) return -1;
			if (o1.getNumXDIArcs() > o2.getNumXDIArcs()) return 1;

			return o1.compareTo(o2);
		}
	};

	public static final Comparator<? super XDIAddress> XDIADDRESS_DESCENDING_COMPARATOR = new Comparator<XDIAddress>() {

		@Override
		public int compare(XDIAddress o1, XDIAddress o2) {

			if (o1.getNumXDIArcs() > o2.getNumXDIArcs()) return -1;
			if (o1.getNumXDIArcs() < o2.getNumXDIArcs()) return 1;

			return o1.compareTo(o2);
		}
	};
}
