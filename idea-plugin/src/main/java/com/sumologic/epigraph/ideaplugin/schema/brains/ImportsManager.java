package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.MultiMap;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaSearchScopeUtil;
import io.epigraph.lang.parser.Fqn;
import io.epigraph.lang.parser.psi.*;
import io.epigraph.lang.parser.psi.impl.EpigraphPsiImplUtil;
import io.epigraph.lang.parser.psi.impl.EpigraphElementFactory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class ImportsManager {
  public static Fqn[] DEFAULT_IMPORTS = new Fqn[]{
      new Fqn("epigraph", "String"),
      new Fqn("epigraph", "Integer"),
      new Fqn("epigraph", "Long"),
      new Fqn("epigraph", "Double"),
      new Fqn("epigraph", "Boolean"),
  };

  public static List<Fqn> DEFAULT_IMPORTS_LIST = Collections.unmodifiableList(Arrays.asList(DEFAULT_IMPORTS));

  public static void addImport(@NotNull SchemaFile file, @NotNull String importToAdd) {
    // TODO this should return false if this would be a clashing import

    EpigraphImports epigraphImports = file.getImportsStatement();

    Project project = file.getProject();
    assert epigraphImports != null;

    /*
    if (schemaImports == null) {
      // can we ever get here?
      schemaImports = SchemaElementFactory.createImports(project, importToAdd);

      SchemaNamespaceDecl namespaceDecl = file.getNamespaceDecl();
      if (namespaceDecl == null) {
        file.add(schemaImports);
      } else {
        file.addAfter(schemaImports, namespaceDecl);
      }

      file.addAfter(newline2(project), schemaImports);
    } else*/
    {
      EpigraphImportStatement importStatement = EpigraphElementFactory.createImport(project, importToAdd);
      List<EpigraphImportStatement> importStatementList = epigraphImports.getImportStatementList();

      if (importStatementList.isEmpty()) {
        epigraphImports.add(importStatement);
        file.addAfter(newline2(project), epigraphImports);
      } else {
        PsiElement e = epigraphImports.addAfter(newline(project), importStatementList.get(importStatementList.size() - 1));
        e = epigraphImports.addAfter(importStatement, e);
        file.addAfter(newline(project), e);
      }
    }

    // TODO(low) reformat?
  }

  private static PsiElement newline(Project project) {
    return EpigraphElementFactory.createWhitespaces(project, "\n");
  }

  private static PsiElement newline2(Project project) {
    return EpigraphElementFactory.createWhitespaces(project, "\n\n"); // TODO(low) rely on reformat instead of this
  }

  public static List<Fqn> findImportsBySuffix(@NotNull SchemaFile file, @NotNull Fqn suffix) {
    EpigraphImports epigraphImports = file.getImportsStatement();
    if (epigraphImports == null) return Collections.emptyList();

    List<EpigraphImportStatement> importStatements = epigraphImports.getImportStatementList();
    if (importStatements.isEmpty()) return Collections.emptyList();

    //noinspection ConstantConditions
    Stream<Fqn> explicitImports = importStatements.stream()
        .filter(st -> {
          EpigraphFqn sfqn = st.getFqn();
          Fqn fqn = sfqn == null ? null : sfqn.getFqn();
          return fqn != null && fqn.endsWith(suffix);
        })
        .map(st -> st.getFqn().getFqn());

    Stream<Fqn> implicitImports = DEFAULT_IMPORTS_LIST.stream()
        .filter(fqn -> fqn.endsWith(suffix));

    return Stream.concat(explicitImports, implicitImports).collect(Collectors.toList());
  }

  public static Set<EpigraphImportStatement> findUnusedImports(@NotNull SchemaFile file) {
    EpigraphImports epigraphImports = file.getImportsStatement();
    if (epigraphImports == null) return Collections.emptySet();

    List<EpigraphImportStatement> importStatements = epigraphImports.getImportStatementList();
    if (importStatements.isEmpty()) return Collections.emptySet();

    MultiMap<Fqn, EpigraphImportStatement> importsByFqn = getImportsByFqn(importStatements);
    for (Fqn defaultImport : DEFAULT_IMPORTS) importsByFqn.remove(defaultImport);

    // first add all imports, then remove those actually used
    final Set<EpigraphImportStatement> res = new HashSet<>(importsByFqn.values());
    final GlobalSearchScope searchScope = SchemaSearchScopeUtil.getSearchScope(file);

    EpigraphVisitor visitor = new EpigraphVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        super.visitElement(element);
        element.acceptChildren(this);
      }

      @Override
      public void visitFqnTypeRef(@NotNull EpigraphFqnTypeRef typeRef) {
        super.visitFqnTypeRef(typeRef);
        PsiReference reference = EpigraphPsiImplUtil.getReference(typeRef);
        if (reference instanceof SchemaFqnReference) {
          SchemaFqnReference schemaFqnReference = (SchemaFqnReference) reference;
          EpigraphFqnReferenceResolver resolver = schemaFqnReference.getResolver();
          Fqn targetFqn = resolver.getTargetTypeDefFqn(typeRef.getProject());

          if (targetFqn != null) {
            Fqn input = resolver.getInput();
            if (!input.equals(targetFqn)) {
              String inputFirstSegment = input.first();
              assert inputFirstSegment != null;

              importsByFqn.entrySet().stream()
                  .filter(entry -> inputFirstSegment.equals(entry.getKey().last()))
                  .forEach(entry -> res.removeAll(entry.getValue()));
            }
          }
        }
      }
    };

    file.accept(visitor);

    // add all unresolved imports (unresolved => unused)
    final Project project = file.getProject();
    for (Map.Entry<Fqn, Collection<EpigraphImportStatement>> entry : importsByFqn.entrySet()) {
      EpigraphTypeDef typeDef = SchemaIndexUtil.findTypeDef(project, entry.getKey(), searchScope);
      if (typeDef == null && SchemaIndexUtil.findNamespace(project, entry.getKey(), searchScope) == null) {
        res.addAll(entry.getValue());
      }
    }

    return res;
  }

  public static Runnable buildImportOptimizer(@NotNull final SchemaFile file) {
    final List<Fqn> optimizedImports = getOptimizedImports(file);

    return () -> {
      List<EpigraphImportStatement> importStatements = file.getImportStatements();
      importStatements.forEach(PsiElement::delete);

      for (Fqn fqn : optimizedImports)
        addImport(file, fqn.toString());
    };
  }

  static List<Fqn> getOptimizedImports(@NotNull SchemaFile file) {
    // de-duplicated imports without implicits
    Set<Fqn> fqns = file.getImportStatements().stream()
        .map(EpigraphImportStatement::getFqn)
        .filter(sfqn -> sfqn != null)
        .map(EpigraphFqn::getFqn)
        .filter(fqn -> !DEFAULT_IMPORTS_LIST.contains(fqn))
        .collect(Collectors.toSet());

    //noinspection ConstantConditions
    findUnusedImports(file).forEach(is -> fqns.remove(is.getFqn().getFqn()));

    final List<Fqn> res = new ArrayList<>(fqns.size());
    res.addAll(fqns);
    Collections.sort(res);

    return res;
  }

  @NotNull
  public static MultiMap<Fqn, EpigraphImportStatement> getImportsByFqn(List<EpigraphImportStatement> importStatements) {
    MultiMap<Fqn, EpigraphImportStatement> importsByFqn = new MultiMap<>();
    for (EpigraphImportStatement importStatement : importStatements) {
      EpigraphFqn epigraphFqn = importStatement.getFqn();
      if (epigraphFqn != null) {
        Fqn fqn = epigraphFqn.getFqn();
        importsByFqn.putValue(fqn, importStatement);
      }
    }
    return importsByFqn;
  }

}
