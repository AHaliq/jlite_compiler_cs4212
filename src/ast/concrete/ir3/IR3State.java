package ast.concrete.ir3;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

public class IR3State {
  private int labelid = 0;
  private int tempid = 0;
  public Stack<String> ir3Parts = new Stack<>();
  public Boolean stopIdentifierRender = false;

  public StringBuilder str = new StringBuilder();
  public StringBuilder strfull = new StringBuilder();
  public String savedT;

  private HashMap<String, Vector<Integer>> temps = new HashMap<>();

  public void resetLabels() {
    labelid = 0;
  }

  public void resetT() {
    tempid = 0;
    temps = new HashMap<>();
  }

  public int getLabel() {
    return ++labelid;
  }

  public String formLabel(int i) {
    return String.format(" Label %d:\n", i);
  }

  public String formGoto(int i) { 
    return formGoto(i, true);
  }

  public String formGoto(int i, Boolean indent) { 
    return String.format("%sgoto %d;%s", indent ? "  " : "", i, indent ? "\n" : "");
  }

  public String getT(String t) {
    int id = ++tempid;
    Vector<Integer> ts = temps.get(t);
    if(ts == null) {
      temps.put(t, new Vector<>(Arrays.asList(new Integer[]{id})));
    }else {
      ts.add(id);
    }
    return formT(id);
  }

  public String formT(int i) {
    return String.format("_t%d", i);
  }

  public String getSavedT() {
    return savedT;
  }

  public void flush() {
    temps.forEach((k, ts) -> {
      ts.forEach((i) -> {
        strfull.append("  ");
        strfull.append(k);
        strfull.append(" ");
        strfull.append(formT(i));
        strfull.append(";\n");
      });
    });
    strfull.append(str.toString());
    str = new StringBuilder();
    resetT();
  }
}
