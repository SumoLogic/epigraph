package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaPsiUtil;
import com.sumologic.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static com.sumologic.epigraph.schema.parser.lexer.SchemaElementTypes.S_WITH;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public abstract class CompletionTypeFilters {
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
      new TypeAlreadySupplemented2Filter(),
      new WrongPrimitiveKindSupplementTargetFilter()
  );

  @NotNull
  public static Predicate<SchemaTypeDef> combined(@NotNull PsiElement element) {
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
    boolean include(@NotNull SchemaTypeDef typeDef, @NotNull PsiElement element);
  }

  interface ExtendsCompletionFilter extends CompletionTypeFilter {
    @Override
    default boolean include(@NotNull SchemaTypeDef typeDef, @NotNull PsiElement element) {
      SchemaTypeDef host = PsiTreeUtil.getParentOfType(element, SchemaTypeDef.class);
      if (host == null) return true;

      SchemaExtendsDecl extendsDecl = PsiTreeUtil.getParentOfType(element, SchemaExtendsDecl.class);
      if (extendsDecl == null) return true;

      return include(typeDef, host, extendsDecl);
    }

    boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaExtendsDecl extendsDecl);
  }

  interface SupplementsCompletionFilter extends CompletionTypeFilter {
    @Override
    default boolean include(@NotNull SchemaTypeDef typeDef, @NotNull PsiElement element) {
      SchemaTypeDef host = PsiTreeUtil.getParentOfType(element, SchemaTypeDef.class);
      if (host == null) return true;

      SchemaSupplementsDecl supplementsDecl = PsiTreeUtil.getParentOfType(element, SchemaSupplementsDecl.class);
      if (supplementsDecl == null) return true;

      return include(typeDef, host, supplementsDecl);
    }

    boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaSupplementsDecl supplementsDecl);
  }

  interface SupplementTargetCompletionFilter extends CompletionTypeFilter {
    @Override
    default boolean include(@NotNull SchemaTypeDef typeDef, @NotNull PsiElement element) {
      SchemaSupplementDef host = PsiTreeUtil.getParentOfType(element, SchemaSupplementDef.class);
      if (host == null) return true;
      if (SchemaPsiUtil.hasPrevSibling(element, S_WITH)) return true; // we're completing source

      return includeInTarget(typeDef, host);
    }

    boolean includeInTarget(@NotNull SchemaTypeDef typeDef, @NotNull SchemaSupplementDef host);
  }

  interface SupplementSourceCompletionFilter extends CompletionTypeFilter {
    @Override
    default boolean include(@NotNull SchemaTypeDef typeDef, @NotNull PsiElement element) {
      SchemaSupplementDef host = PsiTreeUtil.getParentOfType(element, SchemaSupplementDef.class);
      if (host == null) return true;
      if (!SchemaPsiUtil.hasPrevSibling(element, S_WITH)) return true; // we're completing target

      return includeInSource(typeDef, host);
    }

    boolean includeInSource(@NotNull SchemaTypeDef typeDef, @NotNull SchemaSupplementDef host);
  }

  // ---------------------- common

  private static abstract class SameTypeFilterBase {
    private boolean notSameType(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host) {
      return !host.equals(typeDef);
    }

    public boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaExtendsDecl extendsDecl) {
      return notSameType(typeDef, host);
    }

    public boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaSupplementsDecl supplementsDecl) {
      return notSameType(typeDef, host);
    }

    public boolean includeInTarget(@NotNull SchemaTypeDef typeDef, @NotNull SchemaSupplementDef host) {
      for (SchemaFqnTypeRef targetRef : host.supplementedRefs()) {
        SchemaTypeDef target = targetRef.resolve();
        if (target != null && typeDef.equals(target)) return false;
      }

      return true;
    }
  }

  private static abstract class SameKindFilterBase {
    public boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaExtendsDecl extendsDecl) {
      return isSameKind(typeDef, host);
    }

    public boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaSupplementsDecl supplementsDecl) {
      return isSameKind(typeDef, host);
    }

    private boolean isSameKind(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host) {
      return typeDef.getKind() == host.getKind();
    }

    public boolean includeInTarget(@NotNull SchemaTypeDef typeDef, @NotNull SchemaSupplementDef host) {
      for (SchemaFqnTypeRef targetRef : host.supplementedRefs()) {
        SchemaTypeDef target = targetRef.resolve();
        if (target != null && typeDef.getKind() != target.getKind()) return false;
      }

      return true;
    }
  }

  private abstract static class WrongPrimitiveKindFilterBase {
    public boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaExtendsDecl extendsDecl) {
      return isSamePrimitiveKind(typeDef, host);
    }

    public boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaSupplementsDecl supplementsDecl) {
      return isSamePrimitiveKind(typeDef, host);
    }

    private boolean isSamePrimitiveKind(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host) {
      if (host.getKind() != TypeKind.PRIMITIVE) return true;
      if (typeDef.getKind() != TypeKind.PRIMITIVE) return false;

      return ((SchemaPrimitiveTypeDef) host).getPrimitiveTypeKind() ==
          ((SchemaPrimitiveTypeDef) typeDef).getPrimitiveTypeKind();
    }

    public boolean includeInTarget(@NotNull SchemaTypeDef typeDef, @NotNull SchemaSupplementDef host) {
      if (typeDef.getKind() != TypeKind.PRIMITIVE) return true;
      PrimitiveTypeKind primitiveTypeKind = ((SchemaPrimitiveTypeDef) typeDef).getPrimitiveTypeKind();

      for (SchemaFqnTypeRef targetRef : host.supplementedRefs()) {
        SchemaTypeDef target = targetRef.resolve();
        if (target instanceof SchemaPrimitiveTypeDef) {
          SchemaPrimitiveTypeDef primitiveTarget = (SchemaPrimitiveTypeDef) target;
          if (primitiveTarget.getPrimitiveTypeKind() != primitiveTypeKind) return false;
        }
      }

      return true;
    }
  }

  // ---------------------- extends clause

  private static class SameTypeExtendsFilter extends SameTypeFilterBase implements ExtendsCompletionFilter {}

  private static class SameKindExtendsFilter extends SameKindFilterBase implements ExtendsCompletionFilter {}

  private static class WrongPrimitiveKindExtendsFilter extends WrongPrimitiveKindFilterBase implements ExtendsCompletionFilter {}

  private static class TypeAlreadyExtendedFilter implements ExtendsCompletionFilter {
    @Override
    public boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaExtendsDecl extendsDecl) {
      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(host.getProject());

      for (SchemaFqnTypeRef fqnTypeRef : extendsDecl.getFqnTypeRefList()) {
        SchemaTypeDef parent = fqnTypeRef.resolve();
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
    public boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaSupplementsDecl supplementsDecl) {
      List<SchemaFqnTypeRef> supplementsList = supplementsDecl.getFqnTypeRefList();
      if (supplementsList.isEmpty()) return true;

      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(host.getProject());
      List<SchemaTypeDef> typeParents = hierarchyCache.getTypeParents(typeDef);

      for (SchemaFqnTypeRef fqnTypeRef : supplementsList) {
        SchemaTypeDef child = fqnTypeRef.resolve();
        if (child != null && child.equals(typeDef) || typeParents.contains(child)) return false;
      }

      return true;
    }
  }

  // ---------------------- supplement target

  private static class SameTypeSupplementTargetFilter extends SameTypeFilterBase implements SupplementTargetCompletionFilter {}

  private static class SameKindSupplementTargetFilter extends SameKindFilterBase implements SupplementTargetCompletionFilter {}

  private static class WrongPrimitiveKindSupplementTargetFilter extends WrongPrimitiveKindFilterBase implements SupplementTargetCompletionFilter {}

  private static class TypeAlreadySupplemented2Filter implements SupplementTargetCompletionFilter {
    @Override
    public boolean includeInTarget(@NotNull SchemaTypeDef typeDef, @NotNull SchemaSupplementDef host) {
      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(host.getProject());
      List<SchemaTypeDef> typeParents = hierarchyCache.getTypeParents(typeDef);

      for (SchemaFqnTypeRef supplementedTypeRef : host.supplementedRefs()) {
        SchemaTypeDef supplemented = supplementedTypeRef.resolve();
        if (supplemented != null && supplemented.equals(typeDef) || typeParents.contains(supplemented)) return false;
      }

      return true;
    }
  }

  // ---------------------- supplement source
}
