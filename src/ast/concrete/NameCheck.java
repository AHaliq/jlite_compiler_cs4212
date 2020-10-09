package ast.concrete;

import java.util.HashSet;

import ast.NonTerminal;
import ast.Node;

public class NameCheck {

  public static NameCheckLambda classCheck(Node n){
    return () -> {
      nameClashCheck((NonTerminal) n, "duplicate class definition");
    };
  }

  public static NameCheckLambda varDeclCheck(Node n, String msg) {
    return () -> {
      nameClashCheck((NonTerminal) n, msg + " duplicate variable declaration ");
    };
  }

  public static NameCheckLambda fmlListCheck(Node n, String msg) {
    return () -> {
      nameClashCheck((NonTerminal) n, msg + " duplicate parameter definition ");
    };
  }

  public static NameCheckLambda mdOverloadCheck(Node n) {
    return () -> {
      // instead of hashSet of string, we need pair of string and array of types (param and return)
    };
  }

  public static void nameClashCheck(NonTerminal n, String msg) throws Exception {
    HashSet<String> s = new HashSet<>();
    n.forEach((mNode) -> {
      String mName = ((NonTerminal) mNode).getName();
      if(s.contains(mName)) {
        throw new Exception(msg + " '" + mName + "'");
      }
      s.add(mName);
    });
  }
}
