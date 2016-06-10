package com.sumologic.epigraph.schema.parser.psi;

import com.intellij.openapi.project.Project;
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
 */
public class SchemaReferenceFactory {
  @Nullable
  public static PsiReference getReference(@NotNull SchemaFqnSegment segment) {
    SchemaFqnReferenceResolver resolver = getReferenceResolver(segment);

    return resolver == null ? null : new SchemaFqnReference(segment, resolver);
  }

  @Nullable
  public static SchemaFqnReferenceResolver getReferenceResolver(@NotNull SchemaFqnSegment segment) {
    Fqn fqn = segment.getFqn();
    if (fqn.isEmpty()) return null;

    final List<Fqn> namespacesToSearch = new ArrayList<>();
    boolean isSingleSegment = fqn.size() == 1;

    final String first = fqn.first();
    assert first != null;

    final Project project = segment.getProject();
    final boolean isImport = PsiTreeUtil.getParentOfType(segment, SchemaImportStatement.class) != null;

    if (!isImport) {
      namespacesToSearch.addAll(
          // add all imports with their last segment removed (because import foo.bar + ref to bar.Baz => search for bar.Baz in foo)
          NamespaceManager.getImportedNamespaces(segment).stream().map(Fqn::removeLastSegment).collect(Collectors.toList())
      );
    }

    if (isSingleSegment) {
      // add current file's namespace
      Fqn currentNamespace = getNamespace(segment);
      if (currentNamespace != null) namespacesToSearch.add(currentNamespace);

      if (!isImport) {
        // add all default namespaces
        Collections.addAll(namespacesToSearch, NamespaceManager.DEFAULT_NAMESPACES);
      }
    }

    // deduplicate
    Set<Fqn> dedupNs = new LinkedHashSet<>(namespacesToSearch);
    namespacesToSearch.clear();
    namespacesToSearch.addAll(dedupNs);

    return new SchemaFqnReferenceResolver(namespacesToSearch, fqn);
  }

//  private static List<Fqn> getAllNamespaces(@NotNull Project project) {
//    return NamespaceManager.getNamespaceManager(project).getAllNamespaces().stream()
//        .map(SchemaNamespaceDecl::getFqn2)
//        .collect(Collectors.toList());
//  }
}
