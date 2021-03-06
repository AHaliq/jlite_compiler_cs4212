import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;

import ast.NonTerminal;
import ast.concrete.arm.Backend;
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
      String outPath = args.length > 3 && !args[3].equalsIgnoreCase("null") ? args[3] : null;
      // resolve execution variables
      
      pt = is_debug ? p.debug_parse() : p.parse();
      NonTerminal ptn = (NonTerminal) pt.value;
      // build ast
      
      ptn.typeCheck();
      // run type checker
      
      if (is_compact) {
        System.out.println(ptn.toRender());
      } else {
        System.out.println(Util.pretty(ptn.toRender(), is_indent));
      }
      // render jlite code

      String ir3 = ptn.toIR3().strfull.toString();
      System.out.println(ir3);
      // render ir3 code

      String arm = Backend.armOfIR3(ir3);
      System.out.println(arm);
      // render arm code


      if (outPath != null) {
        File fo = new File(outPath);
        FileWriter w = new FileWriter(fo);
        w.write(arm);
        w.close();
      }
      // output arm s file
    } catch (Exception e) {
      System.out.println("EXCP: " + e.getMessage());
    } finally {
      r.close();
    }
  }
}