package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.sumologic.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public abstract class CompletionTypeFilter {
  private static final List<CompletionTypeFilter> FILTERS = Arrays.asList(
      new SameTypeExtendsFilter(),
      new SameKindFilter(),
      new TypeAlreadyExtendedFilter(),
      new WrongPrimitiveKindFilter()
  );

  /**
   * Checks if particular {@code typeDef} should stay in the completion variants
   *
   * @param typeDef type def to check
   * @param element element completion was invoked on
   * @return {@code true} iff {@code typeDef} should stay included
   */
  protected abstract boolean include(@NotNull SchemaTypeDef typeDef, @NotNull PsiElement element);

  @NotNull
  public static Predicate<SchemaTypeDef> combined(@NotNull PsiElement element) {
    return typeDef -> {
      for (CompletionTypeFilter filter : FILTERS) if (!filter.include(typeDef, element)) return false;
      return true;
    };
  }

  @Nullable
  private static SchemaTypeDef findTypeDef(@NotNull PsiElement element) {
    return PsiTreeUtil.getParentOfType(element, SchemaTypeDef.class);
  }

  // ---------------------- extends clause
  
  private static abstract class ExtendsFilter extends CompletionTypeFilter {
    @Override
    protected boolean include(@NotNull SchemaTypeDef typeDef, @NotNull PsiElement element) {
      SchemaTypeDef host = findTypeDef(element);
      if (host == null) return true;
     
      SchemaExtendsDecl extendsDecl = PsiTreeUtil.getParentOfType(element, SchemaExtendsDecl.class);
      if (extendsDecl == null) return true;
      
      return include(typeDef, host, extendsDecl);
    }

    protected abstract boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaExtendsDecl extendsDecl);
  }

  private static class SameTypeExtendsFilter extends ExtendsFilter {
    @Override
    protected boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaExtendsDecl extendsDecl) {
      return !host.equals(typeDef);
    }
  }

  private static class SameKindFilter extends ExtendsFilter {
    @Override
    protected boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaExtendsDecl extendsDecl) {
      return typeDef.getKind() == host.getKind();
    }
  }

  private static class TypeAlreadyExtendedFilter extends ExtendsFilter {
    @Override
    protected boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaExtendsDecl extendsDecl) {
      HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(host.getProject());

      for (SchemaFqnTypeRef fqnTypeRef : extendsDecl.getFqnTypeRefList()) {
        SchemaTypeDef parent = fqnTypeRef.resolve();
        if (parent != null) {
          if (parent.equals(typeDef) || hierarchyCache.isParent(parent, typeDef)) return false;
        }
      }

      return true;
    }
  }

  private static class WrongPrimitiveKindFilter extends ExtendsFilter {
    @Override
    protected boolean include(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef host, @NotNull SchemaExtendsDecl extendsDecl) {
      if (host.getKind() != TypeKind.PRIMITIVE) return true;
      if (typeDef.getKind() != TypeKind.PRIMITIVE) return false;

      return ((SchemaPrimitiveTypeDef) host).getPrimitiveTypeKind() ==
          ((SchemaPrimitiveTypeDef) typeDef).getPrimitiveTypeKind();
    }
  }
}
