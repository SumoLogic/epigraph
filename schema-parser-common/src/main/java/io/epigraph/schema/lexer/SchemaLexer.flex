package io.epigraph.schema.lexer;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static io.epigraph.schema.lexer.SchemaElementTypes.*;

%%

%{
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

// %states BACKTICK

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

SPACE=[ \t\n\x0B\f\r]+
//BLOCK_COMMENT=("/*"[^"*"]{COMMENT_TAIL})|"/*"
// DOC_COMMENT="/*""*"+("/"|([^"/""*"]{COMMENT_TAIL}))?
//COMMENT_TAIL=([^"*"]*("*"+[^"*""/"])?)*("*"+"/")? // TODO disallow non-closed block comments?
BLOCK_COMMENT="/*" !([^]* "*/" [^]*) ("*/")?
LINE_COMMENT="/""/"[^\r\n]*

STRING=\" ( [^\"\\] | \\ ( [\"\\/bfnrt] | u[0-9]{4} ) )* \"
NUMBER=([:digit:])+(\.([:digit:])+)?

ID=([:letter:]([:letter:]|[:digit:])*)|(`[^`]*`)

%%
<YYINITIAL> {
  {WHITE_SPACE}        { return com.intellij.psi.TokenType.WHITE_SPACE; }
  {SPACE}              { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "import"             { return S_IMPORT; }
  "namespace"          { return S_NAMESPACE; }
  "default"            { return S_DEFAULT; }
  "nodefault"          { return S_NODEFAULT; }
  "map"                { return S_MAP; }
  "list"               { return S_LIST; }
  "record"             { return S_RECORD; }
  "extends"            { return S_EXTENDS; }
  "meta"               { return S_META; }
  "supplement"         { return S_SUPPLEMENT; }
  "supplements"        { return S_SUPPLEMENTS; }
  "with"               { return S_WITH; }
  "vartype"            { return S_VARTYPE; }
  "polymorphic"        { return S_POLYMORPHIC; }
  "abstract"           { return S_ABSTRACT; }
  "override"           { return S_OVERRIDE; }
  "enum"               { return S_ENUM; }
  "integer"            { return S_INTEGER_T; }
  "long"               { return S_LONG_T; }
  "double"             { return S_DOUBLE_T; }
  "boolean"            { return S_BOOLEAN_T; }
  "string"             { return S_STRING_T; }
  ":"                  { return S_COLON; }
  "."                  { return S_DOT; }
  ","                  { return S_COMMA; }
  "="                  { return S_EQ; }
  "{"                  { return S_CURLY_LEFT; }
  "}"                  { return S_CURLY_RIGHT; }
  "["                  { return S_BRACKET_LEFT; }
  "]"                  { return S_BRACKET_RIGHT; }
  "("                  { return S_PAREN_LEFT; }
  ")"                  { return S_PAREN_RIGHT; }
  "<"                  { return S_ANGLE_LEFT; }
  ">"                  { return S_ANGLE_RIGHT; }
  "/"                  { return S_SLASH; }

  {STRING}             { return S_STRING; }
  {NUMBER}             { return S_NUMBER; }
  "true"               { return S_BOOLEAN; }
  "false"              { return S_BOOLEAN; }
  "null"               { return S_NULL; }

  {LINE_COMMENT}       { return S_COMMENT; }
  {BLOCK_COMMENT}      { return S_BLOCK_COMMENT; }
  {ID}                 { return S_ID; }
}

[^]                  { return com.intellij.psi.TokenType.BAD_CHARACTER; }
