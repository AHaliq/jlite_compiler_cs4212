package ast.concrete.types;

import java.util.HashMap;
import java.util.stream.Stream;

public class LocalEnv {
  private HashMap<String, String> fd = new HashMap<>();
  private HashMap<String, MethodSignature> sig = new HashMap<>();

  public LocalEnv() {}

  @SuppressWarnings("unchecked")
  public LocalEnv(HashMap<String, String> fd, HashMap<String, MethodSignature> sig) {
    this.fd = (HashMap<String,String>) fd.clone();
    this.sig = (HashMap<String, MethodSignature>) sig.clone();
  }

  public void put(String id, String val) { 
    fd.put(id, val);
  }

  public void put(String id, MethodSignature val) {
    sig.put(id,val);
  }

  public String getFd(String id) {
    return fd.get(id);
  }

  public MethodSignature getSg(String id) {
    return sig.get(id);
  }

  public LocalEnv clone() {
    return new LocalEnv(fd, sig);
  }

  public Stream<String> illegalTypes(HashMap<String,LocalEnv> cd) {
    Stream<String> fdTypesNotInT = fd.values().stream().filter(t -> !TypeCheck.isT(cd, t));
    Stream<String> sigTypesNotInT = sig.values().stream().filter(mds -> !mds.inT(cd)).map(mds -> mds.toString());
    return Stream.concat(fdTypesNotInT, sigTypesNotInT);
  }

  @Override
  public String toString() {
    return fd.toString() + sig.toString();
  }
}