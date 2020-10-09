package ast.concrete.types;

import java.util.HashMap;

import ast.NonTerminal;

public interface TypeCheckLambda {
  String check(HashMap<String,LocalEnv> cd, LocalEnv le, NonTerminal n) throws Exception;
}
