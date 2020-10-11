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
  public NonTerminal mdBody;  // actually not necessary to have reference to method body for caller nodes
  private String ir3Name;

  public MethodSignature(NonTerminal mdDecl) throws Exception{
    this(
      mdDecl.get(0).toRender(),
      mdDecl.get(1).toRender(),
      mdDecl.getVariant() == 0 ? (NonTerminal) mdDecl.get(2) : null,
      (NonTerminal) mdDecl.get(mdDecl.getVariant() == 0 ? 3 : 2));
  }

  public MethodSignature(String retType, String id, NonTerminal mdBody) throws Exception {
    this(retType, id, null, mdBody);
  }

  public MethodSignature(String retType, String id, NonTerminal fmlList, NonTerminal mdBody) throws Exception {
    this.mdBody = mdBody;
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

  public void setIR3Name(String n) { this.ir3Name = n; }
  public String getIR3Name() { return ir3Name; }

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
