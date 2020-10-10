package ast.concrete.types;

public enum PrimTypes {
  VOID("Void"),
  BOOL("Bool"),
  INT("Int"),
  STRING("String"),
  IS_OK("isOk"),  // not a type but is used in type checking
  RET("~Ret"),    // not a type but is identifier for local env return type
  THIS("~this");  // not a type but is identifier for this in local env

  private String s;

  PrimTypes(String s) {
    this.s = s;
  }
  
  public String getStr() {
    return s;
  }
}
