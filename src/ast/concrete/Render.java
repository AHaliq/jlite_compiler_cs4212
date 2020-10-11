package ast.concrete;

public class Render {

  public static final RenderLambda linearRender = (n) -> {
    StringBuilder str = new StringBuilder();
    n.forEach((e) -> {
      str.append(" ");
      str.append(e.toRender());
    });
    return str.length() > 0 ? str.toString().substring(1) : "";
  };

  public static final RenderLambda concatRender = (n) -> {
    StringBuilder str = new StringBuilder();
    n.forEach((e) -> {
      str.append(e.toRender());
    });
    return str.toString();
  };

  public static final RenderLambda mainClass = (n) -> {
    String c = n.get(0).toRender();
    String f = n.getVariant() == 0 ? n.get(1).toRender() : "";
    String m = n.get(n.getVariant() == 0 ? 2 : 1).toRender();
    return String.format("class %s{void main(%s)%s}", c, f, m);
  };

  public static final RenderLambda classDecl = (n) -> {
    String c = n.get(0).toRender();
    String v = n.get(1).toRender();
    String m = n.get(2).toRender();
    if (v.length() > 0) {
      return String.format("class %s{%s%s}", c, v, m);
    }
    return String.format("class %s{%s}", c, m);
  };

  public static final RenderLambda varDecl = (n) -> {
    return Render.linearRender.render(n) + ";";
  };

  public static final RenderLambda mdDecl = (n) -> {
    String t = n.get(0).toRender();
    String i = n.get(1).toRender();
    String f = n.getVariant() == 0 ? n.get(2).toRender() : "";
    String m = n.get(n.getVariant() == 0 ? 3 : 2).toRender();
    return String.format("%s %s(%s)%s", t, i, f, m);
  };

  public static final RenderLambda fmlList = (n) -> {
    if (n.getVariant() == 1) {
      return String.format("%s %s", n.get(0).toRender(), n.get(1).toRender());
    }
    StringBuilder str = new StringBuilder();
    n.forEach((e) -> {
      str.append(",");
      str.append(e.toRender());
    });
    return str.toString().substring(1);
  };

  public static final RenderLambda type = (n) -> {
    switch (n.getVariant()) {
      case 0:
        return "Int";
      case 1:
        return "Bool";
      case 2:
        return "String";
      case 3:
        return "Void";
      default:
        return n.get(0).toRender();
    }
  };

  public static final RenderLambda mdBody = (n) -> {
    String v = n.get(0).toRender();
    String s = n.get(1).toRender();
    return String.format("{%s%s}", v, s);
  };

  public static final RenderLambda stmt = (n) -> {
    switch (n.getVariant()) {
      case 0:
        return String.format("If(%s){%s}else{%s}", n.get(0).toRender(), n.get(1).toRender(), n.get(2).toRender());
      case 1:
        return String.format("While(%s){%s}", n.get(0).toRender(), n.get(1).toRender());
      case 2:
        return String.format("While(%s){}", n.get(0).toRender());
      case 3:
        return String.format("readln(%s);", n.get(0).toRender());
      case 4:
        return String.format("println(%s);", n.get(0).toRender());
      case 5:
        return String.format("%s=%s;", n.get(0).toRender(), n.get(1).toRender());
      case 6:
        return String.format("%s.%s=%s;", n.get(0).toRender(), n.get(1).toRender(), n.get(2).toRender());
      case 7:
        return String.format("%s(%s);", n.get(0).toRender(), n.get(1).toRender());
      case 8:
        return String.format("Return %s;", n.get(0).toRender());
      case 9:
        return "Return;";
      case 10:
        return String.format("%s();", n.get(0).toRender());
      default:
        return Render.linearRender.render(n);
    }
  };

  public static final RenderLambda bExp = (n) -> {
    return String.format("[%s,%s](||)", n.get(0).toRender(), n.get(1).toRender());
  };

  public static final RenderLambda conj = (n) -> {
    return String.format("[%s,%s](&&)", n.get(0).toRender(), n.get(1).toRender());
  };

  public static final RenderLambda bOp = (n) -> {
    switch (n.getVariant()) {
      case 0:
        return "<";
      case 1:
        return ">";
      case 2:
        return "<=";
      case 3:
        return ">=";
      case 4:
        return "==";
      case 5:
        return "!=";
      default:
        return Render.linearRender.render(n);
    }
  };

  public static final RenderLambda bGrd = (n) -> {
    switch (n.getVariant()) {
      case 0:
        return String.format("(!)[%s]", n.get(0).toRender());
      case 1:
        return "true";
      case 2:
        return "false";
      default:
        return Render.linearRender.render(n);
    }
  };

  public static final RenderLambda aExp = (n) -> {
    switch (n.getVariant()) {
      case 0:
        return String.format("%s + %s", n.get(0).toRender(), n.get(1).toRender());
      case 1:
        return String.format("%s - %s", n.get(0).toRender(), n.get(1).toRender());
      default:
        return Render.linearRender.render(n);
    }
  };

  public static final RenderLambda term = (n) -> {
    switch (n.getVariant()) {
      case 0:
        return String.format("%s * %s", n.get(0).toRender(), n.get(1).toRender());
      case 1:
        return String.format("%s / %s", n.get(0).toRender(), n.get(1).toRender());
      default:
        return Render.linearRender.render(n);
    }
  };

  public static final RenderLambda ftr = (n) -> {
    switch (n.getVariant()) {
      case 1:
        return String.format("(-)[%s]", n.get(0).toRender());
      default:
        return Render.linearRender.render(n);
    }
  };

  public static final RenderLambda sExp = (n) -> {
    switch (n.getVariant()) {
      case 0:
        return String.format("%s + %s", n.get(0).toRender(), n.get(1).toRender());
      case 1:
        return String.format("\"%s\"", n.get(0).toRender());
      default:
        return Render.linearRender.render(n);
    }
  };

  public static final RenderLambda atom = (n) -> {
    switch (n.getVariant()) {
      case 0:
        return String.format("%s.%s", n.get(0).toRender(), n.get(1).toRender());
      case 1:
        return String.format("[%s(%s)]", n.get(0).toRender(), n.get(1).toRender());
      case 2:
        return String.format("[%s()]", n.get(0).toRender());
      case 3:
        return "this";
      case 5:
        return String.format("new %s()", n.get(0).toRender());
      case 6:
        return String.format("(%s)", n.get(0).toRender());
      case 7:
        return "null";
      default:
        return Render.linearRender.render(n);
    }
  };
}
