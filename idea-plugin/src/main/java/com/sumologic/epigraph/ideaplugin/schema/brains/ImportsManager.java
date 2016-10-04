package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.MultiMap;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaSearchScopeUtil;
import io.epigraph.lang.Qn;
import io.epigraph.schema.parser.psi.*;
import io.epigraph.schema.parser.psi.impl.SchemaElementFactory;
import io.epigraph.schema.parser.psi.impl.SchemaPsiImplUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class ImportsManager {
  public static Qn[] DEFAULT_IMPORTS = new Qn[]{
      new Qn("epigraph", "String"),
      new Qn("epigraph", "Integer"),
      new Qn("epigraph", "Long"),
      new Qn("epigraph", "Double"),
      new Qn("epigraph", "Boolean"),
  };

  public static List<Qn> DEFAULT_IMPORTS_LIST = Collections.unmodifiableList(Arrays.asList(DEFAULT_IMPORTS));

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

  public static List<Qn> findImportsBySuffix(@NotNull SchemaFile file, @NotNull Qn suffix) {
    SchemaImports schemaImports = file.getImportsStatement();
    if (schemaImports == null) return Collections.emptyList();

    List<SchemaImportStatement> importStatements = schemaImports.getImportStatementList();
    if (importStatements.isEmpty()) return Collections.emptyList();

    //noinspection ConstantConditions
    Stream<Qn> explicitImports = importStatements.stream()
                                                 .filter(st -> {
          SchemaQn sqn = st.getQn();
          Qn qn = sqn == null ? null : sqn.getQn();
          return qn != null && qn.endsWith(suffix);
        })
                                                 .map(st -> st.getQn().getQn());

    Stream<Qn> implicitImports = DEFAULT_IMPORTS_LIST.stream()
                                                     .filter(qn -> qn.endsWith(suffix));

    return Stream.concat(explicitImports, implicitImports).collect(Collectors.toList());
  }

  public static Set<SchemaImportStatement> findUnusedImports(@NotNull SchemaFile file) {
    SchemaImports schemaImports = file.getImportsStatement();
    if (schemaImports == null) return Collections.emptySet();

    List<SchemaImportStatement> importStatements = schemaImports.getImportStatementList();
    if (importStatements.isEmpty()) return Collections.emptySet();

    MultiMap<Qn, SchemaImportStatement> importsByQn = getImportsByQn(importStatements);
    for (Qn defaultImport : DEFAULT_IMPORTS) importsByQn.remove(defaultImport);

    // first add all imports, then remove those actually used
    final Set<SchemaImportStatement> res = new HashSet<>(importsByQn.values());
    final GlobalSearchScope searchScope = SchemaSearchScopeUtil.getSearchScope(file);

    SchemaVisitor visitor = new SchemaVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        super.visitElement(element);
        element.acceptChildren(this);
      }

      @Override
      public void visitQnTypeRef(@NotNull SchemaQnTypeRef typeRef) {
        super.visitQnTypeRef(typeRef);
        PsiReference reference = SchemaPsiImplUtil.getReference(typeRef);
        if (reference instanceof SchemaQnReference) {
          SchemaQnReference schemaQnReference = (SchemaQnReference) reference;
          SchemaQnReferenceResolver resolver = schemaQnReference.getResolver();
          Qn targetQn = resolver.getTargetTypeDefQn(typeRef.getProject());

          if (targetQn != null) {
            Qn input = resolver.getInput();
            if (!input.equals(targetQn)) {
              String inputFirstSegment = input.first();
              assert inputFirstSegment != null;

              importsByQn.entrySet().stream()
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
    for (Map.Entry<Qn, Collection<SchemaImportStatement>> entry : importsByQn.entrySet()) {
      SchemaTypeDef typeDef = SchemaIndexUtil.findTypeDef(project, entry.getKey(), searchScope);
      if (typeDef == null && SchemaIndexUtil.findNamespace(project, entry.getKey(), searchScope) == null) {
        res.addAll(entry.getValue());
      }
    }

    return res;
  }

  public static Runnable buildImportOptimizer(@NotNull final SchemaFile file) {
    final List<Qn> optimizedImports = getOptimizedImports(file);

    return () -> {
      List<SchemaImportStatement> importStatements = file.getImportStatements();
      importStatements.forEach(PsiElement::delete);

      for (Qn qn : optimizedImports)
        addImport(file, qn.toString());
    };
  }

  static List<Qn> getOptimizedImports(@NotNull SchemaFile file) {
    // de-duplicated imports without implicits
    Set<Qn> qns = file.getImportStatements().stream()
                      .map(SchemaImportStatement::getQn)
                      .filter(sqn -> sqn != null)
                      .map(SchemaQn::getQn)
                      .filter(qn -> !DEFAULT_IMPORTS_LIST.contains(qn))
                      .collect(Collectors.toSet());

    //noinspection ConstantConditions
    findUnusedImports(file).forEach(is -> qns.remove(is.getQn().getQn()));

    final List<Qn> res = new ArrayList<>(qns.size());
    res.addAll(qns);
    Collections.sort(res);

    return res;
  }

  @NotNull
  public static MultiMap<Qn, SchemaImportStatement> getImportsByQn(List<SchemaImportStatement> importStatements) {
    MultiMap<Qn, SchemaImportStatement> importsByQn = new MultiMap<>();
    for (SchemaImportStatement importStatement : importStatements) {
      SchemaQn schemaQn = importStatement.getQn();
      if (schemaQn != null) {
        Qn qn = schemaQn.getQn();
        importsByQn.putValue(qn, importStatement);
      }
    }
    return importsByQn;
  }

}
