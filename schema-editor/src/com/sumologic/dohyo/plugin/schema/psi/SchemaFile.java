package com.sumologic.dohyo.plugin.schema.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.sumologic.dohyo.plugin.schema.SchemaFileType;
import com.sumologic.dohyo.plugin.schema.SchemaLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

import static com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes.S_NAMESPACE;
import static com.sumologic.dohyo.plugin.schema.lexer.SchemaElementTypes.S_NAMESPACED_TYPEDEFS;

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

  @NotNull
  public SchemaNamespacedTypedefs[] getNamespacedTypedefs() {
    // TODO figure out stubs & indexing
//    StubElement stub = getStub();
//    if (stub != null) {
//      return stub.getChildrenByType(???
//    }

    return calcTreeElement().getChildrenAsPsiElements(S_NAMESPACED_TYPEDEFS, SchemaNamespacedTypedefs[]::new);
  }

  @Override
  public String toString() {
    return "Schema file";
  }
}
