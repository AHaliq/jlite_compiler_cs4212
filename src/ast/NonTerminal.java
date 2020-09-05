package ast;

import java.util.ArrayList;

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
      String[] ps = ns.get(i).toTree().split("\n");
      for (int j = 0; j < ps.length; j++) {
        ps[j] = "  " + ps[j];
      }
      ts[i] = String.join("\n", ps);
    }
    return "*\n" + String.join("\n", ts);
  }
}
