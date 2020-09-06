package ast;

public interface Node {
  public String toSexp();

  public String toString();

  public int getSym();

  public int getVariant();
}
