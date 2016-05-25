package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqnSegment;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaImportStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

import static com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class ReferenceFactory {

  @Nullable
  public static PsiReference getReference(@NotNull SchemaFqnSegment segment) {
    SchemaFqnReferenceResolver resolver = getReferenceResolver(segment);

    return resolver == null ? null : new SchemaFqnReference(segment, resolver);
  }

  @Nullable
  public static SchemaFqnReferenceResolver getReferenceResolver(@NotNull SchemaFqnSegment segment) {
    Fqn fqn = segment.getFqn();
    if (fqn.isEmpty()) return null;

    final Set<Fqn> namespacesToSearch;
    boolean isSingleSegment = fqn.size() == 1;

    final String first = fqn.getFirst();
    assert first != null;

    final Project project = segment.getProject();
    final boolean isImport = PsiTreeUtil.getParentOfType(segment, SchemaImportStatement.class) != null;

    if (isSingleSegment) {
      // add all star namespaces
      namespacesToSearch = getStarNamespaces(segment);

      // add all namespaces ending with our first segment, e.g. import foo.bar + ref to bar.Baz => add "foo"
      Set<Fqn> allNamespaces = isImport
          ? NamespaceManager.getNamespacesByPrefix(project, null)
          : NamespaceManager.getVisibleNamespaces(segment, false);
      namespacesToSearch.addAll(Fqn.getMatchingWithSuffixRemoved(allNamespaces, null));

      // add current file's namespace
      String currentNamespace = getNamespace(segment);
      if (currentNamespace != null) namespacesToSearch.add(new Fqn(currentNamespace));

      // add all default namespaces
      Collections.addAll(namespacesToSearch, NamespaceManager.DEFAULT_NAMESPACES);
    } else {
      Set<Fqn> allNamespaces = isImport
          ? NamespaceManager.getNamespacesByPrefix(project, null)
          : NamespaceManager.getVisibleNamespaces(segment, false);
      namespacesToSearch = Fqn.getMatchingWithSuffixRemoved(allNamespaces, null);
    }

    return new SchemaFqnReferenceResolver(namespacesToSearch, fqn);
  }
}
