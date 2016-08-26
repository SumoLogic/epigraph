package io.epigraph.lang.parser.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import io.epigraph.lang.schema.SchemaLanguage;
import io.epigraph.lang.schema.parser.SchemaFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static io.epigraph.lang.lexer.EpigraphElementTypes.E_DEFS;
import static io.epigraph.lang.lexer.EpigraphElementTypes.E_IMPORTS;
import static io.epigraph.lang.lexer.EpigraphElementTypes.E_NAMESPACE_DECL;

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

//  @Nullable
//  @Override
//  public Icon getIcon(int flags) {
//    return SchemaFileType.INSTANCE.getIcon();
//  }

  @Nullable
  public EpigraphDefs getDefs() {
    return (EpigraphDefs) calcTreeElement().findPsiChildByType(E_DEFS);
  }

  @Nullable
  public EpigraphImports getImportsStatement() {
    return (EpigraphImports) calcTreeElement().findPsiChildByType(E_IMPORTS);
  }

  @NotNull
  public List<EpigraphImportStatement> getImportStatements() {
    EpigraphImports importsStatement = getImportsStatement();
    if (importsStatement == null) return Collections.emptyList();

    return importsStatement.getImportStatementList();
  }

  @Nullable
  public EpigraphNamespaceDecl getNamespaceDecl() {
    return (EpigraphNamespaceDecl) calcTreeElement().findPsiChildByType(E_NAMESPACE_DECL);
  }

  @Override
  public String toString() {
    return "Schema file";
  }
}
