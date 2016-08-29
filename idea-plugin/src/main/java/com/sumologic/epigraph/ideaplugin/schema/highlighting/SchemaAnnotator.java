package com.sumologic.epigraph.ideaplugin.schema.highlighting;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.*;
import com.sumologic.epigraph.ideaplugin.schema.SchemaBundle;
import com.sumologic.epigraph.ideaplugin.schema.brains.ImportsManager;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.HierarchyCache;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.TypeMembers;
import com.sumologic.epigraph.ideaplugin.schema.features.actions.fixes.ImportTypeIntentionFix;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaSearchScopeUtil;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import io.epigraph.lang.parser.Fqn;
import io.epigraph.lang.parser.NamingConventions;
import io.epigraph.lang.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static io.epigraph.lang.lexer.EpigraphElementTypes.E_FQN_TYPE_REF;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaAnnotator implements Annotator {
  // TODO change most of annotations to inspections? See http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/code_inspections_and_intentions.html
  // TODO unnecessary backticks (with quickfix?)

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    element.accept(new EpigraphVisitor() {

      @Override
      public void visitFieldDecl(@NotNull EpigraphFieldDecl fieldDecl) {
        PsiElement id = fieldDecl.getQid();
        setHighlighting(id, holder, SchemaSyntaxHighlighter.FIELD);

        String namingError = NamingConventions.validateFieldName(id.getText());
        if (namingError != null) {
          holder.createErrorAnnotation(id, namingError);
        }

        PsiElement override = fieldDecl.getOverride();
        if (override != null && TypeMembers.getOverridenFields(fieldDecl).isEmpty()) {
          holder.createErrorAnnotation(override, SchemaBundle.message("annotator.field.overrides.nothing"));
        }
      }

      @Override
      public void visitVarTagDecl(@NotNull EpigraphVarTagDecl memberDecl) {
        PsiElement id = memberDecl.getQid();
        setHighlighting(id, holder, SchemaSyntaxHighlighter.VAR_MEMBER);

        String namingError = NamingConventions.validateVarTypeTagName(id.getText());
        if (namingError != null)
          holder.createErrorAnnotation(id, namingError);

        PsiElement override = memberDecl.getOverride();
        if (override != null && TypeMembers.getOverridenTags(memberDecl).isEmpty()) {
          holder.createErrorAnnotation(override, SchemaBundle.message("annotator.tag.overrides.nothing"));
        }
      }

      @Override
      public void visitDefaultOverride(@NotNull EpigraphDefaultOverride defaultOverride) {
        EpigraphVarTagRef varTagRef = defaultOverride.getVarTagRef();
        if (varTagRef != null) {
          PsiElement id = varTagRef.getQid();
          setHighlighting(id, holder, SchemaSyntaxHighlighter.VAR_MEMBER);
        }
      }

      @Override
      public void visitTypeDef(@NotNull EpigraphTypeDef typeDef) {
        PsiElement id = typeDef.getNameIdentifier();
        if (id != null) {
          setHighlighting(id, holder, SchemaSyntaxHighlighter.DECL_TYPE_NAME);

          String typeName = typeDef.getName();
          if (typeName != null) {
            Fqn shortTypeNameFqn = new Fqn(typeName);
            Fqn fullTypeNameFqn = typeDef.getFqn();

            String namingError = NamingConventions.validateTypeName(typeName);
            if (namingError != null) {
              holder.createErrorAnnotation(id, namingError);
            }

            // check if it hides an import
            List<Fqn> importsBySuffix = ImportsManager.findImportsBySuffix((SchemaFile) typeDef.getContainingFile(), shortTypeNameFqn);
            if (!importsBySuffix.isEmpty()) {
              Fqn importFqn = importsBySuffix.iterator().next();
              boolean isImplicit = ImportsManager.DEFAULT_IMPORTS_LIST.contains(importFqn);
              holder.createWarningAnnotation(id,
                  SchemaBundle.message(isImplicit ?
                          "annotator.type.shadowed.by.implicit.import" :
                          "annotator.type.shadowed.by.import",
                      typeName, importFqn));
            }

            // check if's already defined
            List<EpigraphTypeDef> typeDefs = SchemaIndexUtil.findTypeDefs(element.getProject(), new Fqn[]{fullTypeNameFqn}, SchemaSearchScopeUtil.getSearchScope(typeDef));
            if (typeDefs.size() > 1) {
              holder.createErrorAnnotation(id, SchemaBundle.message("annotator.type.already.defined", fullTypeNameFqn));
            }

            // check for circular inheritance
            HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(element.getProject());
            List<EpigraphTypeDef> typeParents = hierarchyCache.getTypeParents(typeDef);
            if (typeParents.contains(typeDef)) {
              holder.createErrorAnnotation(id, SchemaBundle.message("annotator.circular.inheritance"));
            }
          }
        }
      }

//      @Override
//      public void visitVarTypeDef(@NotNull SchemaVarTypeDef varTypeDef) {
//        visitTypeDef(varTypeDef);
//      }
//
//      @Override
//      public void visitRecordTypeDef(@NotNull SchemaRecordTypeDef recordTypeDef) {
//        visitTypeDef(recordTypeDef);
//        // TODO check that non-abstract record type def has no (+inherited) abstract fields
//      }

      @Override
      public void visitExtendsDecl(@NotNull EpigraphExtendsDecl epigraphExtendsDecl) {
        EpigraphTypeDef typeDef = (EpigraphTypeDef) epigraphExtendsDecl.getParent();
        if (typeDef == null) return;

        List<EpigraphFqnTypeRef> typeRefList = epigraphExtendsDecl.getFqnTypeRefList();
        for (EpigraphFqnTypeRef fqnTypeRef : typeRefList) {
          boolean wrongKind = false;

          EpigraphTypeDef parent = fqnTypeRef.resolve();
          if (parent != null) {
            if (typeDef.getKind() != parent.getKind()) wrongKind = true;
          }

          if (wrongKind)
            holder.createErrorAnnotation(fqnTypeRef, SchemaBundle.message("annotator.wrong.parent.type.kind"));
        }
      }

      @Override
      public void visitSupplementsDecl(@NotNull EpigraphSupplementsDecl o) {
        // TODO similar to the above
      }

      @Override
      public void visitSupplementDef(@NotNull EpigraphSupplementDef supplementDef) {
        // TODO check types compatibility (and circular inheritance?)
      }

      @Override
      public void visitCustomParam(@NotNull EpigraphCustomParam customParam) {
        setHighlighting(customParam.getQid(), holder, SchemaSyntaxHighlighter.PARAM_NAME);
      }

      @Override
      public void visitFqnTypeRef(@NotNull EpigraphFqnTypeRef typeRef) {
        EpigraphFqn fqn = typeRef.getFqn();
        highlightFqn(fqn, holder, new ImportTypeIntentionFix(typeRef));
      }

      @Override
      public void visitFqn(@NotNull EpigraphFqn fqn) {
        PsiElement parent = fqn.getParent();
        // TODO don't check ref in the namespace decl?
        if (parent.getNode().getElementType() != E_FQN_TYPE_REF) {
          highlightFqn(fqn, holder, null);
        }
      }

      @Override
      public void visitVarTagRef(@NotNull EpigraphVarTagRef tagRef) {
        PsiReference reference = tagRef.getReference();
        if (reference == null || reference.resolve() == null) {
          holder.createErrorAnnotation(tagRef.getNode(), SchemaBundle.message("annotator.unresolved.reference"));
        }
      }
    });
  }

  private void validateExtendsList(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphAnonList anonList) {
    // TODO check types compatibility, lists are covariant?
  }

  private void validateExtendsMap(@NotNull EpigraphTypeDef typeDef, @NotNull EpigraphAnonMap anonMap) {
    // TODO check types compatibility, maps are covariant?
  }

