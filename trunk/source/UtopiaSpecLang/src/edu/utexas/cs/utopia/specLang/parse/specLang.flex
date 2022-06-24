package edu.utexas.cs.utopia.specLang.parse;

import com.github.jhoenicke.javacup.runtime.*;
import java.math.BigInteger;

%%

/**
 * 选项与声明:
 * class: 定义生成词法分析器Java文件的文件名，如果不定义该选项，则默认生成”Yylex.java”。
 * implements: 使得生成的词法分析器类实现特定的接口，可以同时实现多个接口。
 * public: 使得生成的类是public的，类似的还有%final和%abstract指令，他们分别生成的类是final和abstract类型的。
 * %{………用户代码…….%} 类代码指令 其中用户代码将被直接复制到生成类文件中，在这里你可以定义自己的成员变量和方法。此规范描述中出现多个类代码指令，那么JFLEX将根据这些类代码指令出现的先后顺序将他们拼接起来。
 * %type 这条指令用于设置扫描函数的返回类型，在这种设置下文件结尾缺省值是null.如果指定的类型不是java.lang.Object的子类那么应该使用%eofval指令或者《EOF》来指定其他文件结束值。
 * %function 用于设置词法扫描函数的名称，如果不设置该指令，那么默认的词法扫描函数名称为：yylex;注意该指令优先于%cup指令，因为%cup指令设置后，默认扫描函数被命名为：next token，也就是说当我们使用了%cup指令后，尽量就不要使用%function指令了。
 */

%class SpecLangLexer
%unicode
%implements com.github.jhoenicke.javacup.runtime.Scanner
%type com.github.jhoenicke.javacup.runtime.Symbol
%function next_token
%line
%column
%public

%state CONSTRAINT ARGS ATOM FUNC ARGLIST CONSTRAINT2

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
  
  int parens = 0;
%} 

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
ws     = {LineTerminator} | [ \t\f]

