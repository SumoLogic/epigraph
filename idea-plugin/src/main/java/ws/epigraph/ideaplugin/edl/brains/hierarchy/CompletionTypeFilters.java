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

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import ws.epigraph.ideaplugin.edl.psi.EdlPsiUtil;
import ws.epigraph.edl.parser.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static ws.epigraph.edl.lexer.EdlElementTypes.S_WITH;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class CompletionTypeFilters {
  // TODO check for correct collection types, e.g. List[Foo] can't extend List[Bar] unless Foo extends Bar

  private static final List<CompletionTypeFilter> FILTERS = Arrays.asList(
      new SameTypeExtendsFilter(),
      new SameKindExtendsFilter(),
      new TypeAlreadyExtendedFilter(),
      new WrongPrimitiveKindExtendsFilter(),

      new SameTypeSupplementsFilter(),
      new SameKindSupplementsFilter(),
      new TypeAlreadySupplementedFilter(),
      new WrongPrimitiveKindSupplementsFilter(),

      new SameTypeSupplementTargetFilter(),
      new SameKindSupplementTargetFilter(),
      new TypeAlreadySupplementedTargetFilter(),
      new WrongPrimitiveKindSupplementTargetFilter(),

      new SameTypeSupplementSourceFilter(),
      new SameKindSupplementSourceFilter(),
      new TypeAlreadySupplementedSourceFilter(),
      new WrongPrimitiveKindSupplementSourceFilter()
  );

  @NotNull
  public static Predicate<EdlTypeDef> combined(@NotNull PsiElement element) {
    return typeDef -> {
      for (CompletionTypeFilter filter : FILTERS) if (!filter.include(typeDef, element)) return false;
      return true;
    };
  }

  interface CompletionTypeFilter {
    /**
     * Checks if particular {@code typeDef} should stay in the completion variants
     *
     * @param typeDef type def to check
     * @param element element completion was invoked on
     * @return {@code true} iff {@code typeDef} should stay included
     */
    boolean include(@NotNull EdlTypeDef typeDef, @NotNull PsiElement element);
  }

  interface ExtendsCompletionFilter extends CompletionTypeFilter {
    @Override
    default boolean include(@NotNull EdlTypeDef typeDef, @NotNull PsiElement element) {
      EdlTypeDef host = PsiTreeUtil.getParentOfType(element, EdlTypeDef.class);
      if (host == null) return true;

      EdlExtendsDecl extendsDecl = PsiTreeUtil.getParentOfType(element, EdlExtendsDecl.class);
      if (extendsDecl == null) return true;

      return include(typeDef, host, extendsDecl);
    }

    boolean include(@NotNull EdlTypeDef typeDef, @NotNull EdlTypeDef host, @NotNull EdlExtendsDecl extendsDecl);
  }

  interface SupplementsCompletionFilter extends CompletionTypeFilter {
    @Override
    default boolean include(@NotNull EdlTypeDef typeDef, @NotNull PsiElement element) {
      EdlTypeDef host = PsiTreeUtil.getParentOfType(element, EdlTypeDef.class);
      if (host == null) return true;

      EdlSupplementsDecl supplementsDecl = PsiTreeUtil.getParentOfType(element, EdlSupplementsDecl.class);
      if (supplementsDecl == null) return true;

      return include(typeDef, host, supplementsDecl);
    }

    boolean include(@NotNull EdlTypeDef typeDef, @NotNull EdlTypeDef host, @NotNull EdlSupplementsDecl supplementsDecl);
  }

  interface SupplementTargetCompletionFilter extends CompletionTypeFilter {
    @Override
    default boolean include(@NotNull EdlTypeDef typeDef, @NotNull PsiElement element) {
      EdlSupplementDef host = PsiTreeUtil.getParentOfType(element, EdlSupplementDef.class);
      if (host == null) return true;
      if (EdlPsiUtil.hasPrevSibling(element.getParent().getParent(), S_WITH)) return true; // we're completing source

      return includeInTarget(typeDef, host);
    }

    boolean includeInTarget(@NotNull EdlTypeDef typeDef, @NotNull EdlSupplementDef host);
  }

  interface SupplementSourceCompletionFilter extends CompletionTypeFilter {
    @Override
    default boolean include(@NotNull EdlTypeDef typeDef, @NotNull PsiElement element) {
      EdlSupplementDef host = PsiTreeUtil.getParentOfType(element, EdlSupplementDef.class);
      if (host == null) return true;
      if (!EdlPsiUtil.hasPrevSibling(element.getParent().getParent(), S_WITH))
        return true; // we're completing target

      return includeInSource(typeDef, host);
    }

    boolean includeInSource(@NotNull EdlTypeDef typeDef, @NotNull EdlSupplementDef host);
  }

  // ---------------------- common

  private static abstract class SameTypeFilterBase {
    private boolean notSameType(@NotNull EdlTypeDef typeDef, @NotNull EdlTypeDef host) {
      return !host.equals(typeDef);
    }

    public boolean include(@NotNull EdlTypeDef typeDef, @NotNull EdlTypeDef host, @NotNull EdlExtendsDecl extendsDecl) {
      return notSameType(typeDef, host);
    }

    public boolean include(@NotNull EdlTypeDef typeDef, @NotNull EdlTypeDef host, @NotNull EdlSupplementsDecl supplementsDecl) {
      return notSameType(typeDef, host);
    }

    private boolean includeInSupplement(@NotNull EdlTypeDef typeDef, @NotNull EdlSupplementDef host, boolean checkSource) {
      if (checkSource && typeDef.equals(host.source())) return false;

      for (EdlQnTypeRef targetRef : host.supplementedRefs()) {
        EdlTypeDef target = targetRef.resolve();
        if (target != null && typeDef.equals(target)) return false;
      }

      return true;
    }

    public boolean includeInTarget(@NotNull EdlTypeDef typeDef, @NotNull EdlSupplementDef host) {
      return includeInSupplement(typeDef, host, true);
    }

    public boolean includeInSource(@NotNull EdlTypeDef typeDef, @NotNull EdlSupplementDef host) {
      return includeInSupplement(typeDef, host, false);
    }
  }

  private static abstract class SameKindFilterBase {
    public boolean include(@NotNull EdlTypeDef typeDef, @NotNull EdlTypeDef host, @NotNull EdlExtendsDecl extendsDecl) {
      return isSameKind(typeDef, host);
    }

    public boolean include(@NotNull EdlTypeDef typeDef, @NotNull EdlTypeDef host, @NotNull EdlSupplementsDecl supplementsDecl) {
      return isSameKind(typeDef, host);
    }

    private boolean isSameKind(@NotNull EdlTypeDef typeDef, @NotNull EdlTypeDef host) {
      return typeDef.getKind() == host.getKind();
    }

    private boolean includeInSupplement(@NotNull EdlTypeDef typeDef, @NotNull EdlSupplementDef host, boolean checkSource) {
      if (checkSource) {
        EdlTypeDef source = host.source();
        if (source != null && typeDef.getKind() != source.getKind()) return false;
      }

      for (EdlQnTypeRef targetRef : host.supplementedRefs()) {
        EdlTypeDef target = targetRef.resolve();
        if (target != null && typeDef.getKind() != target.getKind()) return false;
      }

      return true;
    }

    public boolean includeInTarget(@NotNull EdlTypeDef typeDef, @NotNull EdlSupplementDef host) {
      return includeInSupplement(typeDef, host, true);
    }

    public boolean includeInSource(@NotNull EdlTypeDef typeDef, @NotNull EdlSupplementDef host) {
      return includeInSupplement(typeDef, host, false);
    }
  }

  private abstract static class WrongPrimitiveKindFilterBase {
    public boolean include(@NotNull EdlTypeDef typeDef, @NotNull EdlTypeDef host, @NotNull EdlExtendsDecl extendsDecl) {
      return isSamePrimitiveKind(typeDef, host);
    }

    public boolean include(@NotNull EdlTypeDef typeDef, @NotNull EdlTypeDef host, @NotNull EdlSupplementsDecl supplementsDecl) {
      return isSamePrimitiveKind(typeDef, host);
    }

    private boolean isSamePrimitiveKind(@NotNull EdlTypeDef typeDef, @NotNull EdlTypeDef host) {
      if (host.getKind() != TypeKind.PRIMITIVE) return true;
      if (typeDef.getKind() != TypeKind.PRIMITIVE) return false;

      return ((EdlPrimitiveTypeDef) host).getPrimitiveTypeKind() ==
          ((EdlPrimitiveTypeDef) typeDef).getPrimitiveTypeKind();
    }

    private boolean includeInSupplement(@NotNull EdlTypeDef typeDef, @NotNull EdlSupplementDef host, boolean checkSource) {
      if (typeDef.getKind() != TypeKind.PRIMITIVE) return true;
      PrimitiveTypeKind primitiveTypeKind = ((EdlPrimitiveTypeDef) typeDef).getPrimitiveTypeKind();

      if (checkSource) {
        EdlTypeDef source = host.source();
        if (source != null) {
          if (source.getKind() != TypeKind.PRIMITIVE) return false;
          if (((EdlPrimitiveTypeDef) source).getPrimitiveTypeKind() != primitiveTypeKind) return false;
        }
      }

      for (EdlQnTypeRef targetRef : host.supplementedRefs()) {
        EdlTypeDef target = targetRef.resolve();
        if (target instanceof EdlPrimitiveTypeDef) {
          EdlPrimitiveTypeDef primitiveTarget = (EdlPrimitiveTypeDef) target;
          if (primitiveTarget.getPrimitiveTypeKind() != primitiveTypeKind) return false;
        }
      }

      return true;
    }

    public boolean includeInTarget(@NotNull EdlTypeDef typeDef, @NotNull EdlSupplementDef host) {
      return includeInSupplement(typeDef, host, true);
    }

    public boolean includeInSource(@NotNull EdlTypeDef typeDef, @NotNull EdlSupplementDef host) {
      return includeInSupplement(typeDef, host, false);
    }
  }

  // ---------------------- extends clause

  private static class SameTypeExtendsFilter extends SameTypeFilterBase implements ExtendsCompletionFilter {}

  private static class SameKindExtendsFilter extends SameKindFilterBase implements ExtendsCompletionFilter {}

  private static class WrongPrimitiveKindExtendsFilter extends WrongPrimitiveKindFilterBase implements ExtendsCompletionFilter {}

  private static class TypeAlreadyExtendedFilter implements ExtendsCompletionFilter {
    @Override
    public boolean include(@NotNull EdlTypeDef typeDef, @NotNull EdlTypeDef host, @NotNull EdlExtendsDecl extendsDecl) {
      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(host.getProject());

      for (EdlQnTypeRef qnTypeRef : extendsDecl.getQnTypeRefList()) {
        EdlTypeDef parent = qnTypeRef.resolve();
        if (parent != null) {
          if (parent.equals(typeDef) || hierarchyCache.getTypeParents(parent).contains(typeDef)) return false;
        }
      }

      return true;
    }
  }

  // ---------------------- supplements clause

  private static class SameKindSupplementsFilter extends SameKindFilterBase implements SupplementsCompletionFilter {}

  private static class SameTypeSupplementsFilter extends SameTypeFilterBase implements SupplementsCompletionFilter {}

  private static class WrongPrimitiveKindSupplementsFilter extends WrongPrimitiveKindFilterBase implements SupplementsCompletionFilter {}

  private static class TypeAlreadySupplementedFilter implements SupplementsCompletionFilter {
    @Override
    public boolean include(@NotNull EdlTypeDef typeDef, @NotNull EdlTypeDef host, @NotNull EdlSupplementsDecl supplementsDecl) {
      List<EdlQnTypeRef> supplementsList = supplementsDecl.getQnTypeRefList();
      if (supplementsList.isEmpty()) return true;

      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(host.getProject());
      List<EdlTypeDef> typeParents = hierarchyCache.getTypeParents(typeDef);

      for (EdlQnTypeRef qnTypeRef : supplementsList) {
        EdlTypeDef child = qnTypeRef.resolve();
        if (child != null && child.equals(typeDef) || typeParents.contains(child)) return false;
      }

      return true;
    }
  }

  // ---------------------- supplement target

  private static class SameTypeSupplementTargetFilter extends SameTypeFilterBase implements SupplementTargetCompletionFilter {}

  private static class SameKindSupplementTargetFilter extends SameKindFilterBase implements SupplementTargetCompletionFilter {}

  private static class WrongPrimitiveKindSupplementTargetFilter extends WrongPrimitiveKindFilterBase implements SupplementTargetCompletionFilter {}

  private static class TypeAlreadySupplementedTargetFilter implements SupplementTargetCompletionFilter {
    @Override
    public boolean includeInTarget(@NotNull EdlTypeDef typeDef, @NotNull EdlSupplementDef host) {
      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(host.getProject());

      EdlTypeDef source = host.source();
      // if candidate is a parent of source then we have a circular inheritance
      if (source != null && hierarchyCache.getTypeParents(source).contains(typeDef)) return false;

      List<EdlTypeDef> typeParents = hierarchyCache.getTypeParents(typeDef);

      // if candidate is a child if source then it's a useless supplement
      if (source != null && typeParents.contains(source)) return false;

      for (EdlQnTypeRef supplementedTypeRef : host.supplementedRefs()) {
        EdlTypeDef supplemented = supplementedTypeRef.resolve();
        if (supplemented != null && supplemented.equals(typeDef) || typeParents.contains(supplemented)) return false;
      }

      return true;
    }
  }

  // ---------------------- supplement source
  
  private static class SameTypeSupplementSourceFilter extends SameTypeFilterBase implements SupplementSourceCompletionFilter {}

  private static class SameKindSupplementSourceFilter extends SameKindFilterBase implements SupplementSourceCompletionFilter {}

  private static class WrongPrimitiveKindSupplementSourceFilter extends WrongPrimitiveKindFilterBase implements SupplementSourceCompletionFilter {}

  private static class TypeAlreadySupplementedSourceFilter implements SupplementSourceCompletionFilter {
    @Override
    public boolean includeInSource(@NotNull EdlTypeDef typeDef, @NotNull EdlSupplementDef host) {
      List<EdlTypeDef> supplementedList = host.supplemented();
      if (supplementedList.isEmpty()) return true;

      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(host.getProject());
      List<EdlTypeDef> typeParents = hierarchyCache.getTypeParents(typeDef);

      boolean allTargetsExtendSource = true;
      for (EdlTypeDef supplemented : supplementedList) {
        if (supplemented != null) {
          if (supplemented.equals(typeDef)) return false; // don't supplement self
          if (typeParents.contains(supplemented)) return false; // circular inheritance
          if (!hierarchyCache.getTypeParents(supplemented).contains(typeDef)) allTargetsExtendSource = false;
        }
      }

      return !allTargetsExtendSource;
    }
  }
}
