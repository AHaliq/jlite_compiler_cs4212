package ast.concrete.arm;

import java.util.HashMap;
import java.util.Vector;

public class Backend {

  public static String armOfIR3(String ir3) throws Exception {
    String[] parts = IR3Parser.splitIR3(ir3);
    Vector<String> sigs = IR3Parser.splitBodies(parts[0]);
    Vector<String> mtds = IR3Parser.splitBodies(parts[1]);
    // split IR3 output

    HashMap<String,Integer> objSizeMap = objectSizeMap(sigs);
    // generate object size map

    StringBuilder buf = new StringBuilder();
    Vector<String> mtdRenders = new Vector<>();

    for(int i = 0; i < mtds.size(); i++) {
      mtdRenders.add(renderMtd(mtds.get(i), i + 1, objSizeMap));
    }
    for(int i = mtdRenders.size() - 1; i >= 0; i--) {
      buf.append(mtdRenders.get(i));
    }
    // render methods

    // render string data

    return buf.toString();
  }

  public static String renderMtd(String mtd, Integer exitIndex, HashMap<String, Integer> objMap) throws Exception {
    StringBuilder buf = new StringBuilder();
    String[] args = IR3Parser.getFunctionArguments(mtd);
    String name = IR3Parser.getFunctionName(mtd);
    Boolean returnZero = IR3Parser.getFunctionReturnType(mtd).equals("Void");
    HashMap<String,Integer> selMap = RegisterAllocation.noMap(args, mtd);
    // generate selection map

    buf.append("\n");
    buf.append(name);
    buf.append(":\n");
    // function name label
    buf.append(ArmRender.stackSetupWSpill(RegisterAllocation.getNumberOfSpills(selMap)));
    // stack setup
    for(int i = 0; i < Math.min(args.length, RegisterAllocation.SCRATCH_R); i++) {
      buf.append(ArmRender.storeVar(args[i], RegisterAllocation.ARG_REGS[i], selMap));
    }
    // store args to free scratch registers

    // render function body

    buf.append(String.format("\nL%sExit:\n", exitIndex));
    buf.append(ArmRender.stackPop(returnZero));
    // render function epilogue
    return buf.toString();
  }

  public static HashMap<String, Integer> objectSizeMap(Vector<String> sigs) {
    HashMap<String, Integer> map = new HashMap<>();
    for (String sig : sigs) {
      String name = IR3Parser.sigToName(sig);
      Integer size = IR3Parser.sigEntries(sig);
      map.put(name, size * ArmRender.WIDTH);
    }
    return map;
  }
}