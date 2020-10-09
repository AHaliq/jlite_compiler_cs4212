package ast.concrete.types;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ast.NonTerminal;

public class InitTypeCheckObjects {
  public static HashMap<String,LocalEnv> initialize(NonTerminal p) throws Exception {
    HashMap<String,LocalEnv> map = new HashMap<>();

    populateMainClass((NonTerminal) p.get(0), map);
    // populate for main class
    
    ((NonTerminal) p.get(1)).forEach((c) -> {
      NonTerminal cnt = (NonTerminal) c;
      map.put(cnt.getName(), localEnvOfClass(cnt));
    });
    // populate for user classes

    String illegals = map.values().stream().reduce(
      Arrays.stream(new String[]{}),
      (a,le) -> Stream.concat(a, le.illegalTypes(map)),
      (a,b) -> Stream.concat(a,b)
    ).distinct().collect(Collectors.joining(", ", "[", "]"));
    if(!illegals.equals("")) {
      throw new Exception("theres field declaration with types or method signature type that have no class definition:\n  " + illegals);
    }
    // validate class declaration
    return map;
  }

  public static void populateMainClass(NonTerminal mainNode, HashMap<String,LocalEnv> map) throws Exception {
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
    mainLocalEnv.put("main", new MethodSignature(params));
    map.put(mainNode.getName(), mainLocalEnv);
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
    e.put(fd.getName(), fd.get(0).toRender());
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
    e.put(md.getName(), new MethodSignature(params));
  }
}
