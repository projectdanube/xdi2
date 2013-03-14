/* -----------------------------------------------------------------------------
 * Parser.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.3
 * Produced : Thu Mar 14 16:34:39 CET 2013
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.parser.full.aparse;

import java.util.Stack;
import java.util.Properties;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;

public class Parser
{
  private Parser() {}

  static public void main(String[] args)
  {
    Properties arguments = new Properties();
    String error = "";
    boolean ok = args.length > 0;

    if (ok)
    {
      arguments.setProperty("Trace", "Off");
      arguments.setProperty("Rule", "xdi-graph");

      for (int i = 0; i < args.length; i++)
      {
        if (args[i].equals("-trace"))
          arguments.setProperty("Trace", "On");
        else if (args[i].equals("-visitor"))
          arguments.setProperty("Visitor", args[++i]);
        else if (args[i].equals("-file"))
          arguments.setProperty("File", args[++i]);
        else if (args[i].equals("-string"))
          arguments.setProperty("String", args[++i]);
        else if (args[i].equals("-rule"))
          arguments.setProperty("Rule", args[++i]);
        else
        {
          error = "unknown argument: " + args[i];
          ok = false;
        }
      }
    }

    if (ok)
    {
      if (arguments.getProperty("File") == null &&
          arguments.getProperty("String") == null)
      {
        error = "insufficient arguments: -file or -string required";
        ok = false;
      }
    }

    if (!ok)
    {
      System.out.println("error: " + error);
      System.out.println("usage: Parser [-rule rulename] [-trace] <-file file | -string string> [-visitor visitor]");
    }
    else
    {
      try
      {
        Rule rule = null;

        if (arguments.getProperty("File") != null)
        {
          rule = 
            parse(
              arguments.getProperty("Rule"), 
              new File(arguments.getProperty("File")), 
              arguments.getProperty("Trace").equals("On"));
        }
        else if (arguments.getProperty("String") != null)
        {
          rule = 
            parse(
              arguments.getProperty("Rule"), 
              arguments.getProperty("String"), 
              arguments.getProperty("Trace").equals("On"));
        }

        if (arguments.getProperty("Visitor") != null)
        {
          Visitor visitor = 
            (Visitor)Class.forName(arguments.getProperty("Visitor")).newInstance();
          rule.accept(visitor);
        }
      }
      catch (IllegalArgumentException e)
      {
        System.out.println("argument error: " + e.getMessage());
      }
      catch (IOException e)
      {
        System.out.println("io error: " + e.getMessage());
      }
      catch (ParserException e)
      {
        System.out.println("parser error: " + e.getMessage());
      }
      catch (ClassNotFoundException e)
      {
        System.out.println("visitor error: class not found - " + e.getMessage());
      }
      catch (IllegalAccessException e)
      {
        System.out.println("visitor error: illegal access - " + e.getMessage());
      }
      catch (InstantiationException e)
      {
        System.out.println("visitor error: instantiation failure - " + e.getMessage());
      }
    }
  }

  static public Rule parse(String rulename, String string)
  throws IllegalArgumentException,
         ParserException
  {
    return parse(rulename, string, false);
  }

  static public Rule parse(String rulename, InputStream in)
  throws IllegalArgumentException,
         IOException,
         ParserException
  {
    return parse(rulename, in, false);
  }

  static public Rule parse(String rulename, File file)
  throws IllegalArgumentException,
         IOException,
         ParserException
  {
    return parse(rulename, file, false);
  }

  static private Rule parse(String rulename, String string, boolean trace)
  throws IllegalArgumentException,
         ParserException
  {
    if (rulename == null)
      throw new IllegalArgumentException("null rulename");
    if (string == null)
      throw new IllegalArgumentException("null string");

    ParserContext context = new ParserContext(string, trace);

    Rule rule = null;
    if (rulename.equalsIgnoreCase("xdi-graph")) rule = Rule_xdi_graph.parse(context);
    else if (rulename.equalsIgnoreCase("xdi-statement")) rule = Rule_xdi_statement.parse(context);
    else if (rulename.equalsIgnoreCase("contextual")) rule = Rule_contextual.parse(context);
    else if (rulename.equalsIgnoreCase("direct")) rule = Rule_direct.parse(context);
    else if (rulename.equalsIgnoreCase("inverse")) rule = Rule_inverse.parse(context);
    else if (rulename.equalsIgnoreCase("absolute")) rule = Rule_absolute.parse(context);
    else if (rulename.equalsIgnoreCase("peer-relative")) rule = Rule_peer_relative.parse(context);
    else if (rulename.equalsIgnoreCase("context-relative")) rule = Rule_context_relative.parse(context);
    else if (rulename.equalsIgnoreCase("collection-relative")) rule = Rule_collection_relative.parse(context);
    else if (rulename.equalsIgnoreCase("absolute-inverse")) rule = Rule_absolute_inverse.parse(context);
    else if (rulename.equalsIgnoreCase("peer-inverse")) rule = Rule_peer_inverse.parse(context);
    else if (rulename.equalsIgnoreCase("context-inverse")) rule = Rule_context_inverse.parse(context);
    else if (rulename.equalsIgnoreCase("collection-inverse")) rule = Rule_collection_inverse.parse(context);
    else if (rulename.equalsIgnoreCase("relational")) rule = Rule_relational.parse(context);
    else if (rulename.equalsIgnoreCase("direct-relation")) rule = Rule_direct_relation.parse(context);
    else if (rulename.equalsIgnoreCase("inverse-relation")) rule = Rule_inverse_relation.parse(context);
    else if (rulename.equalsIgnoreCase("literal")) rule = Rule_literal.parse(context);
    else if (rulename.equalsIgnoreCase("literal-value")) rule = Rule_literal_value.parse(context);
    else if (rulename.equalsIgnoreCase("literal-ref")) rule = Rule_literal_ref.parse(context);
    else if (rulename.equalsIgnoreCase("context")) rule = Rule_context.parse(context);
    else if (rulename.equalsIgnoreCase("peer")) rule = Rule_peer.parse(context);
    else if (rulename.equalsIgnoreCase("peer-relative-context")) rule = Rule_peer_relative_context.parse(context);
    else if (rulename.equalsIgnoreCase("relative-context")) rule = Rule_relative_context.parse(context);
    else if (rulename.equalsIgnoreCase("collection-context")) rule = Rule_collection_context.parse(context);
    else if (rulename.equalsIgnoreCase("member-context")) rule = Rule_member_context.parse(context);
    else if (rulename.equalsIgnoreCase("literal-context")) rule = Rule_literal_context.parse(context);
    else if (rulename.equalsIgnoreCase("xref")) rule = Rule_xref.parse(context);
    else if (rulename.equalsIgnoreCase("inner-root")) rule = Rule_inner_root.parse(context);
    else if (rulename.equalsIgnoreCase("iri-literal")) rule = Rule_iri_literal.parse(context);
    else if (rulename.equalsIgnoreCase("xdi-literal")) rule = Rule_xdi_literal.parse(context);
    else if (rulename.equalsIgnoreCase("subgraph")) rule = Rule_subgraph.parse(context);
    else if (rulename.equalsIgnoreCase("collection")) rule = Rule_collection.parse(context);
    else if (rulename.equalsIgnoreCase("member")) rule = Rule_member.parse(context);
    else if (rulename.equalsIgnoreCase("entity-member")) rule = Rule_entity_member.parse(context);
    else if (rulename.equalsIgnoreCase("attribute-member")) rule = Rule_attribute_member.parse(context);
    else if (rulename.equalsIgnoreCase("order-ref")) rule = Rule_order_ref.parse(context);
    else if (rulename.equalsIgnoreCase("singleton")) rule = Rule_singleton.parse(context);
    else if (rulename.equalsIgnoreCase("attribute-singleton")) rule = Rule_attribute_singleton.parse(context);
    else if (rulename.equalsIgnoreCase("entity-singleton")) rule = Rule_entity_singleton.parse(context);
    else if (rulename.equalsIgnoreCase("type")) rule = Rule_type.parse(context);
    else if (rulename.equalsIgnoreCase("instance")) rule = Rule_instance.parse(context);
    else if (rulename.equalsIgnoreCase("specific")) rule = Rule_specific.parse(context);
    else if (rulename.equalsIgnoreCase("generic")) rule = Rule_generic.parse(context);
    else if (rulename.equalsIgnoreCase("person")) rule = Rule_person.parse(context);
    else if (rulename.equalsIgnoreCase("organization")) rule = Rule_organization.parse(context);
    else if (rulename.equalsIgnoreCase("mutable")) rule = Rule_mutable.parse(context);
    else if (rulename.equalsIgnoreCase("immutable")) rule = Rule_immutable.parse(context);
    else if (rulename.equalsIgnoreCase("xdi-scheme")) rule = Rule_xdi_scheme.parse(context);
    else if (rulename.equalsIgnoreCase("uuid")) rule = Rule_uuid.parse(context);
    else if (rulename.equalsIgnoreCase("time-low")) rule = Rule_time_low.parse(context);
    else if (rulename.equalsIgnoreCase("time-mid")) rule = Rule_time_mid.parse(context);
    else if (rulename.equalsIgnoreCase("time-high-and-version")) rule = Rule_time_high_and_version.parse(context);
    else if (rulename.equalsIgnoreCase("clock-seq-and-reserved")) rule = Rule_clock_seq_and_reserved.parse(context);
    else if (rulename.equalsIgnoreCase("clock-seq-low")) rule = Rule_clock_seq_low.parse(context);
    else if (rulename.equalsIgnoreCase("node")) rule = Rule_node.parse(context);
    else if (rulename.equalsIgnoreCase("hexoctet")) rule = Rule_hexoctet.parse(context);
    else if (rulename.equalsIgnoreCase("ipv6")) rule = Rule_ipv6.parse(context);
    else if (rulename.equalsIgnoreCase("data-xref")) rule = Rule_data_xref.parse(context);
    else if (rulename.equalsIgnoreCase("data-iri")) rule = Rule_data_iri.parse(context);
    else if (rulename.equalsIgnoreCase("iri-scheme")) rule = Rule_iri_scheme.parse(context);
    else if (rulename.equalsIgnoreCase("iri-char")) rule = Rule_iri_char.parse(context);
    else if (rulename.equalsIgnoreCase("nonparen-delim")) rule = Rule_nonparen_delim.parse(context);
    else if (rulename.equalsIgnoreCase("xdi-char")) rule = Rule_xdi_char.parse(context);
    else if (rulename.equalsIgnoreCase("iunreserved")) rule = Rule_iunreserved.parse(context);
    else if (rulename.equalsIgnoreCase("pct-encoded")) rule = Rule_pct_encoded.parse(context);
    else if (rulename.equalsIgnoreCase("ucschar")) rule = Rule_ucschar.parse(context);
    else if (rulename.equalsIgnoreCase("ALPHA")) rule = Rule_ALPHA.parse(context);
    else if (rulename.equalsIgnoreCase("DIGIT")) rule = Rule_DIGIT.parse(context);
    else if (rulename.equalsIgnoreCase("HEXDIG")) rule = Rule_HEXDIG.parse(context);
    else if (rulename.equalsIgnoreCase("CRLF")) rule = Rule_CRLF.parse(context);
    else if (rulename.equalsIgnoreCase("CR")) rule = Rule_CR.parse(context);
    else if (rulename.equalsIgnoreCase("LF")) rule = Rule_LF.parse(context);
    else throw new IllegalArgumentException("unknown rule");

    if (rule == null)
    {
      throw new ParserException(
        "rule \"" + (String)context.getErrorStack().peek() + "\" failed",
        context.text,
        context.getErrorIndex(),
        context.getErrorStack());
    }

    if (context.text.length() > context.index)
    {
      ParserException primaryError = 
        new ParserException(
          "extra data found",
          context.text,
          context.index,
          new Stack<String>());

      if (context.getErrorIndex() > context.index)
      {
        ParserException secondaryError = 
          new ParserException(
            "rule \"" + (String)context.getErrorStack().peek() + "\" failed",
            context.text,
            context.getErrorIndex(),
            context.getErrorStack());

        primaryError.initCause(secondaryError);
      }

      throw primaryError;
    }

    return rule;
  }

  static private Rule parse(String rulename, InputStream in, boolean trace)
  throws IllegalArgumentException,
         IOException,
         ParserException
  {
    if (rulename == null)
      throw new IllegalArgumentException("null rulename");
    if (in == null)
      throw new IllegalArgumentException("null input stream");

    int ch = 0;
    StringBuffer out = new StringBuffer();
    while ((ch = in.read()) != -1)
      out.append((char)ch);

    return parse(rulename, out.toString(), trace);
  }

  static private Rule parse(String rulename, File file, boolean trace)
  throws IllegalArgumentException,
         IOException,
         ParserException
  {
    if (rulename == null)
      throw new IllegalArgumentException("null rulename");
    if (file == null)
      throw new IllegalArgumentException("null file");

    BufferedReader in = new BufferedReader(new FileReader(file));
    int ch = 0;
    StringBuffer out = new StringBuffer();
    while ((ch = in.read()) != -1)
      out.append((char)ch);

    in.close();

    return parse(rulename, out.toString(), trace);
  }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
