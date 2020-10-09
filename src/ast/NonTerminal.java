package ast;

import java.util.ArrayList;

import ast.concrete.NameCheckLambda;
import ast.concrete.Render;
import ast.concrete.RenderLambda;
import util.Util;

public class NonTerminal implements Node {

  private int sym;
  private int var;
  private RenderLambda r;
  private String n;
  private NameCheckLambda[] nc;
  protected ArrayList<Node> ns = new ArrayList<>();

  public NonTerminal(int sym, Node... ns) throws Exception {
    this(sym, Render.linearRender, null, null, 0, ns);
  }

  public NonTerminal(int sym, RenderLambda r, Node... ns) throws Exception {
    this(sym, r, null, null, 0, ns);
  }

  public NonTerminal(int sym, int var, Node... ns) throws Exception {
    this(sym, Render.linearRender, null, null, var, ns);
  }

  public NonTerminal(int sym, RenderLambda r, int var, Node... ns) throws Exception {
    this(sym, r, null, null, var, ns);
  }

  public NonTerminal(int sym, RenderLambda r, Object name, int var, Node... ns) throws Exception {
    this(sym, r, name, null, var, ns);
  }

  public NonTerminal(int sym, RenderLambda r, NameCheckLambda[] nc, int var, Node... ns) throws Exception {
    this(sym, r, null, nc, var, ns);
  }

  public NonTerminal(int sym, RenderLambda r, Object name, NameCheckLambda[] nc, int var, Node... ns) throws Exception {
    for (Node n : ns) {
      this.ns.add(n);
    }
    this.sym = sym;
    this.var = var;
    this.r = r;
    this.n = (String) name;
    this.nc = nc;
    if (nc != null) for (int i = 0; i < nc.length; i++) nc[i].check();
  }

  public int length() {
    return ns.size();
  }

  public Node get(int i) throws IndexOutOfBoundsException {
    return ns.get(i);
  }

  public void forEach(ForEachLambda f) throws Exception {
    for (Node n : this.ns) {
      f.each(n);
    }
  }

  public NonTerminal join(NonTerminal n) throws Exception {
    Node[] nns = new Node[this.length() + n.length()];
    for (int i = 0; i < this.length(); i++) {
      nns[i] = this.get(i);
    }
    for (int i = 0; i < n.length(); i++) {
      nns[i + this.length()] = n.get(i);
    }
    return new NonTerminal(this.sym, this.r, this.n, this.nc, this.var, nns);
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
  public String toSexp() throws Exception {
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
  public String toRender() throws Exception {
    return r.render(this);
  }

  public String getName() {
    return n;
  }
}
