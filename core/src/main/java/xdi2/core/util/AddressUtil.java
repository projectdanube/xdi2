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
 * Various utility methods for working with address 3.0 syntax.
 * 
 * @author markus
 */
public final class AddressUtil {

	private static final Logger log = LoggerFactory.getLogger(AddressUtil.class);

	private AddressUtil() { }

	/**
	 * Checks if an address starts with a certain other address.
	 */
	public static XDIAddress startsWith(final XDIAddress address, final XDIAddress startAddress, final boolean variablesInAddress, final boolean variablesInStart) {

		if (address == null) throw new NullPointerException();
		if (startAddress == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			if (startAddress.equals(XDIConstants.XDI_ADD_ROOT)) { result = XDIConstants.XDI_ADD_ROOT; return result; }
			if (address.equals(XDIConstants.XDI_ADD_ROOT)) { result = null; return result; }

			int addressIndex = 0, startIndex = 0;

			while (true) {

				if (startIndex == startAddress.getNumArcs()) { result = AddressUtil.parentAddress(address, addressIndex); return result; }
				if (addressIndex == address.getNumArcs()) { result = null; return result; }

				// check variables

				if (variablesInAddress && VariableUtil.isVariable(address.getArc(addressIndex))) {

					if (VariableUtil.matches(address.getArc(addressIndex), startAddress.getArc(startIndex))) {

						startIndex++;

						if (VariableUtil.isMultiple(address.getArc(addressIndex))) {

							while (true) {

								if (startIndex == startAddress.getNumArcs()) break;
								if (! VariableUtil.matches(address.getArc(addressIndex), startAddress.getArc(startIndex))) break;
								if (addressIndex + 1 < address.getNumArcs() && address.getArc(addressIndex + 1).equals(startAddress.getArc(startIndex))) break;

								startIndex++;
							}
						}

						addressIndex++;

						continue;
					} else {

						{ result = null; return result; }
					}
				}

				if (variablesInStart && VariableUtil.isVariable(startAddress.getArc(startIndex))) {

					if (VariableUtil.matches(startAddress.getArc(startIndex), address.getArc(addressIndex))) {

						addressIndex++;

						if (VariableUtil.isMultiple(startAddress.getArc(startIndex))) {

							while (true) {

								if (addressIndex == address.getNumArcs()) break;
								if (! VariableUtil.matches(startAddress.getArc(startIndex), address.getArc(addressIndex))) break;
								if (startIndex + 1 < startAddress.getNumArcs() && address.getArc(addressIndex).equals(startAddress.getArc(startIndex + 1))) break;

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

				if (! (address.getArc(addressIndex).equals(startAddress.getArc(startIndex)))) { result = null; return result; }

				addressIndex++;
				startIndex++;
			}
		} finally {

			if (log.isTraceEnabled()) log.trace("startsWith(" + address + "," + startAddress + "," + variablesInAddress + "," + variablesInStart + ") --> " + result);
		}
	}

	/**
	 * Checks if an address starts with a certain other address.
	 */
	public static XDIAddress startsWith(XDIAddress address, XDIAddress startAddress) {

		return startsWith(address, startAddress, false, false);
	}

	/**
	 * Checks if an address ends with a certain other address.
	 */
	public static XDIAddress endsWith(final XDIAddress address, final XDIAddress endAddress, final boolean variablesInAddress, final boolean variablesInEnd) {

		if (address == null) throw new NullPointerException();
		if (endAddress == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			if (endAddress.equals(XDIConstants.XDI_ADD_ROOT)) { result = XDIConstants.XDI_ADD_ROOT; return result; }
			if (address.equals(XDIConstants.XDI_ADD_ROOT)) { result = null; return result; }

			int addressIndex = address.getNumArcs() - 1, endIndex = endAddress.getNumArcs() - 1;

			while (true) {

				if (endIndex == -1) { result = AddressUtil.localAddress(address, - addressIndex - 1); return result; }
				if (addressIndex == -1) { result = null; return result; }

				// check variables

				if (variablesInAddress && VariableUtil.isVariable(address.getArc(addressIndex))) {

					if (VariableUtil.matches(address.getArc(addressIndex), endAddress.getArc(endIndex))) {

						endIndex--;

						if (VariableUtil.isMultiple(address.getArc(addressIndex))) {

							while (true) {

								if (endIndex == -1) break;
								if (! VariableUtil.matches(address.getArc(addressIndex), endAddress.getArc(endIndex))) break;
								if (addressIndex - 1 > -1 && address.getArc(addressIndex - 1).equals(endAddress.getArc(endIndex))) break;

								endIndex--;
							}
						}

						addressIndex--;

						continue;
					} else {

						{ result = null; return result; }
					}
				}

				if (variablesInEnd && VariableUtil.isVariable(endAddress.getArc(endIndex))) {

					if (VariableUtil.matches(endAddress.getArc(endIndex), address.getArc(addressIndex))) {

						addressIndex--;

						if (VariableUtil.isMultiple(endAddress.getArc(endIndex))) {

							while (true) {

								if (addressIndex == -1) break;
								if (! VariableUtil.matches(endAddress.getArc(endIndex), address.getArc(addressIndex))) break;
								if (endIndex - 1 > -1 && address.getArc(addressIndex).equals(endAddress.getArc(endIndex - 1))) break;

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

				if (! (address.getArc(addressIndex).equals(endAddress.getArc(endIndex)))) { result = null; return result; }

				addressIndex--;
				endIndex--;
			}
		} finally {

			if (log.isTraceEnabled()) log.trace("endsWith(" + address + "," + endAddress + "," + variablesInAddress + "," + variablesInEnd + ") --> " + result);
		}
	}

	/**
	 * Checks if an address ends with a certain other address.
	 */
	public static XDIAddress endsWith(final XDIAddress address, final XDIAddress endAddress) {

		return endsWith(address, endAddress, false, false);
	}

	/**
	 * Extracts an address's parent arc(s), counting either from the start or the end.
	 * For =a*b*c*d and 1, this returns =a
	 * For =a*b*c*d and -1, this returns =a*b*c
	 */
	public static XDIAddress parentAddress(final XDIAddress address, final int numArcs) {

		if (address == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			List<XDIArc> arcs = new ArrayList<XDIArc> ();

			if (numArcs > 0) {

				for (int i = 0; i < numArcs; i++) arcs.add(address.getArc(i));
			} else if (numArcs < 0) {

				for (int i = 0; i < address.getNumArcs() - (- numArcs); i++) arcs.add(address.getArc(i));
			} else {

				{ result = address; return result; }
			}

			if (arcs.size() == 0) { result = null; return result; }

			{ result = XDIAddress.fromComponents(arcs); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("parentAddress(" + address + "," + numArcs + ") --> " + result);
		}
	}

	/**
	 * Extracts an address's local arc(s).
	 * For =a*b*c*d and 1, this returns *d
	 * For =a*b*c*d and -1, this returns *b*c*d
	 */
	public static XDIAddress localAddress(final XDIAddress address, final int numArcs) {

		if (address == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			List<XDIArc> arcs = new ArrayList<XDIArc> ();

			if (numArcs > 0) {

				for (int i = address.getNumArcs() - numArcs; i < address.getNumArcs(); i++) arcs.add(address.getArc(i));
			} else if (numArcs < 0) {

				for (int i = (- numArcs); i < address.getNumArcs(); i++) arcs.add(address.getArc(i));
			} else {

				{ result = address; return address; }
			}

			if (arcs.size() == 0) { result = null; return result; }

			{ result = XDIAddress.fromComponents(arcs); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("localAddress(" + address + "," + numArcs + ") --> " + result);
		}
	}

	/**
	 * Extracts an address from an address.
	 * For =a*b*c*d and 1, this returns *d
	 * For =a*b*c*d and -1, this returns *b*c*d
	 */
	public static XDIAddress subAddress(final XDIAddress address, final int startIndex, final int endIndex) {

		if (address == null) throw new NullPointerException();

		return AddressUtil.localAddress(AddressUtil.parentAddress(address, endIndex), - startIndex);
	}

	/**
	 * Get the index of an address inside an address.
	 * For =a*b*c*d and *b, this returns =a*b
	 * For =a*b*c*d and *c, this returns =a*b*c
	 * For =a*b*c*d and *x, this returns null
	 */
	public static int indexOfAddress(final XDIAddress address, final XDIArc search) {

		if (address == null) throw new NullPointerException();
		if (search == null) throw new NullPointerException();

		for (int i=0; i<address.getNumArcs(); i++) {

			XDIArc arc = address.getArc(i);

			if (arc.equals(search)) return i;
		}

		return -1;
	}

	/**
	 * Get the last index of an address inside an address.
	 * For =a*b*c*d and *b, this returns *b*c*d
	 * For =a*b*c*d and *c, this returns *c*d
	 * For =a*b*c*d and *x, this returns null
	 */
	public static int lastIndexOfAddress(final XDIAddress address, final XDIArc search) {

		if (address == null) throw new NullPointerException();
		if (search == null) throw new NullPointerException();

		for (int i=address.getNumArcs()-1; i>=0; i--) {

			XDIArc arc = address.getArc(i);

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
	public static XDIAddress removeStartAddress(final XDIAddress address, final XDIAddress start, final boolean variablesInAddress, final boolean variablesInStart) {

		if (address == null) throw new NullPointerException();
		if (start == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			if (start.equals(XDIConstants.XDI_ADD_ROOT)) { result = address; return result; }
			if (address.equals(XDIConstants.XDI_ADD_ROOT)) { result = null; return result; }

			XDIAddress startAddress = AddressUtil.startsWith(address, start, variablesInAddress, variablesInStart);
			if (startAddress == null) { result = null; return result; }

			if (address.equals(startAddress)) { result = XDIConstants.XDI_ADD_ROOT; return result; }

			{ result = AddressUtil.localAddress(address, - startAddress.getNumArcs()); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("removeStartAddress(" + address + "," + start + "," + variablesInAddress + "," + variablesInStart + ") --> " + result);
		}
	}

	/**
	 * Removes a start address from an address.
	 * E.g. for =a*b*c*d and =a*b, this returns *c*d
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns ()
	 * E.g. for =a*b*c*d and =x, this returns null
	 */
	public static XDIAddress removeStartAddress(final XDIAddress address, final XDIAddress start) {

		return removeStartAddress(address, start, false, false);
	}

	/**
	 * Removes an end address from an address.
	 * E.g. for =a*b*c*d and *c*d, this returns =a*b
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns (empty address)
	 * E.g. for =a*b*c*d and *y, this returns null
	 */
	public static XDIAddress removeEndAddress(final XDIAddress address, final XDIAddress end, final boolean variablesInAddress, final boolean variablesInEnd) {

		if (address == null) throw new NullPointerException();
		if (end == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			if (end.equals(XDIConstants.XDI_ADD_ROOT)) { result = address; return result; }
			if (address.equals(XDIConstants.XDI_ADD_ROOT)) { result = null; return result; }

			XDIAddress endAddress = AddressUtil.endsWith(address, end, variablesInAddress, variablesInEnd);
			if (endAddress == null) { result = null; return result; }

			if (address.equals(endAddress)) { result = XDIConstants.XDI_ADD_ROOT; return result; }

			{ result = AddressUtil.parentAddress(address, - endAddress.getNumArcs()); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("removeEndAddress(" + address + "," + end + "," + variablesInAddress + "," + variablesInEnd + ") --> " + result);
		}
	}

	/**
	 * Removes an end address from an address.
	 * E.g. for =a*b*c*d and *c*d, this returns =a*b
	 * E.g. for =a*b*c*d and (), this returns =a*b*c*d
	 * E.g. for =a*b*c*d and =a*b*c*d, this returns ()
	 * E.g. for =a*b*c*d and *y, this returns null
	 */
	public static XDIAddress removeEndAddress(final XDIAddress address, final XDIAddress end) {

		return removeEndAddress(address, end, false, false);
	}

	/**
	 * Replaces all occurences of an arc with an address.
	 */
	public static XDIAddress replaceAddress(final XDIAddress address, final XDIArc oldArc, final XDIAddress newAddress) {

		if (address == null) throw new NullPointerException();
		if (oldArc == null) throw new NullPointerException();
		if (newAddress == null) throw new NullPointerException();

		XDIAddress result = null;

		try {

			List<XDIArc> arcs = new ArrayList<XDIArc> ();

			for (XDIArc arc : address.getArcs()) {

				if (arc.equals(oldArc)) {

					arcs.addAll(newAddress.getArcs());

					continue;
				}

				if (arc.hasXRef() && arc.getXRef().hasAddress()) {

					XDIAddress xRefAddress = arc.getXRef().getAddress();

					xRefAddress = replaceAddress(xRefAddress, oldArc, newAddress);

					arcs.add(XDIArc.fromComponents(arc.getCs(), arc.isClassXs(), arc.isAttributeXs(), null, XDIXRef.fromComponents(arc.getXRef().getXs(), xRefAddress, null, null, null, null)));

					continue;
				}

				if (arc.hasXRef() && arc.getXRef().hasPartialSubjectAndPredicate()) {

					XDIAddress xRefPartialSubject = arc.getXRef().getPartialSubject();
					XDIAddress xRefPartialPredicate = arc.getXRef().getPartialPredicate();

					xRefPartialSubject = replaceAddress(xRefPartialSubject, oldArc, newAddress);
					xRefPartialPredicate = replaceAddress(xRefPartialPredicate, oldArc, newAddress);

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
	public static XDIAddress concatAddresses(final XDIAddress... addresses) {

		XDIAddress result = null;

		try {

			List<XDIArc> arcs = new ArrayList<XDIArc> ();

			if (addresses != null) {

				for (XDIAddress address : addresses) {

					if (address != null) arcs.addAll(address.getArcs());
				}
			}

			if (arcs.size() == 0) arcs.addAll(XDIConstants.XDI_ADD_ROOT.getArcs());

			{ result = XDIAddress.fromComponents(arcs); return result; }
		} finally {

			if (log.isTraceEnabled()) log.trace("concatAddresses(" + Arrays.asList(addresses) + ") --> " + result);
		}
	}

	/**
	 * Concats address(es) and arc(s) into a new address.
	 */
	public static XDIAddress concatAddresses(final XDIArc... arcs) {

		XDIAddress[] addresses = new XDIAddress[arcs.length];
		for (int i=0; i<arcs.length; i++) addresses[i] = XDIAddress.fromComponent(arcs[i]);

		return concatAddresses(addresses);
	}

	/**
	 * Concats address(es) and arc(s) into a new address.
	 */
	public static XDIAddress concatAddresses(final XDIAddress address, final XDIArc arc) {

		return concatAddresses(address, arc == null ? null : XDIAddress.fromComponent(arc));
	}

	/**
	 * Concats address(es) and arc(s) into a new address.
	 */
	public static XDIAddress concatAddresses(final XDIArc arc, final XDIAddress address) {

		return concatAddresses(arc == null ? null : XDIAddress.fromComponent(arc), address);
	}

	/*
	 * Helper classes
	 */

	public static final Comparator<? super XDIAddress> XDIAddress_ASCENDING_COMPARATOR = new Comparator<XDIAddress>() {

		@Override
		public int compare(XDIAddress o1, XDIAddress o2) {

			if (o1.getNumArcs() < o2.getNumArcs()) return -1;
			if (o1.getNumArcs() > o2.getNumArcs()) return 1;

			return o1.compareTo(o2);
		}
	};

	public static final Comparator<? super XDIAddress> XDIAddress_DESCENDING_COMPARATOR = new Comparator<XDIAddress>() {

		@Override
		public int compare(XDIAddress o1, XDIAddress o2) {

			if (o1.getNumArcs() > o2.getNumArcs()) return -1;
			if (o1.getNumArcs() < o2.getNumArcs()) return 1;

			return o1.compareTo(o2);
		}
	};
}
