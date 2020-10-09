package ast.concrete.types;

import java.util.Arrays;
import java.util.HashMap;

public class TypeCheck {

  public static boolean isT(HashMap<String,LocalEnv> cd, String t) {
    return cd.containsKey(t) || isPrimitive(t);
  }

  public static boolean isPrimitive(String t) {
    return Arrays.stream(PrimTypes.values()).reduce(
      false,
      (a,x) -> a || x.getStr().equals(t),
      (a,b) -> a || b);
  }

  // check in T functions -----------------------------------------------------
  
  public static TypeCheckLambda nullCheck = (cd, le, n) -> {
    return null;
  };

  // typecheck lambdas --------------------------------------------------------
}
