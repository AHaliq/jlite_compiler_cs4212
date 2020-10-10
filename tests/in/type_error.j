class Main {
  Void main() {
    return new F().f(new F());
  }
}
class F {
  F f(G g) {
    return 0;
  }
}
class G {

}