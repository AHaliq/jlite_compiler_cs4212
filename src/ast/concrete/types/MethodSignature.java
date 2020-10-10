package ast.concrete.types;

import java.util.Arrays;
import java.util.HashMap;

import ast.NonTerminal;

public class MethodSignature {

  public static final String SEP = " -> ";
  public static final String UNIT = "()";
  
  private String retType;
  private String[] paramTypes;
  private String[] paramIds;

  public MethodSignature(NonTerminal mdDecl) throws Exception{
    this(mdDecl.get(0).toRender(), mdDecl.get(1).toRender(), mdDecl.getVariant() == 0 ? (NonTerminal) mdDecl.get(2) : null);
  }

  public MethodSignature(String retType, String id) throws Exception {
    this(retType, id, null);
  }

  public MethodSignature(String retType, String id, NonTerminal fmlList) throws Exception {
    this.retType = retType;
    if(fmlList != null) {
      paramTypes = new String[fmlList.length()];
      paramIds = new String[fmlList.length()];
      for(int i = 0; i < fmlList.length(); i++) {
        NonTerminal p = (NonTerminal) fmlList.get(i);
        paramTypes[i] = p.get(0).toRender();
        paramIds[i] = p.get(1).toRender();
      }
    }else {
      paramTypes = new String[]{};
      paramIds = new String[]{};
    }
  }

  public int paramLength() {
    return paramTypes.length;
  }

  public String getType(int i) {
    return paramTypes[i];
  }

  public String getId(int i) {
    return paramIds[i];
  }

  public int length() {
    return paramTypes.length;
  }

  public String getReturn() {
    return retType;
  }

  public boolean inT(HashMap<String, LocalEnv> cd) {
    if(!TypeCheck.isT(cd, retType)) return false;
    return Arrays.stream(paramTypes).allMatch(t -> TypeCheck.isT(cd, t));
  }

  public String toTypeSignature() {
    StringBuilder str = new StringBuilder();
    if (paramTypes.length == 0) {
      str.append(UNIT);
      str.append(SEP);
    }else {
      for(int i = 0; i < paramTypes.length; i++) {
        str.append(paramTypes[i]);
        str.append(SEP);
      }
    }
    str.append(retType);
    return str.toString();
  }

  @Override
  public String toString() {
    return toTypeSignature();
  }
}
