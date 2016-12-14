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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import ws.epigraph.edl.parser.psi.SchemaFile;
import ws.epigraph.edl.parser.psi.SchemaQnSegment;
import ws.epigraph.edl.parser.psi.SchemaImportStatement;
import ws.epigraph.edl.parser.psi.SchemaVarTagRef;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaUsageTypeProvider implements UsageTypeProvider {
  @Nullable
  @Override
  public UsageType getUsageType(PsiElement element) {
    final PsiFile psiFile = element.getContainingFile();

    if (!(psiFile instanceof SchemaFile)) return null;

    if (element instanceof SchemaQnSegment) {
      if (PsiTreeUtil.getParentOfType(element, SchemaImportStatement.class) != null) {
        return IMPORT_USAGE_TYPE;
      }
      return TYPE_REF_USAGE_TYPE; // be more precise: extends, list getElement type etc ?
    }

    if (PsiTreeUtil.getParentOfType(element, SchemaVarTagRef.class) != null)
      return VAR_TAG_USAGE_TYPE;

    return null;
  }

  private static final UsageType TYPE_REF_USAGE_TYPE = new UsageType("Type reference");
  private static final UsageType IMPORT_USAGE_TYPE = new UsageType("Import statement");
  private static final UsageType VAR_TAG_USAGE_TYPE = new UsageType("Default override");
}
