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
import com.sumologic.epigraph.ideaplugin.schema.psi.impl.SchemaPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
      public void visitVarTypeMemberDecl(@NotNull SchemaVarTypeMemberDecl memberDecl) {
        PsiElement id = memberDecl.getId();
        setHighlighting(id, holder, SchemaSyntaxHighlighter.VAR_MEMBER);

        String namingError = NamingConventions.validateVarTypeMemberName(id.getText());
        if (namingError != null)
          holder.createErrorAnnotation(id, namingError);
      }

      @Override
      public void visitDefaultOverride(@NotNull SchemaDefaultOverride defaultOverride) {
        PsiElement id = defaultOverride.getId();
        if (id != null)
          setHighlighting(id, holder, SchemaSyntaxHighlighter.VAR_MEMBER);
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
      public void visitExtendsDecl(@NotNull SchemaExtendsDecl schemaExtendsDecl) {
        SchemaTypeDef typeDef = (SchemaTypeDef) schemaExtendsDecl.getParent();
        if (typeDef == null) return;

        List<SchemaTypeRef> typeRefList = schemaExtendsDecl.getTypeRefList();
        for (SchemaTypeRef typeRef : typeRefList) {
          boolean wrongKind = false;

          if (typeRef.getAnonList() != null) {
            if (typeDef.getKind() != TypeKind.LIST) wrongKind = true;
            testExtendsList(typeDef, typeRef.getAnonList());
          }

          if (typeRef.getAnonMap() != null) {
            if (typeDef.getKind() != TypeKind.MAP) wrongKind = true;
            testExtendsMap(typeDef, typeRef.getAnonMap());
          }

          SchemaFqnTypeRef fqnTypeRef = typeRef.getFqnTypeRef();
          if (fqnTypeRef != null) {
            PsiReference reference = SchemaPsiImplUtil.getReference(fqnTypeRef);
            if (reference != null) {
              SchemaTypeDef parent = (SchemaTypeDef) reference.resolve();
              if (parent != null) {
                if (typeDef.getKind() != parent.getKind()) wrongKind = true;
              }
            }
          }

          if (wrongKind) holder.createErrorAnnotation(typeRef, "Wrong parent type kind");
        }
      }

      // TODO check supplements too

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

  private void testExtendsList(@NotNull SchemaTypeDef typeDef, @NotNull SchemaAnonList anonList) {
    // TODO
  }

  private void testExtendsMap(@NotNull SchemaTypeDef typeDef, @NotNull SchemaAnonMap anonMap) {
    // TODO
  }

  private void testExtends(@NotNull SchemaTypeDef typeDef, @NotNull SchemaTypeDef parent) {
    // TODO
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
