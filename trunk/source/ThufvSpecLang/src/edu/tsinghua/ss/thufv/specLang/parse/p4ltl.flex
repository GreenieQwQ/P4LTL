package edu.tsinghua.ss.thufv.specLang.parse;

import com.github.jhoenicke.javacup.runtime.*;
import java.math.BigInteger;

%%

%class P4LTLLexer
%unicode
%implements com.github.jhoenicke.javacup.runtime.Scanner
%type com.github.jhoenicke.javacup.runtime.Symbol
%function next_token
%line
%column
%public

%state PREDICATE

%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
    private Symbol symbol(int type, String value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
  
      private Symbol symbol(int type, int value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%} 

LineTerminator = \r|\n|\r\n
ws     = {LineTerminator} | [ \t\f]

Identifier =  [_a-zA-Z~][a-zA-Z0-9_~#\.]*
Header = [_a-zA-Z~][a-zA-Z0-9_~#\.]*
DecIntegerLiteral = 0 | [1-9][0-9]*
BvIntegerLiteral = {DecIntegerLiteral} "bv" {DecIntegerLiteral}
 
%%

/**
 * LEXICAL RULES:
 */

<YYINITIAL>{ 
	"("					{ return symbol(P4LTLSymbols.LPAR); }
	")"					{ return symbol(P4LTLSymbols.RPAR); }
	
 	"match"				{ yybegin(PREDICATE); return symbol(P4LTLSymbols.MATCH); }
 	"drop"				{ return symbol(P4LTLSymbols.DROP); }
	
	"[]"				{ return symbol(P4LTLSymbols.ALWAYS); }
	"<>"				{ return symbol(P4LTLSymbols.EVENTUALLY); }
	"U"					{ return symbol(P4LTLSymbols.UNTIL); }
	"R"					{ return symbol(P4LTLSymbols.RELEASE); }
	"&&"				{ return symbol(P4LTLSymbols.AND); }
	"||"				{ return symbol(P4LTLSymbols.OR); }
	"==>"				{ return symbol(P4LTLSymbols.IMPLIES); }
	"X"					{ return symbol(P4LTLSymbols.NEXT); }
	"!"					{ return symbol(P4LTLSymbols.NEG); }

	{ws}    			{ /* ignore */ }
	
	{Identifier}    	{ return symbol(P4LTLSymbols.NAME, yytext()); }
 }
 <PREDICATE>{
  	"("						{ return symbol(P4LTLSymbols.LPAR); }
  	"="						{ return symbol(P4LTLSymbols.EQ); }
  	"!="					{ return symbol(P4LTLSymbols.NEQ); }
  	")"						{ yybegin(YYINITIAL); return symbol(P4LTLSymbols.RPAR); }
  	","						{ return symbol(P4LTLSymbols.COMMA); }
 	{ws}    				{ /* ignore */ }
 	{Header}	    		{ return symbol(P4LTLSymbols.NAME, yytext()); }
 	{BvIntegerLiteral}		{ return symbol(P4LTLSymbols.BVINT, yytext()); }
 	{DecIntegerLiteral}		{ return symbol(P4LTLSymbols.INT, new BigInteger(yytext())); }
 }

 
<<EOF>>                 { return symbol(P4LTLSymbols.EOF); }
 