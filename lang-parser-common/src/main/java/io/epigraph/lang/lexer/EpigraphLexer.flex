package io.epigraph.lang.lexer;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static io.epigraph.lang.lexer.EpigraphElementTypes.*;

%%

%{
  public EpigraphLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class EpigraphLexer
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

  "import"             { return E_IMPORT; }
  "namespace"          { return E_NAMESPACE; }
  "default"            { return E_DEFAULT; }
  "nodefault"          { return E_NODEFAULT; }
  "map"                { return E_MAP; }
  "list"               { return E_LIST; }
  "record"             { return E_RECORD; }
  "extends"            { return E_EXTENDS; }
  "meta"               { return E_META; }
  "supplement"         { return E_SUPPLEMENT; }
  "supplements"        { return E_SUPPLEMENTS; }
  "with"               { return E_WITH; }
  "vartype"            { return E_VARTYPE; }
  "polymorphic"        { return E_POLYMORPHIC; }
  "abstract"           { return E_ABSTRACT; }
  "override"           { return E_OVERRIDE; }
  "enum"               { return E_ENUM; }
  "integer"            { return E_INTEGER_T; }
  "long"               { return E_LONG_T; }
  "double"             { return E_DOUBLE_T; }
  "boolean"            { return E_BOOLEAN_T; }
  "string"             { return E_STRING_T; }
  "forbidden"          { return E_FORBIDDEN; }
  "required"           { return E_REQUIRED; }
  "parameters"         { return E_PARAMETERS; }
  ":"                  { return E_COLON; }
  "."                  { return E_DOT; }
  ","                  { return E_COMMA; }
  "="                  { return E_EQ; }
  "{"                  { return E_CURLY_LEFT; }
  "}"                  { return E_CURLY_RIGHT; }
  "["                  { return E_BRACKET_LEFT; }
  "]"                  { return E_BRACKET_RIGHT; }
  "("                  { return E_PAREN_LEFT; }
  ")"                  { return E_PAREN_RIGHT; }
  "<"                  { return E_ANGLE_LEFT; }
  ">"                  { return E_ANGLE_RIGHT; }
  "/"                  { return E_SLASH; }

  {STRING}             { return E_STRING; }
  {NUMBER}             { return E_NUMBER; }
  "true"               { return E_BOOLEAN; }
  "false"              { return E_BOOLEAN; }
  "null"               { return E_NULL; }

  {LINE_COMMENT}       { return E_COMMENT; }
  {BLOCK_COMMENT}      { return E_BLOCK_COMMENT; }
  {ID}                 { return E_ID; }
}

[^]                  { return com.intellij.psi.TokenType.BAD_CHARACTER; }
