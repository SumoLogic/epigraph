package com.sumologic.epigraph.schema.parser.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.sumologic.epigraph.schema.parser.SchemaFileType;
import com.sumologic.epigraph.schema.parser.SchemaLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import java.util.Collections;
import java.util.List;

import static com.sumologic.epigraph.schema.parser.lexer.SchemaElementTypes.S_DEFS;
import static com.sumologic.epigraph.schema.parser.lexer.SchemaElementTypes.S_IMPORTS;
import static com.sumologic.epigraph.schema.parser.lexer.SchemaElementTypes.S_NAMESPACE_DECL;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFile extends PsiFileBase {
  public SchemaFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, SchemaLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return SchemaFileType.INSTANCE;
  }

  @Nullable
  @Override
  public Icon getIcon(int flags) {
    return SchemaFileType.INSTANCE.getIcon();
  }

  @Nullable
  public SchemaDefs getDefs() {
    return (SchemaDefs) calcTreeElement().findPsiChildByType(S_DEFS);
  }

  @Nullable
  public SchemaImports getImportsStatement() {
    return (SchemaImports) calcTreeElement().findPsiChildByType(S_IMPORTS);
  }

  @NotNull
  public List<SchemaImportStatement> getImportStatements() {
    SchemaImports importsStatement = getImportsStatement();
    if (importsStatement == null) return Collections.emptyList();

    return importsStatement.getImportStatementList();
  }

  @Nullable
  public SchemaNamespaceDecl getNamespaceDecl() {
    return (SchemaNamespaceDecl) calcTreeElement().findPsiChildByType(S_NAMESPACE_DECL);
  }

  @Override
  public String toString() {
    return "Schema file";
  }
}