package ast;

public class IfElseThen extends NonTerminal {

  public IfElseThen(Node... ns) {
    super(ns);
    if (ns.length != 3)
      throw new IllegalArgumentException("non terminal must have exactly three nodes");
  }

  @Override
  public String toString() {
    return "if " + this.get(0).toString() + " then " + this.get(1).toString() + " else " + this.get(2).toString();
  }
}
