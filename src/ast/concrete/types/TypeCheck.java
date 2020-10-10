package ast.concrete.types;

import java.util.Arrays;
import java.util.HashMap;

import ast.NonTerminal;

public class TypeCheck {

  public static boolean isT(HashMap<String,LocalEnv> cd, String t) {
    return t == null || cd.containsKey(t) || isPrimitive(t);
  }

  public static boolean isPrimitive(String t) {
    return Arrays.stream(PrimTypes.values()).reduce(
      false,
      (a,x) -> a || x.getStr().equals(t),
      (a,b) -> a || b);
  }

  // check in T functions -----------------------------------------------------

  public static TypeCheckLambda programCheck = (cd, le, n) -> {
    return TypeCheck.allOk.check(InitTypeCheckObjects.initialize(n), new LocalEnv(), n);
  };

  public static TypeCheckLambda mainCheck = (cd, le, n) -> {
    String cname = n.getName();
    LocalEnv le2 = le.clone();
    le2.put(cname, cd.get(cname));
    NonTerminal mdBody = (NonTerminal) (n.getVariant() == 0 ? n.get(2) : n.get(1));
    methodCheck(cd, le2, "main", mdBody);
    return PrimTypes.IS_OK.getStr();
  };

  public static TypeCheckLambda classCheck = (cd, le, n) -> {
    String cname = n.getName();
    LocalEnv le2 = le.clone();
    le2.put(cname, cd.get(cname));
    ((NonTerminal) n.get(2)).forEach(md -> {
      ((NonTerminal) md).typeCheck(cd, le2);
    });
    return PrimTypes.IS_OK.getStr();
  };

  public static TypeCheckLambda mdDeclCheck = (cd, le, n) -> {
    NonTerminal mdBody = (NonTerminal) (n.getVariant() == 0 ? n.get(3) : n.get(2));
    methodCheck(cd, le, n.getName(), mdBody);
    return PrimTypes.IS_OK.getStr();
  };

  public static TypeCheckLambda varDeclCheck = (cd, le, n) -> {
    String t = n.get(0).toRender();
    if(!isT(cd, t)) {
      throw new Exception("local variable declared in method with undefined type '" + t + "'");
    }
    return PrimTypes.IS_OK.getStr();
  };

  public static TypeCheckLambda ifStmtCheck = (cd, le, n) -> {
    if (!teq(PrimTypes.BOOL.getStr(), ((NonTerminal) n.get(0)).typeCheck(cd, le))) {
      throw new Exception("expression in if statement must be a '" + PrimTypes.BOOL.getStr() + "'");
    }
    String ifType = ((NonTerminal) n.get(1)).typeCheck(cd, le);
    String elseType = ((NonTerminal) n.get(2)).typeCheck(cd, le);
    if (!teq(ifType,elseType)) {
      throw new Exception("block type in if statement does not match block type in else statement");
    }
    return elseType;
  };

  public static TypeCheckLambda whileStmtCheck = (cd, le, n) -> {
    TypeCheck.vacuousWhileStmtCheck.check(cd, le, n);
    return ((NonTerminal) n.get(1)).typeCheck(cd, le);
  };

  public static TypeCheckLambda vacuousWhileStmtCheck = (cd, le, n) -> {
    if (!teq(PrimTypes.BOOL.getStr(), ((NonTerminal) n.get(0)).typeCheck(cd, le))) {
      throw new Exception("expression in while statement must be a '" + PrimTypes.BOOL.getStr() + "'");
    }
    return PrimTypes.VOID.getStr();
  };

  public static TypeCheckLambda printCheck = readOrPrintCheck("argument for println must be Int, Bool or String");

  public static TypeCheckLambda readCheck = readOrPrintCheck("argument for readln must be Int, Bool or String");

