package ast.concrete.types;

import java.util.HashMap;

import ast.NonTerminal;

public class InitTypeCheckObjects {
  public static HashMap<String,LocalEnv> initialize(NonTerminal p) throws Exception {
    HashMap<String,LocalEnv> map = new HashMap<>();

    NonTerminal mainNode = (NonTerminal) p.get(0);
    NonTerminal fml = (NonTerminal) mainNode.get(1);
    String[] params;
    if (fml.getSym() == 0) {
      params = new String[fml.length() + 1];
      for(int i = 0; i < fml.length(); i++) {
        params[i + 1] = ((NonTerminal) fml.get(i)).get(0).toRender();
      }
    } else {
      params = new String[1];
    }
    params[0] = PrimTypes.VOID.getStr();
    
    LocalEnv mainLocalEnv = new LocalEnv();
    mainLocalEnv.add("main", new MethodSignature(params));
    map.put(mainNode.getName(), mainLocalEnv);
    // populate for main class
    
    ((NonTerminal) p.get(1)).forEach((c) -> {
      NonTerminal cnt = (NonTerminal) c;
      map.put(cnt.getName(), localEnvOfClass(cnt));
    });
    // populate for user classes
    return map;
  }

  public static LocalEnv localEnvOfClass(NonTerminal c) throws Exception {
    LocalEnv e = new LocalEnv();
    addFdDeclStarToLocalEnv((NonTerminal) c.get(1), e);
    addMdDeclStarToLocalEnv((NonTerminal) c.get(2), e);
    return e;
  }

  public static void addFdDeclStarToLocalEnv(NonTerminal fds, LocalEnv e) throws Exception {
    fds.forEach((n) -> addFdToLocalEnv((NonTerminal) n, e));
  }

  public static void addMdDeclStarToLocalEnv(NonTerminal mds, LocalEnv e) throws Exception {
    mds.forEach((n) -> addMdToLocalEnv((NonTerminal) n, e));
  }

  public static void addFdToLocalEnv(NonTerminal fd, LocalEnv e) throws Exception {
    e.add(fd.getName(), fd.get(0).toRender());
  }

  public static void addMdToLocalEnv(NonTerminal md, LocalEnv e) throws Exception {
    String[] params;
    if (md.getVariant() == 0) {
      NonTerminal fml = (NonTerminal) md.get(2);
      params = new String[fml.length() + 1];
      for(int i = 0; i < fml.length(); i++) {
        params[i + 1] = ((NonTerminal) fml.get(i)).get(0).toRender();
      }
    } else {
      params = new String[1];
    }
    params[0] = md.get(0).toRender();
    e.add(md.getName(), new MethodSignature(params));
  }
}
