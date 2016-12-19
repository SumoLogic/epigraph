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

package ws.epigraph.ideaplugin.schema.features;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ws.epigraph.schema.lexer.EdlElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlBraceMatcher implements PairedBraceMatcher {
  private static final BracePair[] pairs = new BracePair[]{
      new BracePair(S_CURLY_LEFT, S_CURLY_RIGHT, true),
      new BracePair(S_BRACKET_LEFT, S_BRACKET_RIGHT, false),
      new BracePair(S_ANGLE_LEFT, S_ANGLE_RIGHT, false),
      new BracePair(S_PAREN_LEFT, S_PAREN_RIGHT, false)
  };

  @Override
  public BracePair[] getPairs() {
    return pairs;
  }

  @Override
  public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
    return true; // TODO
  }

  @Override
  public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
    // TODO implement DeclarationRangeHandler's

    /*
    PsiElement element = file.findElementAt(openingBraceOffset);
    if (element == null || element instanceof PsiFile) return openingBraceOffset;

    PsiElement parent = element.getParent();
    if (parent == null) return openingBraceOffset;

    if (parent instanceof EdlVarTagDecl || parent instanceof EdlFieldDecl) {
      TextRange range = DeclarationRangeUtil.getDeclarationRange(parent);
      return range.getStartOffset();
    }

    PsiElement parent2 = parent.getParent();
    if (parent2 == null) return openingBraceOffset;

    if (parent2 instanceof EdlTypeDef) {
      TextRange range = DeclarationRangeUtil.getDeclarationRange(parent2);
      return range.getStartOffset();
    }
    */
    return openingBraceOffset;
  }
}
