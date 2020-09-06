package ast;

import java.util.ArrayList;

import javasrc.cup.Sym;
import util.Util;

public class NonTerminal implements Node {

  private int sym;
  private int var;
  private ArrayList<Node> ns = new ArrayList<>();

  public NonTerminal(int sym, Node... ns) {
    this(sym, 0, ns);
  }

  public NonTerminal(int sym, int var, Node... ns) {
    for (Node n : ns) {
      this.ns.add(n);
    }
    this.sym = sym;
    this.var = var;
  }

  public int length() {
    return ns.size();
  }

  public Node get(int i) throws IndexOutOfBoundsException {
    return ns.get(i);
  }

  public NonTerminal join(NonTerminal n) {
    Node[] nns = new Node[this.length() + n.length()];
    for (int i = 0; i < this.length(); i++) {
      nns[i] = this.get(i);
    }
    for (int i = 0; i < n.length(); i++) {
      nns[i + this.length()] = n.get(i);
    }
    return new NonTerminal(this.sym, this.var, nns);
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
    String[] ts = new String[this.ns.size()];
    for (int i = 0; i < ts.length; i++) {
      ts[i] = Util.indent(ns.get(i).toSexp());
    }

    return "( " + this.sym + ":" + this.var + "\n" + String.join("\n", ts) + "\n)";
  }
}
