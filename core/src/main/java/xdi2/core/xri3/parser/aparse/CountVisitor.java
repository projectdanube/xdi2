package xdi2.core.xri3.parser.aparse;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CountVisitor extends AbstractVisitor
{
	private Map<String, Integer> count;
	public CountVisitor()
	{
		this.count = new HashMap<String, Integer> ();
	}

	public Map<String, Integer> getCount() {
		
		TreeMap<String, Integer> count = new TreeMap<String, Integer> (new Comparator<String> () {

			@Override
			public int compare(String string1, String string2) {

				return CountVisitor.this.count.get(string1).intValue() < CountVisitor.this.count.get(string2).intValue() ? 1 : -1;
			}
		});
		
		count.putAll(this.count);
		
		return count;
	}

	@Override
	public Object visitRule(Rule rule)
	{
		Integer n = this.count.get(ParserRules.ruleNameForClass(rule.getClass()));
		n = n == null ? Integer.valueOf(1) : Integer.valueOf(n.intValue() + 1);
		this.count.put(ParserRules.ruleNameForClass(rule.getClass()), n);
		for (Rule innerrule : rule.rules) {
			innerrule.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(Terminal_StringValue value) {

		return null;
	}

	@Override
	public Object visit(Terminal_NumericValue value) {

		return null;
	}
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */