class MainC {
Void main (){
  C c;
  c = new C();
  c.f("it works\n");
}
}
class C {
  Void f(String s) {
    println(s);
  }
}