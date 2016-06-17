package com.sumologic.epigraph.ideaplugin.schema.highlighting;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.sumologic.epigraph.ideaplugin.schema.actions.ImportTypeIntentionFix;
import com.sumologic.epigraph.ideaplugin.schema.brains.SchemaFqnReferenceResolver;
import com.sumologic.epigraph.schema.parser.Fqn;
import com.sumologic.epigraph.schema.parser.psi.*;
import com.sumologic.epigraph.schema.parser.NamingConventions;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.HierarchyCache;
import com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy.TypeMembers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.sumologic.epigraph.schema.parser.lexer.SchemaElementTypes.S_FQN_TYPE_REF;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaAnnotator implements Annotator {
  // TODO highlight clashing imports, e.g import foo.bar, import baz.bar

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    element.accept(new SchemaVisitor() {
      @Override
      public void visitFieldDecl(@NotNull SchemaFieldDecl fieldDecl) {
        PsiElement id = fieldDecl.getQid();
        setHighlighting(id, holder, SchemaSyntaxHighlighter.FIELD);

        String namingError = NamingConventions.validateFieldName(id.getText());
        if (namingError != null) {
          holder.createErrorAnnotation(id, namingError);
        }

        PsiElement override = fieldDecl.getOverride();
        if (override != null && TypeMembers.getOverridenFields(fieldDecl).isEmpty()) {
          holder.createErrorAnnotation(override, "Field overrides nothing");
        }
      }

      @Override
      public void visitVarTagDecl(@NotNull SchemaVarTagDecl memberDecl) {
        PsiElement id = memberDecl.getQid();
        setHighlighting(id, holder, SchemaSyntaxHighlighter.VAR_MEMBER);

        String namingError = NamingConventions.validateVarTypeMemberName(id.getText());
        if (namingError != null)
          holder.createErrorAnnotation(id, namingError);

        PsiElement override = memberDecl.getOverride();
        if (override != null && TypeMembers.getOverridenTags(memberDecl).isEmpty()) {
          holder.createErrorAnnotation(override, "Tag overrides nothing");
        }
      }

      @Override
      public void visitDefaultOverride(@NotNull SchemaDefaultOverride defaultOverride) {
        SchemaVarTagRef varTagRef = defaultOverride.getVarTagRef();
        if (varTagRef != null) {
          PsiElement id = varTagRef.getQid();
          setHighlighting(id, holder, SchemaSyntaxHighlighter.VAR_MEMBER);
        }
      }

      @Override
      public void visitTypeDef(@NotNull SchemaTypeDef typeDef) {
        PsiElement id = typeDef.getNameIdentifier();
        if (id != null) {
          setHighlighting(id, holder, SchemaSyntaxHighlighter.DECL_TYPE_NAME);

          String namingError = NamingConventions.validateTypeName(id.getText());
          if (namingError != null) {
            holder.createErrorAnnotation(id, namingError);
          }

          SchemaFqnReferenceResolver referenceResolver = SchemaReferenceFactory
              .getFqnReferenceResolver((SchemaFile) typeDef.getContainingFile(), new Fqn(typeDef.getName()), false);
          if (referenceResolver != null && referenceResolver.multiResolve(typeDef.getProject()).length > 1) {
            holder.createErrorAnnotation(id, "Type \"" + typeDef.getName() + "\" is already defined");
          }

          HierarchyCache hierarchyCache = HierarchyCache.getHierarchyCache(element.getProject());
          List<SchemaTypeDef> typeParents = hierarchyCache.getTypeParents(typeDef);
          if (typeParents.contains(typeDef)) {
            holder.createErrorAnnotation(id, "Circular inheritance");
          }
        }
      }

      @Override
      public void visitRecordTypeDef(@NotNull SchemaRecordTypeDef recordTypeDef) {
        visitTypeDef(recordTypeDef);
        // TODO check that non-abstract record type def has no (+inherited) abstract fields
      }

      @Override
      public void visitExtendsDecl(@NotNull SchemaExtendsDecl schemaExtendsDecl) {
        SchemaTypeDef typeDef = (SchemaTypeDef) schemaExtendsDecl.getParent();
        if (typeDef == null) return;

        List<SchemaFqnTypeRef> typeRefList = schemaExtendsDecl.getFqnTypeRefList();
        for (SchemaFqnTypeRef fqnTypeRef : typeRefList) {
          boolean wrongKind = false;

          SchemaTypeDef parent = fqnTypeRef.resolve();
          if (parent != null) {
            if (typeDef.getKind() != parent.getKind()) wrongKind = true;
          }

          if (wrongKind) holder.createErrorAnnotation(fqnTypeRef, "Wrong parent type kind");
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
      public void visitCustomParam(@NotNull SchemaCustomParam customParam) {
        setHighlighting(customParam.getQid(), holder, SchemaSyntaxHighlighter.PARAM_NAME);
      }

      @Override
      public void visitFqnTypeRef(@NotNull SchemaFqnTypeRef typeRef) {
        SchemaFqn fqn = typeRef.getFqn();
        highlightFqn(fqn, holder, new ImportTypeIntentionFix(typeRef));
      }

      @Override
      public void visitFqn(@NotNull SchemaFqn fqn) {
        PsiElement parent = fqn.getParent();
        if (parent.getNode().getElementType() != S_FQN_TYPE_REF) {
          highlightFqn(fqn, holder, null);
        }
      }

      @Override
      public void visitVarTagRef(@NotNull SchemaVarTagRef tagRef) {
        PsiReference reference = tagRef.getReference();
        if (reference == null || reference.resolve() == null) {
          holder.createErrorAnnotation(tagRef.getNode(), "Unresolved reference");
        }
      }
    });
  }

  private void testExtendsList(@NotNull SchemaTypeDef typeDef, @NotNull SchemaAnonList anonList) {
    // TODO check types compatibility, lists are covariant?
  }

  private void testExtendsMap(@NotNull SchemaTypeDef typeDef, @NotNull SchemaAnonMap anonMap) {
    // TODO check types compatibility, maps are covariant?
  }

//  private void testExtends(@NotNull SchemaTypeDefElement typeDef, @NotNull SchemaTypeDef parent) {
//    // TODO
//  }

  private void highlightFqn(@Nullable SchemaFqn schemaFqn, @NotNull AnnotationHolder holder,
                            @Nullable IntentionAction unresolvedTypeRefFix) {
    if (schemaFqn != null) {
//      setHighlighting(schemaFqn.getLastChild(), holder, SchemaSyntaxHighlighter.TYPE_REF);

      PsiPolyVariantReference reference = (PsiPolyVariantReference) schemaFqn.getLastChild().getReference();
      assert reference != null;

      if (reference.resolve() == null) {
        ResolveResult[] resolveResults = reference.multiResolve(false);
        int numTypeRefs = 0;
        for (ResolveResult resolveResult : resolveResults) {
          if (resolveResult.getElement() instanceof SchemaTypeDef)
            numTypeRefs++;
        }

        if (resolveResults.length == 0) {
          Annotation annotation = holder.createErrorAnnotation(schemaFqn.getNode(), "Unresolved reference");
          if (unresolvedTypeRefFix != null)
            annotation.registerFix(unresolvedTypeRefFix);
        } else if (numTypeRefs > 1) {
          holder.createErrorAnnotation(schemaFqn.getNode(), "Ambiguous type reference");
        } // else we have import prefix matching varTypeple namespaces, OK
      }
    }
  }

  private static void setHighlighting(@NotNull PsiElement element, @NotNull AnnotationHolder holder,
                                      @NotNull TextAttributesKey key) {
    holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(
        EditorColorsManager.getInstance().getGlobalScheme().getAttributes(key));
  }
}
