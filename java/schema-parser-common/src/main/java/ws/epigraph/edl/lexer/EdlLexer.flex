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

  "import"             { return S_IMPORT; }
  "namespace"          { return S_NAMESPACE; }
  "default"            { return S_DEFAULT; }
  "resource"           { return S_RESOURCE; }
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
  ";"                  { return S_SEMICOLON; }
  "="                  { return S_EQ; }
  "{"                  { return S_CURLY_LEFT; }
  "}"                  { return S_CURLY_RIGHT; }
  "["                  { return S_BRACKET_LEFT; }
  "]"                  { return S_BRACKET_RIGHT; }
  "~"                  { return S_TILDA; }
  "*"                  { return S_STAR; }
  "+"                  { return S_PLUS; }
  "("                  { return S_PAREN_LEFT; }
  ")"                  { return S_PAREN_RIGHT; }
  "<"                  { return S_ANGLE_LEFT; }
  ">"                  { return S_ANGLE_RIGHT; }
  "/"                  { return S_SLASH; }
  "@"                  { return S_AT; }
  "#"                  { return S_HASH; }
  "_"                  { return S_UNDERSCORE; }
  "!"                  { return S_BANG; }

  // idl - specific
  "forbidden"          { return S_FORBIDDEN; }
  "required"           { return S_REQUIRED; }
  "default"            { return S_DEFAULT; }
  "GET"                { return S_GET; }
  "POST"               { return S_POST; }
  "PUT"                { return S_PUT; }
  "DELETE"             { return S_DELETE; }
  "read"               { return S_OP_READ; }
  "create"             { return S_OP_CREATE; }
  "update"             { return S_OP_UPDATE; }
  "delete"             { return S_OP_DELETE; }
  "custom"             { return S_OP_CUSTOM; }
  "method"             { return S_METHOD; }
  "inputType"          { return S_INPUT_TYPE; }
  "inputProjection"    { return S_INPUT_PROJECTION; }
  "outputType"         { return S_OUTPUT_TYPE; }
  "outputProjection"   { return S_OUTPUT_PROJECTION; }
  "deleteProjection"   { return S_DELETE_PROJECTION; }
  "path"               { return S_PATH; }

  {STRING}             { return S_STRING; }
  {NUMBER}             { return S_NUMBER; }
  "true"               { return S_BOOLEAN; }
  "false"              { return S_BOOLEAN; }
  "null"               { return S_NULL; }

  {LINE_COMMENT}       { return S_COMMENT; }
  {BLOCK_COMMENT}      { return S_BLOCK_COMMENT; }
  {ID}                 { return S_ID; }
}

[^]                    { return com.intellij.psi.TokenType.BAD_CHARACTER; }
