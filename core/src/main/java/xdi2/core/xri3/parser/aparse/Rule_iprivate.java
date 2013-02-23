/* -----------------------------------------------------------------------------
 * Rule_iprivate.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.3
 * Produced : Wed Feb 20 10:37:39 CET 2013
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.parser.aparse;

import java.util.ArrayList;

final public class Rule_iprivate extends Rule
{
  private Rule_iprivate(String spelling, ArrayList<Rule> rules)
  {
    super(spelling, rules);
  }

  public Object accept(Visitor visitor)
  {
    return visitor.visit(this);
  }

  public static Rule_iprivate parse(ParserContext context)
  {
    context.push("iprivate");

    boolean parsed = true;
    int s0 = context.index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    parsed = false;
    if (!parsed)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = context.index;
        parsed = true;
        if (parsed)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = Terminal_NumericValue.parse(context, "%xE000-F8FF", "[\\uE000-\\uF8FF]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          parsed = c1 == 1;
        }
        if (parsed)
          e0.addAll(e1);
        else
          context.index = s1;
      }
    }

    rule = null;
    if (parsed)
      rule = new Rule_iprivate(context.text.substring(s0, context.index), e0);
    else
      context.index = s0;

    context.pop("iprivate", parsed);

    return (Rule_iprivate)rule;
  }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
