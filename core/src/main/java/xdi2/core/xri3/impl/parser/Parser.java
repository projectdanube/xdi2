package xdi2.core.xri3.impl.parser;

/* -----------------------------------------------------------------------------
 * Parser.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 0.5
 * Produced : Sat Dec 20 01:35:48 CET 2008
 *
 * -----------------------------------------------------------------------------
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.regex.Pattern;

public class Parser
{
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
        Parser parser = new Parser();
        Rule rule = null;

        parser.traceOff();
        if (arguments.getProperty("Trace").equals("On")) parser.traceOn();

        if (arguments.getProperty("File") != null)
          rule = parser.parse(arguments.getProperty("Rule"), new File(arguments.getProperty("File")));
        else if (arguments.getProperty("String") != null)
          rule = parser.parse(arguments.getProperty("Rule"), arguments.getProperty("String"));

        if (arguments.getProperty("Visitor") != null)
        {
          Visitor visitor = 
            (Visitor)Class.forName(arguments.getProperty("Visitor")).newInstance();
          visitor.visit(rule);
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

  /* ---------------------------------------------------------------------------
   * public parsers
   * ---------------------------------------------------------------------------
   */

  public Rule parse(String rulename, String string)
  throws IllegalArgumentException,
         ParserException
  {
    if (rulename == null)
      throw new IllegalArgumentException("null rulename");
    if (string == null)
      throw new IllegalArgumentException("null string");

    return decode(rulename, string);
  }

  public Rule parse(String rulename, InputStream in)
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

    return decode(rulename, out.toString());
  }

  public Rule parse(String rulename, File file)
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

    return decode(rulename, out.toString());
  }

  /* ---------------------------------------------------------------------------
   * private data
   * ---------------------------------------------------------------------------
   */

  private String text;
  private int    index = 0;

  private boolean trace = false;
  private int level = 0;
  private int error = -1;
  private Stack<String> callStack = new Stack<String>();
  private Stack<String> errorStack = new Stack<String>();

  static final private String newline = System.getProperty("line.separator", "\n");

  /* ---------------------------------------------------------------------------
   * private trace
   * ---------------------------------------------------------------------------
   */

  private void traceOn() {trace = true;}
  private void traceOff() {trace = false;}

  private void push(String function)
  {
    callStack.push(function);
    if (trace)
    {
      System.out.println("-> " + ++level + ": " + function + "()");
      System.out.println(index + ": " + text.substring(index, index + 10 > text.length() ? text.length() : index + 10).replaceAll("[^\\p{Print}]", " "));
    }
  }

  private void push(String function, String regex)
  {
    callStack.push(function);
    if (trace)
    {
      System.out.println("-> " + ++level + ": " + function + "(" + regex + ")");
      System.out.println(index + ": " + text.substring(index, index + 10 > text.length() ? text.length() : index + 10).replaceAll("[^\\p{Print}]", " "));
    }
  }

  private void push(String function, String spelling, String regex)
  {
    callStack.push(function);
    if (trace)
    {
      System.out.println("-> " + ++level + ": " + function + "(" + spelling + ", " + regex + ")");
      System.out.println(index + ": " + text.substring(index, index + 10 > text.length() ? text.length() : index + 10).replaceAll("[^\\p{Print}]", " "));
    }
  }

  private void pop(String function, boolean result, int length)
  {
    callStack.pop();
    if (trace)
    {
      System.out.println("<- " + level-- + ": " + function + "(" + (result ? "true," : "false,") + length + ")");
    }
    if (!result)
    {
      if (index > error)
      {
        error = index;
        errorStack = new Stack<String>();
        errorStack.addAll(callStack);
      }
    }
    else
    {
      if (index > error) error = -1;
    }
  }

  /* ---------------------------------------------------------------------------
   * private decoders
   * ---------------------------------------------------------------------------
   */

  private Rule decode(String rulename, String text)
  throws ParserException
  {
    this.text = text;

    Rule rule = null;
    if (true == false) ;
    else if (rulename.equalsIgnoreCase("xri")) rule = decode_xri();
    else if (rulename.equalsIgnoreCase("xri-reference")) rule = decode_xri_reference();
    else if (rulename.equalsIgnoreCase("relative-xri-ref")) rule = decode_relative_xri_ref();
    else if (rulename.equalsIgnoreCase("relative-xri-part")) rule = decode_relative_xri_part();
    else if (rulename.equalsIgnoreCase("xri-hier-part")) rule = decode_xri_hier_part();
    else if (rulename.equalsIgnoreCase("xri-authority")) rule = decode_xri_authority();
    else if (rulename.equalsIgnoreCase("subseg")) rule = decode_subseg();
    else if (rulename.equalsIgnoreCase("global-subseg")) rule = decode_global_subseg();
    else if (rulename.equalsIgnoreCase("local-subseg")) rule = decode_local_subseg();
    else if (rulename.equalsIgnoreCase("gcs-char")) rule = decode_gcs_char();
    else if (rulename.equalsIgnoreCase("lcs-char")) rule = decode_lcs_char();
    else if (rulename.equalsIgnoreCase("literal")) rule = decode_literal();
    else if (rulename.equalsIgnoreCase("literal-nc")) rule = decode_literal_nc();
    else if (rulename.equalsIgnoreCase("xref")) rule = decode_xref();
    else if (rulename.equalsIgnoreCase("xref-empty")) rule = decode_xref_empty();
    else if (rulename.equalsIgnoreCase("xref-xri-reference")) rule = decode_xref_xri_reference();
    else if (rulename.equalsIgnoreCase("xref-IRI")) rule = decode_xref_IRI();
    else if (rulename.equalsIgnoreCase("xri-path")) rule = decode_xri_path();
    else if (rulename.equalsIgnoreCase("xri-path-abempty")) rule = decode_xri_path_abempty();
    else if (rulename.equalsIgnoreCase("xri-path-abs")) rule = decode_xri_path_abs();
    else if (rulename.equalsIgnoreCase("xri-path-noscheme")) rule = decode_xri_path_noscheme();
    else if (rulename.equalsIgnoreCase("xri-segment")) rule = decode_xri_segment();
    else if (rulename.equalsIgnoreCase("xri-segment-nz")) rule = decode_xri_segment_nz();
    else if (rulename.equalsIgnoreCase("xri-segment-nc")) rule = decode_xri_segment_nc();
    else if (rulename.equalsIgnoreCase("xri-pchar")) rule = decode_xri_pchar();
    else if (rulename.equalsIgnoreCase("xri-pchar-nc")) rule = decode_xri_pchar_nc();
    else if (rulename.equalsIgnoreCase("xri-reserved")) rule = decode_xri_reserved();
    else if (rulename.equalsIgnoreCase("xri-gen-delims")) rule = decode_xri_gen_delims();
    else if (rulename.equalsIgnoreCase("xri-sub-delims")) rule = decode_xri_sub_delims();
    else if (rulename.equalsIgnoreCase("IRI")) rule = decode_IRI();
    else if (rulename.equalsIgnoreCase("scheme")) rule = decode_scheme();
    else if (rulename.equalsIgnoreCase("ihier-part")) rule = decode_ihier_part();
    else if (rulename.equalsIgnoreCase("iauthority")) rule = decode_iauthority();
    else if (rulename.equalsIgnoreCase("iuserinfo")) rule = decode_iuserinfo();
    else if (rulename.equalsIgnoreCase("ihost")) rule = decode_ihost();
    else if (rulename.equalsIgnoreCase("IP-literal")) rule = decode_IP_literal();
    else if (rulename.equalsIgnoreCase("IPvFuture")) rule = decode_IPvFuture();
    else if (rulename.equalsIgnoreCase("IPv6address")) rule = decode_IPv6address();
    else if (rulename.equalsIgnoreCase("ls32")) rule = decode_ls32();
    else if (rulename.equalsIgnoreCase("h16")) rule = decode_h16();
    else if (rulename.equalsIgnoreCase("IPv4address")) rule = decode_IPv4address();
    else if (rulename.equalsIgnoreCase("dec-octet")) rule = decode_dec_octet();
    else if (rulename.equalsIgnoreCase("ireg-name")) rule = decode_ireg_name();
    else if (rulename.equalsIgnoreCase("port")) rule = decode_port();
    else if (rulename.equalsIgnoreCase("ipath-abempty")) rule = decode_ipath_abempty();
    else if (rulename.equalsIgnoreCase("ipath-abs")) rule = decode_ipath_abs();
    else if (rulename.equalsIgnoreCase("ipath-rootless")) rule = decode_ipath_rootless();
    else if (rulename.equalsIgnoreCase("ipath-empty")) rule = decode_ipath_empty();
    else if (rulename.equalsIgnoreCase("isegment")) rule = decode_isegment();
    else if (rulename.equalsIgnoreCase("isegment-nz")) rule = decode_isegment_nz();
    else if (rulename.equalsIgnoreCase("iquery")) rule = decode_iquery();
    else if (rulename.equalsIgnoreCase("iprivate")) rule = decode_iprivate();
    else if (rulename.equalsIgnoreCase("ifragment")) rule = decode_ifragment();
    else if (rulename.equalsIgnoreCase("ipchar")) rule = decode_ipchar();
    else if (rulename.equalsIgnoreCase("iunreserved")) rule = decode_iunreserved();
    else if (rulename.equalsIgnoreCase("pct-encoded")) rule = decode_pct_encoded();
    else if (rulename.equalsIgnoreCase("ucschar")) rule = decode_ucschar();
    else if (rulename.equalsIgnoreCase("reserved")) rule = decode_reserved();
    else if (rulename.equalsIgnoreCase("gen-delims")) rule = decode_gen_delims();
    else if (rulename.equalsIgnoreCase("sub-delims")) rule = decode_sub_delims();
    else if (rulename.equalsIgnoreCase("unreserved")) rule = decode_unreserved();
    else if (rulename.equalsIgnoreCase("ALPHA")) rule = decode_ALPHA();
    else if (rulename.equalsIgnoreCase("BIT")) rule = decode_BIT();
    else if (rulename.equalsIgnoreCase("CHAR")) rule = decode_CHAR();
    else if (rulename.equalsIgnoreCase("CR")) rule = decode_CR();
    else if (rulename.equalsIgnoreCase("CRLF")) rule = decode_CRLF();
    else if (rulename.equalsIgnoreCase("CTL")) rule = decode_CTL();
    else if (rulename.equalsIgnoreCase("DIGIT")) rule = decode_DIGIT();
    else if (rulename.equalsIgnoreCase("DQUOTE")) rule = decode_DQUOTE();
    else if (rulename.equalsIgnoreCase("HEXDIG")) rule = decode_HEXDIG();
    else if (rulename.equalsIgnoreCase("HTAB")) rule = decode_HTAB();
    else if (rulename.equalsIgnoreCase("LF")) rule = decode_LF();
    else if (rulename.equalsIgnoreCase("LWSP")) rule = decode_LWSP();
    else if (rulename.equalsIgnoreCase("OCTET")) rule = decode_OCTET();
    else if (rulename.equalsIgnoreCase("SP")) rule = decode_SP();
    else if (rulename.equalsIgnoreCase("VCHAR")) rule = decode_VCHAR();
    else if (rulename.equalsIgnoreCase("WSP")) rule = decode_WSP();
    else throw new IllegalArgumentException("unknown rule");

    if (rule == null)
    {
      String marker = "                              ";
      StringBuffer errorBuffer = new StringBuffer();
      int start = (error < 30) ? 0: error - 30;
      int end = (text.length() < error + 30) ? text.length() : error + 30;

      errorBuffer.append("rule \"" + (String)errorStack.peek() + "\" failed" + newline);
      errorBuffer.append(text.substring(start, end).replaceAll("[^\\p{Print}]", " ") + newline);
      errorBuffer.append(marker.substring(0, error < 30 ? error : 30) + "^" + newline);
      errorBuffer.append("rule stack:");

      for (String error :  errorStack)
        errorBuffer.append(newline + "  " + error);

      throw new ParserException(errorBuffer.toString());
    }

    if (text.length() > index)
    {
      String marker = "                              ";
      StringBuffer errorBuffer = new StringBuffer();
      int start = (index < 30) ? 0: index - 30;
      int end = (text.length() < index + 30) ? text.length(): index + 30;

      errorBuffer.append("extra data found" + newline);
      errorBuffer.append(text.substring(start, end).replaceAll("[^\\p{Print}]", " ") + newline);
      errorBuffer.append(marker.substring(0, index < 30 ? index : 30) + "^" + newline);

      throw new ParserException(errorBuffer.toString());
    }

    return rule;
  }

  private xri decode_xri()
  {
    push("xri");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_hier_part();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("?");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_iquery();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("#");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_ifragment();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri", decoded, index - s0);

    return (xri)rule;
  }

  private xri_reference decode_xri_reference()
  {
    push("xri-reference");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_relative_xri_ref();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_reference(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-reference", decoded, index - s0);

    return (xri_reference)rule;
  }

  private relative_xri_ref decode_relative_xri_ref()
  {
    push("relative-xri-ref");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_relative_xri_part();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("?");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_iquery();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("#");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_ifragment();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new relative_xri_ref(text.substring(s0, index), e0);
    else
      index = s0;

    pop("relative-xri-ref", decoded, index - s0);

    return (relative_xri_ref)rule;
  }

  private relative_xri_part decode_relative_xri_part()
  {
    push("relative-xri-part");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_path_abs();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_path_noscheme();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ipath_empty();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new relative_xri_part(text.substring(s0, index), e0);
    else
      index = s0;

    pop("relative-xri-part", decoded, index - s0);

    return (relative_xri_part)rule;
  }

  private xri_hier_part decode_xri_hier_part()
  {
    push("xri-hier-part");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_authority();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_path_abempty();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_hier_part(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-hier-part", decoded, index - s0);

    return (xri_hier_part)rule;
  }

  private xri_authority decode_xri_authority()
  {
    push("xri-authority");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_global_subseg();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            rule = decode_subseg();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_authority(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-authority", decoded, index - s0);

    return (xri_authority)rule;
  }

  private subseg decode_subseg()
  {
    push("subseg");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_global_subseg();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_local_subseg();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xref();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new subseg(text.substring(s0, index), e0);
    else
      index = s0;

    pop("subseg", decoded, index - s0);

    return (subseg)rule;
  }

  private global_subseg decode_global_subseg()
  {
    push("global-subseg");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_gcs_char();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_local_subseg();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_xref();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_literal();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new global_subseg(text.substring(s0, index), e0);
    else
      index = s0;

    pop("global-subseg", decoded, index - s0);

    return (global_subseg)rule;
  }

  private local_subseg decode_local_subseg()
  {
    push("local-subseg");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_lcs_char();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_xref();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_literal();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new local_subseg(text.substring(s0, index), e0);
    else
      index = s0;

    pop("local-subseg", decoded, index - s0);

    return (local_subseg)rule;
  }

  private gcs_char decode_gcs_char()
  {
    push("gcs-char");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("=");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("@");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("+");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("$");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new gcs_char(text.substring(s0, index), e0);
    else
      index = s0;

    pop("gcs-char", decoded, index - s0);

    return (gcs_char)rule;
  }

  private lcs_char decode_lcs_char()
  {
    push("lcs-char");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("*");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("!");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new lcs_char(text.substring(s0, index), e0);
    else
      index = s0;

    pop("lcs-char", decoded, index - s0);

    return (lcs_char)rule;
  }

  private literal decode_literal()
  {
    push("literal");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_pchar();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          while (f1)
          {
            rule = decode_xri_pchar();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 >= 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new literal(text.substring(s0, index), e0);
    else
      index = s0;

    pop("literal", decoded, index - s0);

    return (literal)rule;
  }

  private literal_nc decode_literal_nc()
  {
    push("literal-nc");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_pchar_nc();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          while (f1)
          {
            rule = decode_xri_pchar_nc();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 >= 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new literal_nc(text.substring(s0, index), e0);
    else
      index = s0;

    pop("literal-nc", decoded, index - s0);

    return (literal_nc)rule;
  }

  private xref decode_xref()
  {
    push("xref");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xref_empty();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xref_xri_reference();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xref_IRI();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xref(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xref", decoded, index - s0);

    return (xref)rule;
  }

  private xref_empty decode_xref_empty()
  {
    push("xref-empty");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("()");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xref_empty(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xref-empty", decoded, index - s0);

    return (xref_empty)rule;
  }

  private xref_xri_reference decode_xref_xri_reference()
  {
    push("xref-xri-reference");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("(");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_reference();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(")");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xref_xri_reference(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xref-xri-reference", decoded, index - s0);

    return (xref_xri_reference)rule;
  }

  private xref_IRI decode_xref_IRI()
  {
    push("xref-IRI");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("(");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_IRI();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(")");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xref_IRI(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xref-IRI", decoded, index - s0);

    return (xref_IRI)rule;
  }

  private xri_path decode_xri_path()
  {
    push("xri-path");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_path_abempty();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_path_abs();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_path_noscheme();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ipath_empty();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_path(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-path", decoded, index - s0);

    return (xri_path)rule;
  }

  private xri_path_abempty decode_xri_path_abempty()
  {
    push("xri-path-abempty");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("/");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_xri_segment();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_path_abempty(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-path-abempty", decoded, index - s0);

    return (xri_path_abempty)rule;
  }

  private xri_path_abs decode_xri_path_abs()
  {
    push("xri-path-abs");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("/");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_xri_segment_nz();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  while (f2)
                  {
                    decoded = false;
                    if (!decoded)
                    {
                      {
                        ArrayList<Rule> e3 = new ArrayList<Rule>();
                        int s3 = index;
                        decoded = true;
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_StringValue("/");
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_xri_segment();
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                          e2.addAll(e3);
                        else
                          index = s3;
                      }
                    }
                    if (decoded) c2++;
                    f2 = decoded;
                  }
                  decoded = true;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_path_abs(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-path-abs", decoded, index - s0);

    return (xri_path_abs)rule;
  }

  private xri_path_noscheme decode_xri_path_noscheme()
  {
    push("xri-path-noscheme");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_segment_nc();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("/");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_xri_segment();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_path_noscheme(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-path-noscheme", decoded, index - s0);

    return (xri_path_noscheme)rule;
  }

  private xri_segment decode_xri_segment()
  {
    push("xri-segment");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_literal();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            rule = decode_subseg();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_segment(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-segment", decoded, index - s0);

    return (xri_segment)rule;
  }

  private xri_segment_nz decode_xri_segment_nz()
  {
    push("xri-segment-nz");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_literal();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_subseg();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            rule = decode_subseg();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_segment_nz(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-segment-nz", decoded, index - s0);

    return (xri_segment_nz)rule;
  }

  private xri_segment_nc decode_xri_segment_nc()
  {
    push("xri-segment-nc");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_literal_nc();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_subseg();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            rule = decode_subseg();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_segment_nc(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-segment-nc", decoded, index - s0);

    return (xri_segment_nc)rule;
  }

  private xri_pchar decode_xri_pchar()
  {
    push("xri-pchar");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_iunreserved();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_pct_encoded();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_sub_delims();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(":");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_pchar(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-pchar", decoded, index - s0);

    return (xri_pchar)rule;
  }

  private xri_pchar_nc decode_xri_pchar_nc()
  {
    push("xri-pchar-nc");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_iunreserved();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_pct_encoded();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_sub_delims();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_pchar_nc(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-pchar-nc", decoded, index - s0);

    return (xri_pchar_nc)rule;
  }

  private xri_reserved decode_xri_reserved()
  {
    push("xri-reserved");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_gen_delims();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_xri_sub_delims();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_reserved(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-reserved", decoded, index - s0);

    return (xri_reserved)rule;
  }

  private xri_gen_delims decode_xri_gen_delims()
  {
    push("xri-gen-delims");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(":");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("/");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("?");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("#");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("[");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("]");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("(");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(")");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_gcs_char();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_lcs_char();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_gen_delims(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-gen-delims", decoded, index - s0);

    return (xri_gen_delims)rule;
  }

  private xri_sub_delims decode_xri_sub_delims()
  {
    push("xri-sub-delims");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("&");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(";");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(",");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("'");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new xri_sub_delims(text.substring(s0, index), e0);
    else
      index = s0;

    pop("xri-sub-delims", decoded, index - s0);

    return (xri_sub_delims)rule;
  }

  private IRI decode_IRI()
  {
    push("IRI");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_scheme();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(":");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ihier_part();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("?");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_iquery();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("#");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_ifragment();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new IRI(text.substring(s0, index), e0);
    else
      index = s0;

    pop("IRI", decoded, index - s0);

    return (IRI)rule;
  }

  private scheme decode_scheme()
  {
    push("scheme");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ALPHA();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_ALPHA();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_DIGIT();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("+");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("-");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue(".");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new scheme(text.substring(s0, index), e0);
    else
      index = s0;

    pop("scheme", decoded, index - s0);

    return (scheme)rule;
  }

  private ihier_part decode_ihier_part()
  {
    push("ihier-part");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("//");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_iauthority();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ipath_abempty();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ipath_abs();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ipath_rootless();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ipath_empty();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new ihier_part(text.substring(s0, index), e0);
    else
      index = s0;

    pop("ihier-part", decoded, index - s0);

    return (ihier_part)rule;
  }

  private iauthority decode_iauthority()
  {
    push("iauthority");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_iuserinfo();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("@");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ihost();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue(":");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_port();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new iauthority(text.substring(s0, index), e0);
    else
      index = s0;

    pop("iauthority", decoded, index - s0);

    return (iauthority)rule;
  }

  private iuserinfo decode_iuserinfo()
  {
    push("iuserinfo");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_iunreserved();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_pct_encoded();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_sub_delims();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue(":");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new iuserinfo(text.substring(s0, index), e0);
    else
      index = s0;

    pop("iuserinfo", decoded, index - s0);

    return (iuserinfo)rule;
  }

  private ihost decode_ihost()
  {
    push("ihost");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_IP_literal();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_IPv4address();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ireg_name();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new ihost(text.substring(s0, index), e0);
    else
      index = s0;

    pop("ihost", decoded, index - s0);

    return (ihost)rule;
  }

  private IP_literal decode_IP_literal()
  {
    push("IP-literal");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("[");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_IPv6address();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_IPvFuture();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("]");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new IP_literal(text.substring(s0, index), e0);
    else
      index = s0;

    pop("IP-literal", decoded, index - s0);

    return (IP_literal)rule;
  }

  private IPvFuture decode_IPvFuture()
  {
    push("IPvFuture");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("v");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_HEXDIG();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          while (f1)
          {
            rule = decode_HEXDIG();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 >= 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(".");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_unreserved();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_sub_delims();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue(":");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          while (f1)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_unreserved();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_sub_delims();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue(":");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = c1 >= 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new IPvFuture(text.substring(s0, index), e0);
    else
      index = s0;

    pop("IPvFuture", decoded, index - s0);

    return (IPvFuture)rule;
  }

  private IPv6address decode_IPv6address()
  {
    push("IPv6address");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 6 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_h16();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue(":");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = c1 == 6;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ls32();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("::");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 5 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_h16();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue(":");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = c1 == 5;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ls32();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_h16();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("::");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 4 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_h16();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue(":");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = c1 == 4;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ls32();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    decoded = false;
                    if (!decoded)
                    {
                      {
                        ArrayList<Rule> e3 = new ArrayList<Rule>();
                        int s3 = index;
                        decoded = true;
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_h16();
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_StringValue(":");
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                          e2.addAll(e3);
                        else
                          index = s3;
                      }
                    }
                    if (decoded) c2++;
                    f2 = decoded;
                  }
                  decoded = true;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_h16();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("::");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 3 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_h16();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue(":");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = c1 == 3;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ls32();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 2 && f2; i2++)
                  {
                    decoded = false;
                    if (!decoded)
                    {
                      {
                        ArrayList<Rule> e3 = new ArrayList<Rule>();
                        int s3 = index;
                        decoded = true;
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_h16();
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_StringValue(":");
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                          e2.addAll(e3);
                        else
                          index = s3;
                      }
                    }
                    if (decoded) c2++;
                    f2 = decoded;
                  }
                  decoded = true;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_h16();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("::");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 2 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_h16();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue(":");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = c1 == 2;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ls32();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 3 && f2; i2++)
                  {
                    decoded = false;
                    if (!decoded)
                    {
                      {
                        ArrayList<Rule> e3 = new ArrayList<Rule>();
                        int s3 = index;
                        decoded = true;
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_h16();
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_StringValue(":");
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                          e2.addAll(e3);
                        else
                          index = s3;
                      }
                    }
                    if (decoded) c2++;
                    f2 = decoded;
                  }
                  decoded = true;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_h16();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("::");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_h16();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(":");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ls32();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 4 && f2; i2++)
                  {
                    decoded = false;
                    if (!decoded)
                    {
                      {
                        ArrayList<Rule> e3 = new ArrayList<Rule>();
                        int s3 = index;
                        decoded = true;
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_h16();
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_StringValue(":");
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                          e2.addAll(e3);
                        else
                          index = s3;
                      }
                    }
                    if (decoded) c2++;
                    f2 = decoded;
                  }
                  decoded = true;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_h16();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("::");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ls32();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 5 && f2; i2++)
                  {
                    decoded = false;
                    if (!decoded)
                    {
                      {
                        ArrayList<Rule> e3 = new ArrayList<Rule>();
                        int s3 = index;
                        decoded = true;
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_h16();
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_StringValue(":");
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                          e2.addAll(e3);
                        else
                          index = s3;
                      }
                    }
                    if (decoded) c2++;
                    f2 = decoded;
                  }
                  decoded = true;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_h16();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("::");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_h16();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 6 && f2; i2++)
                  {
                    decoded = false;
                    if (!decoded)
                    {
                      {
                        ArrayList<Rule> e3 = new ArrayList<Rule>();
                        int s3 = index;
                        decoded = true;
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_h16();
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_StringValue(":");
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                          e2.addAll(e3);
                        else
                          index = s3;
                      }
                    }
                    if (decoded) c2++;
                    f2 = decoded;
                  }
                  decoded = true;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_h16();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("::");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new IPv6address(text.substring(s0, index), e0);
    else
      index = s0;

    pop("IPv6address", decoded, index - s0);

    return (IPv6address)rule;
  }

  private ls32 decode_ls32()
  {
    push("ls32");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_h16();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue(":");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_h16();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_IPv4address();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new ls32(text.substring(s0, index), e0);
    else
      index = s0;

    pop("ls32", decoded, index - s0);

    return (ls32)rule;
  }

  private h16 decode_h16()
  {
    push("h16");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_HEXDIG();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          for (int i1 = 1; i1 < 4 && f1; i1++)
          {
            rule = decode_HEXDIG();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 >= 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new h16(text.substring(s0, index), e0);
    else
      index = s0;

    pop("h16", decoded, index - s0);

    return (h16)rule;
  }

  private IPv4address decode_IPv4address()
  {
    push("IPv4address");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_dec_octet();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(".");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_dec_octet();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(".");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_dec_octet();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(".");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_dec_octet();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new IPv4address(text.substring(s0, index), e0);
    else
      index = s0;

    pop("IPv4address", decoded, index - s0);

    return (IPv4address)rule;
  }

  private dec_octet decode_dec_octet()
  {
    push("dec-octet");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_DIGIT();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x31-39", "[\\x31-\\x39]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_DIGIT();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("1");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 2 && f1; i1++)
          {
            rule = decode_DIGIT();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 2;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("2");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x30-34", "[\\x30-\\x34]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_DIGIT();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("25");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x30-35", "[\\x30-\\x35]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new dec_octet(text.substring(s0, index), e0);
    else
      index = s0;

    pop("dec-octet", decoded, index - s0);

    return (dec_octet)rule;
  }

  private ireg_name decode_ireg_name()
  {
    push("ireg-name");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_iunreserved();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_pct_encoded();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_sub_delims();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new ireg_name(text.substring(s0, index), e0);
    else
      index = s0;

    pop("ireg-name", decoded, index - s0);

    return (ireg_name)rule;
  }

  private port decode_port()
  {
    push("port");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            rule = decode_DIGIT();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new port(text.substring(s0, index), e0);
    else
      index = s0;

    pop("port", decoded, index - s0);

    return (port)rule;
  }

  private ipath_abempty decode_ipath_abempty()
  {
    push("ipath-abempty");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("/");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_isegment();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new ipath_abempty(text.substring(s0, index), e0);
    else
      index = s0;

    pop("ipath-abempty", decoded, index - s0);

    return (ipath_abempty)rule;
  }

  private ipath_abs decode_ipath_abs()
  {
    push("ipath-abs");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("/");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_isegment_nz();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  while (f2)
                  {
                    decoded = false;
                    if (!decoded)
                    {
                      {
                        ArrayList<Rule> e3 = new ArrayList<Rule>();
                        int s3 = index;
                        decoded = true;
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_StringValue("/");
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                        {
                          boolean f3 = true;
                          int c3 = 0;
                          for (int i3 = 0; i3 < 1 && f3; i3++)
                          {
                            rule = decode_isegment();
                            if ((f3 = rule != null))
                            {
                              e3.add(rule);
                              c3++;
                            }
                          }
                          decoded = c3 == 1;
                        }
                        if (decoded)
                          e2.addAll(e3);
                        else
                          index = s3;
                      }
                    }
                    if (decoded) c2++;
                    f2 = decoded;
                  }
                  decoded = true;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new ipath_abs(text.substring(s0, index), e0);
    else
      index = s0;

    pop("ipath-abs", decoded, index - s0);

    return (ipath_abs)rule;
  }

  private ipath_rootless decode_ipath_rootless()
  {
    push("ipath-rootless");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_isegment_nz();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("/");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_isegment();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new ipath_rootless(text.substring(s0, index), e0);
    else
      index = s0;

    pop("ipath-rootless", decoded, index - s0);

    return (ipath_rootless)rule;
  }

  private ipath_empty decode_ipath_empty()
  {
    push("ipath-empty");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new ipath_empty(text.substring(s0, index), e0);
    else
      index = s0;

    pop("ipath-empty", decoded, index - s0);

    return (ipath_empty)rule;
  }

  private isegment decode_isegment()
  {
    push("isegment");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            rule = decode_ipchar();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new isegment(text.substring(s0, index), e0);
    else
      index = s0;

    pop("isegment", decoded, index - s0);

    return (isegment)rule;
  }

  private isegment_nz decode_isegment_nz()
  {
    push("isegment-nz");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ipchar();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          while (f1)
          {
            rule = decode_ipchar();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 >= 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new isegment_nz(text.substring(s0, index), e0);
    else
      index = s0;

    pop("isegment-nz", decoded, index - s0);

    return (isegment_nz)rule;
  }

  private iquery decode_iquery()
  {
    push("iquery");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_ipchar();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_iprivate();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("/");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("?");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new iquery(text.substring(s0, index), e0);
    else
      index = s0;

    pop("iquery", decoded, index - s0);

    return (iquery)rule;
  }

  private iprivate decode_iprivate()
  {
    push("iprivate");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%xE000-F8FF", "[\\uE000-\\uF8FF]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new iprivate(text.substring(s0, index), e0);
    else
      index = s0;

    pop("iprivate", decoded, index - s0);

    return (iprivate)rule;
  }

  private ifragment decode_ifragment()
  {
    push("ifragment");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_ipchar();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("/");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_StringValue("?");
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new ifragment(text.substring(s0, index), e0);
    else
      index = s0;

    pop("ifragment", decoded, index - s0);

    return (ifragment)rule;
  }

  private ipchar decode_ipchar()
  {
    push("ipchar");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_iunreserved();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_pct_encoded();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_sub_delims();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(":");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("@");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new ipchar(text.substring(s0, index), e0);
    else
      index = s0;

    pop("ipchar", decoded, index - s0);

    return (ipchar)rule;
  }

  private iunreserved decode_iunreserved()
  {
    push("iunreserved");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ALPHA();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_DIGIT();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("-");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(".");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("_");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("~");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ucschar();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new iunreserved(text.substring(s0, index), e0);
    else
      index = s0;

    pop("iunreserved", decoded, index - s0);

    return (iunreserved)rule;
  }

  private pct_encoded decode_pct_encoded()
  {
    push("pct-encoded");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("%");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_HEXDIG();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_HEXDIG();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new pct_encoded(text.substring(s0, index), e0);
    else
      index = s0;

    pop("pct-encoded", decoded, index - s0);

    return (pct_encoded)rule;
  }

  private ucschar decode_ucschar()
  {
    push("ucschar");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%xA0-D7FF", "[\\xA0-\\uD7FF]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%xF900-FDCF", "[\\uF900-\\uFDCF]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%xFDF0-FFEF", "[\\uFDF0-\\uFFEF]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new ucschar(text.substring(s0, index), e0);
    else
      index = s0;

    pop("ucschar", decoded, index - s0);

    return (ucschar)rule;
  }

  private reserved decode_reserved()
  {
    push("reserved");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_gen_delims();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_sub_delims();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new reserved(text.substring(s0, index), e0);
    else
      index = s0;

    pop("reserved", decoded, index - s0);

    return (reserved)rule;
  }

  private gen_delims decode_gen_delims()
  {
    push("gen-delims");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(":");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("/");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("?");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("#");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("[");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("]");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("@");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new gen_delims(text.substring(s0, index), e0);
    else
      index = s0;

    pop("gen-delims", decoded, index - s0);

    return (gen_delims)rule;
  }

  private sub_delims decode_sub_delims()
  {
    push("sub-delims");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("!");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("$");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("&");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("'");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("*");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("+");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(",");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(";");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("=");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new sub_delims(text.substring(s0, index), e0);
    else
      index = s0;

    pop("sub-delims", decoded, index - s0);

    return (sub_delims)rule;
  }

  private unreserved decode_unreserved()
  {
    push("unreserved");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_ALPHA();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_DIGIT();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("-");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue(".");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("_");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("~");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new unreserved(text.substring(s0, index), e0);
    else
      index = s0;

    pop("unreserved", decoded, index - s0);

    return (unreserved)rule;
  }

  private ALPHA decode_ALPHA()
  {
    push("ALPHA");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x41-5A", "[\\x41-\\x5A]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x61-7A", "[\\x61-\\x7A]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new ALPHA(text.substring(s0, index), e0);
    else
      index = s0;

    pop("ALPHA", decoded, index - s0);

    return (ALPHA)rule;
  }

  private BIT decode_BIT()
  {
    push("BIT");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("0");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("1");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new BIT(text.substring(s0, index), e0);
    else
      index = s0;

    pop("BIT", decoded, index - s0);

    return (BIT)rule;
  }

  private CHAR decode_CHAR()
  {
    push("CHAR");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x01-7F", "[\\x01-\\x7F]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new CHAR(text.substring(s0, index), e0);
    else
      index = s0;

    pop("CHAR", decoded, index - s0);

    return (CHAR)rule;
  }

  private CR decode_CR()
  {
    push("CR");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x0D", "[\\x0D]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new CR(text.substring(s0, index), e0);
    else
      index = s0;

    pop("CR", decoded, index - s0);

    return (CR)rule;
  }

  private CRLF decode_CRLF()
  {
    push("CRLF");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_CR();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_LF();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new CRLF(text.substring(s0, index), e0);
    else
      index = s0;

    pop("CRLF", decoded, index - s0);

    return (CRLF)rule;
  }

  private CTL decode_CTL()
  {
    push("CTL");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x00-1F", "[\\x00-\\x1F]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x7F", "[\\x7F]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new CTL(text.substring(s0, index), e0);
    else
      index = s0;

    pop("CTL", decoded, index - s0);

    return (CTL)rule;
  }

  private DIGIT decode_DIGIT()
  {
    push("DIGIT");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x30-39", "[\\x30-\\x39]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new DIGIT(text.substring(s0, index), e0);
    else
      index = s0;

    pop("DIGIT", decoded, index - s0);

    return (DIGIT)rule;
  }

  private DQUOTE decode_DQUOTE()
  {
    push("DQUOTE");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x22", "[\\x22]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new DQUOTE(text.substring(s0, index), e0);
    else
      index = s0;

    pop("DQUOTE", decoded, index - s0);

    return (DQUOTE)rule;
  }

  private HEXDIG decode_HEXDIG()
  {
    push("HEXDIG");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_DIGIT();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("A");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("B");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("C");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("D");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("E");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_StringValue("F");
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new HEXDIG(text.substring(s0, index), e0);
    else
      index = s0;

    pop("HEXDIG", decoded, index - s0);

    return (HEXDIG)rule;
  }

  private HTAB decode_HTAB()
  {
    push("HTAB");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x09", "[\\x09]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new HTAB(text.substring(s0, index), e0);
    else
      index = s0;

    pop("HTAB", decoded, index - s0);

    return (HTAB)rule;
  }

  private LF decode_LF()
  {
    push("LF");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x0A", "[\\x0A]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new LF(text.substring(s0, index), e0);
    else
      index = s0;

    pop("LF", decoded, index - s0);

    return (LF)rule;
  }

  private LWSP decode_LWSP()
  {
    push("LWSP");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          while (f1)
          {
            decoded = false;
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_WSP();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (!decoded)
            {
              {
                ArrayList<Rule> e2 = new ArrayList<Rule>();
                int s2 = index;
                decoded = true;
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_CRLF();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                {
                  boolean f2 = true;
                  int c2 = 0;
                  for (int i2 = 0; i2 < 1 && f2; i2++)
                  {
                    rule = decode_WSP();
                    if ((f2 = rule != null))
                    {
                      e2.add(rule);
                      c2++;
                    }
                  }
                  decoded = c2 == 1;
                }
                if (decoded)
                  e1.addAll(e2);
                else
                  index = s2;
              }
            }
            if (decoded) c1++;
            f1 = decoded;
          }
          decoded = true;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new LWSP(text.substring(s0, index), e0);
    else
      index = s0;

    pop("LWSP", decoded, index - s0);

    return (LWSP)rule;
  }

  private OCTET decode_OCTET()
  {
    push("OCTET");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x00-FF", "[\\x00-\\xFF]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new OCTET(text.substring(s0, index), e0);
    else
      index = s0;

    pop("OCTET", decoded, index - s0);

    return (OCTET)rule;
  }

  private SP decode_SP()
  {
    push("SP");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x20", "[\\x20]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new SP(text.substring(s0, index), e0);
    else
      index = s0;

    pop("SP", decoded, index - s0);

    return (SP)rule;
  }

  private VCHAR decode_VCHAR()
  {
    push("VCHAR");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_NumericValue("%x21-7E", "[\\x21-\\x7E]", 1);
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new VCHAR(text.substring(s0, index), e0);
    else
      index = s0;

    pop("VCHAR", decoded, index - s0);

    return (VCHAR)rule;
  }

  private WSP decode_WSP()
  {
    push("WSP");

    boolean decoded = true;
    int s0 = index;
    ArrayList<Rule> e0 = new ArrayList<Rule>();
    Rule rule;

    decoded = false;
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_SP();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }
    if (!decoded)
    {
      {
        ArrayList<Rule> e1 = new ArrayList<Rule>();
        int s1 = index;
        decoded = true;
        if (decoded)
        {
          boolean f1 = true;
          int c1 = 0;
          for (int i1 = 0; i1 < 1 && f1; i1++)
          {
            rule = decode_HTAB();
            if ((f1 = rule != null))
            {
              e1.add(rule);
              c1++;
            }
          }
          decoded = c1 == 1;
        }
        if (decoded)
          e0.addAll(e1);
        else
          index = s1;
      }
    }

    rule = null;
    if (decoded)
      rule = new WSP(text.substring(s0, index), e0);
    else
      index = s0;

    pop("WSP", decoded, index - s0);

    return (WSP)rule;
  }

  public class StringValue extends Rule
  {
    public StringValue(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_StringValue(this);
    }
  }

  private StringValue decode_StringValue(String regex)
  {
    push("*StringValue", regex);

    boolean decoded = true;
    int start = index;

    StringValue stringValue = null;
    try
    {
      String value = text.substring(index, index + regex.length());
      if ((decoded = value.equalsIgnoreCase(regex)))
      {
        index += regex.length();
        stringValue = new StringValue(value, null);
      }
    }
    catch (IndexOutOfBoundsException e) {decoded = false;}

    pop("*StringValue", decoded, index - start);

    return stringValue;
  }

  public class NumericValue extends Rule
  {
    public NumericValue(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_NumericValue(this);
    }
  }

  private NumericValue decode_NumericValue(String spelling, String regex, int length)
  {
    push("*NumericValue", spelling, regex);

    boolean decoded = true;
    int start = index;

    NumericValue numericValue = null;
    try
    {
      String value = text.substring(index, index + length);
      if ((decoded = Pattern.matches(regex, value)))
      {
        index += length;
        numericValue = new NumericValue(value, null);
      }
    }
    catch (IndexOutOfBoundsException e) {decoded = false;}

    pop("*NumericValue", decoded, index - start);

    return numericValue;
  }

  /* ---------------------------------------------------------------------------
   * public rule classes
   * ---------------------------------------------------------------------------
   */

  static final public class xri extends Rule
  {
    private xri(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri(xri rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri(this);
    }
  }

  static final public class xri_reference extends Rule
  {
    private xri_reference(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_reference(xri_reference rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_reference(this);
    }
  }

  static final public class relative_xri_ref extends Rule
  {
    private relative_xri_ref(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public relative_xri_ref(relative_xri_ref rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_relative_xri_ref(this);
    }
  }

  static final public class relative_xri_part extends Rule
  {
    private relative_xri_part(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public relative_xri_part(relative_xri_part rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_relative_xri_part(this);
    }
  }

  static final public class xri_hier_part extends Rule
  {
    private xri_hier_part(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_hier_part(xri_hier_part rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_hier_part(this);
    }
  }

  static final public class xri_authority extends Rule
  {
    private xri_authority(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_authority(xri_authority rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_authority(this);
    }
  }

  static final public class subseg extends Rule
  {
    private subseg(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public subseg(subseg rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_subseg(this);
    }
  }

  static final public class global_subseg extends Rule
  {
    private global_subseg(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public global_subseg(global_subseg rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_global_subseg(this);
    }
  }

  static final public class local_subseg extends Rule
  {
    private local_subseg(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public local_subseg(local_subseg rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_local_subseg(this);
    }
  }

  static final public class gcs_char extends Rule
  {
    private gcs_char(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public gcs_char(gcs_char rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_gcs_char(this);
    }
  }

  static final public class lcs_char extends Rule
  {
    private lcs_char(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public lcs_char(lcs_char rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_lcs_char(this);
    }
  }

  static final public class literal extends Rule
  {
    private literal(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public literal(literal rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_literal(this);
    }
  }

  static final public class literal_nc extends Rule
  {
    private literal_nc(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public literal_nc(literal_nc rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_literal_nc(this);
    }
  }

  static final public class xref extends Rule
  {
    private xref(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xref(xref rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xref(this);
    }
  }

  static final public class xref_empty extends Rule
  {
    private xref_empty(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xref_empty(xref_empty rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xref_empty(this);
    }
  }

  static final public class xref_xri_reference extends Rule
  {
    private xref_xri_reference(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xref_xri_reference(xref_xri_reference rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xref_xri_reference(this);
    }
  }

  static final public class xref_IRI extends Rule
  {
    private xref_IRI(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xref_IRI(xref_IRI rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xref_IRI(this);
    }
  }

  static final public class xri_path extends Rule
  {
    private xri_path(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_path(xri_path rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_path(this);
    }
  }

  static final public class xri_path_abempty extends Rule
  {
    private xri_path_abempty(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_path_abempty(xri_path_abempty rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_path_abempty(this);
    }
  }

  static final public class xri_path_abs extends Rule
  {
    private xri_path_abs(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_path_abs(xri_path_abs rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_path_abs(this);
    }
  }

  static final public class xri_path_noscheme extends Rule
  {
    private xri_path_noscheme(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_path_noscheme(xri_path_noscheme rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_path_noscheme(this);
    }
  }

  static final public class xri_segment extends Rule
  {
    private xri_segment(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_segment(xri_segment rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_segment(this);
    }
  }

  static final public class xri_segment_nz extends Rule
  {
    private xri_segment_nz(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_segment_nz(xri_segment_nz rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_segment_nz(this);
    }
  }

  static final public class xri_segment_nc extends Rule
  {
    private xri_segment_nc(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_segment_nc(xri_segment_nc rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_segment_nc(this);
    }
  }

  static final public class xri_pchar extends Rule
  {
    private xri_pchar(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_pchar(xri_pchar rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_pchar(this);
    }
  }

  static final public class xri_pchar_nc extends Rule
  {
    private xri_pchar_nc(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_pchar_nc(xri_pchar_nc rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_pchar_nc(this);
    }
  }

  static final public class xri_reserved extends Rule
  {
    private xri_reserved(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_reserved(xri_reserved rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_reserved(this);
    }
  }

  static final public class xri_gen_delims extends Rule
  {
    private xri_gen_delims(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_gen_delims(xri_gen_delims rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_gen_delims(this);
    }
  }

  static final public class xri_sub_delims extends Rule
  {
    private xri_sub_delims(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public xri_sub_delims(xri_sub_delims rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_xri_sub_delims(this);
    }
  }

  static final public class IRI extends Rule
  {
    private IRI(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public IRI(IRI rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_IRI(this);
    }
  }

  static final public class scheme extends Rule
  {
    private scheme(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public scheme(scheme rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_scheme(this);
    }
  }

  static final public class ihier_part extends Rule
  {
    private ihier_part(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public ihier_part(ihier_part rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_ihier_part(this);
    }
  }

  static final public class iauthority extends Rule
  {
    private iauthority(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public iauthority(iauthority rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_iauthority(this);
    }
  }

  static final public class iuserinfo extends Rule
  {
    private iuserinfo(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public iuserinfo(iuserinfo rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_iuserinfo(this);
    }
  }

  static final public class ihost extends Rule
  {
    private ihost(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public ihost(ihost rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_ihost(this);
    }
  }

  static final public class IP_literal extends Rule
  {
    private IP_literal(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public IP_literal(IP_literal rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_IP_literal(this);
    }
  }

  static final public class IPvFuture extends Rule
  {
    private IPvFuture(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public IPvFuture(IPvFuture rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_IPvFuture(this);
    }
  }

  static final public class IPv6address extends Rule
  {
    private IPv6address(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public IPv6address(IPv6address rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_IPv6address(this);
    }
  }

  static final public class ls32 extends Rule
  {
    private ls32(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public ls32(ls32 rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_ls32(this);
    }
  }

  static final public class h16 extends Rule
  {
    private h16(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public h16(h16 rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_h16(this);
    }
  }

  static final public class IPv4address extends Rule
  {
    private IPv4address(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public IPv4address(IPv4address rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_IPv4address(this);
    }
  }

  static final public class dec_octet extends Rule
  {
    private dec_octet(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public dec_octet(dec_octet rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_dec_octet(this);
    }
  }

  static final public class ireg_name extends Rule
  {
    private ireg_name(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public ireg_name(ireg_name rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_ireg_name(this);
    }
  }

  static final public class port extends Rule
  {
    private port(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public port(port rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_port(this);
    }
  }

  static final public class ipath_abempty extends Rule
  {
    private ipath_abempty(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public ipath_abempty(ipath_abempty rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_ipath_abempty(this);
    }
  }

  static final public class ipath_abs extends Rule
  {
    private ipath_abs(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public ipath_abs(ipath_abs rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_ipath_abs(this);
    }
  }

  static final public class ipath_rootless extends Rule
  {
    private ipath_rootless(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public ipath_rootless(ipath_rootless rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_ipath_rootless(this);
    }
  }

  static final public class ipath_empty extends Rule
  {
    private ipath_empty(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public ipath_empty(ipath_empty rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_ipath_empty(this);
    }
  }

  static final public class isegment extends Rule
  {
    private isegment(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public isegment(isegment rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_isegment(this);
    }
  }

  static final public class isegment_nz extends Rule
  {
    private isegment_nz(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public isegment_nz(isegment_nz rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_isegment_nz(this);
    }
  }

  static final public class iquery extends Rule
  {
    private iquery(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public iquery(iquery rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_iquery(this);
    }
  }

  static final public class iprivate extends Rule
  {
    private iprivate(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public iprivate(iprivate rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_iprivate(this);
    }
  }

  static final public class ifragment extends Rule
  {
    private ifragment(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public ifragment(ifragment rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_ifragment(this);
    }
  }

  static final public class ipchar extends Rule
  {
    private ipchar(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public ipchar(ipchar rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_ipchar(this);
    }
  }

  static final public class iunreserved extends Rule
  {
    private iunreserved(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public iunreserved(iunreserved rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_iunreserved(this);
    }
  }

  static final public class pct_encoded extends Rule
  {
    private pct_encoded(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public pct_encoded(pct_encoded rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_pct_encoded(this);
    }
  }

  static final public class ucschar extends Rule
  {
    private ucschar(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public ucschar(ucschar rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_ucschar(this);
    }
  }

  static final public class reserved extends Rule
  {
    private reserved(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public reserved(reserved rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_reserved(this);
    }
  }

  static final public class gen_delims extends Rule
  {
    private gen_delims(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public gen_delims(gen_delims rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_gen_delims(this);
    }
  }

  static final public class sub_delims extends Rule
  {
    private sub_delims(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public sub_delims(sub_delims rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_sub_delims(this);
    }
  }

  static final public class unreserved extends Rule
  {
    private unreserved(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public unreserved(unreserved rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_unreserved(this);
    }
  }

  static final public class ALPHA extends Rule
  {
    private ALPHA(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public ALPHA(ALPHA rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_ALPHA(this);
    }
  }

  static final public class BIT extends Rule
  {
    private BIT(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public BIT(BIT rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_BIT(this);
    }
  }

  static final public class CHAR extends Rule
  {
    private CHAR(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public CHAR(CHAR rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_CHAR(this);
    }
  }

  static final public class CR extends Rule
  {
    private CR(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public CR(CR rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_CR(this);
    }
  }

  static final public class CRLF extends Rule
  {
    private CRLF(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public CRLF(CRLF rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_CRLF(this);
    }
  }

  static final public class CTL extends Rule
  {
    private CTL(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public CTL(CTL rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_CTL(this);
    }
  }

  static final public class DIGIT extends Rule
  {
    private DIGIT(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public DIGIT(DIGIT rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_DIGIT(this);
    }
  }

  static final public class DQUOTE extends Rule
  {
    private DQUOTE(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public DQUOTE(DQUOTE rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_DQUOTE(this);
    }
  }

  static final public class HEXDIG extends Rule
  {
    private HEXDIG(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public HEXDIG(HEXDIG rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_HEXDIG(this);
    }
  }

  static final public class HTAB extends Rule
  {
    private HTAB(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public HTAB(HTAB rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_HTAB(this);
    }
  }

  static final public class LF extends Rule
  {
    private LF(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public LF(LF rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_LF(this);
    }
  }

  static final public class LWSP extends Rule
  {
    private LWSP(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public LWSP(LWSP rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_LWSP(this);
    }
  }

  static final public class OCTET extends Rule
  {
    private OCTET(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public OCTET(OCTET rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_OCTET(this);
    }
  }

  static final public class SP extends Rule
  {
    private SP(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public SP(SP rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_SP(this);
    }
  }

  static final public class VCHAR extends Rule
  {
    private VCHAR(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public VCHAR(VCHAR rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_VCHAR(this);
    }
  }

  static final public class WSP extends Rule
  {
    private WSP(String spelling, ArrayList<Rule> rules)
    {
      super(spelling, rules);
    }

    public WSP(WSP rule)
    {
      super(rule.spelling, rule.rules);
    }

    public Object visit(Visitor visitor)
    {
      return visitor.visit_WSP(this);
    }
  }
}
/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
