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
        for (String s3 : s2.split("\\} else \\{")) {
          if (firstS3) {
            firstS3 = false;
          } else {
            str.append("\n");
            appendIndent(str, "} else {", il - 1);
          }
          Boolean firstS4 = true;
          for (String s4 : s3.split("\\}")) {
            if (firstS4) {
              firstS4 = false;
            } else {
              appendIndent(str, "}\n", --il);
            }
            if (s4.length() > 0)
              appendIndent(str, s4, il);
          }
        }
      }
    }
    String fs = str.toString();
    return fs.substring(0, fs.length() - 1);
  }

  public static void appendIndent(StringBuilder buf, String str, int i) {
    for (int j = 0; j < i; j++) {
      buf.append("  ");
    }
    buf.append(str);
  }
}

/**
 * 
 * split '{' each element increase indent and new line split ';' each element
 * new line split '} else {' reduce indent, print increase indent split '}' each
 * element decrease indent and new line
 */