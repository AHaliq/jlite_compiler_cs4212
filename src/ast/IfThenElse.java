package ast;

import util.Util;

public class IfThenElse extends NonTerminal {

  public IfThenElse(Node... ns) {
    super(ns);
    if (ns.length != 3)
      throw new IllegalArgumentException("non terminal must have exactly three nodes");
  }

  @Override
  public String toString() {
    String cond = Util.indent(this.get(0).toString());
    String exp1 = Util.indent(this.get(1).toString());
    String exp2 = Util.indent(this.get(2).toString());
    return "if\n" + cond + "\nthen\n" + exp1 + "\nelse\n" + exp2;
  }
}
