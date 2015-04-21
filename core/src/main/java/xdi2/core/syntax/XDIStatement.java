package xdi2.core.syntax;

import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.syntax.parser.ParserRegistry;
import xdi2.core.util.XDIAddressUtil;

public class XDIStatement extends XDIIdentifier {

	private static final long serialVersionUID = -1416735368366011077L;

	private XDIAddress subject;
	private Object predicate;
	private Object object;

	private XDIStatement(String string, XDIAddress subject, Object predicate, Object object) {

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

	static XDIStatement fromComponents(String string, XDIAddress subject, Object predicate, Object object) {

		if (predicate instanceof String && 
				XDIConstants.STRING_CONTEXT.equals(predicate) && 
				object instanceof XDIArc && 
				! XDIConstants.XDI_ADD_ROOT.equals(object.toString())) {

			return fromContextNodeComponents(string, subject, (XDIArc) object);
		} else if (predicate instanceof XDIAddress && 
				! XDIConstants.STRING_CONTEXT.equals(predicate.toString()) && 
				! XDIConstants.XDI_ARC_LITERAL.toString().equals(predicate.toString()) && 
				object instanceof XDIAddress) {

			return fromRelationComponents(string, subject, (XDIAddress) predicate, (XDIAddress) object);
		} else if (predicate instanceof XDIArc && 
				XDIConstants.XDI_ARC_LITERAL.equals(predicate) && 
				AbstractLiteralNode.isValidLiteralData(object)) {

			return fromLiteralComponents(string, subject, object);
		} else {

			throw new IllegalArgumentException("Invalid statement components: " + subject + "/" + predicate + "/" + object);
		}
	}

	public static XDIStatement fromComponents(XDIAddress subject, Object predicate, Object object) {

		return fromComponents(null, subject, predicate, object);
	}

	static XDIStatement fromContextNodeComponents(String string, XDIAddress contextNodeXDIAddress, XDIArc contextNodeXDIArc) {

		if (string == null) string = contextNodeXDIAddress.toString() + "/" + XDIConstants.STRING_CONTEXT + "/" + contextNodeXDIArc.toString();

		if (contextNodeXDIAddress.isLiteralNodeXDIAddress()) throw new IllegalArgumentException("Cannot have literal node address " + contextNodeXDIAddress + " as subject of a contextual statement: " + string);
		if (contextNodeXDIArc.isLiteralNodeXDIArc()) throw new IllegalArgumentException("Cannot have literal node arc " + contextNodeXDIArc + " as object of a contextual statement: " + string);

		return new XDIStatement(string, contextNodeXDIAddress, XDIConstants.STRING_CONTEXT, contextNodeXDIArc);
	}

	public static XDIStatement fromContextNodeComponents(XDIAddress contextNodeXDIAddress, XDIArc contextNodeXDIArc) {

		return fromContextNodeComponents(null, contextNodeXDIAddress, contextNodeXDIArc);
	}

	static XDIStatement fromRelationComponents(String string, XDIAddress contextNodeXDIAddress, XDIAddress relationAddress, XDIAddress targetXDIAddress) {

		if (string == null) string = contextNodeXDIAddress.toString() + "/" + relationAddress.toString() + "/" + targetXDIAddress.toString();

		if (contextNodeXDIAddress.isLiteralNodeXDIAddress()) throw new IllegalArgumentException("Cannot have literal node address " + contextNodeXDIAddress + " as subject of a relational statement: " + string);
		if (relationAddress.isLiteralNodeXDIAddress()) throw new IllegalArgumentException("Cannot have literal node address " + relationAddress + " as predicate of a relational statement: " + string);

		return new XDIStatement(string, contextNodeXDIAddress, relationAddress, targetXDIAddress);
	}

	public static XDIStatement fromRelationComponents(XDIAddress contextNodeXDIAddress, XDIAddress relationAddress, XDIAddress targetXDIAddress) {

		return fromRelationComponents(null, contextNodeXDIAddress, relationAddress, targetXDIAddress);
	}

	static XDIStatement fromLiteralComponents(String string, XDIAddress contextNodeXDIAddress, Object literalData) {

		if (string == null) string = contextNodeXDIAddress.toString() + "/" + XDIConstants.XDI_ARC_LITERAL + "/" + AbstractLiteralNode.literalDataToString(literalData);

		if (contextNodeXDIAddress.isLiteralNodeXDIAddress()) throw new IllegalArgumentException("Cannot have literal node address " + contextNodeXDIAddress + " as subject of a literal statement: " + string);

		return new XDIStatement(string, contextNodeXDIAddress, XDIConstants.XDI_ARC_LITERAL, literalData);
	}
	
	public static XDIStatement fromLiteralComponents(XDIAddress contextNodeXDIAddress, Object literalData) {
		
		return fromLiteralComponents(null, contextNodeXDIAddress, literalData);
	}

	/*
	 * Instance methods
	 */

	public XDIAddress getSubject() {

		return this.subject;
	}

	public Object getPredicate() {

		return this.predicate;
	}

	public Object getObject() {

		return this.object;
	}

	public boolean isContextNodeStatement() {

		if (! (this.getPredicate() instanceof String)) return false;
		if (! XDIConstants.STRING_CONTEXT.equals(this.getPredicate())) return false;
		if (! (this.getObject() instanceof XDIArc)) return false;
		if (XDIConstants.XDI_ADD_ROOT.toString().equals(this.getObject().toString())) return false;

		return true;
	}

	public boolean isRelationStatement() {

		if (! (this.getPredicate() instanceof XDIAddress)) return false;
		if (XDIConstants.STRING_CONTEXT.equals(this.getPredicate().toString())) return false;
		if (XDIConstants.XDI_ARC_LITERAL.toString().equals(this.getPredicate().toString())) return false;
		if (! (this.getObject() instanceof XDIAddress)) return false;

		return true;
	}

	public boolean isLiteralStatement() {

		if (! (this.getPredicate() instanceof XDIArc)) return false;
		if (! XDIConstants.XDI_ARC_LITERAL.equals(this.getPredicate())) return false;
		if (! AbstractLiteralNode.isValidLiteralData(this.getObject())) return false;

		return true;
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

			return (XDIAddress) this.getPredicate();
		}

		return null;
	}

	public XDIAddress getTargetXDIAddress() {

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