//  private void validateExtends(@NotNull SchemaTypeDefElement typeDef, @NotNull SchemaTypeDef parent) {
//    // TODO
//  }

  private void highlightFqn(@Nullable EpigraphFqn epigraphFqn, @NotNull AnnotationHolder holder,
                            @Nullable IntentionAction unresolvedTypeRefFix) {
    if (epigraphFqn != null) {
//      setHighlighting(schemaFqn.getLastChild(), holder, SchemaSyntaxHighlighter.TYPE_REF);

      PsiPolyVariantReference reference = (PsiPolyVariantReference) epigraphFqn.getLastChild().getReference();
      if (reference == null) return;
//      assert reference != null;

//      if (reference.resolve() == null) {
        ResolveResult[] resolveResults = reference.multiResolve(false);
        List<String> typeDefFqns = new ArrayList<>();
        for (ResolveResult resolveResult : resolveResults) {
          if (resolveResult.getElement() instanceof EpigraphTypeDef)
            typeDefFqns.add(SchemaPresentationUtil.getName((PsiNamedElement) resolveResult.getElement(), true));
        }

        if (resolveResults.length == 0) {
          Annotation annotation = holder.createErrorAnnotation(epigraphFqn.getNode(),
              SchemaBundle.message("annotator.unresolved.reference"));

          if (unresolvedTypeRefFix != null)
            annotation.registerFix(unresolvedTypeRefFix);
        } else if (typeDefFqns.size() > 1) {
          Annotation annotation = holder.createErrorAnnotation(epigraphFqn.getNode(), SchemaBundle.message("annotator.ambiguous.type.reference"));
          StringBuilder tooltipText = new StringBuilder(SchemaBundle.message("annotator.ambiguous.type.reference.candidates"));
          for (String typeDefFqn : typeDefFqns) {
            tooltipText.append('\n');
            tooltipText.append("''").append(typeDefFqn).append("''");
          }
          annotation.setTooltip(tooltipText.toString());
        } // else we have import prefix matching varTypeple namespaces, OK
//      }
    }
  }

  private static Annotation setHighlighting(@NotNull PsiElement element, @NotNull AnnotationHolder holder,
                                            @NotNull TextAttributesKey key) {
    Annotation annotation = holder.createInfoAnnotation(element, null);
    annotation.setEnforcedTextAttributes(EditorColorsManager.getInstance().getGlobalScheme().getAttributes(key));
    return annotation;
  }
}
