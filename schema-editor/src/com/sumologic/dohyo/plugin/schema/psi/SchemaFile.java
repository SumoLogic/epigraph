package com.sumologic.dohyo.plugin.schema.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.sumologic.dohyo.plugin.schema.SchemaFileType;
import com.sumologic.dohyo.plugin.schema.SchemaLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes.*;

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

  @NotNull
  public SchemaImportStatement[] getImportStatements() {
    // TODO figure out stubs & indexing
//    StubElement stub = getStub();
//    if (stub != null) {
//      return stub.getChildrenByType(???
//    }

    return calcTreeElement().getChildrenAsPsiElements(S_IMPORT_STATEMENT, SchemaImportStatement[]::new);
  }

  @Override
  public String toString() {
    return "Schema file";
  }
}
