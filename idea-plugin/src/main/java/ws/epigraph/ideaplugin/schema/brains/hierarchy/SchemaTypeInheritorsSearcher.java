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

package ws.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.application.ReadActionProcessor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiAnchor;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.Stack;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaTypeInheritorsSearcher extends QueryExecutorBase<SchemaTypeDef, SchemaTypeInheritorsSearch.SearchParameters> {
  @Override
  public void processQuery(@NotNull SchemaTypeInheritorsSearch.SearchParameters parameters, @NotNull Processor<SchemaTypeDef> consumer) {
    SchemaTypeDef baseType = parameters.schemaTypeDef;

    ProgressIndicator progress = ProgressIndicatorProvider.getGlobalProgressIndicator();
    if (progress != null) {
      progress.pushState();

      String typeName = ApplicationManager.getApplication().runReadAction((Computable<String>) baseType::getName);
      progress.setText(typeName == null ?
          "Searching for inheritors" : "Searching for inheritors of " + typeName
      );
    }

    try {
      processInheritors(consumer, baseType, parameters);
    } finally {
      if (progress != null) progress.popState();
    }
  }

  private static void processInheritors(@NotNull Processor<SchemaTypeDef> consumer,
                                        @NotNull SchemaTypeDef baseType,
                                        @NotNull SchemaTypeInheritorsSearch.SearchParameters parameters) {

    // see JavaClassInheritorsSearcher

//    final Project project = PsiUtilCore.getProjectInReadAction(baseType);
    final Ref<SchemaTypeDef> currentBase = Ref.create();
    final Stack<PsiAnchor> stack = new Stack<>();
    final Set<PsiAnchor> processed = ContainerUtil.newTroveSet();

    final Processor<SchemaTypeDef> processor = new ReadActionProcessor<SchemaTypeDef>() {
      @Override
      public boolean processInReadAction(SchemaTypeDef inheritor) {
        if (!consumer.process(inheritor)) return false;
        stack.push(PsiAnchor.create(inheritor));
        return true;
      }
    };

    ApplicationManager.getApplication().runReadAction(() -> {
      stack.push(PsiAnchor.create(baseType));
    });

    // DFS
    while (!stack.isEmpty()) {
      ProgressManager.checkCanceled();

      final PsiAnchor anchor = stack.pop();
      if (!processed.add(anchor)) continue;

      SchemaTypeDef typeDef = ApplicationManager.getApplication().runReadAction(
          (Computable<SchemaTypeDef>) () -> (SchemaTypeDef) anchor.retrieve()
      );
      if (typeDef == null) continue;

      currentBase.set(typeDef);
      if (!SchemaDirectTypeInheritorsSearch.search(typeDef).forEach(processor))
        return;
    }
  }
}
