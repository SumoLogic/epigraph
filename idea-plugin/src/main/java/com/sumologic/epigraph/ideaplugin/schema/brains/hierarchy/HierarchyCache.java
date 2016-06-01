package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class HierarchyCache {
  private final HierarchyModificationTracker hierarchyModificationTracker = new HierarchyModificationTracker();
  private final ParameterizedCachedValue<List<SchemaTypeDef>, SchemaTypeDef> supertypesCachedValue;

  @NotNull
  public static HierarchyCache getHierarchyCache(@NotNull Project project) {
    return project.getComponent(HierarchyCache.class);
  }

  public HierarchyCache(@NotNull Project project) {
    PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {

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

        if (invalidate)
          hierarchyModificationTracker.tick();

      }
    }, project);

    CachedValuesManager cachedValuesManager = CachedValuesManager.getManager(project);
    supertypesCachedValue = cachedValuesManager.createParameterizedCachedValue(new SupertypesHierarchyProvider(), false);
  }

  @NotNull
  public List<SchemaTypeDef> getTransitiveSupertypes(@NotNull SchemaTypeDef typeDef) {
    return supertypesCachedValue.getValue(typeDef);
  }

  private class SupertypesHierarchyProvider implements ParameterizedCachedValueProvider<List<SchemaTypeDef>, SchemaTypeDef> {

    @Nullable
    @Override
    public CachedValueProvider.Result<List<SchemaTypeDef>> compute(SchemaTypeDef typeDef) {
      // TODO implement
      return null;
    }
  }

  private static class HierarchyModificationTracker implements ModificationTracker {
    private int modificationCount;

    protected void tick() {
      modificationCount++;
    }

    @Override
    public long getModificationCount() {
      return modificationCount;
    }
  }

}
