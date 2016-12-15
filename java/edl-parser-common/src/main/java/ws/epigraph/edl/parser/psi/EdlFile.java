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

package ws.epigraph.edl.parser.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import ws.epigraph.edl.parser.EdlFileType;
import ws.epigraph.edl.parser.EdlLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static ws.epigraph.edl.lexer.EdlElementTypes.S_DEFS;
import static ws.epigraph.edl.lexer.EdlElementTypes.S_IMPORTS;
import static ws.epigraph.edl.lexer.EdlElementTypes.S_NAMESPACE_DECL;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlFile extends PsiFileBase {
  public EdlFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, EdlLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return EdlFileType.INSTANCE;
  }

  @Nullable
  public EdlDefs getDefs() {
    return (EdlDefs) calcTreeElement().findPsiChildByType(S_DEFS);
  }

  @Nullable
  public EdlImports getImportsStatement() {
    return (EdlImports) calcTreeElement().findPsiChildByType(S_IMPORTS);
  }

  @NotNull
  public List<EdlImportStatement> getImportStatements() {
    EdlImports importsStatement = getImportsStatement();
    if (importsStatement == null) return Collections.emptyList();

    return importsStatement.getImportStatementList();
  }

  @Nullable
  public EdlNamespaceDecl getNamespaceDecl() {
    return (EdlNamespaceDecl) calcTreeElement().findPsiChildByType(S_NAMESPACE_DECL);
  }

  @Override
  public String toString() {
    return "Epigraph Declarations file";
  }
}
