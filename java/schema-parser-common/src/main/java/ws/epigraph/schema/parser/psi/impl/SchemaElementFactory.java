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

package ws.epigraph.schema.parser.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import ws.epigraph.schema.parser.SchemaLanguage;
import ws.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class SchemaElementFactory {
  private SchemaElementFactory() {}

  public static @NotNull SchemaFile createFileFromText(@NotNull Project project, @NotNull String text) {
    return (SchemaFile) PsiFileFactory.getInstance(project).createFileFromText("a.s", SchemaLanguage.INSTANCE, text);
  }

  public static @NotNull PsiElement createId(@NotNull Project project, String text) {
    final SchemaFile file = createFileFromText(project, "namespace " + text);
    //noinspection ConstantConditions
    return ((SchemaQnSegment) file.getNamespaceDecl().getQn().getLastChild()).getQid().getId();
  }

  public static @NotNull PsiElement createBackTick(@NotNull Project project) {
    final SchemaFile file = createFileFromText(project, "namespace some\n long `LL`");
    //noinspection ConstantConditions
    return file.getDefs().getTypeDefWrapperList().get(0).getPrimitiveTypeDef().getQid().getFirstChild();
  }

  public static @NotNull SchemaQn createFqn(@NotNull Project project, String text) {
    final SchemaFile file = createFileFromText(project, "namespace " + text);
    //noinspection ConstantConditions
    return file.getNamespaceDecl().getQn();
  }

  public static @NotNull SchemaImports createImports(@NotNull Project project, String importToAdd) {
    final SchemaFile file = createFileFromText(project, "namespace some\n import " + importToAdd);
    //noinspection ConstantConditions
    return file.getImportsStatement();
  }

  public static @NotNull SchemaImportStatement createImport(@NotNull Project project, String importToAdd) {
    final SchemaFile file = createFileFromText(project, "namespace some\n import " + importToAdd);
    return file.getImportStatements().get(0);
  }

  public static @NotNull SchemaRecordTypeDef createRecordTypeDef(@NotNull Project project, String name) {
    final SchemaFile file = createFileFromText(project, "namespace some\nrecord " + name);
    //noinspection ConstantConditions
    return file.getDefs().getTypeDefWrapperList().get(0).getRecordTypeDef();
  }

  public static @NotNull PsiElement createWhitespaces(@NotNull Project project, String text) {
    final SchemaFile file = createFileFromText(project, text + "namespace some");
    return file.getChildren()[0];
  }

  public static @NotNull SchemaRetroDecl createRetroDecl(@NotNull Project project, @NotNull String tagName) {
    final SchemaFile file = createFileFromText(project, "namespace some\nrecord X{foo:X retro " + tagName + "}");
    //noinspection ConstantConditions

    return file.getDefs().getTypeDefWrapperList().get(0).getRecordTypeDef().getRecordTypeBody().
        getFieldDeclList().get(0).getValueTypeRef().getRetroDecl();
  }

}
