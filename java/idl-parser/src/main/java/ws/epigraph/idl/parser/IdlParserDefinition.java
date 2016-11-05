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

package ws.epigraph.idl.parser;

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
import ws.epigraph.idl.lexer.IdlElementTypes;
import ws.epigraph.idl.lexer.IdlLexer;
import ws.epigraph.idl.parser.psi.IdlFile;
import ws.epigraph.idl.parser.psi.stubs.IdlStubElementTypes;
import org.jetbrains.annotations.NotNull;

import static ws.epigraph.idl.lexer.IdlElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlParserDefinition implements ParserDefinition {
  public static IdlParserDefinition INSTANCE = new IdlParserDefinition();

  public final static TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE);
  public final static TokenSet IDENTIFIERS = TokenSet.create(I_ID);
  public final static TokenSet COMMENTS = TokenSet.create(I_COMMENT, I_BLOCK_COMMENT);
  public final static TokenSet CURLY_BRACES = TokenSet.create(I_CURLY_LEFT, I_CURLY_RIGHT);

  public final static TokenSet KEYWORDS = TokenSet.create(
      I_NAMESPACE,
      I_IMPORT,
      I_META,
      I_MAP,
      I_LIST,
      I_FORBIDDEN,
      I_REQUIRED,
      I_DEFAULT,
      I_RESOURCE,
      I_GET,
      I_POST,
      I_PUT,
      I_DELETE,
      I_READ,
      I_CREATE,
      I_UPDATE,
      I_DELETE,
      I_CUSTOM,
      I_METHOD,
      I_PATH,
      I_INPUT_TYPE,
      I_INPUT_PROJECTION,
      I_OUTPUT_TYPE,
      I_OUTPUT_PROJECTION,
      I_DELETE_PROJECTION
  );

  public final static TokenSet STRING_LITERALS = TokenSet.create(I_STRING);
  public final static TokenSet LITERALS = TokenSet.andSet(STRING_LITERALS, TokenSet.create(I_NUMBER, I_BOOLEAN));

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new FlexAdapter(new IdlLexer());
  }

  @Override
  public PsiParser createParser(Project project) {
    return new IdlParser();
  }

  @Override
  public IFileElementType getFileNodeType() {
    return IdlStubElementTypes.IDL_FILE;
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
    return IdlElementTypes.Factory.createElement(astNode);
  }

  @Override
  public PsiFile createFile(FileViewProvider fileViewProvider) {
    return new IdlFile(fileViewProvider);
  }

  @Override
  public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode1) {
    return SpaceRequirements.MAY;
  }
}
