package com.sumologic.epigraph.ideaplugin.schema.lexer;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.*;

%%

%{
  int curlyCount = 0;

  public SchemaLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class SchemaLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

%states BACKTICK, DATA_VALUE

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

SPACE=[ \t\n\x0B\f\r]+
//BLOCK_COMMENT=("/*"[^"*"]{COMMENT_TAIL})|"/*"
// DOC_COMMENT="/*""*"+("/"|([^"/""*"]{COMMENT_TAIL}))?
//COMMENT_TAIL=([^"*"]*("*"+[^"*""/"])?)*("*"+"/")? // TODO disallow non-closed block comments
BLOCK_COMMENT="/*" !([^]* "*/" [^]*) ("*/")?
LINE_COMMENT="/""/"[^\r\n]*

DATA_VALUE=[^;]*
ID=[:letter:]([:letter:]|[:digit:])*

%%
<YYINITIAL> {
  {WHITE_SPACE}        { return com.intellij.psi.TokenType.WHITE_SPACE; }
  {SPACE}              { return com.intellij.psi.TokenType.WHITE_SPACE; }

   "`"                  { yybegin(BACKTICK); return S_BACKTICK; }

  "import"             { return curlyCount == 0 ? S_IMPORT : S_ID; }
  "namespace"          { return curlyCount == 0 ? S_NAMESPACE : S_ID; }
  "default"            { return curlyCount < 2 ? S_DEFAULT : S_ID; }
  "nodefault"          { return curlyCount < 2 ? S_NODEFAULT : S_ID; }
  "map"                { return curlyCount < 2 ? S_MAP : S_ID; }
  "list"               { return curlyCount < 2 ? S_LIST : S_ID; }
  "record"             { return curlyCount == 0 ? S_RECORD : S_ID; }
  "extends"            { return curlyCount == 0 ? S_EXTENDS : S_ID; }
  "meta"               { return curlyCount == 0 ? S_META : S_ID; }
  "supplement"         { return curlyCount == 0 ? S_SUPPLEMENT : S_ID; }
  "supplements"        { return curlyCount == 0 ? S_SUPPLEMENTS : S_ID; }
  "with"               { return curlyCount == 0 ? S_WITH : S_ID; }
  "vartype"            { return curlyCount == 0 ? S_VARTYPE : S_ID; }
  "polymorphic"        { return curlyCount == 0 ? S_POLYMORPHIC : S_ID; }
  "abstract"           { return curlyCount < 2 ? S_ABSTRACT : S_ID; }
  "override"           { return curlyCount == 1 ? S_OVERRIDE : S_ID; }
  "enum"               { return curlyCount == 0 ? S_ENUM : S_ID; }
  "integer"            { return curlyCount == 0 ? S_INTEGER_T : S_ID; }
  "long"               { return curlyCount == 0 ? S_LONG_T : S_ID; }
  "double"             { return curlyCount == 0 ? S_DOUBLE_T : S_ID; }
  "boolean"            { return curlyCount == 0 ? S_BOOLEAN_T : S_ID; }
  "string"             { return curlyCount == 0 ? S_STRING_T : S_ID; }
  ":"                  { return S_COLON; }
  ";"                  { return S_SEMI_COLON; }
  "*"                  { return S_STAR; }
  "."                  { return S_DOT; }
  ","                  { return S_COMMA; }
  "="                  { yybegin(DATA_VALUE); return S_EQ; }
  "+"                  { return S_PLUS; }
  "{"                  { curlyCount++; return S_CURLY_LEFT; }
  "}"                  { curlyCount = (curlyCount == 0 ? 0 : curlyCount - 1) ; return S_CURLY_RIGHT; }
  "["                  { return S_BRACKET_LEFT; }
  "]"                  { return S_BRACKET_RIGHT; }

  {LINE_COMMENT}       { return S_COMMENT; }
  {BLOCK_COMMENT}      { return S_BLOCK_COMMENT; }
  {ID}                 { return S_ID; }
}

<BACKTICK> {
  {ID}                 { return S_ID; }
  "`"                  { yybegin(YYINITIAL); return S_BACKTICK; }
}

<DATA_VALUE> {
  // TODO find a way to implement escaping for ';' inside data
  {DATA_VALUE}         { return S_DATA_VALUE; }
  ";"                  { yybegin(YYINITIAL); return S_SEMI_COLON; }
}

[^]                  { return com.intellij.psi.TokenType.BAD_CHARACTER; }
