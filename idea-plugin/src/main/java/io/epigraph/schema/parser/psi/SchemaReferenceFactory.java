package io.epigraph.schema.parser.psi;

import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager;
import com.sumologic.epigraph.ideaplugin.schema.brains.SchemaQnReference;
import com.sumologic.epigraph.ideaplugin.schema.brains.SchemaQnReferenceResolver;
import com.sumologic.epigraph.ideaplugin.schema.brains.SchemaVarTagReference;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaSearchScopeUtil;
import io.epigraph.lang.Qn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager.getNamespace;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 * @see <a href="https://github.com/SumoLogic/epigraph/wiki/References%20implementation#reference-resolution-algorithm">Reference resolution algorithm</a>
 */
public class SchemaReferenceFactory {
  @Nullable
  public static PsiReference getQnReference(@NotNull SchemaQnSegment segment) {
    SchemaQnReferenceResolver resolver = getQnReferenceResolver(segment);

    return resolver == null ? null : new SchemaQnReference(segment, resolver);
  }

  @Nullable
  public static SchemaQnReferenceResolver getQnReferenceResolver(@NotNull SchemaQnSegment segment) {
    final SchemaFile file = (SchemaFile) segment.getContainingFile();
    if (file == null) return null;

    final boolean isImport = PsiTreeUtil.getParentOfType(segment, SchemaImportStatement.class) != null;

    return getQnReferenceResolver(file, segment.getQn(), isImport);
  }

  @Nullable
  public static SchemaQnReferenceResolver getQnReferenceResolver(@NotNull SchemaFile file, @NotNull Qn fqn, boolean isImport) {
    if (fqn.isEmpty()) return null;

    final List<Qn> prefixes = new ArrayList<>();
    boolean isSingleSegment = fqn.size() == 1;

    final String first = fqn.first();
    assert first != null;


    if (!isImport) {
      prefixes.addAll(
          // imports ending with our first segment, with last segment removed
          NamespaceManager.getImportedNamespaces(file).stream()
              .filter(f -> first.equals(f.last()))
              .map(Qn::removeLastSegment)
              .collect(Collectors.toList())
      );
    }

    if (isSingleSegment) {
      if (!isImport) {
        // add all default namespaces
        Collections.addAll(prefixes, NamespaceManager.DEFAULT_NAMESPACES);
      }

      // current namespace
      Qn currentNamespace = getNamespace(file);
      if (currentNamespace != null) {
        prefixes.add(currentNamespace);
      }

    } else {
      prefixes.add(Qn.EMPTY);
    }

    // deduplicate, preserving order
    Set<Qn> dedupPrefixes = new LinkedHashSet<>(prefixes);
    prefixes.clear();
    prefixes.addAll(dedupPrefixes);

    return new SchemaQnReferenceResolver(prefixes, fqn, SchemaSearchScopeUtil.getSearchScope(file));
  }

  @Nullable
  public static PsiReference getVarTagReference(@NotNull SchemaVarTagRef varTagRef) {
    SchemaValueTypeRef valueTypeRef = PsiTreeUtil.getParentOfType(varTagRef, SchemaValueTypeRef.class);
    if (valueTypeRef == null) return null;

    SchemaTypeRef varTypeRef = valueTypeRef.getTypeRef();
    if (varTypeRef instanceof SchemaQnTypeRef) {
      SchemaQnTypeRef fqnVarTypeRef = (SchemaQnTypeRef) varTypeRef;
      SchemaTypeDef typeDef = fqnVarTypeRef.resolve();
      if (typeDef instanceof SchemaVarTypeDef) {
        SchemaVarTypeDef varTypeDef = (SchemaVarTypeDef) typeDef;
        return new SchemaVarTagReference(varTypeDef, varTagRef.getQid());
      }
    }

    return null;
  }
}
