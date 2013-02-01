package xdi2.core.xri3.parser.aparse;

import java.io.PrintStream;

public class TreeDisplayer extends AbstractVisitor
{
	private int indent;
	private PrintStream stream;
	public TreeDisplayer(PrintStream stream)
	{
		this.indent = 0;
		this.stream = stream;
	}

	@Override
	public Object visit(Terminal_StringValue value)
	{
		stream.println('"' + value.spelling + '"');
		return null;
	}

	@Override
	public Object visit(Terminal_NumericValue value)
	{
		stream.println('"' + value.spelling + '"');
		return null;
	}

	@Override
	public Object visitRule(Rule rule)
	{
		stream.println(ParserRules.ruleNameForClass(rule.getClass()));
		for (Rule innerrule : rule.rules) {
			indent++;
			for (int i=0; i<indent*2; i++) stream.print(' ');
			innerrule.accept(this);
			indent--;
		}
		return null;
	}
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */