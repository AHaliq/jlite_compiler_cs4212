class MainC {
Void main (){
String s;
F f;
f.g = new G();
f.g.h = new H();
f.g.h.y = f.g.h.x();
f.g.h.greet();
println(f.g.h.y);
}
}
class F {
G g;
}
class G {
  H h;
}
class H {
  String y;
  String x() {
    return "it works\n";
  }
  Void greet() {
    println("hello\n");
    return;
  }
}