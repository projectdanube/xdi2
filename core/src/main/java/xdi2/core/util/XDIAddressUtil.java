package xdi2.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
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
	public static XDIAddress startsWithXDIAddress(final XDIAddress address, final XDIAddress startAddress, final boolean variablesInAddress, final boolean variablesInStart) {

		if (address == null) throw new NullPointerException();
		if (startAddress == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			if (startAddress.equals(XDIConstants.XDI_ADD_ROOT)) { result = XDIConstants.XDI_ADD_ROOT; return result; }
			if (address.equals(XDIConstants.XDI_ADD_ROOT)) { result = null; return result; }

			int addressIndex = 0, startIndex = 0;

			while (true) {

				if (startIndex == startAddress.getNumXDIArcs()) { result = XDIAddressUtil.parentXDIAddress(address, addressIndex); return result; }
				if (addressIndex == address.getNumXDIArcs()) { result = null; return result; }

				// check variables

				if (variablesInAddress && VariableUtil.isVariable(address.getXDIArc(addressIndex))) {

					if (VariableUtil.matches(address.getXDIArc(addressIndex), startAddress.getXDIArc(startIndex))) {

						startIndex++;

						if (VariableUtil.isMultiple(address.getXDIArc(addressIndex))) {

							while (true) {

								if (startIndex == startAddress.getNumXDIArcs()) break;
								if (! VariableUtil.matches(address.getXDIArc(addressIndex), startAddress.getXDIArc(startIndex))) break;
								if (addressIndex + 1 < address.getNumXDIArcs() && address.getXDIArc(addressIndex + 1).equals(startAddress.getXDIArc(startIndex))) break;

								startIndex++;
							}
						}

						addressIndex++;

						continue;
					} else {

						{ result = null; return result; }
					}
				}

				if (variablesInStart && VariableUtil.isVariable(startAddress.getXDIArc(startIndex))) {

					if (VariableUtil.matches(startAddress.getXDIArc(startIndex), address.getXDIArc(addressIndex))) {

						addressIndex++;

						if (VariableUtil.isMultiple(startAddress.getXDIArc(startIndex))) {

							while (true) {

								if (addressIndex == address.getNumXDIArcs()) break;
								if (! VariableUtil.matches(startAddress.getXDIArc(startIndex), address.getXDIArc(addressIndex))) break;
								if (startIndex + 1 < startAddress.getNumXDIArcs() && address.getXDIArc(addressIndex).equals(startAddress.getXDIArc(startIndex + 1))) break;

								addressIndex++;
							}
						}

						startIndex++;

						continue;
					} else {

						{ result = null; return result; }
					}
				}

				// no variables? just match the arc

				if (! (address.getXDIArc(addressIndex).equals(startAddress.getXDIArc(startIndex)))) { result = null; return result; }

				addressIndex++;
				startIndex++;
			}
		} finally {

			if (log.isTraceEnabled()) log.trace("startsWithXDIAddress(" + address + "," + startAddress + "," + variablesInAddress + "," + variablesInStart + ") --> " + result);
		}
	}

	/**
	 * Checks if an address starts with a certain other address.
	 */
	public static XDIAddress startsWithXDIAddress(XDIAddress address, XDIAddress startAddress) {

		return startsWithXDIAddress(address, startAddress, false, false);
	}

	/**
	 * Checks if an address ends with a certain other address.
	 */
	public static XDIAddress endsWithXDIAddress(final XDIAddress address, final XDIAddress endAddress, final boolean variablesInAddress, final boolean variablesInEnd) {

		if (address == null) throw new NullPointerException();
		if (endAddress == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			if (endAddress.equals(XDIConstants.XDI_ADD_ROOT)) { result = XDIConstants.XDI_ADD_ROOT; return result; }
			if (address.equals(XDIConstants.XDI_ADD_ROOT)) { result = null; return result; }

			int addressIndex = address.getNumXDIArcs() - 1, endIndex = endAddress.getNumXDIArcs() - 1;

			while (true) {

				if (endIndex == -1) { result = XDIAddressUtil.localXDIAddress(address, - addressIndex - 1); return result; }
				if (addressIndex == -1) { result = null; return result; }

				// check variables

				if (variablesInAddress && VariableUtil.isVariable(address.getXDIArc(addressIndex))) {

					if (VariableUtil.matches(address.getXDIArc(addressIndex), endAddress.getXDIArc(endIndex))) {

						endIndex--;

						if (VariableUtil.isMultiple(address.getXDIArc(addressIndex))) {

							while (true) {

								if (endIndex == -1) break;
								if (! VariableUtil.matches(address.getXDIArc(addressIndex), endAddress.getXDIArc(endIndex))) break;
								if (addressIndex - 1 > -1 && address.getXDIArc(addressIndex - 1).equals(endAddress.getXDIArc(endIndex))) break;

								endIndex--;
							}
						}

						addressIndex--;

						continue;
					} else {

						{ result = null; return result; }
					}
				}

				if (variablesInEnd && VariableUtil.isVariable(endAddress.getXDIArc(endIndex))) {

					if (VariableUtil.matches(endAddress.getXDIArc(endIndex), address.getXDIArc(addressIndex))) {

						addressIndex--;

						if (VariableUtil.isMultiple(endAddress.getXDIArc(endIndex))) {

							while (true) {

								if (addressIndex == -1) break;
								if (! VariableUtil.matches(endAddress.getXDIArc(endIndex), address.getXDIArc(addressIndex))) break;
								if (endIndex - 1 > -1 && address.getXDIArc(addressIndex).equals(endAddress.getXDIArc(endIndex - 1))) break;

								addressIndex--;
							}
						}

						endIndex--;

						continue;
					} else {

						{ result = null; return result; }
					}
				}

				// no variables? just match the arc

				if (! (address.getXDIArc(addressIndex).equals(endAddress.getXDIArc(endIndex)))) { result = null; return result; }

				addressIndex--;
				endIndex--;
			}
		} finally {

			if (log.isTraceEnabled()) log.trace("endsWithXDIAddress(" + address + "," + endAddress + "," + variablesInAddress + "," + variablesInEnd + ") --> " + result);
		}
	}

	/**
	 * Checks if an address ends with a certain other address.
	 */
	public static XDIAddress endsWithXDIAddress(final XDIAddress address, final XDIAddress endAddress) {

		return endsWithXDIAddress(address, endAddress, false, false);
	}

	/**
	 * Extracts an address's parent arc(s), counting either from the start or the end.
	 * For =a*b*c*d and 1, this returns =a
	 * For =a*b*c*d and -1, this returns =a*b*c
	 */
	public static XDIAddress parentXDIAddress(final XDIAddress address, final int numArcs) {

		if (address == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			List<XDIArc> arcs = new ArrayList<XDIArc> ();

			if (numArcs > 0) {

				for (int i = 0; i < numArcs; i++) arcs.add(address.getXDIArc(i));
			} else if (numArcs < 0) {

				for (int i = 0; i < address.getNumXDIArcs() - (- numArcs); i++) arcs.add(address.getXDIArc(i));
			} else {

				{ result = address; return result; }
			}

			if (arcs.size() == 0) { result = null; return result; }

			{ result = XDIAddress.fromComponents(arcs); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("parentXDIAddress(" + address + "," + numArcs + ") --> " + result);
		}
	}

	/**
	 * Extracts an address's local arc(s).
	 * For =a*b*c*d and 1, this returns *d
	 * For =a*b*c*d and -1, this returns *b*c*d
	 */
	public static XDIAddress localXDIAddress(final XDIAddress address, final int numArcs) {

		if (address == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			List<XDIArc> arcs = new ArrayList<XDIArc> ();

			if (numArcs > 0) {

				for (int i = address.getNumXDIArcs() - numArcs; i < address.getNumXDIArcs(); i++) arcs.add(address.getXDIArc(i));
			} else if (numArcs < 0) {

				for (int i = (- numArcs); i < address.getNumXDIArcs(); i++) arcs.add(address.getXDIArc(i));
			} else {

				{ result = address; return address; }
			}

			if (arcs.size() == 0) { result = null; return result; }

			{ result = XDIAddress.fromComponents(arcs); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("localXDIAddress(" + address + "," + numArcs + ") --> " + result);
		}
	}

	/**
	 * Extracts an address from an address.
	 * For =a*b*c*d and 1, this returns *d
	 * For =a*b*c*d and -1, this returns *b*c*d
	 */
	public static XDIAddress subXDIAddress(final XDIAddress address, final int startIndex, final int endIndex) {

		if (address == null) throw new NullPointerException();

		return XDIAddressUtil.localXDIAddress(XDIAddressUtil.parentXDIAddress(address, endIndex), - startIndex);
	}

	/**
	 * Get the index of an arc inside an address.
	 * For =a*b*c*d and *b, this returns =a*b
	 * For =a*b*c*d and *c, this returns =a*b*c
	 * For =a*b*c*d and *x, this returns null
	 */
	public static int indexOfXDIArc(final XDIAddress address, final XDIArc search) {

		if (address == null) throw new NullPointerException();
		if (search == null) throw new NullPointerException();

		for (int i=0; i<address.getNumXDIArcs(); i++) {

			XDIArc arc = address.getXDIArc(i);

			if (arc.equals(search)) return i;
		}

		return -1;
	}

	/**
	 * Get the last index of an arc inside an address.
	 * For =a*b*c*d and *b, this returns *b*c*d
	 * For =a*b*c*d and *c, this returns *c*d
	 * For =a*b*c*d and *x, this returns null
	 */
	public static int lastIndexOfXDIArc(final XDIAddress address, final XDIArc search) {

		if (address == null) throw new NullPointerException();
		if (search == null) throw new NullPointerException();

		for (int i=address.getNumXDIArcs()-1; i>=0; i--) {

			XDIArc arc = address.getXDIArc(i);

			if (arc.equals(search)) return i;
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
	public static XDIAddress removeStartXDIAddress(final XDIAddress address, final XDIAddress start, final boolean variablesInAddress, final boolean variablesInStart) {

		if (address == null) throw new NullPointerException();
		if (start == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			if (start.equals(XDIConstants.XDI_ADD_ROOT)) { result = address; return result; }
			if (address.equals(XDIConstants.XDI_ADD_ROOT)) { result = null; return result; }

			XDIAddress startAddress = XDIAddressUtil.startsWithXDIAddress(address, start, variablesInAddress, variablesInStart);
			if (startAddress == null) { result = null; return result; }

			if (address.equals(startAddress)) { result = XDIConstants.XDI_ADD_ROOT; return result; }

			{ result = XDIAddressUtil.localXDIAddress(address, - startAddress.getNumXDIArcs()); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("removeStartXDIAddress(" + address + "," + start + "," + variablesInAddress + "," + variablesInStart + ") --> " + result);
		}
	}

	/**
	 * Removes a start address from an address.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns ()
	 * E.g. for =a*b*c*d and =x, this returns null
	 */
	public static XDIAddress removeStartXDIAddress(final XDIAddress address, final XDIAddress start) {

		return removeStartXDIAddress(address, start, false, false);
	}

	/**
	 * Removes an end address from an address.
	 * E.g. for =a*b*c*d and *c*d, this returns =a*b
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns (empty address)
	 * E.g. for =a*b*c*d and *y, this returns null
	 */
	public static XDIAddress removeEndXDIAddress(final XDIAddress address, final XDIAddress end, final boolean variablesInAddress, final boolean variablesInEnd) {

		if (address == null) throw new NullPointerException();
		if (end == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			if (end.equals(XDIConstants.XDI_ADD_ROOT)) { result = address; return result; }
			if (address.equals(XDIConstants.XDI_ADD_ROOT)) { result = null; return result; }

			XDIAddress endAddress = XDIAddressUtil.endsWithXDIAddress(address, end, variablesInAddress, variablesInEnd);
			if (endAddress == null) { result = null; return result; }

			if (address.equals(endAddress)) { result = XDIConstants.XDI_ADD_ROOT; return result; }

			{ result = XDIAddressUtil.parentXDIAddress(address, - endAddress.getNumXDIArcs()); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("removeEndXDIAddress(" + address + "," + end + "," + variablesInAddress + "," + variablesInEnd + ") --> " + result);
		}
	}

	/**
	 * Removes an end address from an address.
	 * E.g. for =a*b*c*d and *c*d, this returns =a*b
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns ()
	 * E.g. for =a*b*c*d and *y, this returns null
	 */
	public static XDIAddress removeEndXDIAddress(final XDIAddress address, final XDIAddress end) {

		return removeEndXDIAddress(address, end, false, false);
	}

	/**
	 * Replaces all occurences of an arc with an address.
	 */
	public static XDIAddress replaceXDIAddress(final XDIAddress address, final XDIArc oldArc, final XDIAddress newAddress) {

		if (address == null) throw new NullPointerException();
		if (oldArc == null) throw new NullPointerException();
		if (newAddress == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			List<XDIArc> arcs = new ArrayList<XDIArc> ();

			for (XDIArc arc : address.getXDIArcs()) {

				if (arc.equals(oldArc)) {

					arcs.addAll(newAddress.getXDIArcs());

					continue;
				}

				if (arc.hasXRef() && arc.getXRef().hasXDIAddress()) {

					XDIAddress xRefAddress = arc.getXRef().getXDIAddress();

					xRefAddress = replaceXDIAddress(xRefAddress, oldArc, newAddress);

					arcs.add(XDIArc.fromComponents(arc.getCs(), arc.isClassXs(), arc.isAttributeXs(), null, XDIXRef.fromComponents(arc.getXRef().getXs(), xRefAddress, null, null, null, null)));

					continue;
				}

				if (arc.hasXRef() && arc.getXRef().hasPartialSubjectAndPredicate()) {

					XDIAddress xRefPartialSubject = arc.getXRef().getPartialSubject();
					XDIAddress xRefPartialPredicate = arc.getXRef().getPartialPredicate();

					xRefPartialSubject = replaceXDIAddress(xRefPartialSubject, oldArc, newAddress);
					xRefPartialPredicate = replaceXDIAddress(xRefPartialPredicate, oldArc, newAddress);

					arcs.add(XDIArc.fromComponents(arc.getCs(), arc.isClassXs(), arc.isAttributeXs(), null, XDIXRef.fromComponents(arc.getXRef().getXs(), null, xRefPartialSubject, xRefPartialPredicate, null, null)));

					continue;
				}

				arcs.add(arc);
			}

			{ result = XDIAddress.fromComponents(arcs); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("replaceAddress(" + address + "," + oldArc + "," + newAddress + ") --> " + result);
		}
	}

	/**
	 * Concats all addresses into a new address.
	 */
	public static XDIAddress concatXDIAddresses(final XDIAddress... addresses) {

		XDIAddress result = null;

		try {

			List<XDIArc> arcs = new ArrayList<XDIArc> ();

			if (addresses != null) {

				for (XDIAddress address : addresses) {

					if (address != null) arcs.addAll(address.getXDIArcs());
				}
			}

			if (arcs.size() == 0) arcs.addAll(XDIConstants.XDI_ADD_ROOT.getXDIArcs());

			{ result = XDIAddress.fromComponents(arcs); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("contactXDIAddresses(" + Arrays.asList(addresses) + ") --> " + result);
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
	public static XDIAddress concatXDIAddresses(final XDIAddress address, final XDIArc arc) {

		return concatXDIAddresses(address, arc == null ? null : XDIAddress.fromComponent(arc));
	}

	/**
	 * Concats address(es) and arc(s) into a new address.
	 */
	public static XDIAddress concatXDIAddresses(final XDIArc arc, final XDIAddress address) {

		return concatXDIAddresses(arc == null ? null : XDIAddress.fromComponent(arc), address);
	}

	/*
	 * Helper classes
	 */

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
