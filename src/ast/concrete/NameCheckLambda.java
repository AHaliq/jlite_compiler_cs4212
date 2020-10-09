package ast.concrete;

import ast.NonTerminal;

public interface NameCheckLambda {
  void check(NonTerminal n) throws Exception;
}
