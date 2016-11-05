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

package ws.epigraph.url.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import ws.epigraph.url.lexer.UrlElementTypes;
import ws.epigraph.url.lexer.UrlLexer;
import ws.epigraph.url.parser.psi.UrlFile;
import ws.epigraph.url.parser.psi.stubs.UrlStubElementTypes;
import org.jetbrains.annotations.NotNull;

import static ws.epigraph.url.lexer.UrlElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlParserDefinition implements ParserDefinition {
  public static UrlParserDefinition INSTANCE = new UrlParserDefinition();

  public final static TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE);
  public final static TokenSet IDENTIFIERS = TokenSet.create(U_ID, U_PARAM_NAME);
  public final static TokenSet COMMENTS = TokenSet.create(U_BLOCK_COMMENT);
  public final static TokenSet CURLY_BRACES = TokenSet.create(U_CURLY_LEFT, U_CURLY_RIGHT);

  public final static TokenSet KEYWORDS = TokenSet.create(
      U_MAP,
      U_LIST,
      U_DEFAULT
  );

  public final static TokenSet STRING_LITERALS = TokenSet.create(U_STRING);
  public final static TokenSet LITERALS = TokenSet.andSet(STRING_LITERALS, TokenSet.create(U_NUMBER, U_BOOLEAN));

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new FlexAdapter(new UrlLexer());
  }

  @Override
  public PsiParser createParser(Project project) {
    return new UrlParser();
  }

  @Override
  public IFileElementType getFileNodeType() {
    return UrlStubElementTypes.URL_FILE;
  }

  @Override
  public @NotNull TokenSet getWhitespaceTokens() {
    return WHITESPACES;
  }

  @Override
  public @NotNull TokenSet getCommentTokens() {
    return COMMENTS;
  }

  @Override
  public @NotNull TokenSet getStringLiteralElements() {
    return STRING_LITERALS;
  }

  @NotNull
  @Override
  public PsiElement createElement(ASTNode astNode) {
    return UrlElementTypes.Factory.createElement(astNode);
  }

  @Override
  public PsiFile createFile(FileViewProvider fileViewProvider) {
    return new UrlFile(fileViewProvider);
  }

  @Override
  public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode1) {
    return SpaceRequirements.MAY;
  }
}
