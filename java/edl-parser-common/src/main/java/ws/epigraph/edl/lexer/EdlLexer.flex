package ws.epigraph.schema.lexer;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static ws.epigraph.edl.lexer.EdlElementTypes.*;

%%

%{
  public EdlLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class EdlLexer
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
  "resource"           { return E_RESOURCE; }
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
  "abstract"           { return E_ABSTRACT; }
  "override"           { return E_OVERRIDE; }
  "enum"               { return E_ENUM; }
  "integer"            { return E_INTEGER_T; }
  "long"               { return E_LONG_T; }
  "double"             { return E_DOUBLE_T; }
  "boolean"            { return E_BOOLEAN_T; }
  "string"             { return E_STRING_T; }
  ":"                  { return E_COLON; }
  "."                  { return E_DOT; }
  ","                  { return E_COMMA; }
  ";"                  { return E_SEMICOLON; }
  "="                  { return E_EQ; }
  "{"                  { return E_CURLY_LEFT; }
  "}"                  { return E_CURLY_RIGHT; }
  "["                  { return E_BRACKET_LEFT; }
  "]"                  { return E_BRACKET_RIGHT; }
  "~"                  { return E_TILDA; }
  "*"                  { return E_STAR; }
  "+"                  { return E_PLUS; }
  "("                  { return E_PAREN_LEFT; }
  ")"                  { return E_PAREN_RIGHT; }
  "<"                  { return E_ANGLE_LEFT; }
  ">"                  { return E_ANGLE_RIGHT; }
  "/"                  { return E_SLASH; }
  "@"                  { return E_AT; }
  "#"                  { return E_HASH; }
  "_"                  { return E_UNDERSCORE; }
  "!"                  { return E_BANG; }

  // idl - specific
  "forbidden"          { return E_FORBIDDEN; }
  "required"           { return E_REQUIRED; }
  "default"            { return E_DEFAULT; }
  "GET"                { return E_GET; }
  "POST"               { return E_POST; }
  "PUT"                { return E_PUT; }
  "DELETE"             { return E_DELETE; }
  "read"               { return E_OP_READ; }
  "create"             { return E_OP_CREATE; }
  "update"             { return E_OP_UPDATE; }
  "delete"             { return E_OP_DELETE; }
  "custom"             { return E_OP_CUSTOM; }
  "method"             { return E_METHOD; }
  "inputType"          { return E_INPUT_TYPE; }
  "inputProjection"    { return E_INPUT_PROJECTION; }
  "outputType"         { return E_OUTPUT_TYPE; }
  "outputProjection"   { return E_OUTPUT_PROJECTION; }
  "deleteProjection"   { return E_DELETE_PROJECTION; }
  "path"               { return E_PATH; }

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
