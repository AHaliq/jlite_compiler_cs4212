package ast.concrete.types;

public class MethodSignature {

  public static final String SEP = " -> ";
  
  private String[] types;

  public MethodSignature(String... types) {
    this.types = types.length == 0 ? new String[]{PrimTypes.VOID.getStr()} : types;
  }

  public int paramLength() {
    return types.length - 1;
  }

  public String get(int i) {
    return types[i + 1];
  }

  public String getReturn() {
    return types[0];
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    if(types.length == 1) {
      str.append("_");
      str.append(SEP);
    } else {
      for(int i = 1; i < types.length; i++) {
        str.append(types[i]);
        str.append(SEP);
      }
    }
    str.append(types[0]);
    return str.toString();
  }
}
