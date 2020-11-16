package ast.concrete.arm;

import java.util.HashMap;
import java.util.Vector;

public class Backend {

  public static String armOfIR3(String ir3) {
    String[] parts = IR3Parser.splitIR3(ir3);
    Vector<String> sigs = IR3Parser.splitBodies(parts[0]);
    Vector<String> mtds = IR3Parser.splitBodies(parts[1]);
    // split IR3 output

    HashMap<String,Integer> objSizeMap = objectSizeMap(sigs);
    // generate object size map

    StringBuilder buf = new StringBuilder();
    // render head

    for(int i = 0; i < sigs.size(); i++) {
      renderMtd(sigs.get(i), mtds.get(i), objSizeMap, buf);
    }
    // render methods

    // render tail

    return "";
  }

  public static String renderMtd(String sig, String mtd, HashMap<String, Integer> objMap, StringBuilder buf) {
    HashMap<String,Integer> selMap = RegisterAllocation.noMap(mtd);
    // generate selection map

    // render function prologue

    // render function body

    // render function epilogue
    return null;
  }

  public static HashMap<String, Integer> objectSizeMap(Vector<String> sigs) {
    HashMap<String, Integer> map = new HashMap<>();
    for (String sig : sigs) {
      String name = IR3Parser.sigToName(sig);
      Integer size = IR3Parser.sigEntries(sig);
      map.put(name, size * 4);
    }
    return map;
  }
}

// IMMEDIATE TODO:
//  ARM code output head 
//  method body prologue