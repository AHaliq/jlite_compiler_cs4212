package ast.concrete.ir3;

import java.util.HashMap;
import java.util.Vector;

import ast.NonTerminal;
import ast.Terminal;
import ast.concrete.types.PrimTypes;
import javasrc.cup.Sym;

public class IR3 {
  public static String DATA_START = "======= CData3 =======\n\n";
  public static String MTD_START = "======= CMtd3 =======\n\n";
  public static String IR3_END = "=====fx== End of IR3 Program =======";
  public static IR3Lambda program = (s, n) -> {
    s.str.append(DATA_START);
    NonTerminal mainNode = (NonTerminal) n.get(0);
    s.str.append(String.format("class %s{\n}\n\n", mainNode.getName()));
    NonTerminal cds = (NonTerminal) n.get(1);
    Vector<HashMap<String,String>> classFieldsTypes = new Vector<>();
    for(int i = 0; i < cds.length(); i++) {
      s.localMap = new HashMap<>();
      NonTerminal c = (NonTerminal) cds.get(i);
      s.str.append(String.format("class %s{\n", c.getName()));
      NonTerminal vds = (NonTerminal) c.get(1);
      s = vds.toIR3(s);
      s.str.append("}\n\n");
      classFieldsTypes.add(s.localMap);
    }
    s.str.append(MTD_START);
    s.flush();
    NonTerminal mainFml = (NonTerminal) (mainNode.getVariant() == 0 ? mainNode.get(1) : null);
    NonTerminal mainBody = (NonTerminal) mainNode.get(mainNode.getVariant() == 0 ? 2 : 1);
    s.thisType = mainNode.getName();
    s = cmtd(s, PrimTypes.VOID.getStr(), "main", mainNode.getName(), mainFml, mainBody, false);
    for(int i = 0; i < cds.length(); i++) {
      NonTerminal c = (NonTerminal) cds.get(i);
      s.thisType = c.getName();
      s.localMap = classFieldsTypes.get(i);
      NonTerminal mdcs = (NonTerminal) c.get(2);
      for(int j = 0; j < mdcs.length(); j++) {
        s.localVars = new Vector<>();
        NonTerminal m = (NonTerminal) mdcs.get(j);
        NonTerminal fml = (NonTerminal) (m.getVariant() == 0 ? m.get(2) : null);
        NonTerminal mdb = (NonTerminal) m.get(m.getVariant() == 0 ? 3 : 2);
        s = cmtd(s, m.get(0).toRender(), m.getName(), c.getName(), fml, mdb);
      }
    }
    s.str.append(IR3_END);
    s.flush();
    return s;
  };

  public static IR3Lambda varDecl = (s, v) -> {
    String var = v.get(1).toRender();
    String tpe = v.get(0).toRender();
    s.localVars.add(var);
    s.localMap.put(var, tpe);
    s.str.append(String.format("  %s %s;\n", tpe, var));
    return s;
  };

  public static IR3Lambda ifStmt = (s, n) -> {
    NonTerminal e = (NonTerminal) n.get(0);
    NonTerminal s1 = (NonTerminal) n.get(1);
    NonTerminal s2 = (NonTerminal) n.get(2);

    s = e.toIR3(s);
    int l1 = s.getLabel();
    s.str.append(ir3if(s, s.getSavedT(), l1));
    s = s2.toIR3(s);
    int l2 = s.getLabel();
    s.str.append(s.formGoto(l2));
    s.str.append(s.formLabel(l1));
    s = s1.toIR3(s);
    s.str.append(s.formLabel(l2));
    return s;
  };

  public static IR3Lambda whileStmt = (s, n) -> {
    NonTerminal st = (NonTerminal) n.get(1);
    return IR3.whileStmtGen(st).render(s, n);
  };

  public static IR3Lambda vacuousWhileStmt = (s, n) -> {
    return IR3.whileStmtGen(null).render(s, n);
  };

  public static IR3Lambda readLn = (s, n) -> {
    s.str.append(String.format("  readln(%s);\n", n.get(0).toRender()));
    return s;
  };

  public static IR3Lambda printLn = (s, n) -> {
    NonTerminal e = (NonTerminal) n.get(0);
    s = e.toIR3(s);
    s.str.append(String.format("  println(%s);\n", s.getSavedT()));
    return s;
  };

