package xdi2.core.xri3;

import java.io.Serializable;

import xdi2.core.features.multiplicity.XdiElement;
import xdi2.core.features.multiplicity.XdiMember;
import xdi2.core.features.multiplicity.XdiValue;
import xdi2.core.features.roots.XdiRoot;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

import com.alibaba.fastjson.JSONObject;

/**
 * An XDI variable, represented as a subsegment.
 * 
 * @author markus
 */
public final class XDI3Variable implements Serializable, Comparable<XDI3Variable> {

	private static final long serialVersionUID = 6311431893343848700L;

	private XDI3SubSegment subSegment;

	protected XDI3Variable(XDI3SubSegment subSegment) {

		if (subSegment == null) throw new NullPointerException();

		this.subSegment = subSegment;
	}

	public XDI3SubSegment getSubSegment() {

		return this.subSegment;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a subsegment is a valid XDI variable.
	 * @param subSegment The subsegment to check.
	 * @return True if the subsegment is a valid XDI variable.
	 */
	public static boolean isValid(XDI3SubSegment subSegment) {

		return subSegment.hasXRef() &&
				XDI3Constants.CF_VARIABLE.equals(subSegment.getXRef().getCf()) &&
				! subSegment.getXRef().hasStatement() &&
				! subSegment.getXRef().hasPartialSubjectAndPredicate() &&
				! subSegment.getXRef().hasIri() &&
				! subSegment.getXRef().hasLiteral();
	}

	/**
	 * Factory method that creates an XDI variable bound to a given subsegment.
	 * @param subSegment The subsegment that is an XDI variable.
	 * @return The XDI variable.
	 */
	public static XDI3Variable fromSubSegment(XDI3SubSegment subSegment) {

		if (! isValid(subSegment)) return null;

		return new XDI3Variable(subSegment);
	}

	/*
	 * Instance methods
	 */

	public String getCf() {

		if (! this.getSubSegment().getXRef().hasSegment()) return null;

		XDI3Segment innerSegment = this.getSubSegment().getXRef().getSegment();
		XDI3SubSegment innerSubSegment = innerSegment.getFirstSubSegment();
		if (! innerSubSegment.hasXRef()) return null;

		return innerSubSegment.getXRef().getCf();
	}

	public String getCss() {

		String css = "" ;

		if (this.getSubSegment().getXRef().hasSegment()) {

			XDI3Segment innerSegment = this.getSubSegment().getXRef().getSegment();
			XDI3SubSegment innerSubSegment = innerSegment.getFirstSubSegment();

			if (innerSubSegment.hasXRef() && innerSubSegment.getXRef().hasSegment()) {

				XDI3Segment innerInnerSegment = innerSubSegment.getXRef().getSegment();

				for (int i=0; i<innerSegment.getNumSubSegments(); i++) {

					css += innerInnerSegment.getSubSegment(i).getCs();
				}
			} else {

				for (int i=0; i<this.getSubSegment().getXRef().getSegment().getNumSubSegments(); i++) {

					css += this.getSubSegment().getXRef().getSegment().getSubSegment(i).getCs();
				}
			}
		}

		return css;
	}

	public boolean isMultiple() {

		if (! this.getSubSegment().getXRef().hasSegment()) return false;

		XDI3Segment innerSegment = this.getSubSegment().getXRef().getSegment();
		if (innerSegment.getNumSubSegments() < 2) return false;

		return "{}".equals(innerSegment.getLastSubSegment().getXRef().toString());
	}

	public boolean matches(XDI3SubSegment subSegment) {

		String cf = this.getCf();
		String css = this.getCss();

		JSONObject o;

		if (cf != null) {

			if (XDI3Constants.CF_ROOT.equals(cf) && ! XdiRoot.isRootArcXri(subSegment)) return false;
			if (XDI3Constants.CF_VARIABLE.equals(cf) && ! XdiMember.isMemberArcXri(subSegment)) return false;
			if (XDI3Constants.CF_ELEMENT.equals(cf) && ! XdiElement.isElementArcXri(subSegment)) return false;
			if (XDI3Constants.CF_VALUE.equals(cf) && ! XdiValue.isValueArcXri(subSegment)) return false;
		}

		if (css.length() > 0) {

			Character cs = null;

			if (subSegment.hasCs()) {

				cs = subSegment.getCs();
			} else {

				if (subSegment.hasXRef() && subSegment.getXRef().hasSegment()) {

					cs = subSegment.getXRef().getSegment().getFirstSubSegment().getCs();
				}
			}

			if (cs == null) return false;
			if (css.indexOf(cs.charValue()) == -1) return false;
		}

		return true;
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getSubSegment().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof XDI3Variable)) return false;
		if (object == this) return true;

		XDI3Variable other = (XDI3Variable) object;

		// two variables are equal if their context nodes are equal

		return this.getSubSegment().equals(other.getSubSegment());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getSubSegment().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(XDI3Variable other) {

		if (other == null || other == this) return 0;

		return this.getSubSegment().compareTo(other.getSubSegment());
	}
}
