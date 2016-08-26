package io.epigraph.lang.parser.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaSearchScopeUtil;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import io.epigraph.lang.parser.Fqn;
import io.epigraph.lang.parser.psi.*;
import io.epigraph.lang.parser.psi.stubs.EpigraphTypeDefStubBase;
import io.epigraph.lang.parser.psi.stubs.SerializedFqnTypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.epigraph.lang.lexer.EpigraphElementTypes.*;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public abstract class EpigraphTypeDefImplBase<S extends EpigraphTypeDefStubBase<T>, T extends EpigraphTypeDef>
    extends StubBasedPsiElementBase<S> implements EpigraphTypeDef {

//  private final static Logger LOG = Logger.getInstance(SchemaTypeDefImplBase.class);

  public EpigraphTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  public EpigraphTypeDefImplBase(@NotNull S stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public ItemPresentation getPresentation() {
    return SchemaPresentationUtil.getPresentation(this, false);
  }

  @Override
  @Nullable
  public EpigraphExtendsDecl getExtendsDecl() {
    return findChildByClass(EpigraphExtendsDecl.class);
  }

  @Override
  @Nullable
  public EpigraphSupplementsDecl getSupplementsDecl() {
    return findChildByClass(EpigraphSupplementsDecl.class);
  }

  @Override
  @Nullable
  public EpigraphMetaDecl getMetaDecl() {
    return findChildByClass(EpigraphMetaDecl.class);
  }

  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(E_ABSTRACT);
  }

  @Nullable
  public PsiElement getPolymorphic() {
    return findChildByType(E_POLYMORPHIC);
  }

  @Override
  @Nullable
  public EpigraphQid getQid() {
    return findChildByType(E_QID);
  }

  @Nullable
  public String getName() {
    S stub = getStub();
    if (stub != null) {
      return stub.getName();
    }

    EpigraphQid id = getQid();
    return id == null ? null : id.getCanonicalName();
  }

  @Nullable
  public PsiElement setName(@NotNull String name) {
    EpigraphQid id = getQid();
    if (id == null) return null;
    else {
      id.setName(name);
      return this;
    }
  }

  @Nullable
  public PsiElement getNameIdentifier() {
    EpigraphQid qid = getQid();
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
  public List<EpigraphTypeDef> extendsParents() {
    S stub = getStub();

    if (stub != null) {
      List<SerializedFqnTypeRef> extendsTypeRefs = stub.getExtendsTypeRefs();
      if (extendsTypeRefs != null) {
        return extendsTypeRefs.stream()
            .map(tr -> tr.resolveTypeDef(getProject(), SchemaSearchScopeUtil.getSearchScope(getContainingFile())))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
      }
    }

    EpigraphExtendsDecl extendsDecl = getExtendsDecl();
    if (extendsDecl == null) return Collections.emptyList();
    List<EpigraphFqnTypeRef> typeRefList = extendsDecl.getFqnTypeRefList();
    if (typeRefList.isEmpty()) return Collections.emptyList();

    List<EpigraphTypeDef> result = new ArrayList<>(typeRefList.size());
    for (EpigraphFqnTypeRef typeRef : typeRefList) {
      EpigraphTypeDef resolved = typeRef.resolve();
      if (resolved != null) result.add(resolved);
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;

    if (obj instanceof EpigraphTypeDef) {
      EpigraphTypeDef other = (EpigraphTypeDef) obj;

      Fqn thisNamespace = getNamespace();
      Fqn otherNamespace = other.getNamespace();

      String thisName = getName();
      String otherName = other.getName();

      if (thisNamespace == null && otherNamespace == null && thisName == null && otherName == null) return false;

      if (thisNamespace == null) {
        if (otherNamespace != null) return false;
      } else if (!thisNamespace.equals(otherNamespace)) return false;


      if (thisName == null) {
        if (otherName != null) return false;
      } else if (!thisName.equals(otherName)) return false;

      return true;
    }

    return false;
  }

  @Override
  public int hashCode() {
    Fqn namespace = getNamespace();
    String name = getName();

    if (namespace == null && name == null) return super.hashCode();

    int hash = 31 + (namespace == null ? 0 : namespace.hashCode());
    hash = 31 * hash + (name == null ? 0 : name.hashCode());
    return hash;
  }

  @Override
  public String toString() {
    return SchemaPresentationUtil.psiToString(this);
  }
}
