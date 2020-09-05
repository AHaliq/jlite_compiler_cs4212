package ast;

import javasrc.cup.sym;

public class Terminal implements Node {

  private int id;

  public Terminal(int id) {
    this.id = id;
  }

  public int get() {
    return id;
  }

  @Override
  public String toTree() {
    return this.toString();
  }

  @Override
  public String toString() throws IndexOutOfBoundsException {
    return sym.terminalNames[id];
  }
}
