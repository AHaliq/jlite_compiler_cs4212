package ast;

public interface Node {
  public String toSexp() throws Exception;

  public String toRender() throws Exception;

  public int getSym();

  public int getVariant();
}
