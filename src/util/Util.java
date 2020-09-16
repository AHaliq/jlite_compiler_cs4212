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

  public static String pretty(String s, boolean is_indent) {
    StringBuilder str = new StringBuilder();
    int il = 0;
    boolean braceIndent = true;
    boolean toIndent = false;
    int[] state = { 0, 0, 0 };
    boolean newLineParen = false;
    boolean inString = false;
    boolean escaped = false;
    for (char c : s.toCharArray()) {
      state = !inString ? updateState(state, c) : state;
      newLineParen = state[0] == 6 || state[1] == 3 || state[2] == 4;
      switch (c) {
        case '\"':
          inString = escaped ? inString : !inString;
          break;
        case '\\':
          escaped = escaped ? false : (inString ? !escaped : escaped);
        case '}':
          if (!inString) {
            il--;
            toIndent = true;
          }
          break;
        case '{':
          if (newLineParen) {
            str.append('\n');
            toIndent = true;
            state[0] = 0;
            state[1] = 0;
            state[2] = 0;
          }
          break;
      }
      if (toIndent && is_indent) {
        istr(str, il);
        toIndent = false;
      }
      str.append(c);
      switch (c) {
        case ';':
          if (!inString) {
            str.append('\n');
            toIndent = true;
          }
          break;
        case '{':
          if (!inString) {
            if (braceIndent) {
              il++;
            }
            str.append('\n');
            toIndent = true;
          }
          break;
        case '}':
          if (!inString) {
            str.append('\n');
            toIndent = true;
          }
          break;
      }
    }
    return str.toString().strip();
  }

  public static void istr(StringBuilder str, int il) {
    for (int i = 0; i < il; i++) {
      str.append("  ");
    }
  }

  public static int[] updateState(int[] s, char c) {
    int[] s2 = { s[0], s[1], s[2] };
    char[] len = { 6, 3, 4 };
    char[][] k = { "While(".toCharArray(), "If(".toCharArray(), "else".toCharArray() };
    for (int i = 0; i < 3; i++) {
      if (s2[i] < len[i]) {
        if (k[i][s2[i]] == c)
          s2[i]++;
        else
          s2[i] = 0;
      }
    }
    return s2;
  }
}