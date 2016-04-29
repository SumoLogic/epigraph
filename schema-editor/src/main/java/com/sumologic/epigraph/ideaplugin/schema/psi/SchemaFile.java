package com.sumologic.epigraph.ideaplugin.schema.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.sumologic.epigraph.ideaplugin.schema.SchemaFileType;
import com.sumologic.epigraph.ideaplugin.schema.SchemaLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import java.util.Collections;
import java.util.List;

import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.*;

/**
 * Todo add doc
 *
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
    // TODO figure out stubs & indexing
//    StubElement stub = getStub();
//    if (stub != null) {
//      return stub.getChildrenByType(???
//    }

    return (SchemaDefs) calcTreeElement().findPsiChildByType(S_DEFS);
  }

  @Nullable
  public SchemaImports getImportsStatement() {
    // TODO figure out stubs & indexing
//    StubElement stub = getStub();
//    if (stub != null) {
//      return stub.getChildrenByType(???
//    }

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
