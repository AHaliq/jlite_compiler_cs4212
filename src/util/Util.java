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
}
