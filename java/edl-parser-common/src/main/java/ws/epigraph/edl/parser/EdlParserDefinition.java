/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.edl.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import ws.epigraph.edl.lexer.EdlElementTypes;
import ws.epigraph.edl.lexer.EdlFlexAdapter;
import ws.epigraph.edl.parser.psi.EdlFile;
import ws.epigraph.edl.parser.psi.stubs.EdlStubElementTypes;
import org.jetbrains.annotations.NotNull;

import static ws.epigraph.edl.lexer.EdlElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlParserDefinition implements ParserDefinition {
  public static final EdlParserDefinition INSTANCE = new EdlParserDefinition();

  public static final TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE);
  public static final TokenSet IDENTIFIERS = TokenSet.create(E_ID);
  public static final TokenSet COMMENTS = TokenSet.create(E_COMMENT, E_BLOCK_COMMENT);
  public static final TokenSet CURLY_BRACES = TokenSet.create(E_CURLY_LEFT, E_CURLY_RIGHT);
  public static final TokenSet KEYWORDS = TokenSet.create(
      E_NAMESPACE,
      E_IMPORT,
      E_MAP,
      E_DEFAULT,
      E_NODEFAULT,
      E_LIST,
      E_RECORD,
      E_EXTENDS,
      E_VARTYPE,
      E_ENUM,
      E_META,
      E_SUPPLEMENT,
      E_SUPPLEMENTS,
      E_WITH,
      E_ABSTRACT,
      E_OVERRIDE,
      E_INTEGER_T,
      E_LONG_T,
      E_DOUBLE_T,
      E_BOOLEAN_T,
      E_STRING_T,
      E_NULL, // or is it a LITERAL?
      E_FORBIDDEN,
      E_REQUIRED,
      E_RESOURCE,
      E_GET,
      E_POST,
      E_PUT,
      E_DELETE,
      E_OP_READ,
      E_OP_CREATE,
      E_OP_UPDATE,
      E_OP_DELETE,
      E_OP_CUSTOM,
      E_METHOD,
      E_PATH,
      E_INPUT_TYPE,
      E_INPUT_PROJECTION,
      E_OUTPUT_TYPE,
      E_OUTPUT_PROJECTION,
      E_DELETE_PROJECTION
  );
  public static final TokenSet STRING_LITERALS = TokenSet.create(E_STRING);
  public static final TokenSet LITERALS = TokenSet.andSet(STRING_LITERALS, TokenSet.create(E_NUMBER, E_BOOLEAN));
  public static final TokenSet TYPE_KINDS = TokenSet.create(E_VARTYPE, E_RECORD, E_MAP, E_LIST, E_ENUM,
      E_STRING_T, E_INTEGER_T, E_LONG_T, E_DOUBLE_T, E_BOOLEAN_T);

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return EdlFlexAdapter.newInstance();
  }

  @Override
  public PsiParser createParser(Project project) {
    return new EdlParser();
  }

  @Override
  public IFileElementType getFileNodeType() {
    return EdlStubElementTypes.EDL_FILE;
  }

  @NotNull
  @Override
  public TokenSet getWhitespaceTokens() {
    return WHITESPACES;
  }

  @NotNull
  @Override
  public TokenSet getCommentTokens() {
    return COMMENTS;
  }

  @NotNull
  @Override
  public TokenSet getStringLiteralElements() {
    return STRING_LITERALS;
  }

  @NotNull
  @Override
  public PsiElement createElement(ASTNode node) {
    return EdlElementTypes.Factory.createElement(node);
  }

  @Override
  public PsiFile createFile(FileViewProvider viewProvider) {
    return new EdlFile(viewProvider);
  }

  @Override
  public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
    return SpaceRequirements.MAY; // TODO refine
  }

  //

  public static boolean isKeyword(@NotNull String name) {
    EdlFlexAdapter lexer = EdlFlexAdapter.newInstance();
    lexer.start(name);
    return EdlParserDefinition.KEYWORDS.contains(lexer.getTokenType()) && lexer.getTokenEnd() == name.length();
  }

  public static boolean isIdentifier(@NotNull String name) {
    EdlFlexAdapter lexer = EdlFlexAdapter.newInstance();
    lexer.start(name);
    return EdlParserDefinition.IDENTIFIERS.contains(lexer.getTokenType()) && lexer.getTokenEnd() == name.length();
  }
}
