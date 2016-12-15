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

package ws.epigraph.ideaplugin.edl.brains.hierarchy;

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
import com.intellij.util.containers.Queue;
import ws.epigraph.edl.parser.psi.EdlTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlTypeParentsSearcher extends QueryExecutorBase<EdlTypeDef, EdlTypeParentsSearch.SearchParameters> {
  @Override
  public void processQuery(@NotNull EdlTypeParentsSearch.SearchParameters parameters, @NotNull Processor<EdlTypeDef> consumer) {
    EdlTypeDef baseType = parameters.edlTypeDef;

    ProgressIndicator progress = ProgressIndicatorProvider.getGlobalProgressIndicator();
    if (progress != null) {
      progress.pushState();

      String typeName = ApplicationManager.getApplication().runReadAction((Computable<String>) baseType::getName);
      progress.setText(typeName == null ?
          "Searching for parents" : "Searching for parents of " + typeName
      );
    }

    try {
      processParents(consumer, baseType, parameters);
    } finally {
      if (progress != null) progress.popState();
    }
  }

  private static void processParents(@NotNull Processor<EdlTypeDef> consumer,
                                     @NotNull EdlTypeDef baseType,
                                     @NotNull EdlTypeParentsSearch.SearchParameters parameters) {

    final Ref<EdlTypeDef> currentBase = Ref.create();
    final Queue<PsiAnchor> queue = new Queue<>(10);
    final Set<PsiAnchor> processed = ContainerUtil.newTroveSet();

    final Processor<EdlTypeDef> processor = new ReadActionProcessor<EdlTypeDef>() {
      @Override
      public boolean processInReadAction(EdlTypeDef inheritor) {
        if (!consumer.process(inheritor)) return false;
        if (inheritor == null) return false;
        queue.addLast(PsiAnchor.create(inheritor));
        return true;
      }
    };

    // seed
    ApplicationManager.getApplication().runReadAction(() -> {
      queue.addLast(PsiAnchor.create(baseType));
    });

    // BFS
    while (!queue.isEmpty()) {
      ProgressManager.checkCanceled();

      final PsiAnchor anchor = queue.pullFirst();
      if (!processed.add(anchor)) continue;

      EdlTypeDef typeDef = ApplicationManager.getApplication().runReadAction(
          (Computable<EdlTypeDef>) () -> (EdlTypeDef) anchor.retrieve()
      );
      if (typeDef == null) continue;

      currentBase.set(typeDef);
      if (!EdlDirectTypeParentsSearch.search(typeDef).forEach(processor))
        return;
    }
  }
}
