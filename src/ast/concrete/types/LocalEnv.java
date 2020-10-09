package ast.concrete.types;

import java.util.HashMap;

public class LocalEnv {
  private Env<String> fd = new Env<>();
  private Env<MethodSignature> sig = new Env<>();

  public void add(String id, String val) { 
    fd.add(id, val);
  }

  public void add(String id, MethodSignature val) {
    sig.add(id,val);
  }

  public String getFd(String id) {
    return fd.get(id);
  }

  public MethodSignature getSg(String id) {
    return sig.get(id);
  }

  @Override
  public String toString() {
    return fd.toString() + sig.toString();
  }
}

class Env<T>{

  private HashMap<String, T> map;

  public Env() {
    map = new HashMap<String, T>();
  }

  public void add(String id, T val) {
    map.put(id, val);
  }
  
  public T get(String id) {
    return map.get(id);
  }

  @Override
  public String toString() {
    return map.toString();
  }
}
