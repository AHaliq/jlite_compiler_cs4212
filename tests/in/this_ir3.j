class Main {
  Void main() {
    F x;
    Int v;
    v = x.g();
    return;
  }
}
class F {
  Int val;

  Int f(String x, Int z) { return this.val; }
  Int g() { return 1; }
}