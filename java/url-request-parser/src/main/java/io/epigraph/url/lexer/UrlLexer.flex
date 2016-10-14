package io.epigraph.url.lexer;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static io.epigraph.url.lexer.UrlElementTypes.*;

%%

%{
  public UrlLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class UrlLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

SPACE=[ \t\n\x0B\f\r]+
BLOCK_COMMENT="/*" !([^]* "*/" [^]*) ("*/")?

// strings are in single quotes for URLs
STRING=\' ( [^\'\\] | \\ ( [\'\\/bfnrt] | u[0-9]{4} ) )* \'
NUMBER=[-+]?[:digit:]*\.?[:digit:]+([eE][-+]?[:digit:]+)? // todo do the same in other *.idl
BOOLEAN=true|false

PARAM_NAME=([:letter:]|[:digit:])([:letter:]|[:digit:]|'_'|'-')*
ID=([:letter:]([:letter:]|[:digit:])*)|(`[^`]*`)

%state PARAM_NAME

%%
<YYINITIAL> {
  {WHITE_SPACE}        { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "list"               { return U_LIST; }
  "map"                { return U_MAP; }
  "default"            { return U_DEFAULT; }

  ":"                  { return U_COLON; }
  "."                  { return U_DOT; }
  ","                  { return U_COMMA; }
  ";"                  { return U_SEMICOLON; }
  "="                  { return U_EQ; }
  "{"                  { return U_CURLY_LEFT; }
  "}"                  { return U_CURLY_RIGHT; }
  "["                  { return U_BRACKET_LEFT; }
  "]"                  { return U_BRACKET_RIGHT; }
  "~"                  { return U_TILDA; }
  "*"                  { return U_STAR; }
  "+"                  { return U_PLUS; }
  "@"                  { return U_AT; }
  "#"                  { return U_HASH; }
  "_"                  { return U_UNDERSCORE; }
  "!"                  { return U_BANG; }
  "?"                  { yybegin(PARAM_NAME); return U_QMARK; }

  "/"                  { return U_SLASH; }
  "("                  { return U_PAREN_LEFT; }
  ")"                  { return U_PAREN_RIGHT; }
  "<"                  { return U_ANGLE_LEFT; }
  ">"                  { return U_ANGLE_RIGHT; }

  "null"               { return U_NULL; }
  {NUMBER}             { return U_NUMBER; }
  {STRING}             { return U_STRING; }
  {BOOLEAN}            { return U_BOOLEAN; }

  {BLOCK_COMMENT}      { return U_BLOCK_COMMENT; }
  {ID}                 { return U_ID; }
}

<PARAM_NAME> {
  {PARAM_NAME}         { return U_PARAM_NAME; }
  "="                  { yybegin(YYINITIAL); return U_EQ; }
}

[^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
