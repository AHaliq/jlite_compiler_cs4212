class Main {
  Void main() {
    F f;
    String x;
    f = new F();
    x = f.f();
    println(x);
    println("wut\n");
  }
}
class F {
  String f() {
    return "it works!\n";
  }
}