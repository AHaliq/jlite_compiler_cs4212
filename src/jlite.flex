package javasrc.jflex;

import java_cup.runtime.*;
import javasrc.cup.sym;
import java_cup.runtime.ComplexSymbolFactory.Location;
import util.Constants;

%%
%public
%class Lexer
%cup
%char
%line
%column

%{
  StringBuffer strBuf = new StringBuffer();
  
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
  return symbolFactory.newSymbol("EOF",
    sym.EOF,
    new Location(yyline+1,yycolumn+1,(int)yychar),
    new Location(yyline+1,yycolumn+1,(int)(yychar+1)));
%eofval}

new_line        = \r|\n|\r\n;
white_space     = {new_line} | [ \t\f]

BoolLiteral = true | false

Identifier      = [a-z][a-zA-Z0-9_]*
ClassName       = [A-Z][a-zA-Z0-9_]*
IntegerLiteral  = [0-9]+

%state STRING

%%

<YYINITIAL>{
/*keywords -------------------------------------- */
"if"    { return symbol("if", sym.IF); }
"then"  { return symbol("then", sym.THEN); }
"else"  { return symbol("else", sym.ELSE); }

/* literals ------------------------------------- */
{BoolLiteral}                   { return symbol("BoolLiteral",
                                    sym.BOOL_LITERAL,
                                    new Boolean(Boolean.parseBoolean(yytext()))); }

{IntegerLiteral}                { return symbol("IntegerLiteral",
                                    sym.INTEGER_LITERAL,
                                    new Integer(yytext())); }

"null"                          { return symbol("StringLiteral",
                                    sym.STRING_LITERAL,
                                    ""); }

/* separators ----------------------------------- */
 \"                             { strBuf.setLength(0); yybegin(STRING); }
"&&"                            { return symbol("and", sym.BOOL_OP, new Integer(Constants.AND)); }
"||"                            { return symbol("or", sym.BOOL_OP, new Integer(Constants.OR)); }
"=="                            { return symbol("eq", sym.BOOL_OP, new Integer(Constants.EQ)); }
"!="                            { return symbol("neq", sym.BOOL_OP, new Integer(Constants.NEQ)); }
">="                            { return symbol("gte", sym.BOOL_OP, new Integer(Constants.GTE)); }
">"                             { return symbol("gt", sym.BOOL_OP, new Integer(Constants.GT)); }
"<="                            { return symbol("lte", sym.BOOL_OP, new Integer(Constants.LTE)); }
"<"                             { return symbol("lt", sym.BOOL_OP, new Integer(Constants.LT)); }


{white_space}                   { /* ignore */ }
}

<STRING> {
\"                              { yybegin(YYINITIAL); return symbol("StringLiteral",
                                  sym.STRING_LITERAL,
                                  strBuf.toString(),
                                  strBuf.length()); }

[^\n\r\"\\]+                    { strBuf.append( yytext() ); }
\\t                             { strBuf.append('\t'); }
\\n                             { strBuf.append('\n'); }

\\r                             { strBuf.append('\r'); }
\\\"                            { strBuf.append('\"'); }
\\                              { strBuf.append('\\'); }
}

/* error fallback */
.|\n {
    error("Illegal character <"+ yytext()+">");
}