package ast.concrete.arm;

import java.util.HashMap;
import java.util.Vector;

public class ObjectMaps {
  
  public static HashMap<String, Integer> objectSizeMap(Vector<String> sigs) {
    HashMap<String, Integer> map = new HashMap<>();
    for (String sig : sigs) {
      String name = IR3Parser.sigToName(sig);
      Integer size = IR3Parser.sigEntries(sig);
      map.put(name, size * ArmRender.WIDTH);
    }
    return map;
  }

  public static HashMap<String, HashMap<String,Integer>> allOffsetMap(Vector<String> sigs) {
    HashMap<String, HashMap<String,Integer>> map = new HashMap<>();
    for(String sig: sigs) {
      String name = IR3Parser.sigToName(sig);
      map.put(name, classOffsetMap(sig));
    }
    return map;
  }

  public static HashMap<String, Integer> classOffsetMap(String sig) {
    HashMap<String, Integer> offsetMap = new HashMap<>();
    String[] parts = sig.split("\n");
    for(int i = 1; i < parts.length - 1; i++) {
      String[] sigParts = IR3Parser.splitSigEntry(parts[i]);
      offsetMap.put(sigParts[1], (i - 1) * ArmRender.WIDTH);
    }
    return offsetMap;
  }
}
