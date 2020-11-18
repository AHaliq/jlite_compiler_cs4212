package ast.concrete.arm;

import java.util.HashMap;

public class ArmRender { 
  public static String INDENT = "  ";
  public static Integer WIDTH = 4;
  public static Integer STACK_BASE_WIDTH = (RegisterAllocation.R + 1) * WIDTH;
  public static String STACK_SETUP = String.format("%sstmfd sp!,{fp,lr,%s}\n%sadd fp,sp,#%d\n", INDENT, String.join(",", RegisterAllocation.VAR_REGS), INDENT, STACK_BASE_WIDTH);
  public static String STACK_POP = String.format("%ssub sp,fp,#%d\n%sldmfd sp!,{fp,pc,%s}\n", INDENT, STACK_BASE_WIDTH, INDENT, String.join(",", RegisterAllocation.VAR_REGS));

  public static String stackSetupWSpill(Integer spill) {
    return String.format("%s%ssub sp,fp,#%d\n", STACK_SETUP, INDENT, STACK_BASE_WIDTH + WIDTH * spill);
  }

  public static String stackPop(Boolean returnZero) {
    if (returnZero) {
      return String.format("%smov r0,#0\n%s", INDENT, STACK_POP);
    }
    return STACK_POP;
  }

  public static String storeVar(String arg, String reg, HashMap<String,Integer> selMap) throws Exception {
    Integer i = selMap.get(arg);
    if (i == null) throw new Exception(String.format("unable to find %s when `storeVar`", reg));
    if (i < RegisterAllocation.R) {
      return String.format("%smov %s,%s\n", INDENT, RegisterAllocation.VAR_REGS[i], reg);
    }
    return String.format("%sstr %s,[fp,#%d]\n", INDENT, reg, RegisterAllocation.getSpillOffset(i));
  }
}