  public static IR3Lambda assg = (s, n) -> {
    String identifier = n.get(0).toRender();
    NonTerminal a = new NonTerminal(0, IR3.ident(identifier), 4, (Terminal) n.get(0));
    NonTerminal e = (NonTerminal) n.get(1);
    if (genNonLocal(identifier, s, a)) {
      identifier = s.getSavedT();
      n.tpe = s.thisType;
    }
    s = e.toIR3(s);
    s.str.append(String.format("  %s = %s;\n", identifier, s.getSavedT()));
    return s;
  };

  public static IR3Lambda fdassg = (s,n) -> {
    return IR3.binOp("  %s."+ n.get(1).toRender() + " = %s;\n", 0, 2).render(s, n);
  };

  public static IR3Lambda expList = (s, n) -> {
    StringBuilder part = new StringBuilder();
    for(int i = 0; i < n.length(); i++) {
      NonTerminal e = (NonTerminal) n.get(i);
      s = e.toIR3(s);
      part.append(s.getSavedT());
      if (i < n.length() - 1) part.append(", ");
    }
    s.ir3Parts.push(part.toString());
    return s;
  };

  public static IR3Lambda funcCall = genFuncCall(false);

  public static IR3Lambda vacuousFuncCall = genFuncCall(false, true);

  public static IR3Lambda retVal = (s, n) -> {
    NonTerminal e = (NonTerminal) n.get(0);
    s = e.toIR3(s);
    s.str.append(String.format("  Return %s;\n", s.getSavedT()));
    return s;
  };

  public static IR3Lambda retVoid = (s, n) -> {
    s.str.append("  Return;\n");
    return s;
  };

  public static IR3Lambda orBinOp = (s, n) -> {
    String t = s.getT(PrimTypes.BOOL.getStr());
    s =  IR3.binOp("  " + t + " = %s || %s;\n", 0, 1).render(s, n);
    s.savedT = t;
    return s;
  };

  public static IR3Lambda andBinOp = (s, n) -> {
    String t = s.getT(PrimTypes.BOOL.getStr());
    s = IR3.binOp("  " + t + " = %s && %s;\n", 0, 1).render(s, n);
    s.savedT = t;
    return s;
  };

  public static IR3Lambda atomDot = (s, n) -> {
    Boolean stopRender = s.stopIdentifierRender;
    s.stopIdentifierRender = false;
    NonTerminal e = (NonTerminal) n.get(0);
    String id = n.get(1).toRender();
    s = e.toIR3(s);
    if (stopRender) return s;
    String at = s.getSavedT();
    String et = s.getT(n.tpe);
    s.str.append(String.format("  %s = %s.%s;\n", et, at, id));
    s.savedT = et;
    return s;
  };

  public static IR3Lambda atomFuncCall = (s, n) -> {
    return genFuncCall(true).render(s, n);
  };


  public static IR3Lambda vacAtomFuncCall = (s, n) -> {
    return genFuncCall(true, true).render(s, n);
  };

  // concrete lambdas ---------------------------------------------------------

  public static IR3Lambda genFuncCall(boolean pre){
    return genFuncCall(pre, false);
  }

  public static IR3Lambda genFuncCall(boolean pre, boolean vac) {
    return (s,n) -> {
      NonTerminal a = (NonTerminal) n.get(0);
      if (a.getVariant() == 4) {
        NonTerminal aux = new NonTerminal(a.getSym(), IR3.constT("this"));
        aux.tpe = s.thisType;
        s = aux.toIR3(s);
      } else {
        s.stopIdentifierRender = true;
        s = a.toIR3(s);
      }
      String tthis = s.getSavedT();
      if(!vac) {
        NonTerminal els = (NonTerminal) n.get(1);
        s = els.toIR3(s);
      }
      String preS = "";
      if(pre) {
        String rt = s.getT(n.mds.getReturn());
        preS = String.format("%s = ", rt);
        s.savedT = rt;
      }
      s.str.append(String.format("  %s%s(%s%s);\n", preS, n.mds.getIR3Name(), tthis, vac ? "" : (", " + s.ir3Parts.pop())));
      return s;
    };
  }

  public static IR3Lambda unaryOp(String op) {
    return (s, n) -> {
      NonTerminal e = (NonTerminal) n.get(0);
      s = e.toIR3(s);
      String et = s.getSavedT();
      String rt = s.getT(n.tpe);
      s.str.append(String.format("  %s = %s%s;\n", rt, op, et));
      s.savedT = rt;
      return s;
    };
  }

  public static IR3Lambda constT(String str) {
    return (s, n) -> {
      System.out.println(n.toRender() + " : " + n.tpe);
      String rt = s.getT(n.tpe);
      s.str.append(String.format("  %s = %s;\n", rt, str));
      s.savedT = rt;
      return s;
    };
  }

