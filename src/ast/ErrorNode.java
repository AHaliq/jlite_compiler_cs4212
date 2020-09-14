package ast;

public class ErrorNode implements Node {

  private String msg;

  public ErrorNode(String msg) {
    this.msg = msg;
  }

  @Override
  public String toSexp() throws Exception {
    throw new Exception(this.msg);
  }

  @Override
  public String toRender() throws Exception {
    throw new Exception(this.msg);
  }

  @Override
  public int getSym() {
    return 0;
  }

  @Override
  public int getVariant() {
    return 0;
  }

}
