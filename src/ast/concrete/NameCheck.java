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

  public static NameCheckLambda mdOverloadCheck(Node n, String msg) {
    return () -> {
      NonTerminal nNt = (NonTerminal) n;
      HashSet<String> s = new HashSet<>();
      nNt.forEach((mNode) -> {
        NonTerminal m = (NonTerminal) mNode;
        StringBuilder str = new StringBuilder();
        str.append(m.getName());
        if(m.getVariant() == 0) {
          ((NonTerminal) m.get(2)).forEach((pNode) -> {
            str.append(", " + ((NonTerminal) pNode).get(0).toRender());
          });
        }
        str.append(" -> " + m.get(0).toRender());
        String key = str.toString();
        if(s.contains(key)) {
          throw new Exception(msg + " method '" + key + "' duplicate found");
        }
        s.add(key);
      });
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
