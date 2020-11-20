package ast.concrete.arm;

import java.util.Vector;

public class ArmState {
  private int intPrints = 0;
  private int truePrints = 0;
  private int falsePrints = 0;
  public int id = 1;
  public Vector<String> asci = new Vector<>();

  public String addAndGetStringLabel(String str) {
    asci.add(str);
    return getPrintLabel(id++);
  }

  public String getFalsePrintLabel() {
    if (falsePrints == 0) {
      falsePrints = id++;
    }
    asci.add("False");
    return getPrintLabel(falsePrints);
  }

  public String getTruePrintLabel() {
    if (truePrints == 0) {
      truePrints = id++;
    }
    asci.add("True");
    return getPrintLabel(truePrints);
  }

  public String getIntPrintLabel() {
    if (intPrints == 0) {
      intPrints = id++;
    }
    asci.add("%i");
    return getPrintLabel(intPrints);
  }

  public String getPrintLabel(int i) {
    return String.format("L%d", i);
  }
}
