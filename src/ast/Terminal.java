package ast;

import javasrc.cup.Sym;

public class Terminal implements Node {

  private int sym;
  private int var;
  private Object v;

  public Terminal(int sym) {
    this(sym, null);
  }

  public Terminal(int sym, Object v) {
    this(sym, 0, v);
  }

  public Terminal(int sym, int var, Object v) {
    this.v = v;
    this.sym = sym;
    this.var = var;
  }

  public Object get() {
    return v;
  }

  @Override
  public int getSym() {
    return this.sym;
  }

  @Override
  public int getVariant() {
    return this.var;
  }

  @Override
  public String toSexp() {
    if (v == null) {
      return this.toString();
    } else {
      return "( " + Sym.terminalNames[sym] + ":" + this.var + " " + v.toString() + " )";
    }
  }

  @Override
  public String toString() {
    if (v == null) {
      return Sym.terminalNames[sym] + ":" + this.var;
    } else {
      return Sym.terminalNames[sym] + ":" + v.toString();
    }
  }
}
