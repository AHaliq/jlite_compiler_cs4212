package ast.concrete.arm;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.text.StringEscapeUtils;

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

  public static Vector<Vector<String>> getFunctionArguments(String ir3part) throws Exception {
    Vector<Vector<String>> ret = new Vector<>();
    Vector<String> tpe = new Vector<>();
    Vector<String> arg = new Vector<>();
    ret.add(tpe);
    ret.add(arg);
    String head = ir3part.split("\n")[0];
    Pattern r = Pattern.compile(".*\\((.*)\\)\\{.*");
    Matcher m = r.matcher(head);
    if(m.matches()) {
      r = Pattern.compile("(.+)\\s(.+)");
      for(String argPair : m.group(1).split(",")) {
        Matcher m2 = r.matcher(argPair);
        if(m2.matches()) {
          tpe.add(m2.group(1));
          arg.add(m2.group(2));
        } else {
          throw new Exception("improper IR3 code when calling `getFunctionArguments` on argument type pair " + argPair);
        }
      }
      return ret;
    }
    throw new Exception("improper IR3 code when calling `getFunctionArguments`");
  }

  public static String getFunctionName(String ir3part) throws Exception {
    String head = ir3part.split("\n")[0];
    Pattern r = Pattern.compile("[A-Z][a-z]*\\s%?(.*)\\(.*");
    Matcher m = r.matcher(head);
    if (m.matches()) {
      return m.group(1);
    }
    throw new Exception("improper IR3 code when calling `getFunctionName`");
  }

  public static String getFunctionReturnType(String ir3part) throws Exception {
    String head = ir3part.split("\n")[0];
    Pattern r = Pattern.compile("([A-Z][a-z]*)\\s.*");
    Matcher m = r.matcher(head);
    if (m.matches()) {
      return m.group(1);
    }
    throw new Exception("improper IR3 code when calling `getFunctionReturnType`");
  }

  public static String sigToName(String sig) {
    String firstLine = sig.split("\n")[0];
    String nameAndBrace = firstLine.split("class ")[1];
    return nameAndBrace.substring(0, nameAndBrace.length() - 1);
  }

  public static Integer sigEntries(String sig) {
    return sig.split("\n").length - 2;
  }

  public static String[] splitSigEntry(String sigEntry) {
    Pattern r = Pattern.compile("\\s\\s([A-Z]([a-z])*)\\s(.+);");
    Matcher m = r.matcher(sigEntry);
    if(m.matches()) {
      String tpe = m.group(1);
      String var = m.group(3);
      return new String[]{tpe,var};
    }
    return null;
  }

  public static Vector<Vector<String>> splitMtd(String mtd) {
    Vector<String> decls = new Vector<>();
    Vector<String> body = new Vector<>();
    Vector<String> tpes = new Vector<>();
    Vector<Vector<String>> ret = new Vector<>();
    ret.add(decls);
    ret.add(body);
    ret.add(tpes);
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
            tpes.add(tpe);
          } else {
            body.add(parts[i]);
          }
        }else {
          body.add(parts[i]);
        }
      } catch(Exception e) {
        body.add(parts[i]);
      }
    }
    return ret;
  }

  public static IR3StmtParse parseStmt(String stmt) throws Exception {
    Pattern r = Pattern.compile("\\s+Label (\\d+):$");
    Matcher m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.LABEL, "." + m.group(1));
    }
    r = Pattern.compile("\\s+(.*):$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.LABEL, m.group(1));
    }
    r = Pattern.compile("\\s+If\\((.*)\\)\\s+goto\\s+(\\d+);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.IF, m.group(1), "." + m.group(2));
    }
    r = Pattern.compile("\\s+If\\((.*)\\)\\s+goto\\s+(.+);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.IF, m.group(1), m.group(2));
    }
    r = Pattern.compile("\\s+goto\\s+(\\d+);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.GOTO, "." + m.group(1));
    }
    r = Pattern.compile("\\s+goto\\s+(.+);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.GOTO, m.group(1));
    }
    r = Pattern.compile("\\s+readln\\((.*)\\);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.READ, m.group(1));
    }
    r = Pattern.compile("\\s+println\\((.*)\\);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.PRINT, m.group(1));
    }
    r = Pattern.compile("\\s+(.+)\\s+=\\s+%?(\\S+)\\((.*)\\);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      String[] args = m.group(3).split(", ");
      String[] argArr = new String[args.length + 2];
      argArr[0] =  m.group(1);
      argArr[1] = m.group(2);
      for(int i = 0; i < args.length; i++) argArr[2 + i] = args[i];
      return new IR3StmtParse(IR3Enums.ASSIGN_FUNCTION_CALL, argArr);
    }
    r = Pattern.compile("\\s+(.+)\\s+=\\s+new\\s+(.+)\\(\\);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.ASSIGN_NEW_OBJ, m.group(1), m.group(2));
    }
    r = Pattern.compile("\\s+(.+)\\s+=\\s+\\\"(.+)\\\";$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.ASSIGN_STRING, m.group(1), StringEscapeUtils.unescapeJava(m.group(2)));
    }
    r = Pattern.compile("\\s+(.+)\\s+=\\s+(\\S+)\\s+(\\S+)\\s+(\\S+);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      IR3Enums e = null;
      switch(m.group(3)) {
        case "||": e = IR3Enums.ASSIGN_OR_OP; break;
        case "&&": e = IR3Enums.ASSIGN_AND_OP; break;
        case "*": e = IR3Enums.ASSIGN_MUL_OP; break;
        case "/": e = IR3Enums.ASSIGN_DIV_OP; break;
        case "+": e = IR3Enums.ASSIGN_ADD_OP; break;
        case "-": e = IR3Enums.ASSIGN_SUB_OP; break;
        case "<": e = IR3Enums.ASSIGN_LT_OP; break;
        case "<=": e = IR3Enums.ASSIGN_LTE_OP; break;
        case ">": e = IR3Enums.ASSIGN_GT_OP; break;
        case ">=": e = IR3Enums.ASSIGN_GTE_OP; break;
        case "==": e = IR3Enums.ASSIGN_EQ_OP; break;
        case "!=": e = IR3Enums.ASSIGN_NEQ_OP; break;
      }
      return new IR3StmtParse(e, m.group(1), m.group(2), m.group(4));
    }
    r = Pattern.compile("\\s+(.+)\\s+=\\s+!(.+);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.ASSIGN_UNARY_INV_OP, m.group(1), m.group(2));
    }
    r = Pattern.compile("\\s+(.+)\\s+=\\s+-(.+);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.ASSIGN_UNARY_NEG_OP, m.group(1), m.group(2));
    }
    r = Pattern.compile("\\s+(.+)\\s+=\\s+(\\S+)\\.(\\S+);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.ASSIGN_DOT_OP, m.group(1), m.group(2), m.group(3));
    }
    r = Pattern.compile("\\s+(.+)\\s+=\\s+(\\d+);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.ASSIGN_INT, m.group(1), m.group(2));
    }
    r = Pattern.compile("\\s+(.+)\\.(.+)\\s+=\\s+(.+);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.DOT_ASSIGN, m.group(1), m.group(2), m.group(3));
    }
    r = Pattern.compile("\\s+(.+)\\s+=\\s+(true|false);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.ASSIGN_BOOL, m.group(1), m.group(2).equals("true") ? "1" : "0");
    }
    r = Pattern.compile("\\s+(.+)\\s+=\\s+(.+);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      return new IR3StmtParse(IR3Enums.ASSIGN_ID, m.group(1), m.group(2));
    }
    r = Pattern.compile("\\s+%?(\\S+)\\((.*)\\);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      String[] args = m.group(2).split(", ");
      String[] argArr = new String[args.length + 1];
      argArr[0] = m.group(1);
      for (int i = 0; i < args.length; i++) argArr[1 + i] = args[i];
      return new IR3StmtParse(IR3Enums.FUNCTION_CALL, argArr);
    }
    r = Pattern.compile("\\s+Return\\s*(\\S*);$");
    m = r.matcher(stmt);
    if (m.matches()) {
      String reg = m.group(1);
      if (reg.isEmpty()) {
        return new IR3StmtParse(IR3Enums.RETURN);
      }
      return new IR3StmtParse(IR3Enums.RETURN, reg);
    }
    throw new Exception("unable to parse IR3 : " + stmt);
  } 
}

//THIS COULD BE DONE BETTER BY MODIFYING IR3 RENDERER IN ASG2 TO RENDER
// A DATA STRUCTURE LIKE THIS RATHER THAN STRING DIRECTLY
