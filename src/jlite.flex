package javasrc.jflex;

import java_cup.runtime.*;
import javasrc.cup.sym;
import java_cup.runtime.ComplexSymbolFactory.Location;

%%
%public
%class Lexer
%cup
%char
%line
%column

%{
  StringBuffer string = new StringBuffer();
  
  public Lexer(java.io.Reader in, ComplexSymbolFactory sf){
	  this(in);
	  symbolFactory = sf;
  }
  
  ComplexSymbolFactory symbolFactory;

  private Symbol symbol(String name, int sym) {
    return symbolFactory.newSymbol(name, sym, new Location(yyline+1,yycolumn+1,(int)yychar), new Location(yyline+1,yycolumn+yylength(),(int)(yychar+yylength())));
  }

  private Symbol symbol(String name, int sym, Object val) {
    Location left = new Location(yyline+1,yycolumn+1,(int)yychar);
    Location right= new Location(yyline+1,yycolumn+yylength(), (int)(yychar+yylength()));
    return symbolFactory.newSymbol(name, sym, left, right,val);
  }
  private Symbol symbol(String name, int sym, Object val,int buflength) {
    Location left = new Location(yyline+1,yycolumn+yylength()-buflength,(int)(yychar+yylength()-buflength));
    Location right= new Location(yyline+1,yycolumn+yylength(), (int)(yychar+yylength()));
    return symbolFactory.newSymbol(name, sym, left, right,val);
  }

  private void error(String message) {
    System.out.println("Error at line "+(yyline+1)+", column "+(yycolumn+1)+" : "+message);
  }
%}

%eofval{
     return symbolFactory.newSymbol("EOF", sym.EOF, new Location(yyline+1,yycolumn+1,(int)yychar), new Location(yyline+1,yycolumn+1,(int)(yychar+1)));
%eofval}

BoolLiteral = true | false
new_line = \r|\n|\r\n;
white_space = {new_line} | [ \t\f]

%%

<YYINITIAL>{
  /*keywords*/
  "if"    { return symbol("if", sym.IF); }
  "then"  { return symbol("then", sym.THEN); }
  "else"  { return symbol("else", sym.ELSE); }

  {BoolLiteral}   { return symbol("Boolconst", sym.BOOLCONST, new Boolean(Boolean.parseBoolean(yytext()))); }

  {white_space}   { /* ignore */ }
}

/* error fallback */
.|\n {
    error("Illegal character <"+ yytext()+">");
}