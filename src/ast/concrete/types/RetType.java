package ast.concrete.types;

public class RetType {
  private String id = null;
  private MethodSignature md = null;
  private Boolean isId = false;

  public RetType(String id) {
    this.isId = true;
    this.id = id;
  }

  public RetType(MethodSignature md) {
    this.isId = false;
    this.md = md;
  }

  public String getId() throws Exception {
    if(!isId) throw new Exception("attempted access return type ID but is not");
    return id;
  }

  public MethodSignature getMd() throws Exception {
    if(isId) throw new Exception("attempted access return type md but its not");
    return md;
  }

  public Boolean isId() {
    return isId;
  }

  @Override
  public String toString() {
    if(isId) {
      if (id == null) return "null";
      return id.toString();
    }
    return md.toString();
  }
}
