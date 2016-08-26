package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

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
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaTypeParentsSearcher extends QueryExecutorBase<EpigraphTypeDef, SchemaTypeParentsSearch.SearchParameters> {
  @Override
  public void processQuery(@NotNull SchemaTypeParentsSearch.SearchParameters parameters, @NotNull Processor<EpigraphTypeDef> consumer) {
    EpigraphTypeDef baseType = parameters.epigraphTypeDef;

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

  private static void processParents(@NotNull Processor<EpigraphTypeDef> consumer,
                                     @NotNull EpigraphTypeDef baseType,
                                     @NotNull SchemaTypeParentsSearch.SearchParameters parameters) {

    final Ref<EpigraphTypeDef> currentBase = Ref.create();
    final Queue<PsiAnchor> queue = new Queue<>(10);
    final Set<PsiAnchor> processed = ContainerUtil.newTroveSet();

    final Processor<EpigraphTypeDef> processor = new ReadActionProcessor<EpigraphTypeDef>() {
      @Override
      public boolean processInReadAction(EpigraphTypeDef inheritor) {
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

      EpigraphTypeDef typeDef = ApplicationManager.getApplication().runReadAction(
          (Computable<EpigraphTypeDef>) () -> (EpigraphTypeDef) anchor.retrieve()
      );
      if (typeDef == null) continue;

      currentBase.set(typeDef);
      if (!SchemaDirectTypeParentsSearch.search(typeDef).forEach(processor))
        return;
    }
  }
}
