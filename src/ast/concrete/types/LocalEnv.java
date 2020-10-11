package ast.concrete.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class LocalEnv {
  private HashMap<String, String> fd = new HashMap<>();
  private HashMap<String, MethodSignature> sig = new HashMap<>();
  private HashMap<String, String> mdIR3Name = new HashMap<>();
  private int mdId = 0;
  private String cname;

  public LocalEnv() { this(""); }
  public LocalEnv(String cname) { this.cname = cname; }

  @SuppressWarnings("unchecked")
  public LocalEnv(HashMap<String, String> fd, HashMap<String, MethodSignature> sig, HashMap<String, String> mdIR3Name, String cname, int mdId) {
    this.mdId = mdId;
    this.cname = cname;
    this.fd = (HashMap<String,String>) fd.clone();
    this.sig = (HashMap<String, MethodSignature>) sig.clone();
    this.mdIR3Name = (HashMap<String, String>) mdIR3Name.clone();
  }

  public void put(String id, String val) { 
    fd.put(id, val);
  }

  public void put(String id, MethodSignature val) {
    sig.put(id,val);
    mdIR3Name.put(id,String.format("%%%s_%d", cname, mdId++));
  }

  public void put(MethodSignature mds) {
    put(PrimTypes.RET.getStr(), mds.getReturn());
    for(var i = 0; i < mds.length(); i++) {
      put(mds.getId(i), mds.getType(i));
    }
  }

  public void put(String cname, LocalEnv e) {
    put(PrimTypes.THIS.getStr(), cname);
    put(e);
  }

  public void put(LocalEnv e) {
    for(Map.Entry<String, String> m : e.getAllFd()) {
      put(m.getKey(), m.getValue());
    }
    for(Map.Entry<String, MethodSignature> m : e.getAllSg()) {
      put(m.getKey(), m.getValue());
    }
  }

  public String getFd(String id) {
    return fd.get(id);
  }

  public Set<Map.Entry<String, String>> getAllFd() {
    return fd.entrySet();
  }

  public MethodSignature getSg(String id) {
    return sig.get(id);
  }

  public Set<Map.Entry<String, MethodSignature>> getAllSg() {
    return sig.entrySet();
  }

  public String getIR3(String id) {
    return mdIR3Name.get(id);
  }

  public LocalEnv clone() {
    return new LocalEnv(fd, sig, mdIR3Name, cname, mdId);
  }

  public LocalEnv clone(String name) {
    return new LocalEnv(fd, sig, mdIR3Name, name, mdId);
  }

  public Stream<String> illegalTypes(HashMap<String,LocalEnv> cd) {
    Stream<String> fdTypesNotInT = fd.values().stream().filter(t -> !TypeCheck.isT(cd, t));
    Stream<String> sigTypesNotInT = sig.values().stream().filter(mds -> !mds.inT(cd)).map(mds -> mds.toTypeSignature());
    return Stream.concat(fdTypesNotInT, sigTypesNotInT);
  }

  @Override
  public String toString() {
    return fd.toString() + sig.toString();
  }
}