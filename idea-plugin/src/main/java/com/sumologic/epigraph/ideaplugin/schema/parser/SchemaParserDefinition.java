package com.sumologic.epigraph.ideaplugin.schema.parser;

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
import com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes;
import com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaFlexAdapter;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFile;
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaStubElementTypes;

import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.*;

import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaParserDefinition implements ParserDefinition {
  public final static TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE);
  public final static TokenSet IDENTIFIERS = TokenSet.create(S_ID);
  public final static TokenSet COMMENTS = TokenSet.create(S_COMMENT, S_BLOCK_COMMENT);
  public final static TokenSet CURLY_BRACES = TokenSet.create(S_CURLY_LEFT, S_CURLY_RIGHT);
  public final static TokenSet KEYWORDS = TokenSet.create(
      S_NAMESPACE,
      S_IMPORT,
      S_MAP,
      S_DEFAULT,
      S_NODEFAULT,
      S_LIST,
      S_RECORD,
      S_EXTENDS,
      S_VARTYPE,
      S_ENUM,
      S_META,
      S_SUPPLEMENT,
      S_SUPPLEMENTS,
      S_WITH,
      S_ABSTRACT,
      S_OVERRIDE,
      S_POLYMORPHIC,
      S_INTEGER_T,
      S_LONG_T,
      S_DOUBLE_T,
      S_BOOLEAN_T,
      S_STRING_T
  );
  public final static TokenSet LITERALS = TokenSet.create(S_DATA_VALUE);

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
    return SchemaStubElementTypes.SCHEMA_FILE;
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
    return LITERALS;
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
