package ast.concrete.types;

public enum PrimTypes {
  VOID("Void"),
  BOOL("Bool"),
  INT("Int"),
  STRING("String");

  private String s;

  PrimTypes(String s) {
    this.s = s;
  }
  
  public String getStr() {
    return s;
  }
}
