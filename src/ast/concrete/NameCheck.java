package ast.concrete;

import java.util.HashSet;

import ast.NonTerminal;
import ast.concrete.types.MethodSignature;
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
        MethodSignature ms = new MethodSignature(m);
        StringBuilder str = new StringBuilder();
        str.append(m.getName());
        str.append(" :: ");
        str.append(ms.toString());
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
