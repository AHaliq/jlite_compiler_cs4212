package ast.concrete.arm;

import java.util.HashMap;
import java.util.Vector;

public class RegisterAllocation {
  public static String[] VAR_REGS = new String[]{"v1", "v2", "v3", "v4", "v5"};
  public static String[] ARG_REGS = new String[]{"a1", "a2", "a3", "a4"};
  public static Integer R = VAR_REGS.length;
  public static Integer SCRATCH_R = ARG_REGS.length;

  public static HashMap<String, Integer> noMap(Vector<String> args, Vector<String> varDecls) {
    HashMap<String, Integer> map = new HashMap<>();
    Integer idx = 0;
    for(int i = 0; i < Math.min(RegisterAllocation.SCRATCH_R, args.size()); i++) {
      map.put(args.get(i), idx++);
    }
    for (int i = RegisterAllocation.SCRATCH_R; i < args.size(); i++) {
      int off = -args.size() + i;
      map.put(args.get(i), off);
    }
    for(String var : varDecls) {
      map.put(var, idx++);
    }
    return map;
  }

  public static Integer getNumberOfSpills(HashMap<String, Integer> map) {
    Integer regs = 0;
    for(Integer v : map.values()) {
      if (v >= 0) regs++;
    }
    regs -= R;
    return regs > 0 ? regs : 0;
  }

  public static Integer getSpillOffset(Integer index) {
    if(index < R && index >= 0) return null;
    if (index >= 0) index += 2;
    index *= ArmRender.WIDTH;
    return -index;
  }
}
