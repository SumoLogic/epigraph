/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.ideaplugin.schema.highlighting;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.psi.*;
import ws.epigraph.ideaplugin.schema.SchemaBundle;
import ws.epigraph.ideaplugin.schema.brains.ImportsManager;
import ws.epigraph.ideaplugin.schema.brains.hierarchy.HierarchyCache;
import ws.epigraph.ideaplugin.schema.brains.hierarchy.TypeMembers;
import ws.epigraph.ideaplugin.schema.features.actions.fixes.ImportTypeIntentionFix;
import ws.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import ws.epigraph.ideaplugin.schema.index.SchemaSearchScopeUtil;
import ws.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.NamingConventions;
import ws.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static ws.epigraph.lang.DefaultImports.DEFAULT_IMPORTS_LIST;
import static ws.epigraph.schema.lexer.SchemaElementTypes.S_QN_TYPE_REF;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaAnnotator extends SchemaAnnotatorBase {
  // TODO change most of annotations to inspections? See http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/code_inspections_and_intentions.html
  // TODO unnecessary backticks (with quickfix?)

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    element.accept(new SchemaVisitor() {

      @Override
      public void visitFieldDecl(@NotNull SchemaFieldDecl fieldDecl) {
        SchemaQid qid = fieldDecl.getQid();
        setHighlighting(qid, holder, SchemaSyntaxHighlighter.FIELD);

        String namingError = NamingConventions.validateFieldName(qid.getText());
        if (namingError != null) {
          holder.createErrorAnnotation(qid, namingError);
        }

        PsiElement override = fieldDecl.getOverride();
        if (override != null && TypeMembers.getOverridenFields(fieldDecl).isEmpty()) {
          holder.createErrorAnnotation(override, SchemaBundle.message("annotator.field.overrides.nothing"));
        }
      }

      @Override
      public void visitVarTagDecl(@NotNull SchemaVarTagDecl memberDecl) {
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
      public void visitRetroDecl(@NotNull final SchemaRetroDecl retroDecl) {
        SchemaVarTagRef varTagRef = retroDecl.getVarTagRef();
        PsiElement id = varTagRef.getQid();
        setHighlighting(id, holder, SchemaSyntaxHighlighter.VAR_MEMBER);
      }

      @Override
      public void visitValueTypeRef(@NotNull SchemaValueTypeRef valueTypeRef) {

        SchemaRetroDecl retroDecl = valueTypeRef.getRetroDecl();

        if (retroDecl != null) {
          if (!TypeMembers.canHaveRetro(valueTypeRef))
            holder.createErrorAnnotation(retroDecl, SchemaBundle.message("annotator.retro.non.var"));
        } else {
          SchemaVarTagDecl defaultTag = TypeMembers.getEffectiveRetro(valueTypeRef);
//          if (defaultTag == null && TypeMembers.canHaveDefault(valueTypeRef)) {
//            Annotation annotation = holder.createWeakWarningAnnotation(valueTypeRef, SchemaBundle.message("annotator.default.override.missing"));
//            annotation.registerFix(new AddDefaultAction());
//          }
        }
      }

      @Override
      public void visitTypeDef(@NotNull SchemaTypeDef typeDef) {
        PsiElement id = typeDef.getNameIdentifier();
        if (id != null) {
          setHighlighting(id, holder, SchemaSyntaxHighlighter.DECL_TYPE_NAME);

          String typeName = typeDef.getName();
          if (typeName != null) {
            Qn shortTypeQn = new Qn(typeName);
            Qn fullTypeNameQn = typeDef.getQn();

            String namingError = NamingConventions.validateTypeName(typeName);
            if (namingError != null) {
              holder.createErrorAnnotation(id, namingError);
            }

            // check if it hides an import
            List<Qn> importsBySuffix = ImportsManager.findImportsBySuffix((SchemaFile) typeDef.getContainingFile(),
                                                                          shortTypeQn
            );
            if (!importsBySuffix.isEmpty()) {
              Qn importQn = importsBySuffix.iterator().next();
              boolean isImplicit = DEFAULT_IMPORTS_LIST.contains(importQn);
              holder.createWarningAnnotation(id,
                  SchemaBundle.message(isImplicit ?
                          "annotator.type.shadowed.by.implicit.import" :
                          "annotator.type.shadowed.by.import",
                                       typeName, importQn
                  ));
            }

            // check if's already defined
            List<SchemaTypeDef> typeDefs = SchemaIndexUtil.findTypeDefs(element.getProject(), new Qn[]{fullTypeNameQn}, SchemaSearchScopeUtil.getSearchScope(typeDef));
            if (typeDefs.size() > 1) {
              holder.createErrorAnnotation(id, SchemaBundle.message("annotator.type.already.defined", fullTypeNameQn));
            }

            // check for circular inheritance
            HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(element.getProject());
            List<SchemaTypeDef> typeParents = hierarchyCache.getTypeParents(typeDef);
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
      public void visitExtendsDecl(@NotNull SchemaExtendsDecl schemaExtendsDecl) {
        SchemaTypeDef typeDef = (SchemaTypeDef) schemaExtendsDecl.getParent();
        if (typeDef == null) return;

        List<SchemaQnTypeRef> typeRefList = schemaExtendsDecl.getQnTypeRefList();
        for (SchemaQnTypeRef qnTypeRef : typeRefList) {
          boolean wrongKind = false;

          SchemaTypeDef parent = qnTypeRef.resolve();
          if (parent != null) {
            if (typeDef.getKind() != parent.getKind()) wrongKind = true;
          }

          if (wrongKind)
            holder.createErrorAnnotation(qnTypeRef, SchemaBundle.message("annotator.wrong.parent.type.kind"));
        }
      }

      @Override
      public void visitSupplementsDecl(@NotNull SchemaSupplementsDecl o) {
        // TODO similar to the above
      }

      @Override
      public void visitSupplementDef(@NotNull SchemaSupplementDef supplementDef) {
        // TODO check types compatibility (and circular inheritance?)
      }

      @Override
      public void visitAnnotation(@NotNull SchemaAnnotation annotation) {
        setHighlighting(annotation.getQid(), holder, SchemaSyntaxHighlighter.PARAM_NAME);
      }

      @Override
      public void visitQnTypeRef(@NotNull SchemaQnTypeRef typeRef) {
        SchemaQn qn = typeRef.getQn();
        highlightQn(qn, holder, new ImportTypeIntentionFix(typeRef));
      }

      @Override
      public void visitQn(@NotNull SchemaQn qn) {
        PsiElement parent = qn.getParent();
        // TODO don't check ref in the namespace decl?
        if (parent.getNode().getElementType() != S_QN_TYPE_REF) {
          highlightQn(qn, holder, null);
        }
      }

      @Override
      public void visitVarTagRef(@NotNull SchemaVarTagRef tagRef) {
        PsiReference reference = tagRef.getReference();
        if (reference == null || reference.resolve() == null) {
          holder.createErrorAnnotation(tagRef.getNode(), SchemaBundle.message("annotator.unresolved.reference"));
        }
      }

      @Override
      public void visitOpInputVarProjectionRef(@NotNull final SchemaOpInputVarProjectionRef o) {
        final SchemaQid qid = o.getQid();
        if (qid != null) setHighlighting(qid, holder, SchemaSyntaxHighlighter.PROJECTION_REF);
      }
      
      @Override
      public void visitOpOutputVarProjectionRef(@NotNull final SchemaOpOutputVarProjectionRef o) {
        final SchemaQid qid = o.getQid();
        if (qid != null) setHighlighting(qid, holder, SchemaSyntaxHighlighter.PROJECTION_REF);
      }

      @Override
      public void visitOpDeleteVarProjectionRef(@NotNull final SchemaOpDeleteVarProjectionRef o) {
        final SchemaQid qid = o.getQid();
        if (qid != null) setHighlighting(qid, holder, SchemaSyntaxHighlighter.PROJECTION_REF);
      }
    });
  }

  private void validateExtendsList(@NotNull SchemaTypeDef typeDef, @NotNull SchemaAnonList anonList) {
    // TODO check types compatibility, lists are covariant?
  }

  private void validateExtendsMap(@NotNull SchemaTypeDef typeDef, @NotNull SchemaAnonMap anonMap) {
    // TODO check types compatibility, maps are covariant?
  }

//  private void validateExtends(@NotNull SchemaTypeDefElement typeDef, @NotNull SchemaTypeDef parent) {
//    // TODO
//  }

  private void highlightQn(@Nullable SchemaQn schemaQn, @NotNull AnnotationHolder holder,
                            @Nullable IntentionAction unresolvedTypeRefFix) {
    if (schemaQn != null) {
//      setHighlighting(schemaQn.getLastChild(), holder, SchemaSyntaxHighlighter.TYPE_REF);

      PsiPolyVariantReference reference = (PsiPolyVariantReference) schemaQn.getLastChild().getReference();
      assert reference != null;

//      if (reference.resolve() == null) {
      ResolveResult[] resolveResults = reference.multiResolve(false);
      List<String> typeDefQns = new ArrayList<>();
      for (ResolveResult resolveResult : resolveResults) {
        if (resolveResult.getElement() instanceof SchemaTypeDef)
          typeDefQns.add(SchemaPresentationUtil.getName((PsiNamedElement) resolveResult.getElement(), true));
      }

      if (resolveResults.length == 0) {
        Annotation annotation = holder.createErrorAnnotation(schemaQn.getNode(),
                                                             SchemaBundle.message("annotator.unresolved.reference"));

        if (unresolvedTypeRefFix != null)
          annotation.registerFix(unresolvedTypeRefFix);
      } else if (typeDefQns.size() > 1) {
        Annotation annotation = holder.createErrorAnnotation(schemaQn.getNode(), SchemaBundle.message("annotator.ambiguous.type.reference"));
        StringBuilder tooltipText = new StringBuilder(SchemaBundle.message("annotator.ambiguous.type.reference.candidates"));
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
