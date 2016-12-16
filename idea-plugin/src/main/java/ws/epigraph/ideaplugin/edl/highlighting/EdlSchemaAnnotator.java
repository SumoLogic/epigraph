/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.ideaplugin.edl.highlighting;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.*;
import ws.epigraph.ideaplugin.edl.EdlBundle;
import ws.epigraph.ideaplugin.edl.brains.ImportsManager;
import ws.epigraph.ideaplugin.edl.brains.hierarchy.HierarchyCache;
import ws.epigraph.ideaplugin.edl.brains.hierarchy.TypeMembers;
import ws.epigraph.ideaplugin.edl.features.actions.fixes.ImportTypeIntentionFix;
import ws.epigraph.ideaplugin.edl.index.EdlIndexUtil;
import ws.epigraph.ideaplugin.edl.index.EdlSearchScopeUtil;
import ws.epigraph.ideaplugin.edl.presentation.EdlPresentationUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.NamingConventions;
import ws.epigraph.edl.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ws.epigraph.lang.DefaultImports.DEFAULT_IMPORTS_LIST;
import static ws.epigraph.edl.lexer.EdlElementTypes.E_QN_TYPE_REF;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlSchemaAnnotator extends EdlAnnotator {
  // TODO change most of annotations to inspections? See http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/code_inspections_and_intentions.html
  // TODO unnecessary backticks (with quickfix?)

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    element.accept(new EdlVisitor() {

      @Override
      public void visitFieldDecl(@NotNull EdlFieldDecl fieldDecl) {
        EdlQid qid = fieldDecl.getQid();
        setHighlighting(qid, holder, EdlSyntaxHighlighter.FIELD);

        String namingError = NamingConventions.validateFieldName(qid.getText());
        if (namingError != null) {
          holder.createErrorAnnotation(qid, namingError);
        }

        PsiElement override = fieldDecl.getOverride();
        if (override != null && TypeMembers.getOverridenFields(fieldDecl).isEmpty()) {
          holder.createErrorAnnotation(override, EdlBundle.message("annotator.field.overrides.nothing"));
        }
      }

      @Override
      public void visitVarTagDecl(@NotNull EdlVarTagDecl memberDecl) {
        PsiElement id = memberDecl.getQid();
        setHighlighting(id, holder, EdlSyntaxHighlighter.VAR_MEMBER);

        String namingError = NamingConventions.validateVarTypeTagName(id.getText());
        if (namingError != null)
          holder.createErrorAnnotation(id, namingError);

        PsiElement override = memberDecl.getOverride();
        if (override != null && TypeMembers.getOverridenTags(memberDecl).isEmpty()) {
          holder.createErrorAnnotation(override, EdlBundle.message("annotator.tag.overrides.nothing"));
        }
      }

      @Override
      public void visitDefaultOverride(@NotNull EdlDefaultOverride defaultOverride) {
        EdlVarTagRef varTagRef = defaultOverride.getVarTagRef();
        PsiElement id = varTagRef.getQid();
        setHighlighting(id, holder, EdlSyntaxHighlighter.VAR_MEMBER);
      }

      @Override
      public void visitValueTypeRef(@NotNull EdlValueTypeRef valueTypeRef) {

        EdlDefaultOverride defaultOverride = valueTypeRef.getDefaultOverride();

        if (defaultOverride != null) {
          if (!TypeMembers.canHaveDefault(valueTypeRef))
            holder.createErrorAnnotation(defaultOverride, EdlBundle.message("annotator.default.override.non.var"));
        } else {
          EdlVarTagDecl defaultTag = TypeMembers.getEffectiveDefault(valueTypeRef);
//          if (defaultTag == null && TypeMembers.canHaveDefault(valueTypeRef)) {
//            Annotation annotation = holder.createWeakWarningAnnotation(valueTypeRef, EdlBundle.message("annotator.default.override.missing"));
//            annotation.registerFix(new AddDefaultAction());
//          }
        }
      }

      @Override
      public void visitTypeDef(@NotNull EdlTypeDef typeDef) {
        PsiElement id = typeDef.getNameIdentifier();
        if (id != null) {
          setHighlighting(id, holder, EdlSyntaxHighlighter.DECL_TYPE_NAME);

          String typeName = typeDef.getName();
          if (typeName != null) {
            Qn shortTypeQn = new Qn(typeName);
            Qn fullTypeNameQn = typeDef.getQn();

            String namingError = NamingConventions.validateTypeName(typeName);
            if (namingError != null) {
              holder.createErrorAnnotation(id, namingError);
            }

            // check if it hides an import
            List<Qn> importsBySuffix = ImportsManager.findImportsBySuffix((EdlFile) typeDef.getContainingFile(),
                                                                          shortTypeQn
            );
            if (!importsBySuffix.isEmpty()) {
              Qn importQn = importsBySuffix.iterator().next();
              boolean isImplicit = DEFAULT_IMPORTS_LIST.contains(importQn);
              holder.createWarningAnnotation(id,
                  EdlBundle.message(isImplicit ?
                          "annotator.type.shadowed.by.implicit.import" :
                          "annotator.type.shadowed.by.import",
                                       typeName, importQn
                  ));
            }

            // check if's already defined
            List<EdlTypeDef> typeDefs = EdlIndexUtil.findTypeDefs(element.getProject(), new Qn[]{fullTypeNameQn}, EdlSearchScopeUtil.getSearchScope(typeDef));
            if (typeDefs.size() > 1) {
              holder.createErrorAnnotation(id, EdlBundle.message("annotator.type.already.defined", fullTypeNameQn));
            }

            // check for circular inheritance
            HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(element.getProject());
            List<EdlTypeDef> typeParents = hierarchyCache.getTypeParents(typeDef);
            if (typeParents.contains(typeDef)) {
              holder.createErrorAnnotation(id, EdlBundle.message("annotator.circular.inheritance"));
            }
          }
        }
      }

//      @Override
//      public void visitVarTypeDef(@NotNull EdlVarTypeDef varTypeDef) {
//        visitTypeDef(varTypeDef);
//      }
//
//      @Override
//      public void visitRecordTypeDef(@NotNull EdlRecordTypeDef recordTypeDef) {
//        visitTypeDef(recordTypeDef);
//        // TODO check that non-abstract record type def has no (+inherited) abstract fields
//      }

      @Override
      public void visitExtendsDecl(@NotNull EdlExtendsDecl edlExtendsDecl) {
        EdlTypeDef typeDef = (EdlTypeDef) edlExtendsDecl.getParent();
        if (typeDef == null) return;

        List<EdlQnTypeRef> typeRefList = edlExtendsDecl.getQnTypeRefList();
        for (EdlQnTypeRef qnTypeRef : typeRefList) {
          boolean wrongKind = false;

          EdlTypeDef parent = qnTypeRef.resolve();
          if (parent != null) {
            if (typeDef.getKind() != parent.getKind()) wrongKind = true;
          }

          if (wrongKind)
            holder.createErrorAnnotation(qnTypeRef, EdlBundle.message("annotator.wrong.parent.type.kind"));
        }
      }

      @Override
      public void visitSupplementsDecl(@NotNull EdlSupplementsDecl o) {
        // TODO similar to the above
      }

      @Override
      public void visitSupplementDef(@NotNull EdlSupplementDef supplementDef) {
        // TODO check types compatibility (and circular inheritance?)
      }

      @Override
      public void visitAnnotation(@NotNull EdlAnnotation annotation) {
        setHighlighting(annotation.getQid(), holder, EdlSyntaxHighlighter.PARAM_NAME);
      }

      @Override
      public void visitQnTypeRef(@NotNull EdlQnTypeRef typeRef) {
        EdlQn qn = typeRef.getQn();
        highlightQn(qn, holder, new ImportTypeIntentionFix(typeRef));
      }

      @Override
      public void visitQn(@NotNull EdlQn qn) {
        PsiElement parent = qn.getParent();
        // TODO don't check ref in the namespace decl?
        if (parent.getNode().getElementType() != E_QN_TYPE_REF) {
          highlightQn(qn, holder, null);
        }
      }

      @Override
      public void visitVarTagRef(@NotNull EdlVarTagRef tagRef) {
        PsiReference reference = tagRef.getReference();
        if (reference == null || reference.resolve() == null) {
          holder.createErrorAnnotation(tagRef.getNode(), EdlBundle.message("annotator.unresolved.reference"));
        }
      }
    });
  }

  private void validateExtendsList(@NotNull EdlTypeDef typeDef, @NotNull EdlAnonList anonList) {
    // TODO check types compatibility, lists are covariant?
  }

  private void validateExtendsMap(@NotNull EdlTypeDef typeDef, @NotNull EdlAnonMap anonMap) {
    // TODO check types compatibility, maps are covariant?
  }

