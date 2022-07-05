package edu.tsinghua.ss.thufv.specLang.parse;

import com.github.jhoenicke.javacup.runtime.*;
import java.math.BigInteger;
import java.util.*;

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
  private void echoToken(String text)
  {
  	// System.out.println("Token: " + text);
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
	"("					{echoToken(yytext()); return symbol(P4LTLSymbols.LPAR); }
	")"					{echoToken(yytext()); return symbol(P4LTLSymbols.RPAR); }
	
 	"match"				{echoToken(yytext()); yybegin(PREDICATE); return symbol(P4LTLSymbols.MATCH); }
 	"drop"				{echoToken(yytext()); return symbol(P4LTLSymbols.DROP); }
	
	"[]"				{echoToken(yytext()); return symbol(P4LTLSymbols.ALWAYS); }
	"<>"				{echoToken(yytext()); return symbol(P4LTLSymbols.EVENTUALLY); }
	"U"					{echoToken(yytext()); return symbol(P4LTLSymbols.UNTIL); }
	"R"					{echoToken(yytext()); return symbol(P4LTLSymbols.RELEASE); }
	"&&"				{echoToken(yytext()); return symbol(P4LTLSymbols.AND); }
	"||"				{echoToken(yytext()); return symbol(P4LTLSymbols.OR); }
	"=>"				{echoToken(yytext()); return symbol(P4LTLSymbols.IMPLIES); }
	"X"					{echoToken(yytext()); return symbol(P4LTLSymbols.NEXT); }
	"!"					{echoToken(yytext()); return symbol(P4LTLSymbols.NEG); }

	{ws}    			{/* ignore */ }
	
	{Identifier}    	{echoToken(yytext()); return symbol(P4LTLSymbols.NAME, yytext()); }
 }
 <PREDICATE>{
  	"("						{echoToken(yytext()); return symbol(P4LTLSymbols.LPAR); }
  	"="						{echoToken(yytext()); return symbol(P4LTLSymbols.EQ); }
  	"!="					{echoToken(yytext()); return symbol(P4LTLSymbols.NEQ); }
  	")"						{echoToken(yytext()); yybegin(YYINITIAL); return symbol(P4LTLSymbols.RPAR); }
  	","						{echoToken(yytext()); return symbol(P4LTLSymbols.COMMA); }
 	{ws}    				{echoToken(yytext()); /* ignore */ }
 	{Header}	    		{echoToken(yytext()); return symbol(P4LTLSymbols.NAME, yytext()); }
 	{BvIntegerLiteral}		{echoToken(yytext()); return symbol(P4LTLSymbols.BVINT, yytext()); }
 	{DecIntegerLiteral}		{echoToken(yytext()); return symbol(P4LTLSymbols.INT, new BigInteger(yytext())); }
 }

 
<<EOF>>                 {echoToken(yytext()); return symbol(P4LTLSymbols.EOF); }