import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;

import ast.Node;
import ast.NonTerminal;
import ast.concrete.Render;
import ast.concrete.types.InitTypeCheckObjects;
import ast.concrete.types.LocalEnv;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import javasrc.jflex.Lexer;
import util.Util;
import javasrc.cup.Parser;

public class App {
  public static void main(String[] args) throws Exception {
    File f = new File(args[0]);
    Reader r = new FileReader(f);
    ComplexSymbolFactory sf = new ComplexSymbolFactory();
    Parser p = new Parser(new Lexer(r, sf), sf);
    Symbol pt = null;
    try {
      Boolean is_debug = args.length > 1 && args[1].equalsIgnoreCase("true");
      Boolean is_indent = args.length > 2 && args[2].equalsIgnoreCase("2");
      Boolean is_compact = args.length > 2 && args[2].equalsIgnoreCase("1");
      pt = is_debug ? p.debug_parse() : p.parse();

      NonTerminal ptn = (NonTerminal) pt.value;

      HashMap<String,LocalEnv> cd = InitTypeCheckObjects.initialize(ptn);
      LocalEnv le = new LocalEnv();
      System.out.println(cd.toString());

      // run type checker

      if (is_compact) {
        System.out.println(ptn.toRender());
      } else {
        System.out.println(Util.pretty(ptn.toRender(), is_indent));
      }
      // render code
    } catch (Exception e) {
      System.out.println("EXCP: " + e.getMessage());
    } finally {
      r.close();
    }
  }
}