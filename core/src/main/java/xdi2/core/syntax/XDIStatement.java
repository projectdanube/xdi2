package xdi2.core.syntax;

import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.syntax.parser.ParserRegistry;
import xdi2.core.util.XDIAddressUtil;

public class XDIStatement extends XDIIdentifier {

	private static final long serialVersionUID = -1416735368366011077L;

	private XDIAddress subject;
	private XDIAddress predicate;
	private Object object;

	XDIStatement(String string, XDIAddress subject, XDIAddress predicate, XDIArc object) {

		this(string, subject, predicate, (Object) object);
	}

	XDIStatement(String string, XDIAddress subject, XDIAddress predicate, XDIAddress object) {

		this(string, subject, predicate, (Object) object);
	}

	XDIStatement(String string, XDIAddress subject, XDIAddress predicate, Object object) {

		super(string);

		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	/*
	 * Factory methods
	 */
	
	public static XDIStatement create(String string) {

		return ParserRegistry.getInstance().getParser().parseXDIStatement(string);
	}

	public static XDIStatement fromComponents(XDIAddress subject, XDIAddress predicate, Object object) {

		if (XDIConstants.XDI_ADD_CONTEXT.equals(predicate)) {

			return fromContextNodeComponents(subject, (XDIArc) object);
		} else if (XDIConstants.XDI_ADD_LITERAL.equals(predicate)) {

			return fromLiteralComponents(subject, object);
		} else {

			return fromRelationComponents(subject, predicate, (XDIAddress) object);
		}
	}

	public static XDIStatement fromContextNodeComponents(XDIAddress contextNodeXDIAddress, XDIArc contextNodeArc) {

		String string = contextNodeXDIAddress.toString() + "/" + XDIConstants.XDI_ADD_CONTEXT.toString() + "/" + contextNodeArc.toString();

		return new XDIStatement(string, contextNodeXDIAddress, XDIConstants.XDI_ADD_CONTEXT, contextNodeArc);
	}

	public static XDIStatement fromRelationComponents(XDIAddress contextNodeXDIAddress, XDIAddress relationAddress, XDIAddress targetContextNodeXDIAddress) {

		String string = contextNodeXDIAddress.toString() + "/" + relationAddress.toString() + "/" + targetContextNodeXDIAddress.toString();

		return new XDIStatement(string, contextNodeXDIAddress, relationAddress, targetContextNodeXDIAddress);
	}

	public static XDIStatement fromLiteralComponents(XDIAddress contextNodeXDIAddress, Object literalData) {

		String string = contextNodeXDIAddress.toString() + "/" + XDIConstants.XDI_ADD_LITERAL.toString() + "/" + AbstractLiteral.literalDataToString(literalData);

		return new XDIStatement(string, contextNodeXDIAddress, XDIConstants.XDI_ADD_LITERAL, literalData);
	}

	/*
	 * Instance methods
	 */
	
	public XDIAddress getSubject() {

		return this.subject;
	}

	public XDIAddress getPredicate() {

		return this.predicate;
	}

	public Object getObject() {

		return this.object;
	}

	public boolean isContextNodeStatement() {

		return XDIConstants.XDI_ADD_CONTEXT.equals(this.getPredicate()) && (this.getObject() instanceof XDIArc);
	}

	public boolean isRelationStatement() {

		return (! XDIConstants.XDI_ADD_CONTEXT.equals(this.getPredicate())) && (! XDIConstants.XDI_ADD_LITERAL.equals(this.getPredicate())) && (this.getObject() instanceof XDIAddress);
	}

	public boolean isLiteralStatement() {

		return XDIConstants.XDI_ADD_LITERAL.equals(this.getPredicate()) && AbstractLiteral.isValidLiteralData(this.getObject());
	}

	public XDIAddress getContextNodeXDIAddress() {

		return this.getSubject();
	}

	public XDIArc getContextNodeXDIArc() {

		if (this.isContextNodeStatement()) {

			return (XDIArc) this.getObject();
		}

		return null;
	}

	public XDIAddress getRelationXDIAddress() {

		if (this.isRelationStatement()) {

			return this.getPredicate();
		}

		return null;
	}

	public XDIAddress getTargetContextNodeXDIAddress() {

		if (this.isContextNodeStatement()) {

			return XDIAddressUtil.concatXDIAddresses(this.getSubject(), (XDIArc) this.getObject());
		} else if (this.isRelationStatement()) {

			return (XDIAddress) this.getObject();
		}

		return null;
	}

	public Object getLiteralData() {

		if (this.isLiteralStatement()) {

			return this.getObject();
		}

		return null;
	}
}
