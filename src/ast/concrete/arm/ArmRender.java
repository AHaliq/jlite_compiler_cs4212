package ast.concrete.arm;

import java.util.HashMap;

import ast.concrete.types.PrimTypes;
import org.apache.commons.text.StringEscapeUtils;

public class ArmRender { 
  public static String INDENT = "  ";
  public static Integer WIDTH = 4;
  public static Integer STACK_BASE_WIDTH = (RegisterAllocation.R + 1) * WIDTH;
  public static String STACK_SETUP = String.format("%sstmfd sp!,{fp,lr,%s}\n%sadd fp,sp,#%d\n", INDENT, String.join(",", RegisterAllocation.VAR_REGS), INDENT, STACK_BASE_WIDTH);
  public static String STACK_POP = String.format("%ssub sp,fp,#%d\n%sldmfd sp!,{fp,pc,%s}\n", INDENT, STACK_BASE_WIDTH, INDENT, String.join(",", RegisterAllocation.VAR_REGS));
  public static String PROLOGUE = String.format("%s.text\n%s.global main\n", INDENT, INDENT);

  public static String stackSetupWSpill(Integer spill) {
    return subSpFpOffset(spill, STACK_SETUP);
  }

  public static String subSpFpOffset(Integer spill) {
    return subSpFpOffset(spill, "");
  }