  public static boolean genNonLocal(String str, IR3State s, NonTerminal n) throws Exception {
    boolean local = false;
      for(String var : s.localVars) {
        if (var.equals(str)) {
          local = true;
          break;
        }
      }
      if (!local) {
        NonTerminal thisNode = new NonTerminal(0, IR3.constT("this"), 3);
        thisNode.tpe = s.thisType;
        n.tpe = s.localMap.get(n.get(0).toRender());
        NonTerminal rep = new NonTerminal(0, IR3.atomDot, 0, thisNode, n);
        rep.tpe = n.tpe;
        s = rep.toIR3(s);
        return true;
      }
      return false;
  }

  public static IR3Lambda ident(String str) {
    return (s,n) -> {
      if (genNonLocal(str, s, n)) {
        return s;
      }
      return constT(str).render(s,n);
    };
  }

  public static IR3Lambda relBinOp = (s, n) -> {
    String rt = s.getT(PrimTypes.BOOL.getStr());
    s = IR3.binOp("  " + rt + " = %s " + n.get(1).toRender() + " %s;\n", 0, 2).render(s, n);
    s.savedT = rt;
    return s;
  };

  public static IR3Lambda arithBinOp(String op) {
    return (s, n) -> {
      String rt = s.getT(n.tpe);
      s = IR3.binOp("  " + rt + " = %s " + op + " %s;\n", 0, 1).render(s, n);
      s.savedT = rt;
      return s;
    };
  }

  public static IR3Lambda binOp(String fmt, int i, int j) {
    return (s, n) -> {
      NonTerminal e1 = (NonTerminal) n.get(i);
      NonTerminal e2 = (NonTerminal) n.get(j);
      s = e1.toIR3(s);
      String t1 = s.getSavedT();
      s = e2.toIR3(s);
      String t2 = s.getSavedT();
      s.str.append(String.format(fmt, t1, t2));
      return s;
    };
  }

  public static IR3Lambda whileStmtGen(NonTerminal st) {
    return (s, n) -> {
      NonTerminal e = (NonTerminal) n.get(0);
      
      int l1 = s.getLabel();
      s.str.append(s.formLabel(l1));
      s = e.toIR3(s);
      int l2 = s.getLabel();
      s.str.append(ir3if(s, s.getSavedT(), l2));
      int l3 = s.getLabel();
      s.str.append(s.formGoto(l3));
      s.str.append(s.formLabel(l2));
      if (st != null) s = st.toIR3(s);
      s.str.append(s.formGoto(l1));
      s.str.append(s.formLabel(l3));
      return s;
    };
  }
  public static IR3Lambda ir3all = (s, vds) -> {
    for(int j = 0; j < vds.length(); j++) {
      s = ((NonTerminal) vds.get(j)).toIR3(s);
    }
    return s;
  };

  public static IR3State cmtd(IR3State s, String retType, String f, String className, NonTerminal fmlList, NonTerminal mdBody) throws Exception {
    return cmtd(s, retType, f, className, fmlList, mdBody, true);
  }

  public static IR3State cmtd(IR3State s, String retType, String f, String className, NonTerminal fmlList, NonTerminal mdBody, Boolean useIR3Name) throws Exception {
    s.str.append(String.format("%s %s(%s this", retType, useIR3Name ? mdBody.mds.getIR3Name() : f, className));
    if(fmlList != null) {
      for (int i = 0; i < fmlList.length(); i++) {
        NonTerminal fm = (NonTerminal) fmlList.get(i);
        String arg = fm.get(1).toRender();
        s.localVars.add(arg);
        s.str.append(String.format(",%s %s", fm.get(0).toRender(), arg));
      }
    }
    s.str.append("){\n");
    s.flush();
    s = mdBody.toIR3(s);
    s.flush();
    s.str.append("}\n\n");
    return s;
  }

  public static IR3Lambda id = (s, n) -> {
    return s;
  };

  public static IR3Lambda renderFirst = (s, n) -> {
    return ((NonTerminal) n.get(0)).toIR3(s);
  };

  public static IR3Lambda probe = (s, n) -> {
    s.str.append("OIIIII\n");
    return s;
  };

  // higher order lambdas -----------------------------------------------------

  public static String ir3if(IR3State s, String e, int l) {
    return String.format("  If(%s) %s\n", e, s.formGoto(l, false));
  }
}
