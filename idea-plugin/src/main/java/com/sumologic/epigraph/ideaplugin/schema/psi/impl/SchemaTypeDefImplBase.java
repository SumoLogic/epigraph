package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager;
import com.sumologic.epigraph.ideaplugin.schema.psi.*;
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaTypeDefStubBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.S_ABSTRACT;
import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.S_ID;
import static com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes.S_POLYMORPHIC;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public abstract class SchemaTypeDefImplBase<S extends SchemaTypeDefStubBase<T>, T extends SchemaTypeDef>
    extends StubBasedPsiElementBase<S> implements SchemaTypeDef {

  public SchemaTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  public SchemaTypeDefImplBase(@NotNull S stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @Override
  @Nullable
  public SchemaExtendsDecl getExtendsDecl() {
    return findChildByClass(SchemaExtendsDecl.class);
  }

  @Override
  @Nullable
  public SchemaMetaDecl getMetaDecl() {
    return findChildByClass(SchemaMetaDecl.class);
  }

  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(S_ABSTRACT);
  }

  @Nullable
  public PsiElement getPolymorphic() {
    return findChildByType(S_POLYMORPHIC);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(S_ID);
  }

  @Nullable
  public String getName() {
    S stub = getStub();
    if (stub != null) {
      return stub.getName();
    }

    PsiElement id = getId();
    return id == null ? null : id.getText();
  }

  @Nullable
  public PsiElement setName(@NotNull String name) {
    PsiElement id = getId();
    if (id == null) return null;
    else {
      PsiElement newId = SchemaElementFactory.createId(getProject(), name);
      id.replace(newId);
      return id;
    }
  }

  @Nullable
  public PsiElement getNameIdentifier() {
    return getId();
  }

  @Nullable
  @Override
  public String getNamespace() {
    S stub = getStub();
    if (stub != null) {
      return stub.getNamespace();
    }

    return NamespaceManager.getNamespace(this);
  }

  public int getTextOffset() {
    PsiElement nameIdentifier = getNameIdentifier();
    return nameIdentifier == null ? 0 : nameIdentifier.getTextOffset();
  }

  @NotNull
  public abstract TypeKind getKind();

  public Icon getIcon(int flags) {
    return AllIcons.Nodes.Class; // TODO our own icon
  }

  @Override
  public void delete() throws IncorrectOperationException {
    getParent().delete();
  }

  @NotNull
  public List<SchemaTypeDef> parents() {
    SchemaExtendsDecl extendsDecl = getExtendsDecl();
    if (extendsDecl == null) return Collections.emptyList();
    List<SchemaTypeRef> typeRefList = extendsDecl.getTypeRefList();
    if (typeRefList.isEmpty()) return Collections.emptyList();

    List<SchemaTypeDef> result = new ArrayList<>(typeRefList.size());
    for (SchemaTypeRef typeRef : typeRefList) {
      SchemaFqnTypeRef fqnTypeRef = typeRef.getFqnTypeRef();
      if (fqnTypeRef != null) {
        PsiReference reference = SchemaPsiImplUtil.getReference(fqnTypeRef);
        if (reference != null) {
          PsiElement resolved = reference.resolve();
          if (resolved instanceof SchemaTypeDef) {
            result.add((SchemaTypeDef) resolved);
          }
        }
      }
    }
    return result;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + getNode().getElementType().toString() + ")";
  }
}
