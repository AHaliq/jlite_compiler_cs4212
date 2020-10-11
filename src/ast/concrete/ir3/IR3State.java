package ast.concrete.ir3;

import java.util.Stack;

public class IR3State {
  private int labelid = 0;
  private int tempid = 0;
  private int savePoint = 0;
  public Stack<Integer> saveChunk = new Stack<>();
  public Stack<String> ir3Parts = new Stack<>();
  public Boolean stopIdentifierRender = false;

  public StringBuilder str = new StringBuilder();

  public void save() {
    tempid++;
    savePoint=tempid;
  }

  public void free() {
    savePoint--;
    resetT();
  }

  public int getSavePoint() {
    return savePoint;
  }

  public void resetLabels() {
    labelid = 0;
  }

  public void resetT() {
    tempid = savePoint;
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

  public String getNextT() {
    return formT(tempid + 1);
  }

  public String getNextT(int offset) {
    return formT(tempid + offset + 1);
  }

  public String getT() {
    return formT(++tempid);
  }

  public String formT(int i) {
    return String.format("_t%d", i);
  }

}
