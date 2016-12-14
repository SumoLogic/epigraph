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

package ws.epigraph.edl.parser.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import ws.epigraph.edl.parser.SchemaLanguage;
import ws.epigraph.edl.parser.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaElementFactory {
  @NotNull
  public static SchemaFile createFileFromText(@NotNull Project project, @NotNull String text) {
    return (SchemaFile) PsiFileFactory.getInstance(project).createFileFromText("a.s", SchemaLanguage.INSTANCE, text);
  }

  @NotNull
  public static PsiElement createId(@NotNull Project project, String text) {
    final SchemaFile file = createFileFromText(project, "namespace " + text);
    //noinspection ConstantConditions
    return ((SchemaQnSegment) file.getNamespaceDecl().getQn().getLastChild()).getQid().getId();
  }

  @NotNull
  public static PsiElement createBackTick(@NotNull Project project) {
    final SchemaFile file = createFileFromText(project, "namespace some\n long `LL`");
    //noinspection ConstantConditions
    return file.getDefs().getTypeDefWrapperList().get(0).getPrimitiveTypeDef().getQid().getFirstChild();
  }

  @NotNull
  public static SchemaQn createFqn(@NotNull Project project, String text) {
    final SchemaFile file = createFileFromText(project, "namespace " + text);
    //noinspection ConstantConditions
    return file.getNamespaceDecl().getQn();
  }

  @NotNull
  public static SchemaImports createImports(@NotNull Project project, String importToAdd) {
    final SchemaFile file = createFileFromText(project, "namespace some\n import " + importToAdd);
    //noinspection ConstantConditions
    return file.getImportsStatement();
  }

  @NotNull
  public static SchemaImportStatement createImport(@NotNull Project project, String importToAdd) {
    final SchemaFile file = createFileFromText(project, "namespace some\n import " + importToAdd);
    return file.getImportStatements().get(0);
  }

  @NotNull
  public static SchemaRecordTypeDef createRecordTypeDef(@NotNull Project project, String name) {
    final SchemaFile file = createFileFromText(project, "namespace some\nrecord " + name);
    //noinspection ConstantConditions
    return file.getDefs().getTypeDefWrapperList().get(0).getRecordTypeDef();
  }

  @NotNull
  public static PsiElement createWhitespaces(@NotNull Project project, String text) {
    final SchemaFile file = createFileFromText(project, text + "namespace some");
    return file.getChildren()[0];
  }

  @NotNull
  public static SchemaDefaultOverride createDefaultOverride(@NotNull Project project, @NotNull String tagName) {
    final SchemaFile file = createFileFromText(project, "namespace some\nrecord X{foo:X default " + tagName + "}");
    //noinspection ConstantConditions

    return file.getDefs().getTypeDefWrapperList().get(0).getRecordTypeDef().getRecordTypeBody().
        getFieldDeclList().get(0).getValueTypeRef().getDefaultOverride();
  }

}