//  private void validateExtends(@NotNull EdlTypeDefElement typeDef, @NotNull EdlTypeDef parent) {
//    // TODO
//  }

  private void highlightQn(@Nullable EdlQn edlQn, @NotNull AnnotationHolder holder,
                            @Nullable IntentionAction unresolvedTypeRefFix) {
    if (edlQn != null) {
//      setHighlighting(edlQn.getLastChild(), holder, EdlSyntaxHighlighter.TYPE_REF);

      PsiPolyVariantReference reference = (PsiPolyVariantReference) edlQn.getLastChild().getReference();
      assert reference != null;

//      if (reference.resolve() == null) {
      ResolveResult[] resolveResults = reference.multiResolve(false);
      List<String> typeDefQns = new ArrayList<>();
      for (ResolveResult resolveResult : resolveResults) {
        if (resolveResult.getElement() instanceof EdlTypeDef)
          typeDefQns.add(EdlPresentationUtil.getName((PsiNamedElement) resolveResult.getElement(), true));
      }

      if (resolveResults.length == 0) {
        Annotation annotation = holder.createErrorAnnotation(edlQn.getNode(),
                                                             EdlBundle.message("annotator.unresolved.reference"));

        if (unresolvedTypeRefFix != null)
          annotation.registerFix(unresolvedTypeRefFix);
      } else if (typeDefQns.size() > 1) {
        Annotation annotation = holder.createErrorAnnotation(edlQn.getNode(), EdlBundle.message("annotator.ambiguous.type.reference"));
        StringBuilder tooltipText = new StringBuilder(EdlBundle.message("annotator.ambiguous.type.reference.candidates"));
        for (String typeDefQn : typeDefQns) {
          tooltipText.append('\n');
          tooltipText.append("''").append(typeDefQn).append("''");
        }
        annotation.setTooltip(tooltipText.toString());
      } // else we have import prefix matching varTypeple namespaces, OK
//      }
    }
  }

}
