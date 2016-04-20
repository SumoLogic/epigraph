package com.sumologic.dohyo.plugin.schema.parser;

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
import com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes;
import com.sumologic.dohyo.plugin.schema.lexer.SchemaFlexAdapter;
import com.sumologic.dohyo.plugin.schema.psi.SchemaFile;
import com.sumologic.dohyo.plugin.schema.stubs.types.SchemaFileStubElementType;

import static com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes.*;

import org.jetbrains.annotations.NotNull;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaParserDefinition implements ParserDefinition {
  public final static TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE);
  public final static TokenSet COMMENTS = TokenSet.create(S_COMMENT, S_BLOCK_COMMENT);
  public final static TokenSet STRINGS = TokenSet.create(S_STRING);
  public final static TokenSet CURLY_BRACES = TokenSet.create(S_CURLY_LEFT, S_CURLY_RIGHT);

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return SchemaFlexAdapter.newInstance();
  }

  @Override
  public PsiParser createParser(Project project) {
    return new SchemaParser();
  }

  @Override
  public IFileElementType getFileNodeType() {
    return SchemaFileStubElementType.INSTANCE;
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
    return STRINGS;
  }

  @NotNull
  @Override
  public PsiElement createElement(ASTNode node) {
    return SchemaElementTypes.Factory.createElement(node);
  }

  @Override
  public PsiFile createFile(FileViewProvider viewProvider) {
    return new SchemaFile(viewProvider);
  }

  @Override
  public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
    return SpaceRequirements.MAY; // TODO refine
  }
}
