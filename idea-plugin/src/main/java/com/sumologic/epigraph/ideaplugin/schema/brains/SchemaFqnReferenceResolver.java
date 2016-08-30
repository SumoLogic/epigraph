package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.schema.parser.Fqn;
import com.sumologic.epigraph.schema.parser.psi.SchemaNamespaceDecl;
import com.sumologic.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 * @see <a href="https://github.com/SumoLogic/epigraph/wiki/References%20implementation#reference-resolution-algorithm">Reference resolution algorithm</a>
 */
public class SchemaFqnReferenceResolver {
  @NotNull
  private final List<Fqn> prefixes;
  @NotNull
  private final Fqn suffix;
  @NotNull
  private final Fqn input;
  @NotNull
  private final GlobalSearchScope searchScope;

  public SchemaFqnReferenceResolver(@NotNull List<Fqn> prefixes, @NotNull Fqn input, @NotNull GlobalSearchScope searchScope) {
    this.searchScope = searchScope;
    if (input.isEmpty()) throw new IllegalArgumentException("Empty input");

    this.input = input;
    this.prefixes = prefixes;
    this.suffix = input;
  }

  @NotNull
  public Fqn getInput() {
    return input;
  }

  @NotNull
  public Fqn getSuffix() {
    return suffix;
  }

  @NotNull
  public List<Fqn> getPrefixes() {
    return prefixes;
  }

  @Nullable
  public PsiElement resolve(@NotNull Project project) {
    SchemaTypeDef typeDef = resolveTypeDef(project);
    if (typeDef != null) return typeDef;

    // we can't find a typedef by this reference, lets check if it points to a namespace declaration

    // type name input (which we tried to append to different prefixes) now becomes
    // source namespace's prefix

    Fqn prefix = input;
    List<SchemaNamespaceDecl> namespaces = resolveNamespaces(project, prefix);
    if (namespaces.size() == 1) {
      SchemaNamespaceDecl namespaceDecl = namespaces.get(0);
      return getTargetSegment(namespaceDecl, prefix.size());
    }

    return null;
  }

  @Nullable
  public Fqn getTargetTypeDefFqn(@NotNull Project project) {
    SchemaTypeDef typeDef = resolveTypeDef(project);
    if (typeDef != null) return typeDef.getFqn();
    return null;
  }

  @Nullable
  private SchemaTypeDef resolveTypeDef(@NotNull Project project) {
    return SchemaIndexUtil.findTypeDef(project, prefixes, suffix, searchScope);
//    return SchemaIndexUtil.findSingleTypeDef(project, prefixes, suffix, searchScope);
  }

  @NotNull
  public ResolveResult[] multiResolve(@NotNull Project project) {
    List<ResolveResult> typeDefs =
        SchemaIndexUtil.findTypeDefs(project, prefixes, suffix, searchScope).stream()
        .filter(Objects::nonNull)
        .map(PsiElementResolveResult::new)
        .collect(Collectors.toList());

    // see comment in `resolve` above re. namespace declaration reference

    Fqn prefix = input;
    int prefixLength = prefix.size();
    List<SchemaNamespaceDecl> namespaceDecls = resolveNamespaces(project, prefix);

    ResolveResult[] namespaces = namespaceDecls.stream()
        .map(ns -> new PsiElementResolveResult(getTargetSegment(ns, prefixLength)))
        .toArray(ResolveResult[]::new);

    return ArrayUtil.mergeArrays(typeDefs.toArray(new ResolveResult[typeDefs.size()]), namespaces);
  }

  /**
   * @return either a list with a single namespace declaration which is exactly our prefix, or a list
   * of namespaces that start with prefix
   */
  @NotNull
  private List<SchemaNamespaceDecl> resolveNamespaces(@NotNull Project project, @NotNull Fqn prefix) {
    List<SchemaNamespaceDecl> namespaces = SchemaIndexUtil.findNamespaces(project, prefix.toString(), searchScope);
    // try to find a namespace which is exactly our prefix
    for (SchemaNamespaceDecl namespace : namespaces) {
      //noinspection ConstantConditions
      if (namespace.getFqn2().equals(prefix))
        return Collections.singletonList(namespace);
    }

    return namespaces;
  }

  private PsiElement getTargetSegment(@NotNull SchemaNamespaceDecl namespaceDecl, @SuppressWarnings("UnusedParameters") int prefixLength) {
    // This forces PSI tree reparse. Addint stubs for SchemaFqn and SchemaFqnSegment is one option.
    // Just pointing to the namespace decl is another

//    SchemaFqn fqn = namespaceDecl.getFqn();
//    assert fqn != null;
//    //noinspection ConstantConditions
////    assert fqnSegment.getName().equals(getElement().getName());
//    return fqn.getFqnSegmentList().get(prefixLength - 1);

    return namespaceDecl;
  }

}
