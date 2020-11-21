package ast.concrete.arm;

import java.util.HashMap;
import java.util.Vector;

public class Backend {

  public static String armOfIR3(String ir3) throws Exception {
    String[] parts = IR3Parser.splitIR3(ir3);
    Vector<String> sigs = IR3Parser.splitBodies(parts[0]);
    Vector<String> mtds = IR3Parser.splitBodies(parts[1]);
    // split IR3 output

    HashMap<String,Integer> objSizeMap = ObjectMaps.objectSizeMap(sigs);
    HashMap<String,HashMap<String,Integer>> objOffsetMap = ObjectMaps.allOffsetMap(sigs);
    // generate object size map

    ArmState s = new ArmState();

    // GENERATE DATA STRUCTURES -----------------------------------------------

    StringBuilder buf = new StringBuilder();
    Vector<String> mtdRenders = new Vector<>();
    

    for(int i = 0; i < mtds.size(); i++) {
      mtdRenders.add(renderMtd(s, mtds.get(i), i + 1, objSizeMap, objOffsetMap));
    }
    for(int i = mtdRenders.size() - 1; i >= 0; i--) {
      buf.append(mtdRenders.get(i));
    }
    // render methods

    StringBuilder buf2 = new StringBuilder();
    buf2.append(".data\n");
    ArmRender.renderStringData(s, buf2);
    // output string data
    buf2.append(ArmRender.PROLOGUE);
    // output ARM prologue
    buf2.append(buf.toString());
    // output methods
    return buf2.toString();
  }

  public static String renderMtd(ArmState s, String mtd, Integer exitIndex, HashMap<String, Integer> objMap, HashMap<String,HashMap<String,Integer>> offMap) throws Exception {
    StringBuilder buf = new StringBuilder();

    Vector<Vector<String>> argWType = IR3Parser.getFunctionArguments(mtd);
    Vector<String> args = argWType.get(1);
    Vector<String> argTpes = argWType.get(0);
    // parse arguments and get decl and type lists

    String name = IR3Parser.getFunctionName(mtd);
    // get name

    Vector<Vector<String>> splitMtd = IR3Parser.splitMtd(mtd);
    Vector<String> varDecls = splitMtd.get(0);
    Vector<String> body = splitMtd.get(1);
    Vector<String> declTpes = splitMtd.get(2);
    // split body from decls and populate decl type var lists

    HashMap<String, String> lTypeMap = localTypeMap(declTpes, varDecls, argTpes, args);
    // generate local variables + args type map

    Boolean returnZero = IR3Parser.getFunctionReturnType(mtd).equals("Void");
    HashMap<String,Integer> selMap = RegisterAllocation.noMap(args, varDecls);
    // generate selection map

    int spillCount = RegisterAllocation.getNumberOfSpills(selMap);
    String resetSpInst = ArmRender.subSpFpOffset(spillCount);

    // GENERATE DATA STRUCTURES -----------------------------------------------

    buf.append("\n");
    buf.append(name);
    buf.append(":\n");
    // function name label
    buf.append(ArmRender.stackSetupWSpill(spillCount));
    // stack setup
    ArmRender.renderArgStore(args, buf, selMap);
    // store args to register
    for (int i = 0; i < body.size(); i++) {
      String inst = body.get(i);
      IR3StmtParse p = IR3Parser.parseStmt(inst);
      if (p == null || p.stmt == null) {
        throw new Exception("Failed parsing IR3 statement : " + inst);
      }else {
        ArmRender.render(s, p.stmt, p.data, buf, selMap, objMap, offMap, lTypeMap, exitIndex, resetSpInst);
      }
    }
    // render function body
    buf.append(ArmRender.gotoExit(exitIndex));
    buf.append(ArmRender.stackPop(returnZero));
    // render function epilogue
    return buf.toString();
  }

  public static HashMap<String, String> localTypeMap(Vector<String> mtdLocalTypes, Vector<String> mtdLocalDecls, Vector<String> argTypes, Vector<String> argDecls) {
    HashMap<String, String> map = new HashMap<>();
    for(int i = 0; i < mtdLocalTypes.size(); i++) {
      map.put(mtdLocalDecls.get(i), mtdLocalTypes.get(i));
    }
    for(int i = 0; i < argTypes.size(); i++) {
      map.put(argDecls.get(i), argTypes.get(i));
    }
    return map;
  }
}