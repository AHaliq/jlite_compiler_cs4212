package util;

public class Util {
  public static String indent(String s) {
    return Util.indent(s, "  ");
  }

  public static String indent(String s, String ind) {
    String[] ps = s.split("\n");
    for (int i = 0; i < ps.length; i++) {
      ps[i] = ind + ps[i];
    }
    return String.join("\n", ps);
  }

  public static String pretty(String s) {
    return Util.pretty(s, true);
  }

  public static String pretty(String s, Boolean indent) {
    StringBuilder str = new StringBuilder();
    int il = 0;
    Boolean firstS1 = true;
    for (String s1 : (s + ".").split("\\{")) {
      if (firstS1) {
        firstS1 = false;
      } else {
        str.append("{\n");
        il++;
      }
      Boolean firstS2 = true;
      for (String s2 : s1.split(";")) {
        if (firstS2) {
          firstS2 = false;
        } else {
          str.append(";\n");
        }
        Boolean firstS3 = true;
        for (String s3 : s2.split("\\}")) {
          if (s3.equals("\nelse\n")) {
            appendIndent(str, "}\n", --il, indent);
            appendIndent(str, "else\n", il, indent);
          } else {
            if (firstS3) {
              firstS3 = false;
            } else {
              appendIndent(str, "}\n", --il, indent);
            }
            if (s3.length() > 0)
              appendIndent(str, s3, il, indent);
          }
        }
      }
    }
    String fs = str.toString();
    return fs.substring(0, fs.length() - 1).strip();
  }

  public static void appendIndent(StringBuilder buf, String str, int i) {
    Util.appendIndent(buf, str, i, true);
  }

  public static void appendIndent(StringBuilder buf, String str, int i, Boolean indent) {
    if (indent) {
      for (int j = 0; j < i; j++) {
        buf.append("  ");
      }
    }
    buf.append(str);
  }
}