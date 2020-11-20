class MainC {
Void main (){
String s;
F f;
f.g = new G();
f.g.h = new H();
f.g.h.y = f.g.h.x();
f.g.h.greet();
}
}
class F {
G g;
}
class G {
  H h;
}
class H {
  Int y;
  Int x() {
    return 2;
  }
  Void greet() {
    println("hello");
    return;
  }
}