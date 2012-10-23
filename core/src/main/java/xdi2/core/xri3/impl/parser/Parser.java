/* -----------------------------------------------------------------------------
 * Parser.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.2
 * Produced : Fri Oct 19 08:29:48 CEST 2012
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.impl.parser;

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
      arguments.setProperty("Rule", "xri");

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
    if (rulename.equalsIgnoreCase("xri")) rule = Rule$xri.parse(context);
    else if (rulename.equalsIgnoreCase("xri-reference")) rule = Rule$xri_reference.parse(context);
    else if (rulename.equalsIgnoreCase("relative-xri-ref")) rule = Rule$relative_xri_ref.parse(context);
    else if (rulename.equalsIgnoreCase("relative-xri-part")) rule = Rule$relative_xri_part.parse(context);
    else if (rulename.equalsIgnoreCase("xri-hier-part")) rule = Rule$xri_hier_part.parse(context);
    else if (rulename.equalsIgnoreCase("xri-authority")) rule = Rule$xri_authority.parse(context);
    else if (rulename.equalsIgnoreCase("subseg")) rule = Rule$subseg.parse(context);
    else if (rulename.equalsIgnoreCase("global-subseg")) rule = Rule$global_subseg.parse(context);
    else if (rulename.equalsIgnoreCase("local-subseg")) rule = Rule$local_subseg.parse(context);
    else if (rulename.equalsIgnoreCase("gcs-char")) rule = Rule$gcs_char.parse(context);
    else if (rulename.equalsIgnoreCase("lcs-char")) rule = Rule$lcs_char.parse(context);
    else if (rulename.equalsIgnoreCase("literal")) rule = Rule$literal.parse(context);
    else if (rulename.equalsIgnoreCase("literal-nc")) rule = Rule$literal_nc.parse(context);
    else if (rulename.equalsIgnoreCase("xref")) rule = Rule$xref.parse(context);
    else if (rulename.equalsIgnoreCase("xref-empty")) rule = Rule$xref_empty.parse(context);
    else if (rulename.equalsIgnoreCase("xref-xri-reference")) rule = Rule$xref_xri_reference.parse(context);
    else if (rulename.equalsIgnoreCase("xref-IRI")) rule = Rule$xref_IRI.parse(context);
    else if (rulename.equalsIgnoreCase("xri-path")) rule = Rule$xri_path.parse(context);
    else if (rulename.equalsIgnoreCase("xri-path-abempty")) rule = Rule$xri_path_abempty.parse(context);
    else if (rulename.equalsIgnoreCase("xri-path-abs")) rule = Rule$xri_path_abs.parse(context);
    else if (rulename.equalsIgnoreCase("xri-path-noscheme")) rule = Rule$xri_path_noscheme.parse(context);
    else if (rulename.equalsIgnoreCase("xri-segment")) rule = Rule$xri_segment.parse(context);
    else if (rulename.equalsIgnoreCase("xri-segment-nz")) rule = Rule$xri_segment_nz.parse(context);
    else if (rulename.equalsIgnoreCase("xri-segment-nc")) rule = Rule$xri_segment_nc.parse(context);
    else if (rulename.equalsIgnoreCase("xri-pchar")) rule = Rule$xri_pchar.parse(context);
    else if (rulename.equalsIgnoreCase("xri-pchar-nc")) rule = Rule$xri_pchar_nc.parse(context);
    else if (rulename.equalsIgnoreCase("xri-reserved")) rule = Rule$xri_reserved.parse(context);
    else if (rulename.equalsIgnoreCase("xri-gen-delims")) rule = Rule$xri_gen_delims.parse(context);
    else if (rulename.equalsIgnoreCase("xri-sub-delims")) rule = Rule$xri_sub_delims.parse(context);
    else if (rulename.equalsIgnoreCase("IRI")) rule = Rule$IRI.parse(context);
    else if (rulename.equalsIgnoreCase("scheme")) rule = Rule$scheme.parse(context);
    else if (rulename.equalsIgnoreCase("ihier-part")) rule = Rule$ihier_part.parse(context);
    else if (rulename.equalsIgnoreCase("iauthority")) rule = Rule$iauthority.parse(context);
    else if (rulename.equalsIgnoreCase("iuserinfo")) rule = Rule$iuserinfo.parse(context);
    else if (rulename.equalsIgnoreCase("ihost")) rule = Rule$ihost.parse(context);
    else if (rulename.equalsIgnoreCase("IP-literal")) rule = Rule$IP_literal.parse(context);
    else if (rulename.equalsIgnoreCase("IPvFuture")) rule = Rule$IPvFuture.parse(context);
    else if (rulename.equalsIgnoreCase("IPv6address")) rule = Rule$IPv6address.parse(context);
    else if (rulename.equalsIgnoreCase("ls32")) rule = Rule$ls32.parse(context);
    else if (rulename.equalsIgnoreCase("h16")) rule = Rule$h16.parse(context);
    else if (rulename.equalsIgnoreCase("IPv4address")) rule = Rule$IPv4address.parse(context);
    else if (rulename.equalsIgnoreCase("dec-octet")) rule = Rule$dec_octet.parse(context);
    else if (rulename.equalsIgnoreCase("ireg-name")) rule = Rule$ireg_name.parse(context);
    else if (rulename.equalsIgnoreCase("port")) rule = Rule$port.parse(context);
    else if (rulename.equalsIgnoreCase("ipath-abempty")) rule = Rule$ipath_abempty.parse(context);
    else if (rulename.equalsIgnoreCase("ipath-abs")) rule = Rule$ipath_abs.parse(context);
    else if (rulename.equalsIgnoreCase("ipath-rootless")) rule = Rule$ipath_rootless.parse(context);
    else if (rulename.equalsIgnoreCase("ipath-empty")) rule = Rule$ipath_empty.parse(context);
    else if (rulename.equalsIgnoreCase("isegment")) rule = Rule$isegment.parse(context);
    else if (rulename.equalsIgnoreCase("isegment-nz")) rule = Rule$isegment_nz.parse(context);
    else if (rulename.equalsIgnoreCase("iquery")) rule = Rule$iquery.parse(context);
    else if (rulename.equalsIgnoreCase("iprivate")) rule = Rule$iprivate.parse(context);
    else if (rulename.equalsIgnoreCase("ifragment")) rule = Rule$ifragment.parse(context);
    else if (rulename.equalsIgnoreCase("ipchar")) rule = Rule$ipchar.parse(context);
    else if (rulename.equalsIgnoreCase("iunreserved")) rule = Rule$iunreserved.parse(context);
    else if (rulename.equalsIgnoreCase("pct-encoded")) rule = Rule$pct_encoded.parse(context);
    else if (rulename.equalsIgnoreCase("ucschar")) rule = Rule$ucschar.parse(context);
    else if (rulename.equalsIgnoreCase("reserved")) rule = Rule$reserved.parse(context);
    else if (rulename.equalsIgnoreCase("gen-delims")) rule = Rule$gen_delims.parse(context);
    else if (rulename.equalsIgnoreCase("sub-delims")) rule = Rule$sub_delims.parse(context);
    else if (rulename.equalsIgnoreCase("unreserved")) rule = Rule$unreserved.parse(context);
    else if (rulename.equalsIgnoreCase("ALPHA")) rule = Rule$ALPHA.parse(context);
    else if (rulename.equalsIgnoreCase("BIT")) rule = Rule$BIT.parse(context);
    else if (rulename.equalsIgnoreCase("CHAR")) rule = Rule$CHAR.parse(context);
    else if (rulename.equalsIgnoreCase("CR")) rule = Rule$CR.parse(context);
    else if (rulename.equalsIgnoreCase("CRLF")) rule = Rule$CRLF.parse(context);
    else if (rulename.equalsIgnoreCase("CTL")) rule = Rule$CTL.parse(context);
    else if (rulename.equalsIgnoreCase("DIGIT")) rule = Rule$DIGIT.parse(context);
    else if (rulename.equalsIgnoreCase("DQUOTE")) rule = Rule$DQUOTE.parse(context);
    else if (rulename.equalsIgnoreCase("HEXDIG")) rule = Rule$HEXDIG.parse(context);
    else if (rulename.equalsIgnoreCase("HTAB")) rule = Rule$HTAB.parse(context);
    else if (rulename.equalsIgnoreCase("LF")) rule = Rule$LF.parse(context);
    else if (rulename.equalsIgnoreCase("LWSP")) rule = Rule$LWSP.parse(context);
    else if (rulename.equalsIgnoreCase("OCTET")) rule = Rule$OCTET.parse(context);
    else if (rulename.equalsIgnoreCase("SP")) rule = Rule$SP.parse(context);
    else if (rulename.equalsIgnoreCase("VCHAR")) rule = Rule$VCHAR.parse(context);
    else if (rulename.equalsIgnoreCase("WSP")) rule = Rule$WSP.parse(context);
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
