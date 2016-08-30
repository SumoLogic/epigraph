package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.sumologic.epigraph.ideaplugin.schema.brains.ModificationTrackerImpl;
import io.epigraph.schema.parser.psi.*;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class HierarchyCache {
  private final Key<ParameterizedCachedValue<List<SchemaTypeDef>, SchemaTypeDef>> TYPE_PARENTS_KEY = Key.create("TYPE_PARENTS");
  private final Key<ParameterizedCachedValue<List<SchemaTypeDef>, SchemaTypeDef>> DIRECT_TYPE_PARENTS_KEY = Key.create("DIRECT_TYPE_PARENTS");
  private final Key<ParameterizedCachedValue<List<SchemaTypeDef>, SchemaTypeDef>> TYPE_INHERITORS_KEY = Key.create("TYPE_INHERITORS");
  private final Key<ParameterizedCachedValue<List<SchemaTypeDef>, SchemaTypeDef>> DIRECT_TYPE_INHERITORS_KEY = Key.create("DIRECT_TYPE_INHERITORS");
  private final Key<ParameterizedCachedValue<List<SchemaSupplementDef>, SchemaTypeDef>> SUPPLEMENTS_BY_SUPPLEMENTED_KEY = Key.create("SUPPLEMENTS_BY_SUPPLEMENTED");

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
  public List<SchemaTypeDef> getTypeParents(@NotNull SchemaTypeDef typeDef) {
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
  public List<SchemaTypeDef> getDirectTypeParents(@NotNull SchemaTypeDef typeDef) {
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
  public List<SchemaTypeDef> getTypeInheritors(@NotNull SchemaTypeDef typeDef) {
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
  public List<SchemaSupplementDef> getSupplementsBySupplemented(@NotNull SchemaTypeDef typeDef) {
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
  public List<SchemaTypeDef> getDirectTypeInheritors(@NotNull SchemaTypeDef typeDef) {
    CachedValuesManager cachedValuesManager = CachedValuesManager.getManager(project);
    return cachedValuesManager.getParameterizedCachedValue(
        typeDef,
        DIRECT_TYPE_INHERITORS_KEY,
        new DirectTypeInheritorsProvider(),
        false,
        typeDef
    );
  }

  private class TypeParentsProvider implements ParameterizedCachedValueProvider<List<SchemaTypeDef>, SchemaTypeDef> {
    @Nullable
    @Override
    public CachedValueProvider.Result<List<SchemaTypeDef>> compute(SchemaTypeDef typeDef) {
      Collection<SchemaTypeDef> parents = SchemaTypeParentsSearch.search(typeDef).findAll();
      return new CachedValueProvider.Result<>(
          new ArrayList<>(parents),
          hierarchyModificationTracker
      );
    }
  }

  private class DirectTypeParentsProvider implements ParameterizedCachedValueProvider<List<SchemaTypeDef>, SchemaTypeDef> {
    @Nullable
    @Override
    public CachedValueProvider.Result<List<SchemaTypeDef>> compute(SchemaTypeDef typeDef) {
      Collection<SchemaTypeDef> parents = SchemaDirectTypeParentsSearch.search(typeDef).findAll();
      return new CachedValueProvider.Result<>(
          new ArrayList<>(parents),
          hierarchyModificationTracker
      );
    }
  }

  private class TypeInheritorsProvider implements ParameterizedCachedValueProvider<List<SchemaTypeDef>, SchemaTypeDef> {
    @Nullable
    @Override
    public CachedValueProvider.Result<List<SchemaTypeDef>> compute(SchemaTypeDef typeDef) {
      Collection<SchemaTypeDef> inheritors = SchemaTypeInheritorsSearch.search(typeDef).findAll();
      return new CachedValueProvider.Result<>(
          new ArrayList<>(inheritors),
          hierarchyModificationTracker
      );
    }
  }

  private class DirectTypeInheritorsProvider implements ParameterizedCachedValueProvider<List<SchemaTypeDef>, SchemaTypeDef> {
    @Nullable
    @Override
    public CachedValueProvider.Result<List<SchemaTypeDef>> compute(SchemaTypeDef typeDef) {
      Collection<SchemaTypeDef> inheritors = SchemaDirectTypeInheritorsSearch.search(typeDef).findAll();
      return new CachedValueProvider.Result<>(
          new ArrayList<>(inheritors),
          hierarchyModificationTracker
      );
    }
  }

  private class SupplementsBySupplementedProvider implements ParameterizedCachedValueProvider<List<SchemaSupplementDef>, SchemaTypeDef> {
    @Nullable
    @Override
    public CachedValueProvider.Result<List<SchemaSupplementDef>> compute(SchemaTypeDef typeDef) {
      List<SchemaSupplementDef> supplements = SchemaIndexUtil.findSupplementsBySupplemented(project, typeDef);
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
      if (PsiTreeUtil.getParentOfType(child, SchemaImports.class) != null) {
        invalidate = true;
      }

      // types added/removed/replaced
      if (child instanceof SchemaTypeDefWrapper || parent instanceof SchemaDefs) {
        invalidate = true;
      }

      // supplements added/removed/replaced
      if (child instanceof SchemaSupplementDef || parent instanceof SchemaDefs) {
        invalidate = true;
      }

      // "extends" clause changed
      if ((element instanceof SchemaExtendsDecl) || PsiTreeUtil.getParentOfType(child, SchemaExtendsDecl.class) != null) {
        invalidate = true;
      }

      // "supplements" clause changed
      if ((element instanceof SchemaSupplementDef) || PsiTreeUtil.getParentOfType(child, SchemaSupplementDef.class) != null) {
        invalidate = true;
      }

      // file added/removed
      if (child instanceof SchemaFile) {
        invalidate = true;
      }

      if (invalidate)
        hierarchyModificationTracker.tick();
    }
  }
}
