/* -----------------------------------------------------------------------------
 * Rule$ucschar.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.2
 * Produced : Fri Oct 19 08:29:48 CEST 2012
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.impl.parser;

import java.util.ArrayList;

final public class Rule$ucschar extends Rule
{
  private Rule$ucschar(String spelling, ArrayList<Rule> rules)
  {
    super(spelling, rules);
  }

  public Object accept(Visitor visitor)
  {
    return visitor.visit(this);
  }

  public static Rule$ucschar parse(ParserContext context)
  {
    context.push("ucschar");

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
            rule = Terminal$NumericValue.parse(context, "%xA0-D7FF", "[\\xA0-\\uD7FF]", 1);
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
            rule = Terminal$NumericValue.parse(context, "%xF900-FDCF", "[\\uF900-\\uFDCF]", 1);
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
            rule = Terminal$NumericValue.parse(context, "%xFDF0-FFEF", "[\\uFDF0-\\uFFEF]", 1);
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
      rule = new Rule$ucschar(context.text.substring(s0, context.index), e0);
    else
      context.index = s0;

    context.pop("ucschar", parsed);

    return (Rule$ucschar)rule;
  }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
