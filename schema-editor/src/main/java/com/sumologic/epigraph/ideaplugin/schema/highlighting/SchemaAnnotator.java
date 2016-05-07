package com.sumologic.epigraph.ideaplugin.schema.highlighting;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.sumologic.epigraph.ideaplugin.schema.brains.NamingConventions;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.S_TYPE_REF;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    element.accept(new SchemaVisitor() {
      @Override
      public void visitFieldDecl(@NotNull SchemaFieldDecl fieldDecl) {
        PsiElement id = fieldDecl.getId();
        setHighlighting(id, holder, SchemaSyntaxHighlighter.FIELD);

        String namingError = NamingConventions.validateFieldName(id.getText());
        if (namingError != null) {
          holder.createErrorAnnotation(id, namingError);
        }
      }

      @Override
      public void visitMultiMemberDecl(@NotNull SchemaMultiMemberDecl memberDecl) {
        PsiElement id = memberDecl.getId();
        setHighlighting(id, holder, SchemaSyntaxHighlighter.MULTI_MEMBER);

//        if (memberDecl.getDefault() != null &&
//            PsiTreeUtil.getPrevSiblingOfType(memberDecl, SchemaMultiMemberDecl.class) != null) {
//          holder.createWarningAnnotation(memberDecl, "Default alias should be the first one");
//        }

        String namingError = NamingConventions.validateMultiMemberName(id.getText());
        if (namingError != null)
          holder.createErrorAnnotation(id, namingError);
      }

      @Override
      public void visitUnionTypeDef(@NotNull SchemaUnionTypeDef unionTypeDef) {
        SchemaUnionTypeBody unionTypeBody = unionTypeDef.getUnionTypeBody();
        boolean noTags = unionTypeBody == null;
        if (unionTypeBody != null) {
          noTags = unionTypeBody.getTagDeclList().size() == 0;
        }

        if (noTags)
          holder.createErrorAnnotation(unionTypeDef, "Union must declare at least one tag");
      }

      @Override
      public void visitTagDecl(@NotNull SchemaTagDecl tagDecl) {
        PsiElement id = tagDecl.getId();
        setHighlighting(id, holder, SchemaSyntaxHighlighter.TAG);

        String namingError = NamingConventions.validateTagName(id.getText());
        if (namingError != null) {
          holder.createErrorAnnotation(id, namingError);
        }
      }

      @Override
      public void visitDefaultOverride(@NotNull SchemaDefaultOverride defaultOverride) {
        setHighlighting(defaultOverride.getId(), holder, SchemaSyntaxHighlighter.MULTI_MEMBER);
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
        }
      }

      @Override
      public void visitCustomParam(@NotNull SchemaCustomParam customParam) {
        setHighlighting(customParam.getId(), holder, SchemaSyntaxHighlighter.PARAM_NAME);
      }

      @Override
      public void visitFqnTypeRef(@NotNull SchemaFqnTypeRef typeRef) {
        highlightTyperef(typeRef, holder);
      }

      @Override
      public void visitFqn(@NotNull SchemaFqn fqn) {
        PsiElement parent = fqn.getParent();
        if (parent.getNode().getElementType() != S_TYPE_REF) {
          highlightFqn(fqn, holder);
        }
      }
    });
  }

  private void highlightTyperef(@Nullable SchemaFqnTypeRef typeRef, @NotNull AnnotationHolder holder) {
    if (typeRef != null) {
      SchemaFqn fqn = typeRef.getFqn();
      highlightFqn(fqn, holder);
      PsiReference reference = fqn.getLastChild().getReference();
      if (reference != null) {
        PsiElement resolve = reference.resolve();
        if (!(resolve instanceof SchemaTypeDef)) {
          holder.createErrorAnnotation(typeRef.getNode(), "Unresolved reference");
        }
      }
    }
  }

  private void highlightFqn(@Nullable SchemaFqn schemaFqn, @NotNull AnnotationHolder holder) {
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
          holder.createErrorAnnotation(schemaFqn.getNode(), "Unresolved reference");
        } else if (numTypeRefs > 1) {
          holder.createErrorAnnotation(schemaFqn.getNode(), "Ambiguous type reference");
        } // else we have import prefix matching multiple namespaces, OK
      }
    }
  }

  private static void setHighlighting(@NotNull PsiElement element, @NotNull AnnotationHolder holder,
                                      @NotNull TextAttributesKey key) {
    holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(
        EditorColorsManager.getInstance().getGlobalScheme().getAttributes(key));
  }
}
