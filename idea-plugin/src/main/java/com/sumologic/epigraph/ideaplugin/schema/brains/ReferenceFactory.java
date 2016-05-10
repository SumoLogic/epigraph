package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.psi.PsiReference;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqnSegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

import static com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class ReferenceFactory {

  @Nullable
  public static PsiReference getReference(@NotNull SchemaFqnSegment segment) {
    Fqn fqn = segment.getFqn();
    if (fqn.isEmpty()) return null;

    Collection<Fqn> namespacesToSearch;
    boolean isSingleSegment = fqn.size() == 1;

    String first = fqn.getFirst();
    assert first != null;

    if (isSingleSegment) {
      namespacesToSearch = getStarNamespaces(segment);

      namespacesToSearch.addAll(getPrefixNamespacesWithLastSegmentRemoved(segment, null /*first*/));

      String currentNamespace = getNamespace(segment);
      if (currentNamespace != null) namespacesToSearch.add(new Fqn(currentNamespace));

      Collections.addAll(namespacesToSearch, NamespaceManager.DEFAULT_NAMESPACES);
    } else {
      namespacesToSearch = getPrefixNamespacesWithLastSegmentRemoved(segment, null /*first*/);
    }

    return new SchemaFqnReference(segment, namespacesToSearch, fqn);
  }
}
