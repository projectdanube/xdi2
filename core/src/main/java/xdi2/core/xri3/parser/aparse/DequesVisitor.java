package xdi2.core.xri3.parser.aparse;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class DequesVisitor extends AbstractVisitor
{
	private Deque<String> currentDeque;
	private List<Deque<String>> deques;
	public DequesVisitor()
	{
		this.deques = new ArrayList<Deque<String>> ();
		this.nextDeque();
	}

	public List<Deque<String>> getDeques() {

		return this.deques;
	}

	@Override
	public Object visit(Terminal_StringValue value)
	{
		this.currentDeque.addLast(value.spelling);
		this.nextDeque();
		this.currentDeque.removeLast();
		return null;
	}

	@Override
	public Object visit(Terminal_NumericValue value)
	{
		this.currentDeque.addLast(value.spelling);
		this.nextDeque();
		this.currentDeque.removeLast();
		return null;
	}

	@Override
	public Object visitRule(Rule rule)
	{
		this.currentDeque.addLast(ParserRules.ruleNameForClass(rule.getClass()));
		for (Rule innerrule : rule.rules) {
			innerrule.accept(this);
		}
		this.currentDeque.removeLast();
		return null;
	}

	private void nextDeque() {
		if (this.currentDeque != null) {
			this.deques.add(this.currentDeque);
			this.currentDeque = new LinkedList<String> (this.currentDeque);
		} else {
			this.currentDeque = new LinkedList<String> ();
		}
	}
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */