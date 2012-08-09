package xdi2.core.impl.file;

import xdi2.core.Literal;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.impl.memory.MemoryLiteral;

public class FileLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = 1602689543198362042L;

	private MemoryLiteral memoryLiteral;

	FileLiteral(FileGraph graph, FileContextNode contextNode, MemoryLiteral memoryLiteral) {

		super(graph, contextNode);

		this.memoryLiteral = memoryLiteral;
	}

	@Override
	public String getLiteralData() {

		return this.memoryLiteral.getLiteralData();
	}

	@Override
	public void setLiteralData(String literalData) {

		this.memoryLiteral.setLiteralData(literalData);
	}
}
