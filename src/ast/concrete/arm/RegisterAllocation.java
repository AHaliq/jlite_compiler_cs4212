package ast.concrete.arm;

import java.util.HashMap;

public class RegisterAllocation {
  public static HashMap<String, Integer> noMap(String mtd) {
    HashMap<String, Integer> map = new HashMap<>();
    Integer idx = 0;
    for(String var : IR3Parser.varDecls(mtd)) {
      map.put(var, idx++);
    }
    return map;
  }
}