  public static String subSpFpOffset(Integer spill, String pre) {
    return String.format("%s%ssub sp,fp,#%d\n", pre, INDENT, STACK_BASE_WIDTH + WIDTH * spill);
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

  public static void render(ArmState s, IR3Enums stmt, String[] data, StringBuilder buf, HashMap<String,Integer> selMap, HashMap<String, Integer> objMap, HashMap<String,HashMap<String,Integer>> offMap, HashMap<String,String> lTypeMap, int exitIndex, String resetSpInst) {
    String reg;
    String reg2;
    String reg3;
    String argType;
    switch(stmt) {
      case LABEL:
        buf.append("\n");
        buf.append(data[0]);
        buf.append(":\n");
        break;
      case IF:
        reg = RegisterAllocation.ARG_REGS[0];
        genLoad(reg, data[0], buf, selMap);
        buf.append(String.format("%scmp %s,#1\n", INDENT, reg));
        buf.append(String.format("%sbeq %s\n", INDENT, data[1]));
        break;
      case GOTO:
        buf.append(String.format("%sb %s\n", INDENT, data[0]));
        break;
      case READ:
        break;
      case PRINT:
        reg = RegisterAllocation.ARG_REGS[0];
        reg2 = RegisterAllocation.ARG_REGS[1];
        argType = lTypeMap.get(data[0]);
        if(argType.equals(PrimTypes.INT.getStr())) {
          String ipl = s.getIntPrintLabel();
          buf.append(String.format("%sldr %s,=%s\n", INDENT, reg, ipl));
          genLoad(reg2, data[0], buf, selMap);
        } else if (argType.equals(PrimTypes.BOOL.getStr())) {

        } else if (argType.equals(PrimTypes.STRING.getStr())) {
          genLoad(reg, data[0], buf, selMap);
        }
        buf.append(INDENT);
        buf.append("bl printf(PLT)\n");
        break;
      case ASSIGN_FUNCTION_CALL:
        functionCall(true, data, buf, selMap, resetSpInst);
        break;
      case ASSIGN_NEW_OBJ:
        int size = objMap.get(data[1]);
        if (size > 0) {
          reg = RegisterAllocation.ARG_REGS[0];
          buf.append(String.format("%smov %s,#%d\n%sbl malloc(PLT)\n", INDENT, reg, size, INDENT));
          genStore(reg, data[0], buf, selMap);
        }
        break;
      case ASSIGN_OR_OP:
        binOp("orr", data, buf, selMap);
        break;
      case ASSIGN_AND_OP:
        binOp("and", data, buf, selMap);
        break;
      case ASSIGN_MUL_OP:
        binOp("mul", data, buf, selMap);
        break;
      case ASSIGN_DIV_OP:
        binOp("sdiv", data, buf, selMap);
        break;
      case ASSIGN_ADD_OP:
        argType = lTypeMap.get(data[0]);
        if (argType.equals(PrimTypes.INT.getStr())) {
          binOp("add", data, buf, selMap);
          // arithmetic add
        } else {
          reg = RegisterAllocation.ARG_REGS[0];
          reg2 = RegisterAllocation.ARG_REGS[1];
          reg3 = RegisterAllocation.ARG_REGS[2];
          genLoad(reg, data[2], buf, selMap);
          String callStrLen = String.format("%sbl strlen(PLT)\n", INDENT);
          String callStrCat = String.format("%sbl strcat(PLT)\n", INDENT);
          buf.append(callStrLen);
          buf.append(String.format("%smov %s,%s\n", INDENT, reg3, reg));
          genLoad(reg, data[1], buf, selMap);
          buf.append(callStrLen);
          buf.append(String.format("%sadd %s,%s,%s\n", INDENT, reg2, reg, reg3));
          buf.append(String.format("%sadd %s,%s,#1\n%sbl malloc(PLT)\n", INDENT, reg, reg2, INDENT));
          genLoad(reg2, data[1], buf, selMap);
          buf.append(callStrCat);
          genLoad(reg2, data[2], buf, selMap);
          buf.append(callStrCat);
          genStore(reg, data[0], buf, selMap);
          // string concat
        }
        break;
      case ASSIGN_SUB_OP:
        binOp("sub", data, buf, selMap);
        break;
      case ASSIGN_LT_OP:
        cmpOp("lt", data, buf, selMap);
        break;
      case ASSIGN_LTE_OP:
        cmpOp("le", data, buf, selMap);
        break;
      case ASSIGN_GT_OP:
        cmpOp("gt", data, buf, selMap);
        break;
      case ASSIGN_GTE_OP:
        cmpOp("ge", data, buf, selMap);
        break;
      case ASSIGN_EQ_OP:
        cmpOp("eq", data, buf, selMap);
        break;
      case ASSIGN_NEQ_OP:
        cmpOp("ne", data, buf, selMap);
        break;
      case ASSIGN_UNARY_INV_OP:
        reg = RegisterAllocation.ARG_REGS[0];
        reg2 = RegisterAllocation.ARG_REGS[1];
        genLoad(reg2, data[1], buf, selMap);
        buf.append(String.format("%srsb %s,%s,#1\n", INDENT, reg, reg2));
        genStore(reg, data[0], buf, selMap);
        break;
      case ASSIGN_UNARY_NEG_OP:
        reg = RegisterAllocation.ARG_REGS[0];
        reg2 = RegisterAllocation.ARG_REGS[1];
        genLoad(reg2, data[1], buf, selMap);
        buf.append(String.format("%srsb %s,%s,#0\n", INDENT, reg, reg2));
        genStore(reg, data[0], buf, selMap);
        break;
      case ASSIGN_DOT_OP:
        reg = RegisterAllocation.ARG_REGS[0];
        reg2 = RegisterAllocation.ARG_REGS[1];
        genLoad(reg2, data[1], buf, selMap);
        int offset = offMap.get(lTypeMap.get(data[1])).get(data[2]);
        buf.append(String.format("%sldr %s,[%s,#%d]\n", INDENT, reg, reg2, offset));
        genStore(reg, data[0], buf, selMap);
        break;
      case ASSIGN_ID:
        reg = RegisterAllocation.ARG_REGS[0];
        reg2 = RegisterAllocation.ARG_REGS[1];
        try {
        genLoad(reg2, data[1], buf, selMap);
        buf.append(String.format("%smov %s,%s\n", INDENT, reg, reg2));
        genStore(reg, data[0], buf, selMap);
        } catch (Exception e) {
          buf.append(" -------- HELP");
        }
        break;
      case ASSIGN_INT:
        reg = RegisterAllocation.ARG_REGS[0];
        buf.append(String.format("%smov %s,#%s\n", INDENT, reg, data[1]));
        genStore(reg, data[0], buf, selMap);
        break;
      case ASSIGN_STRING:
        String label = s.addAndGetStringLabel(data[1]);
        reg = RegisterAllocation.ARG_REGS[0];
        buf.append(String.format("%sldr %s,=%s\n", INDENT, reg, label));
        genStore(reg, data[0], buf, selMap);
        break;
      case ASSIGN_BOOL:
        reg = RegisterAllocation.ARG_REGS[0];
        buf.append(String.format("%smov %s,#%s\n", INDENT, reg, data[1]));
        genStore(reg, data[0], buf, selMap);
        break;
      case FUNCTION_CALL:
        functionCall(false, data, buf, selMap, resetSpInst);
        break;
      case RETURN:
        if(data.length > 0) {
          reg = RegisterAllocation.ARG_REGS[0];
          genLoad(reg, data[0], buf, selMap);
        }
        buf.append(String.format("%sgoto %s", INDENT, gotoExit(exitIndex)));
        break;
      default:
        buf.append(INDENT);
        buf.append("?\n");
        break;
    }
    buf.append("|"+ new IR3StmtParse(stmt, data).toString() + "\n");
  }

  public static void cmpOp(String op, String[] data, StringBuilder buf, HashMap<String,Integer> selMap) {
    String reg1 = RegisterAllocation.ARG_REGS[0];
    String reg2 = RegisterAllocation.ARG_REGS[1];
    String reg3 = RegisterAllocation.ARG_REGS[2];
    genLoad(reg2, data[1], buf, selMap);
    genLoad(reg3, data[2], buf, selMap);
    buf.append(String.format("%scmp %s,%s\n%smov %s,#0\n%smov%s %s,#1\n", INDENT, reg2, reg3, INDENT, reg1, INDENT, op, reg1));
    genStore(reg1, data[0], buf, selMap);
  }

  public static void binOp(String op, String[] data, StringBuilder buf, HashMap<String,Integer> selMap) {
    String reg1 = RegisterAllocation.ARG_REGS[0];
    String reg2 = RegisterAllocation.ARG_REGS[1];
    String reg3 = RegisterAllocation.ARG_REGS[2];
    genLoad(reg2, data[1], buf, selMap);
    genLoad(reg3, data[2], buf, selMap);
    buf.append(String.format("%s%s %s,%s,%s\n", INDENT, op, reg1, reg2, reg3));
    genStore(reg1, data[0], buf, selMap);
  }

  public static void functionCall(boolean assign, String[] data, StringBuilder buf, HashMap<String,Integer> selMap, String resetSpInst) {
    int io = assign ? 2 : 1;
    StringBuilder str = new StringBuilder();
    boolean argsInStack = false;
    for (int i = io + RegisterAllocation.SCRATCH_R; i < data.length; i++) {
      argsInStack = true;
      for (int j = 0; j < RegisterAllocation.SCRATCH_R; j++) {
        if(j == 0) {
          str.append(INDENT);
          str.append("stmfd sp!,{");
        }
        else str.append(",");
        str.append(RegisterAllocation.ARG_REGS[j]);
        int k = i + j;
        if (k >= data.length || j == RegisterAllocation.SCRATCH_R - 1) {
          str.append("}\n");
          break;
        }
        genLoad(RegisterAllocation.ARG_REGS[j], data[k], buf, selMap);
      }
      buf.append(str.toString());
    }
    // load more than SCRATCH_R args to stack
    int min = Math.min(RegisterAllocation.SCRATCH_R, data.length - 2);
    for (int i = 0; i < min; i++) {
      genLoad(RegisterAllocation.ARG_REGS[i], data[i + io], buf, selMap);
    }
    // load arguments to arg register
    buf.append(String.format("%sb %s\n", INDENT, data[io-1]));
    // make the call
    if (argsInStack) buf.append(resetSpInst);
    // reset SP
    if (assign) genStore(RegisterAllocation.ARG_REGS[0], data[0], buf, selMap);
    // optional save return value
  }

  public static void genLoad(String reg, String var, StringBuilder buf, HashMap<String,Integer> selMap) {
    int i = selMap.get(var);
    if (i < RegisterAllocation.R) {
      buf.append(String.format("%smov %s,%s\n", INDENT, reg, RegisterAllocation.VAR_REGS[i]));
      return;
    }
    buf.append(String.format("%sldr %s%s\n", INDENT, reg, memOperandOffset(i)));
  }

  public static void genStore(String reg, String var, StringBuilder buf, HashMap<String, Integer> selMap) {
    int i = selMap.get(var);
    if (i < RegisterAllocation.R) {
      buf.append(String.format("%smov %s,%s\n", INDENT, RegisterAllocation.VAR_REGS[i], reg));
      return;
    }
    buf.append(String.format("%sstr %s%s\n", INDENT, reg, memOperandOffset(i)));
  }

  public static String memOperandOffset(int index) {
    return String.format(",[fp,#%d]", RegisterAllocation.getSpillOffset(index));
  }

  public static void renderStringData(ArmState s, StringBuilder buf) {
    for(int i = 0; i < s.asci.size(); i++) {
      int ind = i + 1;
      buf.append("\nL");
      buf.append(ind);
      buf.append(":\n");
      buf.append(String.format("%s.asciz \"%s\"\n", INDENT, StringEscapeUtils.escapeJava(s.asci.get(i))));
    }
  }

  public static String gotoExit(int index) {
    return String.format("\nL%sExit:\n", index);
  }
}
