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
import io.epigraph.lang.parser.psi.impl.SchemaElementFactory;
import io.epigraph.lang.parser.psi.impl.SchemaPsiImplUtil;
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

    SchemaImports schemaImports = file.getImportsStatement();

    Project project = file.getProject();
    assert schemaImports != null;

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
      SchemaImportStatement importStatement = SchemaElementFactory.createImport(project, importToAdd);
      List<SchemaImportStatement> importStatementList = schemaImports.getImportStatementList();

      if (importStatementList.isEmpty()) {
        schemaImports.add(importStatement);
        file.addAfter(newline2(project), schemaImports);
      } else {
        PsiElement e = schemaImports.addAfter(newline(project), importStatementList.get(importStatementList.size() - 1));
        e = schemaImports.addAfter(importStatement, e);
        file.addAfter(newline(project), e);
      }
    }

    // TODO(low) reformat?
  }

  private static PsiElement newline(Project project) {
    return SchemaElementFactory.createWhitespaces(project, "\n");
  }

  private static PsiElement newline2(Project project) {
    return SchemaElementFactory.createWhitespaces(project, "\n\n"); // TODO(low) rely on reformat instead of this
  }

  public static List<Fqn> findImportsBySuffix(@NotNull SchemaFile file, @NotNull Fqn suffix) {
    SchemaImports schemaImports = file.getImportsStatement();
    if (schemaImports == null) return Collections.emptyList();

    List<SchemaImportStatement> importStatements = schemaImports.getImportStatementList();
    if (importStatements.isEmpty()) return Collections.emptyList();

    //noinspection ConstantConditions
    Stream<Fqn> explicitImports = importStatements.stream()
        .filter(st -> {
          SchemaFqn sfqn = st.getFqn();
          Fqn fqn = sfqn == null ? null : sfqn.getFqn();
          return fqn != null && fqn.endsWith(suffix);
        })
        .map(st -> st.getFqn().getFqn());

    Stream<Fqn> implicitImports = DEFAULT_IMPORTS_LIST.stream()
        .filter(fqn -> fqn.endsWith(suffix));

    return Stream.concat(explicitImports, implicitImports).collect(Collectors.toList());
  }

  public static Set<SchemaImportStatement> findUnusedImports(@NotNull SchemaFile file) {
    SchemaImports schemaImports = file.getImportsStatement();
    if (schemaImports == null) return Collections.emptySet();

    List<SchemaImportStatement> importStatements = schemaImports.getImportStatementList();
    if (importStatements.isEmpty()) return Collections.emptySet();

    MultiMap<Fqn, SchemaImportStatement> importsByFqn = getImportsByFqn(importStatements);
    for (Fqn defaultImport : DEFAULT_IMPORTS) importsByFqn.remove(defaultImport);

    // first add all imports, then remove those actually used
    final Set<SchemaImportStatement> res = new HashSet<>(importsByFqn.values());
    final GlobalSearchScope searchScope = SchemaSearchScopeUtil.getSearchScope(file);

    SchemaVisitor visitor = new SchemaVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        super.visitElement(element);
        element.acceptChildren(this);
      }

      @Override
      public void visitFqnTypeRef(@NotNull SchemaFqnTypeRef typeRef) {
        super.visitFqnTypeRef(typeRef);
        PsiReference reference = SchemaPsiImplUtil.getReference(typeRef);
        if (reference instanceof SchemaFqnReference) {
          SchemaFqnReference schemaFqnReference = (SchemaFqnReference) reference;
          SchemaFqnReferenceResolver resolver = schemaFqnReference.getResolver();
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
    for (Map.Entry<Fqn, Collection<SchemaImportStatement>> entry : importsByFqn.entrySet()) {
      SchemaTypeDef typeDef = SchemaIndexUtil.findTypeDef(project, entry.getKey(), searchScope);
      if (typeDef == null && SchemaIndexUtil.findNamespace(project, entry.getKey(), searchScope) == null) {
        res.addAll(entry.getValue());
      }
    }

    return res;
  }

  public static Runnable buildImportOptimizer(@NotNull final SchemaFile file) {
    final List<Fqn> optimizedImports = getOptimizedImports(file);

    return () -> {
      List<SchemaImportStatement> importStatements = file.getImportStatements();
      importStatements.forEach(PsiElement::delete);

      for (Fqn fqn : optimizedImports)
        addImport(file, fqn.toString());
    };
  }

  static List<Fqn> getOptimizedImports(@NotNull SchemaFile file) {
    // de-duplicated imports without implicits
    Set<Fqn> fqns = file.getImportStatements().stream()
        .map(SchemaImportStatement::getFqn)
        .filter(sfqn -> sfqn != null)
        .map(SchemaFqn::getFqn)
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
  public static MultiMap<Fqn, SchemaImportStatement> getImportsByFqn(List<SchemaImportStatement> importStatements) {
    MultiMap<Fqn, SchemaImportStatement> importsByFqn = new MultiMap<>();
    for (SchemaImportStatement importStatement : importStatements) {
      SchemaFqn schemaFqn = importStatement.getFqn();
      if (schemaFqn != null) {
        Fqn fqn = schemaFqn.getFqn();
        importsByFqn.putValue(fqn, importStatement);
      }
    }
    return importsByFqn;
  }

}
