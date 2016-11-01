package io.epigraph.idl.lexer;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static io.epigraph.idl.lexer.IdlElementTypes.*;

%%

%{
  public IdlLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class IdlLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

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
BOOLEAN=true|false

ID=([:letter:]([:letter:]|[:digit:])*)|(`[^`]*`)

%%
<YYINITIAL> {
  {WHITE_SPACE}        { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "namespace"          { return I_NAMESPACE; }
  "import"             { return I_IMPORT; }
  "meta"               { return I_META; }
  "list"               { return I_LIST; }
  "map"                { return I_MAP; }
  "forbidden"          { return I_FORBIDDEN; }
  "required"           { return I_REQUIRED; }
  "default"            { return I_DEFAULT; }
  "resource"           { return I_RESOURCE; }
  "GET"                { return I_GET; }
  "POST"               { return I_POST; }
  "PUT"                { return I_PUT; }
  "READ"               { return I_READ; }
  "CREATE"             { return I_CREATE; }
  "UPDATE"             { return I_UPDATE; }
  "DELETE"             { return I_DELETE; }
  "CUSTOM"             { return I_CUSTOM; }
  "method"             { return I_METHOD; }
  "inputType"          { return I_INPUT_TYPE; }
  "inputProjection"    { return I_INPUT_PROJECTION; }
  "outputType"         { return I_OUTPUT_TYPE; }
  "outputProjection"   { return I_OUTPUT_PROJECTION; }
  "deleteProjection"   { return I_DELETE_PROJECTION; }
  "path"               { return I_PATH; }
  ":"                  { return I_COLON; }
  "."                  { return I_DOT; }
  ","                  { return I_COMMA; }
  ";"                  { return I_SEMICOLON; }
  "="                  { return I_EQ; }
  "{"                  { return I_CURLY_LEFT; }
  "}"                  { return I_CURLY_RIGHT; }
  "["                  { return I_BRACKET_LEFT; }
  "]"                  { return I_BRACKET_RIGHT; }
  "~"                  { return I_TILDA; }
  "*"                  { return I_STAR; }
  "+"                  { return I_PLUS; }
  "@"                  { return I_AT; }
  "#"                  { return I_HASH; }
  "_"                  { return I_UNDERSCORE; }
  "!"                  { return I_BANG; }

  "/"                  { return I_SLASH; }
  "("                  { return I_PAREN_LEFT; }
  ")"                  { return I_PAREN_RIGHT; }
  "<"                  { return I_ANGLE_LEFT; }
  ">"                  { return I_ANGLE_RIGHT; }

  "null"               { return I_NULL; }
  {NUMBER}             { return I_NUMBER; }
  {STRING}             { return I_STRING; }
  {BOOLEAN}            { return I_BOOLEAN; }

  {LINE_COMMENT}       { return I_COMMENT; }
  {BLOCK_COMMENT}      { return I_BLOCK_COMMENT; }
  {ID}                 { return I_ID; }

}

[^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
