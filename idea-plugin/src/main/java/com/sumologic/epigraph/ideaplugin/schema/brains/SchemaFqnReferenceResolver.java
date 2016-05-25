package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.util.ArrayUtil;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqn;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqnSegment;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaNamespaceDecl;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFqnReferenceResolver {
  private final Set<Fqn> namespacesToSearch;
  @Nullable
  private Set<String> namespacesToSearchStr = null;

  private final Fqn shortName;
  @Nullable
  private String shortNameStr = null;

  private final Fqn suffix;

  public SchemaFqnReferenceResolver(Set<Fqn> namespacesToSearch, Fqn suffix) {
    this.suffix = suffix;

    if (suffix.isEmpty()) throw new IllegalArgumentException("Empty suffix");

    if (suffix.size() == 1) {
      this.namespacesToSearch = namespacesToSearch;
      this.shortName = suffix;
    } else {
      final Fqn suffixPrefix = suffix.getPrefix();
      assert suffixPrefix != null;

      this.namespacesToSearch = namespacesToSearch.stream()
          .map(fqn -> fqn.append(suffixPrefix)).collect(Collectors.toSet());
      this.shortName = new Fqn(suffix.getLast());
    }
  }

  @NotNull
  public Fqn getShortName() {
    return shortName;
  }

  @NotNull
  private String getShortNameStr() {
    if (shortNameStr == null) shortNameStr = shortName.toString();
    return shortNameStr;
  }

  @NotNull
  public Set<Fqn> getNamespacesToSearch() {
    return namespacesToSearch;
  }

  @NotNull
  Set<String> getNamespacesToSearchStr() {
    if (namespacesToSearchStr == null)
      namespacesToSearchStr = namespacesToSearch.stream().map(Fqn::toString).collect(Collectors.toSet());

    return namespacesToSearchStr;
  }

  @Nullable
  public PsiElement resolve(@NotNull Project project) {
    SchemaTypeDef typeDef = SchemaIndexUtil.findTypeDef(project, getNamespacesToSearchStr(), getShortNameStr());
    if (typeDef != null) return typeDef;

    // we can't find a typedef by this reference, lets check if it points to a namespace declaration

    // type name suffix (which we tried to append to different namespacesToSearchStr) now becomes
    // target namespace's prefix

    Fqn prefix = suffix;
    List<SchemaNamespaceDecl> namespaces = resolveNamespaces(project, prefix);
    if (namespaces.size() == 1) {
      SchemaNamespaceDecl namespaceDecl = namespaces.get(0);
      return getTargetSegment(namespaceDecl, prefix.size());
    }

    return null;
  }

  @NotNull
  public ResolveResult[] multiResolve(@NotNull Project project) {
    ResolveResult[] typeDefs = SchemaIndexUtil.findTypeDefs(project, namespacesToSearchStr, shortNameStr).stream()
        .map(PsiElementResolveResult::new)
        .toArray(ResolveResult[]::new);

    // see comment in `resolve` above

    Fqn prefix = suffix;
    int prefixLength = prefix.size();
    List<SchemaNamespaceDecl> namespaceDecls = resolveNamespaces(project, prefix);

    ResolveResult[] namespaces = namespaceDecls.stream()
        .map(ns -> new PsiElementResolveResult(getTargetSegment(ns, prefixLength)))
        .toArray(ResolveResult[]::new);

    return ArrayUtil.mergeArrays(typeDefs, namespaces);
  }

  /**
   * @return either a list with a single namespace declaration which is exactly our prefix, or a list
   * of namespaces that start with prefix
   */
  @NotNull
  private List<SchemaNamespaceDecl> resolveNamespaces(@NotNull Project project, @NotNull Fqn prefix) {
    List<SchemaNamespaceDecl> namespaces = SchemaIndexUtil.findNamespaces(project, prefix.toString());
    // try to find a namespace which is exactly our prefix
    for (SchemaNamespaceDecl namespace : namespaces) {
      //noinspection ConstantConditions
      if (namespace.getFqn2().equals(prefix))
        return Collections.singletonList(namespace);
    }

    return namespaces;
  }

  private SchemaFqnSegment getTargetSegment(@NotNull SchemaNamespaceDecl namespaceDecl, int prefixLength) {
    SchemaFqn fqn = namespaceDecl.getFqn();
    assert fqn != null;
    //noinspection ConstantConditions
//    assert fqnSegment.getName().equals(getElement().getName());
    return fqn.getFqnSegmentList().get(prefixLength - 1);
  }

}
