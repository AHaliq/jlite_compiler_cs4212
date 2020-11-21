class Main {
  Void main() {
    C c;
    c = new C();
    c.x = "it works\n";
    c.f();
  }
}
class C {
  String x;
  Void f() {
    println(x);
  }
}