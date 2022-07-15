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
  
  int parens = 0;
%} 

LineTerminator = \r|\n|\r\n
ws     = {LineTerminator} | [ \t\f]

Identifier =  [_a-zA-Z~][a-zA-Z0-9_~#\.]*
Header = [_a-zA-Z~][a-zA-Z0-9_~#\.]*
DecIntegerLiteral = 0 | [1-9][0-9]*
BvIntegerLiteral = {DecIntegerLiteral} "bv" {DecIntegerLiteral}
Digit = [0-9]
IPInt = 0|1{Digit}?{Digit}?|2[0-4]?{Digit}?|25[0-5]?|[3-9]{Digit}?
IP = ({IPInt}\.){3}({IPInt})
MaskInt = 0|[1-2]{Digit}?|3[0-2]|[4-9]
IPMask = {IP} "/" {MaskInt}

%%

/**
 * LEXICAL RULES:
 */

<YYINITIAL>{ 
	"("					{echoToken(yytext()); return symbol(P4LTLSymbols.LPAR); }
	")"					{echoToken(yytext()); return symbol(P4LTLSymbols.RPAR); }
	
 	"match"				{echoToken(yytext()); yybegin(PREDICATE); return symbol(P4LTLSymbols.MATCH); }
 	"modify"			{echoToken(yytext()); yybegin(PREDICATE); return symbol(P4LTLSymbols.MODIFY); }
 	"fwd"				{echoToken(yytext()); yybegin(PREDICATE); return symbol(P4LTLSymbols.FWD); }
 	"drop"				{echoToken(yytext()); return symbol(P4LTLSymbols.DROP); }
 	"valid_after"		{echoToken(yytext()); yybegin(PREDICATE); return symbol(P4LTLSymbols.VALID_AFTER); }
 	"valid_before"		{echoToken(yytext()); yybegin(PREDICATE); return symbol(P4LTLSymbols.VALID_BEFORE); }
	
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
  	"("						{echoToken(yytext()); parens++; return symbol(P4LTLSymbols.LPAR); }
  	"="						{echoToken(yytext()); return symbol(P4LTLSymbols.EQ); }
  	"!="					{echoToken(yytext()); return symbol(P4LTLSymbols.NEQ); }
  	")"						{echoToken(yytext()); parens--; if(parens == 0) { yybegin(YYINITIAL); } return symbol(P4LTLSymbols.RPAR); }
  	","						{echoToken(yytext()); return symbol(P4LTLSymbols.COMMA); }
  	"old"					{echoToken(yytext()); return symbol(P4LTLSymbols.OLD); }
	"+"						{echoToken(yytext()); return symbol(P4LTLSymbols.PLUS); }
	"-"						{echoToken(yytext()); return symbol(P4LTLSymbols.MINUS); }
 	{ws}    				{/* ignore */ }
 	{Header}	    		{echoToken(yytext()); return symbol(P4LTLSymbols.NAME, yytext()); }
 	{BvIntegerLiteral}		{echoToken(yytext()); return symbol(P4LTLSymbols.BVINT, yytext()); }
 	{IPMask}				{echoToken(yytext()); return symbol(P4LTLSymbols.IPMASK, yytext()); }
 	{DecIntegerLiteral}		{echoToken(yytext()); return symbol(P4LTLSymbols.INT, new BigInteger(yytext())); }
 }

 
<<EOF>>                 {echoToken(yytext()); return symbol(P4LTLSymbols.EOF); }