  public static TypeCheckLambda idAssignCheck = (cd, le, n) -> {
    String id = n.get(0).toRender();
    String it = le.getFd(id);
    NonTerminal en = (NonTerminal) n.get(1);
    String et = en.typeCheck(cd, le);
    if(!teq(it,et)) {
      throw new Exception("cannot assign '" + en.toRender() + "' to identifier '" + id + "' of type '" + it + "'");
    }
    return PrimTypes.VOID.getStr();
  };

  public static TypeCheckLambda fdCheck = (cd, le, n) -> {
    NonTerminal an = (NonTerminal) n.get(0);
    String fd = n.get(1).toRender();

    String at = an.typeCheck(cd, le);
    if (at == null) {
      throw new Exception("undefined field of '" + fd + "' in null value");
    }

    LocalEnv le2 = cd.get(at);
    String fdt = le2.getFd(fd);
    if(fdt == null) {
      fdt = le2.getSg(fd).toTypeSignature();
      // hack to return method signature and use in method call in a different production rule
      // if we separated function types and value types and encoded them differently this hack wouldnt work
      // thankfully we encoded everything as a string so we can pass the signature to method call prod. rule
      if (fdt == null) {
        throw new Exception("identifier '" + fd + "' is not defined for class '" + at + "'");
      }
    }
    return fdt;
  };

  public static TypeCheckLambda fdAssignCheck = (cd, le, n) -> {
    NonTerminal an = (NonTerminal) n.get(0);
    String fd = n.get(1).toRender();
    NonTerminal en = (NonTerminal) n.get(2);

    String at = an.typeCheck(cd, le);
    if (at == null) {
      throw new Exception("undefined field of '" + fd + "' in null value");
    }
    String et = en.typeCheck(cd, le);
    String fdt = cd.get(at).getFd(fd);
    if(fdt == null) {
      throw new Exception("field '" + fd + "' is not defined for class '" + at + "'");
    }
    if (!teq(et, fdt)) {
      throw new Exception("cannot assign '" + en.toRender() + "' to field '" + fd + "' of type '" + fdt + "'");
    }
    return PrimTypes.VOID.getStr();
  };

  public static TypeCheckLambda funcCallCheck = (cd, le, n) -> {
    return methodCallCheck(cd, le, (NonTerminal) n.get(0), (NonTerminal) n.get(1));
  };

  public static TypeCheckLambda vacuousFuncCallCheck = (cd, le, n) -> {
    return methodCallCheck(cd, le, (NonTerminal) n.get(0), null);
  };

  public static TypeCheckLambda returnCheck = (cd, le, n) -> {
    String t = TypeCheck.passFirstCheck.check(cd, le, n);
    String t2 = le.getFd(PrimTypes.RET.getStr());
    if (!teq(t,t2)) {
      throw new Exception("return type of '" + n.toRender() + "' does not match expecting return type of '" + t2 + "'");
    }
    return t2;
  };

  public static TypeCheckLambda returnVoidCheck = (cd, le, n) -> {
    String t = le.getFd(PrimTypes.RET.getStr());
    if (!teq(t, PrimTypes.VOID.getStr())) {
      throw new Exception("function expecting return type of '" + t + "' but void return was given");
    }
    return PrimTypes.VOID.getStr();
  };

  public static TypeCheckLambda thisCheck = lookupLE(PrimTypes.THIS.getStr());

  public static TypeCheckLambda idCheck = (cd, le, n) -> {
    return TypeCheck.lookupLE(n.get(0).toRender()).check(cd, le, n);
  };

  public static TypeCheckLambda constructorCheck = (cd, le, n) -> {
    String c = n.get(0).toRender();
    if(!cd.containsKey(c)) {
      throw new Exception("cannot construct instance of undefined class '" + c + "'");
    }
    return c;
  };

  // typecheck lambdas --------------------------------------------------------

  public static TypeCheckLambda passFirstCheck = (cd, le, n) -> {
    return ((NonTerminal) n.get(0)).typeCheck(cd, le);
  };

