package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.psi.PsiReference;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqnSegment;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqnTypeRef;
import com.sumologic.epigraph.ideaplugin.schema.psi.references.SchemaTypeReference;
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
  public static PsiReference getReference(@NotNull SchemaFqnSegment segment, @NotNull SchemaFqnTypeRef fqnTypeRef) {

    Fqn fqn = fqnTypeRef.getFqn().getFqn();
    if (fqn.isEmpty()) return null;

    Collection<Fqn> namespacesToSearch;
    boolean isSingleSegment = fqn.size() == 1;

    String first = fqn.getFirst();
    assert first != null;

    if (isSingleSegment) {
      namespacesToSearch = getStarNamespaces(fqnTypeRef);

      namespacesToSearch.addAll(getPrefixNamespacesWithLastSegmentRemoved(fqnTypeRef, first));

      String currentNamespace = getCurrentNamespace(fqnTypeRef);
      if (currentNamespace != null) namespacesToSearch.add(new Fqn(currentNamespace));

      Collections.addAll(namespacesToSearch, NamespaceManager.DEFAULT_NAMESPACES);
    } else {
      namespacesToSearch = getPrefixNamespacesWithLastSegmentRemoved(fqnTypeRef, first);
    }

    return new SchemaTypeReference(segment, namespacesToSearch, fqn);
  }
}
