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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaReferenceFactory {
  @Nullable
  public static PsiReference getReference(@NotNull SchemaFqnSegment segment) {
    // list of all potential FQNs: imports, default imports, etc
    List<Fqn> visibleFqns = getVisibleFqns(segment);
    if (visibleFqns == null) return null;
    SchemaFqnReferenceResolver resolver = getReferenceResolver(segment, visibleFqns);
    return resolver == null ? null : new SchemaFqnReference(segment, resolver, visibleFqns);
  }

  @Nullable
  private static List<Fqn> getVisibleFqns(@NotNull SchemaFqnSegment segment) {
    Fqn fqn = segment.getFqn();
    boolean isSingleSegment = fqn.size() == 1;

    if (fqn.isEmpty()) return null;

    String first = fqn.first();
    assert first != null;

    final Project project = segment.getProject();
    final boolean isImport = PsiTreeUtil.getParentOfType(segment, SchemaImportStatement.class) != null;

    final List<Fqn> visibleFqns;

    if (isSingleSegment) {
      // add all namespaces 
      List<Fqn> allNamespaces = isImport
          ? getAllNamespaces(project)
          : NamespaceManager.getImportedNamespaces(segment); //all imports

      // add all default namespaces
      Collections.addAll(allNamespaces, NamespaceManager.DEFAULT_NAMESPACES);

      visibleFqns = new ArrayList<>(allNamespaces);
    } else {
      List<Fqn> allNamespaces = isImport
          ? getAllNamespaces(project)
          : NamespaceManager.getImportedNamespaces(segment);

      visibleFqns = allNamespaces.stream().map(Fqn::removeLastSegment).collect(Collectors.toList());
    }
    
    return visibleFqns;
  }

  @Nullable
  public static SchemaFqnReferenceResolver getReferenceResolver(@NotNull SchemaFqnSegment segment, @NotNull List<Fqn> visibleFqns) {
    Fqn fqn = segment.getFqn();
    boolean isSingleSegment = fqn.size() == 1;

    if (fqn.isEmpty()) return null;

    String first = fqn.first();
    assert first != null;

    ArrayList<Fqn> resultingFqns = new ArrayList<>(visibleFqns.size() + 1);

    // only leave Fqns ending with our first segment
    if (visibleFqns.size() > 0) {
      Fqn tail = fqn.removeFirstSegment();

      for (Fqn namespace : visibleFqns) {
        if (first.equals(namespace.last())) {
          if (isSingleSegment) resultingFqns.add(namespace);
          else resultingFqns.add(namespace.append(tail));
        }
      }
    }

    if (isSingleSegment) {
      // add current file's namespace + single segment
      Fqn currentNamespace = getNamespace(segment);
      if (currentNamespace != null) {
        resultingFqns.add(currentNamespace.append(first));
      }
    }

    if (resultingFqns.size() == 0) {
      // no visibles found, so maybe we've got a full name from the start?
      // we can already have a full reference
      resultingFqns.add(fqn);
    }

    return new SchemaFqnReferenceResolver(fqn, resultingFqns.toArray(new Fqn[resultingFqns.size()]));
  }

  private static List<Fqn> getAllNamespaces(@NotNull Project project) {
    return NamespaceManager.getNamespaceManager(project).getAllNamespaces().stream()
        .map(SchemaNamespaceDecl::getFqn2)
        .collect(Collectors.toList());
  }
}
