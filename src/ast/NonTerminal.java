package ast;

import java.util.ArrayList;

import util.Util;

public class NonTerminal implements Node {

  private ArrayList<Node> ns = new ArrayList<>();

  public NonTerminal(Node... ns) {
    for (Node n : ns) {
      this.ns.add(n);
    }
  }

  public int length() {
    return ns.size();
  }

  public Node get(int i) throws IndexOutOfBoundsException {
    return ns.get(i);
  }

  @Override
  public String toTree() {
    String[] ts = new String[this.ns.size()];
    for (int i = 0; i < ts.length; i++) {
      ts[i] = Util.indent(ns.get(i).toTree());
    }
    return "*\n" + String.join("\n", ts);
  }
}