  public static TypeCheckLambda lookupLE(String id) {
    return (cd,le,n) -> le.getFd(id);
  }

  public static String methodCallCheck(HashMap<String, LocalEnv> cd, LocalEnv le, NonTerminal f, NonTerminal expList) throws Exception {
    String mds = f.typeCheck(cd, le);
    String[] ts = mds.split(MethodSignature.SEP);
    if(ts.length < 2) {
      throw new Exception("the function '" + f.toRender() + "' does not type check to a valid function");
    }
    if(expList == null) {
      if (ts[0] != MethodSignature.UNIT || ts.length != 2) {
        throw new Exception("The function is expecting arguments however none were specified");
      }
      return ts[1];
    }
    if (expList.length() != ts.length - 1) {
      throw new Exception("mismatch number of arguments");
    }
    for (int i = 0; i < expList.length(); i++) {
      NonTerminal e = (NonTerminal) expList.get(i);
      String t = e.typeCheck(cd, le);
      if(!teq(t, ts[i])) {
        throw new Exception("Expression '" + e.toRender() + "' does not match function param type in the function call");
      }
    }
    return ts[ts.length-1];
  }

  public static void methodCheck(HashMap<String,LocalEnv> cd, LocalEnv le, String id, NonTerminal mdBody) throws Exception {
    LocalEnv le2 = le.clone();
    MethodSignature ms = le2.getSg(id);
    le2.put(ms);
    if (!teq(ms.getReturn(), mdBody.typeCheck(cd, le2))) {
      throw new Exception("method '" + id + "' return type is not as specified.");
    }
  }

  public static TypeCheckLambda readOrPrintCheck(String msg){
    return (cd, le, n) -> {
      String t = ((NonTerminal) n.get(0)).typeCheck(cd, le);
      if (!teq(t, PrimTypes.BOOL.getStr()) && !teq(t, PrimTypes.INT.getStr()) && !teq(t, PrimTypes.STRING.getStr())) {
        throw new Exception(msg);
      }
      return PrimTypes.VOID.getStr();
    };
  }

  public static TypeCheckLambda allOk = (cd, le, n) -> {
    n.forEach(mn -> ((NonTerminal) mn).typeCheck(cd, le));
    return PrimTypes.IS_OK.getStr();
  };

  public static TypeCheckLambda allBool = allCheck(PrimTypes.BOOL.getStr());

  public static TypeCheckLambda allInt = allCheck(PrimTypes.INT.getStr());

  public static TypeCheckLambda allString = allCheck(PrimTypes.STRING.getStr());

  public static TypeCheckLambda allCheck(String t) {
    return (cd, le, n) -> {
      for (int i = 0; i < n.length(); i++) {
        if (!teq(((NonTerminal) n.get(i)).typeCheck(cd, le), t)) {
          throw new Exception("expecting all operands to be " + t + " but encountered '" + n.get(i).toRender() + "'");
        }
      }
      return t;
    };
  }

  public static TypeCheckLambda takeLast = (cd, le, n) -> {
    String last = null;
    for (int i = 0; i < n.length(); i++) {
      last = ((NonTerminal) n.get(i)).typeCheck(cd ,le);
    }
    return last;
  };

  public static TypeCheckLambda nullCheck = constCheck(null);

  public static TypeCheckLambda voidCheck = constCheck(PrimTypes.VOID.getStr());

  public static TypeCheckLambda intCheck = constCheck(PrimTypes.INT.getStr());

  public static TypeCheckLambda boolCheck = constCheck(PrimTypes.BOOL.getStr());

  public static TypeCheckLambda stringCheck = constCheck(PrimTypes.STRING.getStr());

  public static TypeCheckLambda constCheck(String s) {
    return (cd, le, n) -> { return s; };
  }

  // higher order lambdas -----------------------------------------------------

  public static boolean teq(String t1, String t2) {
    return t1 == null || t2 == null || t1.equals(t2);
  }

}
