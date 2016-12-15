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

package ws.epigraph.ideaplugin.edl.features.usages;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import ws.epigraph.ideaplugin.edl.presentation.EdlPresentationUtil;
import ws.epigraph.edl.parser.EdlParserDefinition;
import ws.epigraph.edl.lexer.EdlFlexAdapter;
import ws.epigraph.edl.parser.psi.EdlQnSegment;
import ws.epigraph.edl.parser.psi.EdlTypeDef;
import ws.epigraph.edl.parser.psi.EdlVarTagDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlFindUsagesProvider implements FindUsagesProvider {
  @Nullable
  @Override
  public WordsScanner getWordsScanner() {
    return new DefaultWordsScanner(EdlFlexAdapter.newInstance(),
        EdlParserDefinition.IDENTIFIERS,
        EdlParserDefinition.COMMENTS,
        EdlParserDefinition.LITERALS);
  }

  @Override
  public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
    // TODO Support fields used by projections

    if (psiElement instanceof EdlTypeDef) {
      EdlTypeDef element = (EdlTypeDef) psiElement;
      return element.getName() != null;
    }

    if (psiElement instanceof EdlQnSegment) {
      EdlQnSegment fqnSegment = (EdlQnSegment) psiElement;
      return fqnSegment.getName() != null;
    }

    if (psiElement instanceof EdlVarTagDecl) {
      EdlVarTagDecl varTagDecl = (EdlVarTagDecl) psiElement;
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
    return EdlPresentationUtil.getType(element);
  }

  @NotNull
  @Override
  public String getDescriptiveName(@NotNull PsiElement element) {
    if (element instanceof PsiNamedElement) {
      PsiNamedElement namedElement = (PsiNamedElement) element;
      String name = EdlPresentationUtil.getName(namedElement, false);
      if (name != null) return name;
    }

    return "Unknown getElement: " + element;
  }

  @NotNull
  @Override
  public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
    if (element instanceof PsiNamedElement) {
      PsiNamedElement namedElement = (PsiNamedElement) element;
      String name = EdlPresentationUtil.getName(namedElement, useFullName);
      if (name != null) return name;
    }
   
    return "";
  }
}
