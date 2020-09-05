package ast;

public class Terminal implements Node {

  private Object s;

  public Terminal(Object s) {
    this.s = s;
  }

  public Object get() {
    return s;
  }

  @Override
  public String toTree() {
    return this.toString();
  }

  @Override
  public String toString() {
    return s.toString();
  }
}
