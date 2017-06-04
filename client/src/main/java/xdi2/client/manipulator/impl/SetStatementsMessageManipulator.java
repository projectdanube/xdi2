package xdi2.client.manipulator.impl;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.ManipulationContext;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.Message;

public class SetStatementsMessageManipulator extends AbstractMessageManipulator implements MessageManipulator {

	private XDIStatement[] statements;

	public SetStatementsMessageManipulator(XDIStatement[] statements) {

		this.statements = statements;
	}

	public SetStatementsMessageManipulator() {

		this.statements = null;
	}

	@Override
	public void manipulate(Message message, ManipulationContext manipulationContext) throws Xdi2ClientException {

		if (this.getStatements() != null) {

			for (XDIStatement statement : this.getStatements()) {

				message.getContextNode().setStatement(statement);
			}
		}
	}

	/*
	 * Getters and setters
	 */

	public XDIStatement[] getStatements() {

		return this.statements;
	}

	public void setStatements(XDIStatement[] statements) {

		this.statements = statements;
	}
}
