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

import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewShortNameLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import ws.epigraph.schema.parser.psi.SchemaEntityTagRef;
import ws.epigraph.schema.parser.psi.SchemaQnSegment;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaElementDescriptionProvider implements ElementDescriptionProvider {
  @Nullable
  @Override
  public String getElementDescription(@NotNull PsiElement element, @NotNull ElementDescriptionLocation location) {
    // https://intellij-support.jetbrains.com/hc/en-us/community/posts/206765785-Custom-name-for-Find-Usages-of-symbol
    if (location instanceof UsageViewLongNameLocation ||
        location instanceof UsageViewShortNameLocation) {

      if (element instanceof SchemaTypeDef) {
        SchemaTypeDef schemaTypeDef = (SchemaTypeDef) element;
        return SchemaPresentationUtil.getName(schemaTypeDef, true);
      }

      if (element instanceof SchemaQnSegment) {
        SchemaQnSegment fqnSegment = (SchemaQnSegment) element;
        return fqnSegment.getQn().toString();
      }

      if (element instanceof SchemaEntityTagRef) {
        SchemaEntityTagRef tagRef = (SchemaEntityTagRef) element;
        return tagRef.getQid().getText();
      }
    }

    return null;
  }
}
