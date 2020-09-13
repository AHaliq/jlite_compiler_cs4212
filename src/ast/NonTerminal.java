package ast;

import java.util.ArrayList;

import ast.concrete.Render;
import ast.concrete.RenderLambda;
import util.Util;

public class NonTerminal implements Node {

  private int sym;
  private int var;
  private RenderLambda r;
  protected ArrayList<Node> ns = new ArrayList<>();

  public NonTerminal(int sym, Node... ns) {
    this(sym, Render.linearRender, 0, ns);
  }

  public NonTerminal(int sym, RenderLambda r, Node... ns) {
    this(sym, r, 0, ns);
  }

  public NonTerminal(int sym, int var, Node... ns) {
    this(sym, Render.linearRender, var, ns);
  }

  public NonTerminal(int sym, RenderLambda r, int var, Node... ns) {
    for (Node n : ns) {
      this.ns.add(n);
    }
    this.sym = sym;
    this.var = var;
    this.r = r;
  }

  public int length() {
    return ns.size();
  }

  public Node get(int i) throws IndexOutOfBoundsException {
    return ns.get(i);
  }

  public void forEach(ForEachLambda f) {
    for (Node n : this.ns) {
      f.each(n);
    }
  }

  public NonTerminal join(NonTerminal n) {
    Node[] nns = new Node[this.length() + n.length()];
    for (int i = 0; i < this.length(); i++) {
      nns[i] = this.get(i);
    }
    for (int i = 0; i < n.length(); i++) {
      nns[i + this.length()] = n.get(i);
    }
    return new NonTerminal(this.sym, this.r, this.var, nns);
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
    if (this.ns.size() > 0) {
      String[] ts = new String[this.ns.size()];
      for (int i = 0; i < ts.length; i++) {
        ts[i] = Util.indent(ns.get(i).toSexp());
      }
      return "( n" + this.sym + (this.var == 0 ? "" : ":" + this.var) + "\n" + String.join("\n", ts) + "\n)";
    }
    return "( n" + this.sym + (this.var == 0 ? "" : ":" + this.var) + " )";
  }

  @Override
  public String toString() {
    return r.render(this);
  }
}
