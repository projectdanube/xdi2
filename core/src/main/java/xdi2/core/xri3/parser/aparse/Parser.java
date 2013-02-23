/* -----------------------------------------------------------------------------
 * Parser.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.3
 * Produced : Wed Feb 20 10:37:39 CET 2013
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
      arguments.setProperty("Rule", "xdi-context");

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
    if (rulename.equalsIgnoreCase("xdi-context")) rule = Rule_xdi_context.parse(context);
    else if (rulename.equalsIgnoreCase("xdi-statement")) rule = Rule_xdi_statement.parse(context);
    else if (rulename.equalsIgnoreCase("xdi-subject")) rule = Rule_xdi_subject.parse(context);
    else if (rulename.equalsIgnoreCase("xdi-predicate")) rule = Rule_xdi_predicate.parse(context);
    else if (rulename.equalsIgnoreCase("xdi-object")) rule = Rule_xdi_object.parse(context);
    else if (rulename.equalsIgnoreCase("xdi-segment")) rule = Rule_xdi_segment.parse(context);
    else if (rulename.equalsIgnoreCase("subseg")) rule = Rule_subseg.parse(context);
    else if (rulename.equalsIgnoreCase("global-subseg")) rule = Rule_global_subseg.parse(context);
    else if (rulename.equalsIgnoreCase("local-subseg")) rule = Rule_local_subseg.parse(context);
    else if (rulename.equalsIgnoreCase("gcs-char")) rule = Rule_gcs_char.parse(context);
    else if (rulename.equalsIgnoreCase("lcs-char")) rule = Rule_lcs_char.parse(context);
    else if (rulename.equalsIgnoreCase("xref")) rule = Rule_xref.parse(context);
    else if (rulename.equalsIgnoreCase("xref-empty")) rule = Rule_xref_empty.parse(context);
    else if (rulename.equalsIgnoreCase("xref-IRI")) rule = Rule_xref_IRI.parse(context);
    else if (rulename.equalsIgnoreCase("xref-segment")) rule = Rule_xref_segment.parse(context);
    else if (rulename.equalsIgnoreCase("xref-subject-predicate")) rule = Rule_xref_subject_predicate.parse(context);
    else if (rulename.equalsIgnoreCase("xref-statement")) rule = Rule_xref_statement.parse(context);
    else if (rulename.equalsIgnoreCase("xref-literal")) rule = Rule_xref_literal.parse(context);
    else if (rulename.equalsIgnoreCase("literal")) rule = Rule_literal.parse(context);
    else if (rulename.equalsIgnoreCase("xdi-pchar")) rule = Rule_xdi_pchar.parse(context);
    else if (rulename.equalsIgnoreCase("IRI")) rule = Rule_IRI.parse(context);
    else if (rulename.equalsIgnoreCase("scheme")) rule = Rule_scheme.parse(context);
    else if (rulename.equalsIgnoreCase("ihier-part")) rule = Rule_ihier_part.parse(context);
    else if (rulename.equalsIgnoreCase("iauthority")) rule = Rule_iauthority.parse(context);
    else if (rulename.equalsIgnoreCase("iuserinfo")) rule = Rule_iuserinfo.parse(context);
    else if (rulename.equalsIgnoreCase("ihost")) rule = Rule_ihost.parse(context);
    else if (rulename.equalsIgnoreCase("IP-literal")) rule = Rule_IP_literal.parse(context);
    else if (rulename.equalsIgnoreCase("IPvFuture")) rule = Rule_IPvFuture.parse(context);
    else if (rulename.equalsIgnoreCase("IPv6address")) rule = Rule_IPv6address.parse(context);
    else if (rulename.equalsIgnoreCase("ls32")) rule = Rule_ls32.parse(context);
    else if (rulename.equalsIgnoreCase("h16")) rule = Rule_h16.parse(context);
    else if (rulename.equalsIgnoreCase("IPv4address")) rule = Rule_IPv4address.parse(context);
    else if (rulename.equalsIgnoreCase("dec-octet")) rule = Rule_dec_octet.parse(context);
    else if (rulename.equalsIgnoreCase("ireg-name")) rule = Rule_ireg_name.parse(context);
    else if (rulename.equalsIgnoreCase("port")) rule = Rule_port.parse(context);
    else if (rulename.equalsIgnoreCase("ipath-abempty")) rule = Rule_ipath_abempty.parse(context);
    else if (rulename.equalsIgnoreCase("ipath-abs")) rule = Rule_ipath_abs.parse(context);
    else if (rulename.equalsIgnoreCase("ipath-rootless")) rule = Rule_ipath_rootless.parse(context);
    else if (rulename.equalsIgnoreCase("ipath-empty")) rule = Rule_ipath_empty.parse(context);
    else if (rulename.equalsIgnoreCase("isegment")) rule = Rule_isegment.parse(context);
    else if (rulename.equalsIgnoreCase("isegment-nz")) rule = Rule_isegment_nz.parse(context);
    else if (rulename.equalsIgnoreCase("iquery")) rule = Rule_iquery.parse(context);
    else if (rulename.equalsIgnoreCase("iprivate")) rule = Rule_iprivate.parse(context);
    else if (rulename.equalsIgnoreCase("ifragment")) rule = Rule_ifragment.parse(context);
    else if (rulename.equalsIgnoreCase("ipchar")) rule = Rule_ipchar.parse(context);
    else if (rulename.equalsIgnoreCase("iunreserved")) rule = Rule_iunreserved.parse(context);
    else if (rulename.equalsIgnoreCase("pct-encoded")) rule = Rule_pct_encoded.parse(context);
    else if (rulename.equalsIgnoreCase("ucschar")) rule = Rule_ucschar.parse(context);
    else if (rulename.equalsIgnoreCase("reserved")) rule = Rule_reserved.parse(context);
    else if (rulename.equalsIgnoreCase("gen-delims")) rule = Rule_gen_delims.parse(context);
    else if (rulename.equalsIgnoreCase("sub-delims")) rule = Rule_sub_delims.parse(context);
    else if (rulename.equalsIgnoreCase("unreserved")) rule = Rule_unreserved.parse(context);
    else if (rulename.equalsIgnoreCase("ALPHA")) rule = Rule_ALPHA.parse(context);
    else if (rulename.equalsIgnoreCase("BIT")) rule = Rule_BIT.parse(context);
    else if (rulename.equalsIgnoreCase("CHAR")) rule = Rule_CHAR.parse(context);
    else if (rulename.equalsIgnoreCase("CR")) rule = Rule_CR.parse(context);
    else if (rulename.equalsIgnoreCase("CRLF")) rule = Rule_CRLF.parse(context);
    else if (rulename.equalsIgnoreCase("CTL")) rule = Rule_CTL.parse(context);
    else if (rulename.equalsIgnoreCase("DIGIT")) rule = Rule_DIGIT.parse(context);
    else if (rulename.equalsIgnoreCase("DQUOTE")) rule = Rule_DQUOTE.parse(context);
    else if (rulename.equalsIgnoreCase("HEXDIG")) rule = Rule_HEXDIG.parse(context);
    else if (rulename.equalsIgnoreCase("HTAB")) rule = Rule_HTAB.parse(context);
    else if (rulename.equalsIgnoreCase("LF")) rule = Rule_LF.parse(context);
    else if (rulename.equalsIgnoreCase("LWSP")) rule = Rule_LWSP.parse(context);
    else if (rulename.equalsIgnoreCase("OCTET")) rule = Rule_OCTET.parse(context);
    else if (rulename.equalsIgnoreCase("SP")) rule = Rule_SP.parse(context);
    else if (rulename.equalsIgnoreCase("VCHAR")) rule = Rule_VCHAR.parse(context);
    else if (rulename.equalsIgnoreCase("WSP")) rule = Rule_WSP.parse(context);
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
