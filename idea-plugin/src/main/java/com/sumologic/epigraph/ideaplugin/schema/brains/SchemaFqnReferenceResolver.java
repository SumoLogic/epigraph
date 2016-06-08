package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.util.ArrayUtil;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.schema.parser.Fqn;
import com.sumologic.epigraph.schema.parser.psi.SchemaNamespaceDecl;
import com.sumologic.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager.getNamespacesByPrefix;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFqnReferenceResolver {
  private final Fqn ref;
  private final Fqn[] fqns;

  /**
   * Creates new reference resolver
   *
   * @param sourceFqn source reference FQN
   * @param fqns      FQNs to try during resolution. Ideally should contain only one entry, e.g.
   *                  {@code sourceFqn} prefixed by appropriate import
   */
  public SchemaFqnReferenceResolver(Fqn sourceFqn, Fqn[] fqns) {
    this.ref = sourceFqn;
    this.fqns = fqns;
  }

  public Fqn getSourceFqn() {
    return ref;
  }

  public Fqn[] getFqns() {
    return fqns;
  }

  @Nullable
  public PsiElement resolve(@NotNull Project project) {
    List<SchemaTypeDef> typeDefs = SchemaIndexUtil.findTypeDefs(project, fqns);

    if (typeDefs.isEmpty()) {
      // we can't find a typedef by this reference, lets check if it points to a namespace declaration

      List<SchemaNamespaceDecl> namespaces = getNamespacesByPrefix(project, ref, true);
      if (namespaces.size() == 1) {
        SchemaNamespaceDecl namespaceDecl = namespaces.get(0);
        Fqn namespaceDeclFqn = namespaceDecl.getFqn2();
        if (ref.equals(namespaceDeclFqn)) {
          return getTargetSegment(namespaceDecl, ref.size());
        }
      }

      return null;
    }

    return typeDefs.get(0);
  }

  @NotNull
  public ResolveResult[] multiResolve(@NotNull Project project) {
    ResolveResult[] typeDefs = SchemaIndexUtil.findTypeDefs(project, fqns).stream()
        .map(PsiElementResolveResult::new)
        .toArray(ResolveResult[]::new);

    int prefixLength = ref.size();
    List<SchemaNamespaceDecl> namespaceDecls = getNamespacesByPrefix(project, ref, true);

    ResolveResult[] namespaces = namespaceDecls.stream()
        .map(ns -> new PsiElementResolveResult(getTargetSegment(ns, prefixLength)))
        .toArray(ResolveResult[]::new);

    return ArrayUtil.mergeArrays(typeDefs, namespaces);
  }

  private PsiElement getTargetSegment(@NotNull SchemaNamespaceDecl namespaceDecl, int prefixLength) {
    // This forces PSI tree reparse. Adding stubs for SchemaFqn and SchemaFqnSegment is one option.
    // Just pointing to the namespace decl is another

//    SchemaFqn fqn = namespaceDecl.getFqn();
//    assert fqn != null;
//    //noinspection ConstantConditions
////    assert fqnSegment.getName().equals(getElement().getName());
//    return fqn.getFqnSegmentList().get(prefixLength - 1);

    return namespaceDecl;
  }
}
