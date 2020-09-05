import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import ast.Node;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import javasrc.jflex.Lexer;
import javasrc.cup.parser;

public class App {
  public static void main(String[] args) throws Exception {
    File f = new File(args[0]);
    Reader r = new FileReader(f);
    ComplexSymbolFactory sf = new ComplexSymbolFactory();
    parser p = new parser(new Lexer(r, sf), sf);
    Symbol pt = null;
    try {
      Boolean is_debug = args.length > 1;
      pt = is_debug ? p.debug_parse() : p.parse();

      Node ptn = (Node) pt.value;
      System.out.println(ptn.toTree());
    } catch (Exception e) {
      System.out.println("EXCP: " + e);
    } finally {
      r.close();
    }
  }
}