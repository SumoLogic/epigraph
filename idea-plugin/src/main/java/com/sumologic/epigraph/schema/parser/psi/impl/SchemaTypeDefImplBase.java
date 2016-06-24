package com.sumologic.epigraph.schema.parser.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import com.sumologic.epigraph.schema.parser.Fqn;
import com.sumologic.epigraph.schema.parser.psi.*;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaTypeDefStubBase;
import com.sumologic.epigraph.schema.parser.psi.stubs.SerializedFqnTypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.sumologic.epigraph.schema.parser.lexer.SchemaElementTypes.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public abstract class SchemaTypeDefImplBase<S extends SchemaTypeDefStubBase<T>, T extends SchemaTypeDef>
    extends StubBasedPsiElementBase<S> implements SchemaTypeDef {

//  private final static Logger LOG = Logger.getInstance(SchemaTypeDefImplBase.class);

  public SchemaTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  public SchemaTypeDefImplBase(@NotNull S stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public ItemPresentation getPresentation() {
    return SchemaPresentationUtil.getPresentation(this, false);
  }

  @Override
  @Nullable
  public SchemaExtendsDecl getExtendsDecl() {
    return findChildByClass(SchemaExtendsDecl.class);
  }

  @Override
  @Nullable
  public SchemaSupplementsDecl getSupplementsDecl() {
    return findChildByClass(SchemaSupplementsDecl.class);
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
  public SchemaQid getQid() {
    return findChildByType(S_QID);
  }

  @Nullable
  public String getName() {
    S stub = getStub();
    if (stub != null) {
      return stub.getName();
    }

    SchemaQid id = getQid();
    return id == null ? null : id.getCanonicalName();
  }

  @Nullable
  public PsiElement setName(@NotNull String name) {
    SchemaQid id = getQid();
    if (id == null) return null;
    else {
      id.setName(name);
      return this;
    }
  }

  @Nullable
  public PsiElement getNameIdentifier() {
    SchemaQid qid = getQid();
    return qid == null ? null : qid.getId();
  }

  @Nullable
  @Override
  public Fqn getNamespace() {
    S stub = getStub();
    if (stub != null) {
      return Fqn.fromNullableDotSeparated(stub.getNamespace());
    }

    return NamespaceManager.getNamespace(this);
  }

  @Nullable
  @Override
  public Fqn getFqn() {
    String name = getName();
    if (name == null) return null;
    Fqn namespace = getNamespace();
    if (namespace == null) return new Fqn(name);
    return namespace.append(name);
  }

  public int getTextOffset() {
    PsiElement nameIdentifier = getNameIdentifier();
    return nameIdentifier == null ? 0 : nameIdentifier.getTextOffset();
  }

  @NotNull
  public abstract TypeKind getKind();

  public Icon getIcon(int flags) {
    return SchemaPresentationUtil.getIcon(this);
  }

  @Override
  public void delete() throws IncorrectOperationException {
    getParent().delete();
  }

  @NotNull
  public List<SchemaTypeDef> extendsParents() {
    S stub = getStub();

    if (stub != null) {
      List<SerializedFqnTypeRef> extendsTypeRefs = stub.getExtendsTypeRefs();
      if (extendsTypeRefs != null) {
        return extendsTypeRefs.stream()
            .map(tr -> tr.resolveTypeDef(getProject()))
            .filter(i -> i != null)
            .collect(Collectors.toList());
      }
    }

    SchemaExtendsDecl extendsDecl = getExtendsDecl();
    if (extendsDecl == null) return Collections.emptyList();
    List<SchemaFqnTypeRef> typeRefList = extendsDecl.getFqnTypeRefList();
    if (typeRefList.isEmpty()) return Collections.emptyList();

    List<SchemaTypeDef> result = new ArrayList<>(typeRefList.size());
    for (SchemaFqnTypeRef typeRef : typeRefList) {
      SchemaTypeDef resolved = typeRef.resolve();
      if (resolved != null) result.add(resolved);
    }
    return result;
  }

  @Override
  public String toString() {
    return SchemaPresentationUtil.psiToString(this);
  }
}
