package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaPsiUtil;
import io.epigraph.lang.parser.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static io.epigraph.lang.lexer.EpigraphElementTypes.E_WITH;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
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
  public static Predicate<EpigraphTypeDef> combined(@NotNull PsiElement element) {
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
    boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull PsiElement element);
  }

  interface ExtendsCompletionFilter extends CompletionTypeFilter {
    @Override
    default boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull PsiElement element) {
      EpigraphTypeDef host = PsiTreeUtil.getParentOfType(element, EpigraphTypeDef.class);
      if (host == null) return true;

      SchemaExtendsDecl extendsDecl = PsiTreeUtil.getParentOfType(element, SchemaExtendsDecl.class);
      if (extendsDecl == null) return true;

      return include(typeDef, host, extendsDecl);
    }

    boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphTypeDef host, @NotNull SchemaExtendsDecl extendsDecl);
  }

  interface SupplementsCompletionFilter extends CompletionTypeFilter {
    @Override
    default boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull PsiElement element) {
      EpigraphTypeDef host = PsiTreeUtil.getParentOfType(element, EpigraphTypeDef.class);
      if (host == null) return true;

      SchemaSupplementsDecl supplementsDecl = PsiTreeUtil.getParentOfType(element, SchemaSupplementsDecl.class);
      if (supplementsDecl == null) return true;

      return include(typeDef, host, supplementsDecl);
    }

    boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphTypeDef host, @NotNull SchemaSupplementsDecl supplementsDecl);
  }

  interface SupplementTargetCompletionFilter extends CompletionTypeFilter {
    @Override
    default boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull PsiElement element) {
      SchemaSupplementDef host = PsiTreeUtil.getParentOfType(element, SchemaSupplementDef.class);
      if (host == null) return true;
      if (SchemaPsiUtil.hasPrevSibling(element.getParent().getParent(), E_WITH)) return true; // we're completing source

      return includeInTarget(typeDef, host);
    }

    boolean includeInTarget(@NotNull EpigraphTypeDef typeDef, @NotNull SchemaSupplementDef host);
  }

  interface SupplementSourceCompletionFilter extends CompletionTypeFilter {
    @Override
    default boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull PsiElement element) {
      SchemaSupplementDef host = PsiTreeUtil.getParentOfType(element, SchemaSupplementDef.class);
      if (host == null) return true;
      if (!SchemaPsiUtil.hasPrevSibling(element.getParent().getParent(), E_WITH))
        return true; // we're completing target

      return includeInSource(typeDef, host);
    }

    boolean includeInSource(@NotNull EpigraphTypeDef typeDef, @NotNull SchemaSupplementDef host);
  }

  // ---------------------- common

  private static abstract class SameTypeFilterBase {
    private boolean notSameType(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphTypeDef host) {
      return !host.equals(typeDef);
    }

    public boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphTypeDef host, @NotNull SchemaExtendsDecl extendsDecl) {
      return notSameType(typeDef, host);
    }

    public boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphTypeDef host, @NotNull SchemaSupplementsDecl supplementsDecl) {
      return notSameType(typeDef, host);
    }

    private boolean includeInSupplement(@NotNull EpigraphTypeDef typeDef, @NotNull SchemaSupplementDef host, boolean checkSource) {
      if (checkSource && typeDef.equals(host.source())) return false;

      for (SchemaFqnTypeRef targetRef : host.supplementedRefs()) {
        EpigraphTypeDef target = targetRef.resolve();
        if (target != null && typeDef.equals(target)) return false;
      }

      return true;
    }

    public boolean includeInTarget(@NotNull EpigraphTypeDef typeDef, @NotNull SchemaSupplementDef host) {
      return includeInSupplement(typeDef, host, true);
    }

    public boolean includeInSource(@NotNull EpigraphTypeDef typeDef, @NotNull SchemaSupplementDef host) {
      return includeInSupplement(typeDef, host, false);
    }
  }

  private static abstract class SameKindFilterBase {
    public boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphTypeDef host, @NotNull SchemaExtendsDecl extendsDecl) {
      return isSameKind(typeDef, host);
    }

    public boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphTypeDef host, @NotNull SchemaSupplementsDecl supplementsDecl) {
      return isSameKind(typeDef, host);
    }

    private boolean isSameKind(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphTypeDef host) {
      return typeDef.getKind() == host.getKind();
    }

    private boolean includeInSupplement(@NotNull EpigraphTypeDef typeDef, @NotNull SchemaSupplementDef host, boolean checkSource) {
      if (checkSource) {
        EpigraphTypeDef source = host.source();
        if (source != null && typeDef.getKind() != source.getKind()) return false;
      }

      for (SchemaFqnTypeRef targetRef : host.supplementedRefs()) {
        EpigraphTypeDef target = targetRef.resolve();
        if (target != null && typeDef.getKind() != target.getKind()) return false;
      }

      return true;
    }

    public boolean includeInTarget(@NotNull EpigraphTypeDef typeDef, @NotNull SchemaSupplementDef host) {
      return includeInSupplement(typeDef, host, true);
    }

    public boolean includeInSource(@NotNull EpigraphTypeDef typeDef, @NotNull SchemaSupplementDef host) {
      return includeInSupplement(typeDef, host, false);
    }
  }

  private abstract static class WrongPrimitiveKindFilterBase {
    public boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphTypeDef host, @NotNull SchemaExtendsDecl extendsDecl) {
      return isSamePrimitiveKind(typeDef, host);
    }

    public boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphTypeDef host, @NotNull SchemaSupplementsDecl supplementsDecl) {
      return isSamePrimitiveKind(typeDef, host);
    }

    private boolean isSamePrimitiveKind(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphTypeDef host) {
      if (host.getKind() != TypeKind.PRIMITIVE) return true;
      if (typeDef.getKind() != TypeKind.PRIMITIVE) return false;

      return ((EpigraphPrimitiveTypeDef) host).getPrimitiveTypeKind() ==
          ((EpigraphPrimitiveTypeDef) typeDef).getPrimitiveTypeKind();
    }

    private boolean includeInSupplement(@NotNull EpigraphTypeDef typeDef, @NotNull SchemaSupplementDef host, boolean checkSource) {
      if (typeDef.getKind() != TypeKind.PRIMITIVE) return true;
      PrimitiveTypeKind primitiveTypeKind = ((EpigraphPrimitiveTypeDef) typeDef).getPrimitiveTypeKind();

      if (checkSource) {
        EpigraphTypeDef source = host.source();
        if (source != null) {
          if (source.getKind() != TypeKind.PRIMITIVE) return false;
          if (((EpigraphPrimitiveTypeDef) source).getPrimitiveTypeKind() != primitiveTypeKind) return false;
        }
      }

      for (SchemaFqnTypeRef targetRef : host.supplementedRefs()) {
        EpigraphTypeDef target = targetRef.resolve();
        if (target instanceof EpigraphPrimitiveTypeDef) {
          EpigraphPrimitiveTypeDef primitiveTarget = (EpigraphPrimitiveTypeDef) target;
          if (primitiveTarget.getPrimitiveTypeKind() != primitiveTypeKind) return false;
        }
      }

      return true;
    }

    public boolean includeInTarget(@NotNull EpigraphTypeDef typeDef, @NotNull SchemaSupplementDef host) {
      return includeInSupplement(typeDef, host, true);
    }

    public boolean includeInSource(@NotNull EpigraphTypeDef typeDef, @NotNull SchemaSupplementDef host) {
      return includeInSupplement(typeDef, host, false);
    }
  }

  // ---------------------- extends clause

  private static class SameTypeExtendsFilter extends SameTypeFilterBase implements ExtendsCompletionFilter {}

  private static class SameKindExtendsFilter extends SameKindFilterBase implements ExtendsCompletionFilter {}

  private static class WrongPrimitiveKindExtendsFilter extends WrongPrimitiveKindFilterBase implements ExtendsCompletionFilter {}

  private static class TypeAlreadyExtendedFilter implements ExtendsCompletionFilter {
    @Override
    public boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphTypeDef host, @NotNull SchemaExtendsDecl extendsDecl) {
      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(host.getProject());

      for (SchemaFqnTypeRef fqnTypeRef : extendsDecl.getFqnTypeRefList()) {
        EpigraphTypeDef parent = fqnTypeRef.resolve();
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
    public boolean include(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphTypeDef host, @NotNull SchemaSupplementsDecl supplementsDecl) {
      List<SchemaFqnTypeRef> supplementsList = supplementsDecl.getFqnTypeRefList();
      if (supplementsList.isEmpty()) return true;

      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(host.getProject());
      List<EpigraphTypeDef> typeParents = hierarchyCache.getTypeParents(typeDef);

      for (SchemaFqnTypeRef fqnTypeRef : supplementsList) {
        EpigraphTypeDef child = fqnTypeRef.resolve();
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
    public boolean includeInTarget(@NotNull EpigraphTypeDef typeDef, @NotNull SchemaSupplementDef host) {
      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(host.getProject());

      EpigraphTypeDef source = host.source();
      // if candidate is a parent of source then we have a circular inheritance
      if (source != null && hierarchyCache.getTypeParents(source).contains(typeDef)) return false;

      List<EpigraphTypeDef> typeParents = hierarchyCache.getTypeParents(typeDef);

      // if candidate is a child if source then it's a useless supplement
      if (source != null && typeParents.contains(source)) return false;

      for (SchemaFqnTypeRef supplementedTypeRef : host.supplementedRefs()) {
        EpigraphTypeDef supplemented = supplementedTypeRef.resolve();
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
    public boolean includeInSource(@NotNull EpigraphTypeDef typeDef, @NotNull SchemaSupplementDef host) {
      List<EpigraphTypeDef> supplementedList = host.supplemented();
      if (supplementedList.isEmpty()) return true;

      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(host.getProject());
      List<EpigraphTypeDef> typeParents = hierarchyCache.getTypeParents(typeDef);

      boolean allTargetsExtendSource = true;
      for (EpigraphTypeDef supplemented : supplementedList) {
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
