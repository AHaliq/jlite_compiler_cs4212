package ast.concrete.types;

public enum PrimTypes {
  VOID("Void"),
  BOOL("Bool"),
  INT("Int"),
  STRING("String"),
  IS_OK("isOk"),  // not a type but is used in type checking
  NOT_OK("notOk");// lowercase will prevent collision with actual types

  private String s;

  PrimTypes(String s) {
    this.s = s;
  }
  
  public String getStr() {
    return s;
  }
}
