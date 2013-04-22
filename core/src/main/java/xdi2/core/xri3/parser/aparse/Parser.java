/* -----------------------------------------------------------------------------
 * Parser.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.3
 * Produced : Mon Apr 22 13:14:58 CEST 2013
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.parser.aparse;

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
    else if (rulename.equalsIgnoreCase("contextual-statement")) rule = Rule_contextual_statement.parse(context);
    else if (rulename.equalsIgnoreCase("direct-contextual")) rule = Rule_direct_contextual.parse(context);
    else if (rulename.equalsIgnoreCase("inverse-contextual")) rule = Rule_inverse_contextual.parse(context);
    else if (rulename.equalsIgnoreCase("absolute")) rule = Rule_absolute.parse(context);
    else if (rulename.equalsIgnoreCase("root-relative")) rule = Rule_root_relative.parse(context);
    else if (rulename.equalsIgnoreCase("context-relative")) rule = Rule_context_relative.parse(context);
    else if (rulename.equalsIgnoreCase("class-relative")) rule = Rule_class_relative.parse(context);
    else if (rulename.equalsIgnoreCase("absolute-inverse")) rule = Rule_absolute_inverse.parse(context);
    else if (rulename.equalsIgnoreCase("root-inverse")) rule = Rule_root_inverse.parse(context);
    else if (rulename.equalsIgnoreCase("context-inverse")) rule = Rule_context_inverse.parse(context);
    else if (rulename.equalsIgnoreCase("class-inverse")) rule = Rule_class_inverse.parse(context);
    else if (rulename.equalsIgnoreCase("relational-statement")) rule = Rule_relational_statement.parse(context);
    else if (rulename.equalsIgnoreCase("direct-relational")) rule = Rule_direct_relational.parse(context);
    else if (rulename.equalsIgnoreCase("inverse-relational")) rule = Rule_inverse_relational.parse(context);
    else if (rulename.equalsIgnoreCase("literal-statement")) rule = Rule_literal_statement.parse(context);
    else if (rulename.equalsIgnoreCase("context")) rule = Rule_context.parse(context);
    else if (rulename.equalsIgnoreCase("relative-context")) rule = Rule_relative_context.parse(context);
    else if (rulename.equalsIgnoreCase("class-context")) rule = Rule_class_context.parse(context);
    else if (rulename.equalsIgnoreCase("class-path")) rule = Rule_class_path.parse(context);
    else if (rulename.equalsIgnoreCase("instance-context")) rule = Rule_instance_context.parse(context);
    else if (rulename.equalsIgnoreCase("literal-context")) rule = Rule_literal_context.parse(context);
    else if (rulename.equalsIgnoreCase("literal-path")) rule = Rule_literal_path.parse(context);
    else if (rulename.equalsIgnoreCase("attribute-path")) rule = Rule_attribute_path.parse(context);
    else if (rulename.equalsIgnoreCase("root")) rule = Rule_root.parse(context);
    else if (rulename.equalsIgnoreCase("outer-root")) rule = Rule_outer_root.parse(context);
    else if (rulename.equalsIgnoreCase("relative-root")) rule = Rule_relative_root.parse(context);
    else if (rulename.equalsIgnoreCase("peer-root")) rule = Rule_peer_root.parse(context);
    else if (rulename.equalsIgnoreCase("inner-root")) rule = Rule_inner_root.parse(context);
    else if (rulename.equalsIgnoreCase("statement-root")) rule = Rule_statement_root.parse(context);
    else if (rulename.equalsIgnoreCase("xref")) rule = Rule_xref.parse(context);
    else if (rulename.equalsIgnoreCase("subpath")) rule = Rule_subpath.parse(context);
    else if (rulename.equalsIgnoreCase("subsegment")) rule = Rule_subsegment.parse(context);
    else if (rulename.equalsIgnoreCase("singleton")) rule = Rule_singleton.parse(context);
    else if (rulename.equalsIgnoreCase("entity-singleton")) rule = Rule_entity_singleton.parse(context);
    else if (rulename.equalsIgnoreCase("authority-singleton")) rule = Rule_authority_singleton.parse(context);
    else if (rulename.equalsIgnoreCase("type-singleton")) rule = Rule_type_singleton.parse(context);
    else if (rulename.equalsIgnoreCase("person-singleton")) rule = Rule_person_singleton.parse(context);
    else if (rulename.equalsIgnoreCase("group-singleton")) rule = Rule_group_singleton.parse(context);
    else if (rulename.equalsIgnoreCase("reserved-type")) rule = Rule_reserved_type.parse(context);
    else if (rulename.equalsIgnoreCase("unreserved-type")) rule = Rule_unreserved_type.parse(context);
    else if (rulename.equalsIgnoreCase("attribute-singleton")) rule = Rule_attribute_singleton.parse(context);
    else if (rulename.equalsIgnoreCase("class")) rule = Rule_class.parse(context);
    else if (rulename.equalsIgnoreCase("meta-class")) rule = Rule_meta_class.parse(context);
    else if (rulename.equalsIgnoreCase("reserved-meta-class")) rule = Rule_reserved_meta_class.parse(context);
    else if (rulename.equalsIgnoreCase("unreserved-meta-class")) rule = Rule_unreserved_meta_class.parse(context);
    else if (rulename.equalsIgnoreCase("concrete-class")) rule = Rule_concrete_class.parse(context);
    else if (rulename.equalsIgnoreCase("entity-class")) rule = Rule_entity_class.parse(context);
    else if (rulename.equalsIgnoreCase("authority-class")) rule = Rule_authority_class.parse(context);
    else if (rulename.equalsIgnoreCase("type-class")) rule = Rule_type_class.parse(context);
    else if (rulename.equalsIgnoreCase("instance-class")) rule = Rule_instance_class.parse(context);
    else if (rulename.equalsIgnoreCase("reserved-class")) rule = Rule_reserved_class.parse(context);
    else if (rulename.equalsIgnoreCase("unreserved-class")) rule = Rule_unreserved_class.parse(context);
    else if (rulename.equalsIgnoreCase("person-class")) rule = Rule_person_class.parse(context);
    else if (rulename.equalsIgnoreCase("group-class")) rule = Rule_group_class.parse(context);
    else if (rulename.equalsIgnoreCase("mutable-id-class")) rule = Rule_mutable_id_class.parse(context);
    else if (rulename.equalsIgnoreCase("immutable-id-class")) rule = Rule_immutable_id_class.parse(context);
    else if (rulename.equalsIgnoreCase("attribute-class")) rule = Rule_attribute_class.parse(context);
    else if (rulename.equalsIgnoreCase("instance")) rule = Rule_instance.parse(context);
    else if (rulename.equalsIgnoreCase("ordered-instance")) rule = Rule_ordered_instance.parse(context);
    else if (rulename.equalsIgnoreCase("unordered-instance")) rule = Rule_unordered_instance.parse(context);
    else if (rulename.equalsIgnoreCase("mutable-id")) rule = Rule_mutable_id.parse(context);
    else if (rulename.equalsIgnoreCase("immutable-id")) rule = Rule_immutable_id.parse(context);
    else if (rulename.equalsIgnoreCase("definition")) rule = Rule_definition.parse(context);
    else if (rulename.equalsIgnoreCase("authority-definition")) rule = Rule_authority_definition.parse(context);
    else if (rulename.equalsIgnoreCase("authority-path")) rule = Rule_authority_path.parse(context);
    else if (rulename.equalsIgnoreCase("type-definition")) rule = Rule_type_definition.parse(context);
    else if (rulename.equalsIgnoreCase("entity-definition")) rule = Rule_entity_definition.parse(context);
    else if (rulename.equalsIgnoreCase("attribute-definition")) rule = Rule_attribute_definition.parse(context);
    else if (rulename.equalsIgnoreCase("variable")) rule = Rule_variable.parse(context);
    else if (rulename.equalsIgnoreCase("json-value")) rule = Rule_json_value.parse(context);
    else if (rulename.equalsIgnoreCase("json-string")) rule = Rule_json_string.parse(context);
    else if (rulename.equalsIgnoreCase("json-number")) rule = Rule_json_number.parse(context);
    else if (rulename.equalsIgnoreCase("json-boolean")) rule = Rule_json_boolean.parse(context);
    else if (rulename.equalsIgnoreCase("json-array")) rule = Rule_json_array.parse(context);
    else if (rulename.equalsIgnoreCase("json-object")) rule = Rule_json_object.parse(context);
    else if (rulename.equalsIgnoreCase("ipv6-literal")) rule = Rule_ipv6_literal.parse(context);
    else if (rulename.equalsIgnoreCase("uuid-literal")) rule = Rule_uuid_literal.parse(context);
    else if (rulename.equalsIgnoreCase("time-low")) rule = Rule_time_low.parse(context);
    else if (rulename.equalsIgnoreCase("time-mid")) rule = Rule_time_mid.parse(context);
    else if (rulename.equalsIgnoreCase("time-high")) rule = Rule_time_high.parse(context);
    else if (rulename.equalsIgnoreCase("clock-seq")) rule = Rule_clock_seq.parse(context);
    else if (rulename.equalsIgnoreCase("clock-seq-low")) rule = Rule_clock_seq_low.parse(context);
    else if (rulename.equalsIgnoreCase("node")) rule = Rule_node.parse(context);
    else if (rulename.equalsIgnoreCase("iri-char")) rule = Rule_iri_char.parse(context);
    else if (rulename.equalsIgnoreCase("nonparen-delim")) rule = Rule_nonparen_delim.parse(context);
    else if (rulename.equalsIgnoreCase("context-symbol")) rule = Rule_context_symbol.parse(context);
    else if (rulename.equalsIgnoreCase("xdi-char")) rule = Rule_xdi_char.parse(context);
    else if (rulename.equalsIgnoreCase("ALPHA")) rule = Rule_ALPHA.parse(context);
    else if (rulename.equalsIgnoreCase("DIGIT")) rule = Rule_DIGIT.parse(context);
    else if (rulename.equalsIgnoreCase("HEXDIG")) rule = Rule_HEXDIG.parse(context);
    else if (rulename.equalsIgnoreCase("CRLF")) rule = Rule_CRLF.parse(context);
    else if (rulename.equalsIgnoreCase("DQUOTE")) rule = Rule_DQUOTE.parse(context);
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
