class Main {
  Void main() {
    Xmas x;
    x = new Xmas();
    x.w = 10;
    x.wish();
  }
}
class Xmas {
  Int w;
  Void wish() {
    aux(1);
  }
  Void aux(Int x) {
    Int off;
    Int on;
    Int i;
    if(x < w) {
      off = w - x;
      on = (x - 1) * 2 + 1;
      i = 0;
      while(i < off) {
        i = i + 1;
        println(" ");
      }
      i = 0;
      while(i < on) {
        i = i + 1;
        println("*");
      }
      println("\n");
      aux(x + 1);
    } else {
      println("\n");
    }
  }
}