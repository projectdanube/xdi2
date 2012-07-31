package xdi2.samples.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import xdi2.core.ContextNode;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.contributor.AbstractContributor;

public class MyFileContributor extends AbstractContributor {

	private File file;

	public MyFileContributor(File file) {

		this.file = file;
	}

	@Override
	public boolean get(XRI3Segment contextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		String string = this.load(operation);

		ContextNode contextNode = messageResult.getGraph().findContextNode(contextNodeXri, true);
		contextNode.createLiteral(string);

		return false;
	}

	@Override
	public boolean modLiteral(XRI3Segment contextNodeXri, String literalData, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.save(literalData, operation);

		return false;
	}

	protected String load(Operation operation) throws Xdi2MessagingException {

		String string;

		try {

			FileReader reader = new FileReader(this.file);
			string = new BufferedReader(reader).readLine();
			reader.close();
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot load file " + this.file.getAbsolutePath() + ": " + ex.getMessage(), ex, operation);
		}

		return string;
	}

	protected void save(String string, Operation operation) throws Xdi2MessagingException {

		try {

			FileWriter writer = new FileWriter(this.file);
			new PrintWriter(writer).println(string);
			writer.close();
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot load file " + this.file.getAbsolutePath() + ": " + ex.getMessage(), ex, operation);
		}
	}
}
