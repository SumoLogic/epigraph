package com.sumologic.epigraph.schema.parser.psi;

import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager;
import com.sumologic.epigraph.ideaplugin.schema.brains.SchemaFqnReference;
import com.sumologic.epigraph.ideaplugin.schema.brains.SchemaFqnReferenceResolver;
import com.sumologic.epigraph.schema.parser.Fqn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager.getNamespace;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 * @see <a href="https://github.com/SumoLogic/sumo-platform/wiki/References%20implementation#reference-resolution-algorithm">Reference resolution algorithm</a>
 */
public class SchemaReferenceFactory {
  @Nullable
  public static PsiReference getFqnReference(@NotNull SchemaFqnSegment segment) {
    SchemaFqnReferenceResolver resolver = getFqnReferenceResolver(segment);

    return resolver == null ? null : new SchemaFqnReference(segment, resolver);
  }

  @Nullable
  public static SchemaFqnReferenceResolver getFqnReferenceResolver(@NotNull SchemaFqnSegment segment) {
    Fqn fqn = segment.getFqn();
    if (fqn.isEmpty()) return null;

    final List<Fqn> prefixes = new ArrayList<>();
    boolean isSingleSegment = fqn.size() == 1;

    final String first = fqn.first();
    assert first != null;

    final boolean isImport = PsiTreeUtil.getParentOfType(segment, SchemaImportStatement.class) != null;

    if (!isImport) {
      prefixes.addAll(
          // imports ending with our first segment, with last segment removed
          NamespaceManager.getImportedNamespaces(segment).stream()
              .filter(f -> first.equals(f.last()))
              .map(Fqn::removeLastSegment)
              .collect(Collectors.toList())
      );
    }

    if (isSingleSegment) {
      if (!isImport) {
        // add all default namespaces
        Collections.addAll(prefixes, NamespaceManager.DEFAULT_NAMESPACES);
      }

      // current namespace
      Fqn currentNamespace = getNamespace(segment);
      if (currentNamespace != null) {
        prefixes.add(currentNamespace);
      }

    } else {
      prefixes.add(Fqn.EMPTY);
    }

    // deduplicate
    Set<Fqn> dedupPrefixes = new LinkedHashSet<>(prefixes);
    prefixes.clear();
    prefixes.addAll(dedupPrefixes);

    return new SchemaFqnReferenceResolver(prefixes, fqn);
  }
}
