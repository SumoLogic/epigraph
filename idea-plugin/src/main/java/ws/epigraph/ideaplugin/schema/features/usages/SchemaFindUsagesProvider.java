/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.ideaplugin.schema.features.usages;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import ws.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import ws.epigraph.schema.parser.SchemaParserDefinition;
import ws.epigraph.schema.lexer.SchemaFlexAdapter;
import ws.epigraph.schema.parser.psi.SchemaQnSegment;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import ws.epigraph.schema.parser.psi.SchemaEntityTagDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaFindUsagesProvider implements FindUsagesProvider {
  @Nullable
  @Override
  public WordsScanner getWordsScanner() {
    return new DefaultWordsScanner(SchemaFlexAdapter.newInstance(),
        SchemaParserDefinition.IDENTIFIERS,
        SchemaParserDefinition.COMMENTS,
        SchemaParserDefinition.LITERALS);
  }

  @Override
  public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
    // TODO Support fields used by projections

    if (psiElement instanceof SchemaTypeDef) {
      SchemaTypeDef element = (SchemaTypeDef) psiElement;
      return element.getName() != null;
    }

    if (psiElement instanceof SchemaQnSegment) {
      SchemaQnSegment fqnSegment = (SchemaQnSegment) psiElement;
      return fqnSegment.getName() != null;
    }

    if (psiElement instanceof SchemaEntityTagDecl) {
      SchemaEntityTagDecl varTagDecl = (SchemaEntityTagDecl) psiElement;
      return varTagDecl.getName() != null;
    }

    return false;
  }

  @Nullable
  @Override
  public String getHelpId(@NotNull PsiElement psiElement) {
    return null;
  }

  @NotNull
  @Override
  public String getType(@NotNull PsiElement element) {
    return SchemaPresentationUtil.getType(element);
  }

  @NotNull
  @Override
  public String getDescriptiveName(@NotNull PsiElement element) {
    if (element instanceof PsiNamedElement) {
      PsiNamedElement namedElement = (PsiNamedElement) element;
      String name = SchemaPresentationUtil.getName(namedElement, false);
      if (name != null) return name;
    }

    return "Unknown getElement: " + element;
  }

  @NotNull
  @Override
  public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
    if (element instanceof PsiNamedElement) {
      PsiNamedElement namedElement = (PsiNamedElement) element;
      String name = SchemaPresentationUtil.getName(namedElement, useFullName);
      if (name != null) return name;
    }
   
    return "";
  }
}
