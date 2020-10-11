package ast.concrete.ir3;

import ast.NonTerminal;

public interface IR3Lambda {
  IR3State render(IR3State s, NonTerminal n) throws Exception;
}
