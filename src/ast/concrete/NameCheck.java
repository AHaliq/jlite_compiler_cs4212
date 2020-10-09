package ast.concrete;

import java.util.HashSet;

import ast.NonTerminal;

public class NameCheck {

  public static NameCheckLambda classCheck = (n) -> {
    NonTerminal classDeclStar = (NonTerminal) n.get(1);

    HashSet<String> s = new HashSet<>();
    classDeclStar.forEach((mNode) -> {
      String mName = ((NonTerminal) mNode).getName();
      if(s.contains(mName)) {
        throw new Exception("duplicate class definition '" + mName + "'");
      }
      s.add(mName);
    });
  };
}
