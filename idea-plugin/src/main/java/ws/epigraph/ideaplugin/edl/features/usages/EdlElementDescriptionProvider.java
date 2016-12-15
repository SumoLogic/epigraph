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

import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewShortNameLocation;
import ws.epigraph.ideaplugin.edl.presentation.EdlPresentationUtil;
import ws.epigraph.edl.parser.psi.EdlQnSegment;
import ws.epigraph.edl.parser.psi.EdlTypeDef;
import ws.epigraph.edl.parser.psi.EdlVarTagRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlElementDescriptionProvider implements ElementDescriptionProvider {
  @Nullable
  @Override
  public String getElementDescription(@NotNull PsiElement element, @NotNull ElementDescriptionLocation location) {
    // https://intellij-support.jetbrains.com/hc/en-us/community/posts/206765785-Custom-name-for-Find-Usages-of-symbol
    if (location instanceof UsageViewLongNameLocation ||
        location instanceof UsageViewShortNameLocation) {

      if (element instanceof EdlTypeDef) {
        EdlTypeDef edlTypeDef = (EdlTypeDef) element;
        return EdlPresentationUtil.getName(edlTypeDef, true);
      }

      if (element instanceof EdlQnSegment) {
        EdlQnSegment fqnSegment = (EdlQnSegment) element;
        return fqnSegment.getQn().toString();
      }

      if (element instanceof EdlVarTagRef) {
        EdlVarTagRef tagRef = (EdlVarTagRef) element;
        return tagRef.getQid().getText();
      }
    }

    return null;
  }
}
