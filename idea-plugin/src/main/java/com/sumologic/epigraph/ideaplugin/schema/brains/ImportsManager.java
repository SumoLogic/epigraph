package com.sumologic.epigraph.ideaplugin.schema.brains;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.containers.MultiMap;
import com.sumologic.epigraph.schema.parser.Fqn;
import com.sumologic.epigraph.schema.parser.psi.*;
import com.sumologic.epigraph.schema.parser.psi.impl.SchemaElementFactory;
import com.sumologic.epigraph.schema.parser.psi.impl.SchemaPsiImplUtil;
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

  public static List<SchemaImportStatement> findUnusedImports(@NotNull SchemaFile file) {
    // TODO unit test
    SchemaImports schemaImports = file.getImportsStatement();
    if (schemaImports == null) return Collections.emptyList();

    List<SchemaImportStatement> importStatements = schemaImports.getImportStatementList();
    if (importStatements.isEmpty()) return Collections.emptyList();

    MultiMap<Fqn, SchemaImportStatement> importsByFqn = getImportsByFqn(importStatements);
    for (Fqn defaultImport : DEFAULT_IMPORTS) importsByFqn.remove(defaultImport);

    // first add all imports, then remove those actually used
    final List<SchemaImportStatement> res = new ArrayList<>(importsByFqn.values());

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

              for (Map.Entry<Fqn, Collection<SchemaImportStatement>> entry : importsByFqn.entrySet()) {
                if (inputFirstSegment.equals(entry.getKey().last())) {
                  res.removeAll(entry.getValue());
                }
              }
            }
          }
        }
      }
    };

    file.accept(visitor);

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
