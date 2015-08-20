package xdi2.messaging.target.exceptions;

import xdi2.core.exceptions.Xdi2Exception;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.operations.Operation;

/**
 * An exception that is thrown when an operation cannot be executed immediately,
 * i.e. it will be delivered via a push link contract later.
 * 
 * @author markus
 */
public class Xdi2PushRequiredException extends Xdi2Exception {

	private static final long serialVersionUID = -6202222604448519285L;

	private XDIAddress XDIaddress;
	private XDIStatement XDIstatement;
	private Operation operation;

	public Xdi2PushRequiredException(XDIAddress XDIaddress, Operation operation) {

		super();

		this.XDIaddress = XDIaddress;
		this.XDIstatement = null;
		this.operation = operation;
	}

	public Xdi2PushRequiredException(XDIStatement XDIstatement, Operation operation) {

		super();

		this.XDIaddress = null;
		this.XDIstatement = XDIstatement;
		this.operation = operation;
	}

	public XDIAddress getXDIaddress() {

		return this.XDIaddress;

	}

	public XDIStatement getXDIstatement() {

		return this.XDIstatement;
	}

	public Operation getOperation() {

		return this.operation;
	}
}