Identifier =  [_a-zA-Z~][a-zA-Z0-9_~#\.]*
Identifier2 = [_a-zA-Z~][a-zA-Z0-9_~#\.]*
Identifier3 = [_a-zA-Z~\*][a-zA-Z0-9_~#\.]*
Int = [0-9]+
hInt = [0][xX][0-9a-fA-F]+
 
%%

/**
 * LEXICAL RULES:
 */

<YYINITIAL>{ 
	"("					{ return symbol(SpecLangSymbols.LPAR); }
	")"					{ return symbol(SpecLangSymbols.RPAR); }
	
 	"started"			{ yybegin(ATOM); return symbol(SpecLangSymbols.STARTED); }
 	"willSucceed"		{ yybegin(ATOM); return symbol(SpecLangSymbols.WILL_SUCCEED); }
	"finished"			{ yybegin(ATOM); return symbol(SpecLangSymbols.FINISHED); }
	"reverted"	    	{ yybegin(ATOM); return symbol(SpecLangSymbols.REVERTED); }
	"sent" 			    { yybegin(CONSTRAINT2); return symbol(SpecLangSymbols.SENT); }

 	"call"				{ yybegin(ARGS); return symbol(SpecLangSymbols.CALL); }
	"success"			{ yybegin(ARGS); return symbol(SpecLangSymbols.SUCCESS); }
	"outOfGas"			{ yybegin(ARGS); return symbol(SpecLangSymbols.OUT_OF_GAS); }
	"revert"			{ yybegin(ARGS); return symbol(SpecLangSymbols.REVERT); }
	"require"			{ yybegin(ARGS); return symbol(SpecLangSymbols.REQUIRE); }
	"assert"			{ yybegin(ARGS); return symbol(SpecLangSymbols.ASSERT); }
	"fail"				{ yybegin(ARGS); return symbol(SpecLangSymbols.FAILL); }		
	"callSuccess"       { yybegin(ARGS); return symbol(SpecLangSymbols.CALL_SUCCESS); }			
	"INV"       		{ yybegin(ARGS); return symbol(SpecLangSymbols.INV); }

	"{"					{ yybegin(CONSTRAINT); return symbol(SpecLangSymbols.CONSTRAINT_BEGIN); }	
	
	"[]"				{ return symbol(SpecLangSymbols.ALWAYS); }
	"<>"				{ return symbol(SpecLangSymbols.EVENTUALLY); }
	"U"					{ return symbol(SpecLangSymbols.UNTIL); }
	"R"					{ return symbol(SpecLangSymbols.RELEASE); }
	"&&"				{ return symbol(SpecLangSymbols.AND); }
	"||"				{ return symbol(SpecLangSymbols.OR); }
	"==>"				{ return symbol(SpecLangSymbols.IMPLIES); }
	"X"					{ return symbol(SpecLangSymbols.NEXT); }
	"!"					{ return symbol(SpecLangSymbols.NEG); }
	";"					{ return symbol(SpecLangSymbols.SEQ); }

	{ws}    			{ /* ignore */ }
	
	{Identifier}    	{ return symbol(SpecLangSymbols.NAME, yytext()); }
 }
 <ATOM>{
  	"("					{ yybegin(FUNC); return symbol(SpecLangSymbols.LPAR); }
 	{ws}    			{ /* ignore */ }
 }
<FUNC>{
	"("					{ yybegin(ARGLIST); return symbol(SpecLangSymbols.LPAR); }
 	")"					{ yybegin(YYINITIAL); return symbol(SpecLangSymbols.RPAR); }
	{Identifier3}    	{ return symbol(SpecLangSymbols.NAME, yytext()); } 
	","					{ parens = 0; yybegin(CONSTRAINT2); return symbol(SpecLangSymbols.ARG_SEP); }
	{ws}    			{ /* ignore */ }
}
<ARGLIST>{
	")"					{ yybegin(FUNC); return symbol(SpecLangSymbols.RPAR); }
  	","					{ return symbol(SpecLangSymbols.ARG_SEP); }
  	{Identifier2}    	{ return symbol(SpecLangSymbols.NAME, yytext()); } 
  	{ws}    			{ /* ignore */ }
}
 <ARGS>{
 	"("					{ return symbol(SpecLangSymbols.LPAR); }
 	")"					{ return symbol(SpecLangSymbols.RPAR); }
	","					{ return symbol(SpecLangSymbols.ARG_SEP); }
	"{"					{ yybegin(CONSTRAINT); return symbol(SpecLangSymbols.CONSTRAINT_BEGIN); }
 	{ws}    			{ /* ignore */ }
	{Identifier3}    	{ return symbol(SpecLangSymbols.NAME, yytext()); } 	
 }
 <CONSTRAINT2>{
 	{hInt}				{ return symbol(SpecLangSymbols.INT, new BigInteger(yytext().substring(2), 16)); }
 	{Int}				{ return symbol(SpecLangSymbols.INT, new BigInteger(yytext())); }
 	
 	"true"				{ return symbol(SpecLangSymbols.TRUE); }
	"false"				{ return symbol(SpecLangSymbols.FALSE); }
	"old"				{ return symbol(SpecLangSymbols.OLD); }
	"fsum"				{ return symbol(SpecLangSymbols.FSUM); }
	"csum"				{ return symbol(SpecLangSymbols.SUM); }	
	"sum"				{ return symbol(SpecLangSymbols.SUM); }
 	 	
 	"["					{ return symbol(SpecLangSymbols.LBRACKET); }
 	"]"					{ return symbol(SpecLangSymbols.RBRACKET); }
 	 	
 	"("					{ parens++; return symbol(SpecLangSymbols.LPAR); }
	")"					{ if(parens > 0) { parens--; } else { yybegin(YYINITIAL); } return symbol(SpecLangSymbols.RPAR); }
	","					{ return symbol(SpecLangSymbols.ARG_SEP); }
	
	"+"					{ return symbol(SpecLangSymbols.PLUS); }
	"-"					{ return symbol(SpecLangSymbols.MINUS); }
	"*"					{ return symbol(SpecLangSymbols.MULTIPLY); }
	"/"					{ return symbol(SpecLangSymbols.DIVIDE); }
		
	"&&"				{ return symbol(SpecLangSymbols.AND); }
	"||"				{ return symbol(SpecLangSymbols.OR); }
	"!"					{ return symbol(SpecLangSymbols.NEG); }
	"==>"				{ return symbol(SpecLangSymbols.IMPLIES); }

	"=="				{ return symbol(SpecLangSymbols.EQ); }
	"!="				{ return symbol(SpecLangSymbols.NEQ); }
	">"					{ return symbol(SpecLangSymbols.GT); }
	">="				{ return symbol(SpecLangSymbols.GEQ); }
	"<"					{ return symbol(SpecLangSymbols.LT); }
	"<="				{ return symbol(SpecLangSymbols.LEQ); }
	
	{ws}				{ /* ignore */ }	
	{Identifier2}    	{ return symbol(SpecLangSymbols.NAME, yytext()); }
 } 
 <CONSTRAINT>{
 	"}"					{ yybegin(YYINITIAL); return symbol(SpecLangSymbols.CONSTRAINT_END); }
 	{hInt}				{ return symbol(SpecLangSymbols.INT, new BigInteger(yytext().substring(2), 16)); }
 	{Int}				{ return symbol(SpecLangSymbols.INT, new BigInteger(yytext())); }
 	
 	"true"				{ return symbol(SpecLangSymbols.TRUE); }
	"false"				{ return symbol(SpecLangSymbols.FALSE); }
	"old"				{ return symbol(SpecLangSymbols.OLD); }
	"fsum"				{ return symbol(SpecLangSymbols.FSUM); }
	"csum"				{ return symbol(SpecLangSymbols.SUM); }		
	"sum"				{ return symbol(SpecLangSymbols.SUM); }
 	 	
 	"["					{ return symbol(SpecLangSymbols.LBRACKET); }
 	"]"					{ return symbol(SpecLangSymbols.RBRACKET); }
 	 	
 	"("					{ return symbol(SpecLangSymbols.LPAR); }
	")"					{ return symbol(SpecLangSymbols.RPAR); }
	","					{ return symbol(SpecLangSymbols.ARG_SEP); }
	
	"+"					{ return symbol(SpecLangSymbols.PLUS); }
	"-"					{ return symbol(SpecLangSymbols.MINUS); }
	"*"					{ return symbol(SpecLangSymbols.MULTIPLY); }
	"/"					{ return symbol(SpecLangSymbols.DIVIDE); }
		
	"&&"				{ return symbol(SpecLangSymbols.AND); }
	"||"				{ return symbol(SpecLangSymbols.OR); }
	"!"					{ return symbol(SpecLangSymbols.NEG); }
	"==>"				{ return symbol(SpecLangSymbols.IMPLIES); }

	"=="				{ return symbol(SpecLangSymbols.EQ); }
	"!="				{ return symbol(SpecLangSymbols.NEQ); }
	">"					{ return symbol(SpecLangSymbols.GT); }
	">="				{ return symbol(SpecLangSymbols.GEQ); }
	"<"					{ return symbol(SpecLangSymbols.LT); }
	"<="				{ return symbol(SpecLangSymbols.LEQ); }
	
	{ws}				{ /* ignore */ }	
	{Identifier2}    	{ return symbol(SpecLangSymbols.NAME, yytext()); }
 } 
 
<<EOF>>                 { return symbol(SpecLangSymbols.EOF); }
 