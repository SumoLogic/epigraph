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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import ws.epigraph.ideaplugin.edl.brains.ModificationTrackerImpl;
import ws.epigraph.edl.parser.psi.*;
import ws.epigraph.ideaplugin.edl.index.EdlIndexUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class HierarchyCache {
  private final Key<ParameterizedCachedValue<List<EdlTypeDef>, EdlTypeDef>> TYPE_PARENTS_KEY = Key.create("TYPE_PARENTS");
  private final Key<ParameterizedCachedValue<List<EdlTypeDef>, EdlTypeDef>> DIRECT_TYPE_PARENTS_KEY = Key.create("DIRECT_TYPE_PARENTS");
  private final Key<ParameterizedCachedValue<List<EdlTypeDef>, EdlTypeDef>> TYPE_INHERITORS_KEY = Key.create("TYPE_INHERITORS");
  private final Key<ParameterizedCachedValue<List<EdlTypeDef>, EdlTypeDef>> DIRECT_TYPE_INHERITORS_KEY = Key.create("DIRECT_TYPE_INHERITORS");
  private final Key<ParameterizedCachedValue<List<EdlSupplementDef>, EdlTypeDef>> SUPPLEMENTS_BY_SUPPLEMENTED_KEY = Key.create("SUPPLEMENTS_BY_SUPPLEMENTED");

  private final Project project;
  private final ModificationTrackerImpl hierarchyModificationTracker = new ModificationTrackerImpl();

  @NotNull
  public static HierarchyCache getHierarchyCache(@NotNull Project project) {
    return project.getComponent(HierarchyCache.class);
  }

  public HierarchyCache(@NotNull Project project) {
    this.project = project;
    PsiManager.getInstance(project).addPsiTreeChangeListener(new InvalidationListener(), project);
  }

  /**
   * Builds transitive type parents, ordered by distance (closest first)
   */
  @NotNull
  public List<EdlTypeDef> getTypeParents(@NotNull EdlTypeDef typeDef) {
    CachedValuesManager cachedValuesManager = CachedValuesManager.getManager(project);
    return cachedValuesManager.getParameterizedCachedValue(
        typeDef,
        TYPE_PARENTS_KEY,
        new TypeParentsProvider(),
        false,
        typeDef
    );
  }

  @NotNull
  public List<EdlTypeDef> getDirectTypeParents(@NotNull EdlTypeDef typeDef) {
    CachedValuesManager cachedValuesManager = CachedValuesManager.getManager(project);
    return cachedValuesManager.getParameterizedCachedValue(
        typeDef,
        DIRECT_TYPE_PARENTS_KEY,
        new DirectTypeParentsProvider(),
        false,
        typeDef
    );
  }

  @NotNull
  public List<EdlTypeDef> getTypeInheritors(@NotNull EdlTypeDef typeDef) {
    CachedValuesManager cachedValuesManager = CachedValuesManager.getManager(project);
    return cachedValuesManager.getParameterizedCachedValue(
        typeDef,
        TYPE_INHERITORS_KEY,
        new TypeInheritorsProvider(),
        false,
        typeDef
    );
  }

  @NotNull
  public List<EdlSupplementDef> getSupplementsBySupplemented(@NotNull EdlTypeDef typeDef) {
    CachedValuesManager cachedValuesManager = CachedValuesManager.getManager(project);
    return cachedValuesManager.getParameterizedCachedValue(
        typeDef,
        SUPPLEMENTS_BY_SUPPLEMENTED_KEY,
        new SupplementsBySupplementedProvider(),
        false,
        typeDef
    );
  }

  @NotNull
  public List<EdlTypeDef> getDirectTypeInheritors(@NotNull EdlTypeDef typeDef) {
    CachedValuesManager cachedValuesManager = CachedValuesManager.getManager(project);
    return cachedValuesManager.getParameterizedCachedValue(
        typeDef,
        DIRECT_TYPE_INHERITORS_KEY,
        new DirectTypeInheritorsProvider(),
        false,
        typeDef
    );
  }

  private class TypeParentsProvider implements ParameterizedCachedValueProvider<List<EdlTypeDef>, EdlTypeDef> {
    @Nullable
    @Override
    public CachedValueProvider.Result<List<EdlTypeDef>> compute(EdlTypeDef typeDef) {
      Collection<EdlTypeDef> parents = EdlTypeParentsSearch.search(typeDef).findAll();
      return new CachedValueProvider.Result<>(
          new ArrayList<>(parents),
          hierarchyModificationTracker
      );
    }
  }

  private class DirectTypeParentsProvider implements ParameterizedCachedValueProvider<List<EdlTypeDef>, EdlTypeDef> {
    @Nullable
    @Override
    public CachedValueProvider.Result<List<EdlTypeDef>> compute(EdlTypeDef typeDef) {
      Collection<EdlTypeDef> parents = EdlDirectTypeParentsSearch.search(typeDef).findAll();
      return new CachedValueProvider.Result<>(
          new ArrayList<>(parents),
          hierarchyModificationTracker
      );
    }
  }

  private class TypeInheritorsProvider implements ParameterizedCachedValueProvider<List<EdlTypeDef>, EdlTypeDef> {
    @Nullable
    @Override
    public CachedValueProvider.Result<List<EdlTypeDef>> compute(EdlTypeDef typeDef) {
      Collection<EdlTypeDef> inheritors = EdlTypeInheritorsSearch.search(typeDef).findAll();
      return new CachedValueProvider.Result<>(
          new ArrayList<>(inheritors),
          hierarchyModificationTracker
      );
    }
  }

  private class DirectTypeInheritorsProvider implements ParameterizedCachedValueProvider<List<EdlTypeDef>, EdlTypeDef> {
    @Nullable
    @Override
    public CachedValueProvider.Result<List<EdlTypeDef>> compute(EdlTypeDef typeDef) {
      Collection<EdlTypeDef> inheritors = EdlDirectTypeInheritorsSearch.search(typeDef).findAll();
      return new CachedValueProvider.Result<>(
          new ArrayList<>(inheritors),
          hierarchyModificationTracker
      );
    }
  }

  private class SupplementsBySupplementedProvider implements ParameterizedCachedValueProvider<List<EdlSupplementDef>, EdlTypeDef> {
    @Nullable
    @Override
    public CachedValueProvider.Result<List<EdlSupplementDef>> compute(EdlTypeDef typeDef) {
      List<EdlSupplementDef> supplements = EdlIndexUtil.findSupplementsBySupplemented(project, typeDef);
      return new CachedValueProvider.Result<>(
          supplements,
          hierarchyModificationTracker
      );
    }
  }

  private class InvalidationListener extends PsiTreeChangeAdapter {
    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
      handle(event);
    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {
      handle(event);
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
      handle(event);
    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
      handle(event);
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
      handle(event);
    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent event) {
      handle(event);
    }

    private void handle(@NotNull PsiTreeChangeEvent event) {
      boolean invalidate = false;

      final PsiElement element = event.getElement();
      final PsiElement child = event.getChild();
      final PsiElement parent = child == null ? null : child.getParent();

      if (child instanceof PsiWhiteSpace) return;

      // imports changed
      if (PsiTreeUtil.getParentOfType(child, EdlImports.class) != null) {
        invalidate = true;
      }

      // types added/removed/replaced
      if (child instanceof EdlTypeDefWrapper || parent instanceof EdlDefs) {
        invalidate = true;
      }

      // supplements added/removed/replaced
      if (child instanceof EdlSupplementDef || parent instanceof EdlDefs) {
        invalidate = true;
      }

      // "extends" clause changed
      if ((element instanceof EdlExtendsDecl) || PsiTreeUtil.getParentOfType(child, EdlExtendsDecl.class) != null) {
        invalidate = true;
      }

      // "supplements" clause changed
      if ((element instanceof EdlSupplementDef) || PsiTreeUtil.getParentOfType(child, EdlSupplementDef.class) != null) {
        invalidate = true;
      }

      // file added/removed
      if (child instanceof EdlFile) {
        invalidate = true;
      }

      if (invalidate)
        hierarchyModificationTracker.tick();
    }
  }
}
