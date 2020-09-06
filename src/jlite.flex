package javasrc.jflex;

import java_cup.runtime.*;
import javasrc.cup.Sym;
import java_cup.runtime.ComplexSymbolFactory.Location;

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

  private Symbol symbol(int sym) {
    return symbolFactory.newSymbol(Sym.terminalNames[sym], sym, new Location(yyline+1,yycolumn+1,(int)yychar), new Location(yyline+1,yycolumn+yylength(),(int)(yychar+yylength())));
  }

  private Symbol symbol(int sym, Object val) {
    Location left = new Location(yyline+1,yycolumn+1,(int)yychar);
    Location right= new Location(yyline+1,yycolumn+yylength(), (int)(yychar+yylength()));
    return symbolFactory.newSymbol(Sym.terminalNames[sym], sym, left, right,val);
  }
  private Symbol symbol(int sym, Object val,int buflength) {
    Location left = new Location(yyline+1,yycolumn+yylength()-buflength,(int)(yychar+yylength()-buflength));
    Location right= new Location(yyline+1,yycolumn+yylength(), (int)(yychar+yylength()));
    return symbolFactory.newSymbol(Sym.terminalNames[sym], sym, left, right,val);
  }

  private void error(String message) {
    System.out.println("Error at line "+(yyline+1)+", column "+(yycolumn+1)+" : "+message);
  }
%}

%eofval{
  return symbolFactory.newSymbol("EOF",
    Sym.EOF,
    new Location(yyline+1,yycolumn+1,(int)yychar),
    new Location(yyline+1,yycolumn+1,(int)(yychar+1)));
%eofval}

new_line        = \r|\n|\r\n;
LineTerminator  = \r|\n|\r\n
InputCharacter  = [^\r\n]
white_space     = {new_line} | [ \t\f]

Identifier      = [a-z][a-zA-Z0-9_]*
ClassName       = [A-Z][a-zA-Z0-9_]*
IntegerLiteral  = [0-9]+

Comment               = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}
TraditionalComment    = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment      = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment  = "/**" {CommentContent} "*"+ "/"
CommentContent        = ( [^*] | \*+ [^/*] )*
/* comment macros */

%state STRING

%%

<YYINITIAL>{
/* keywords ------------------------------------- */

"class"                         { return symbol(Sym.key_class); }
"main"                          { return symbol(Sym.key_main); }

"Void"                          { return symbol(Sym.key_void); }
"Int"                           { return symbol(Sym.key_int); }
"Bool"                          { return symbol(Sym.key_bool); }
"String"                        { return symbol(Sym.key_string); }

"if"                            { return symbol(Sym.key_if); }
"else"                          { return symbol(Sym.key_else); }
"while"                         { return symbol(Sym.key_while); }

"readln"                        { return symbol(Sym.key_readln); }
"println"                       { return symbol(Sym.key_println); }

"this"                          { return symbol(Sym.key_this); }
"new"                           { return symbol(Sym.key_new); }
"return"                        { return symbol(Sym.key_return); }

/* literals ------------------------------------- */

{IntegerLiteral}                { return symbol(Sym.integer_literal, yytext()); }

"true"                          { return symbol(Sym.key_true); }
"false"                         { return symbol(Sym.key_false); }
"null"                          { return symbol(Sym.key_null); }

{Identifier}                    { return symbol(Sym.id, yytext()); }

{ClassName}                     { return symbol(Sym.cname, yytext()); }

/* separators ----------------------------------- */

 \"                             { strBuf.setLength(0); yybegin(STRING); }

"{"                             { return symbol(Sym.tok_lbrace); }
"}"                             { return symbol(Sym.tok_rbrace); }
"("                             { return symbol(Sym.tok_lparen); }
")"                             { return symbol(Sym.tok_rparen); }

";"                             { return symbol(Sym.tok_scolon); }
","                             { return symbol(Sym.tok_comma); }
"."                             { return symbol(Sym.tok_dot); }
"="                             { return symbol(Sym.tok_assign); }

"||"                            { return symbol(Sym.tok_or); }
"&&"                            { return symbol(Sym.tok_and); }
"=="                            { return symbol(Sym.tok_eq); }
"!="                            { return symbol(Sym.tok_neq); }

"<="                            { return symbol(Sym.tok_lte); }
">="                            { return symbol(Sym.tok_gte); }
"<"                             { return symbol(Sym.tok_lt); }
">"                             { return symbol(Sym.tok_gt); }

"!"                             { return symbol(Sym.tok_neg); }

"+"                             { return symbol(Sym.tok_plus); }
"-"                             { return symbol(Sym.tok_minus); }
"*"                             { return symbol(Sym.tok_times); }
"/"                             { return symbol(Sym.tok_divide); }

{Comment}                       { /* ignore */ }

{white_space}                   { /* ignore */ }
}

<STRING> {
\"                              { yybegin(YYINITIAL); return symbol(Sym.string_literal, strBuf.toString(), strBuf.length()); }

[^\n\r\"\\]+                    { strBuf.append(yytext()); }
\\t                             { strBuf.append('\t'); }
\\n                             { strBuf.append('\n'); }

\\r                             { strBuf.append('\r'); }
\\\"                            { strBuf.append('\"'); }
\\                              { strBuf.append('\\'); }
}

/* error fallback */
[^]|\n {
    error("Illegal character <"+ yytext()+">");
}