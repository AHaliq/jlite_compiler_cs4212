package ast.concrete.arm;

import org.apache.commons.text.StringEscapeUtils;

public class IR3StmtParse {
  public IR3Enums stmt;
  public String[] data;

  public IR3StmtParse(IR3Enums stmt, String... data) {
    this.stmt = stmt;
    this.data = data;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append(stmt);
    str.append(" | ");
    str.append(String.join(", ", data));
    return StringEscapeUtils.escapeJava(str.toString());
  }
}
