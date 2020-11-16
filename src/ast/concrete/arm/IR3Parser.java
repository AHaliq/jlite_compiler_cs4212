package ast.concrete.arm;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ast.concrete.ir3.IR3;

public class IR3Parser {
  public static String[] splitIR3(String ir3) {
    String[] parts = ir3.split(IR3.MTD_START);
    String sig = parts[0].split(IR3.DATA_START)[1];
    String mtd = parts[1].split(IR3.IR3_END)[0];
    return new String[]{sig, mtd};
  }

  public static Vector<String> splitBodies(String ir3Part) {
    StringBuilder buf = new StringBuilder();
    Vector<String> bodies = new Vector<>();
    Boolean inBody = false;
    Boolean inString = false;
    Boolean inEscape = false;
    for(int i = 0; i < ir3Part.length(); i++) {
      char c = ir3Part.charAt(i);
      if (c != ' ' && c != '\n' && !inBody) {
        inBody = true;
      }
      if (inBody) {
        if (c == '"' && !inEscape) {
          inString = !inString;
        }
        if (c == '\\' && !inEscape) {
          inEscape = true;
        } else {
          inEscape = false;
        }
        if (inBody) {
          buf.append(c);
        }
        if (c == '}' && !inString ) {
          inBody = false;
          bodies.add(buf.toString());
          buf = new StringBuilder();
        }
      }
    }
    return bodies;
  }

  public static String sigToName(String sig) {
    String firstLine = sig.split("\n")[0];
    String nameAndBrace = firstLine.split("class ")[1];
    return nameAndBrace.substring(0, nameAndBrace.length() - 1);
  }

  public static Integer sigEntries(String sig) {
    return sig.split("\n").length - 2;
  }

  public static Vector<String> varDecls(String mtd) {
    Vector<String> decls = new Vector<>();
    String[] parts = mtd.split("\n");
    for (int i = 1; i < parts.length - 1; i++) {
      try {
        Pattern r = Pattern.compile("\\s\\s([A-Z]([a-z])*)\\s(.+);");
        Matcher m = r.matcher(parts[i]);
        if(m.matches()) {
          String tpe = m.group(1);
          String var = m.group(3);
          if (!tpe.equalsIgnoreCase("Return")) {
            decls.add(var);
          }
        }
      } catch(Exception e) {
        break;
      }
    }
    return decls;
  }
}
