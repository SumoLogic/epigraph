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

package ws.epigraph.schema.parser.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import ws.epigraph.ideaplugin.schema.brains.NamespaceManager;
import ws.epigraph.ideaplugin.schema.index.EdlSearchScopeUtil;
import ws.epigraph.ideaplugin.schema.presentation.EdlPresentationUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.schema.parser.psi.stubs.EdlTypeDefStubBase;
import ws.epigraph.schema.parser.psi.stubs.SerializedFqnTypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ws.epigraph.schema.lexer.EdlElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class EdlTypeDefImplBase<S extends EdlTypeDefStubBase<T>, T extends EdlTypeDef>
    extends StubBasedPsiElementBase<S> implements EdlTypeDef {

//  private final static Logger LOG = Logger.getInstance(EdlTypeDefImplBase.class);

  public EdlTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  public EdlTypeDefImplBase(@NotNull S stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public ItemPresentation getPresentation() {
    return EdlPresentationUtil.getPresentation(this, false);
  }

  @Override
  @Nullable
  public EdlExtendsDecl getExtendsDecl() {
    return findChildByClass(EdlExtendsDecl.class);
  }

  @Override
  @Nullable
  public EdlSupplementsDecl getSupplementsDecl() {
    return findChildByClass(EdlSupplementsDecl.class);
  }

  @Override
  @Nullable
  public EdlMetaDecl getMetaDecl() {
    return findChildByClass(EdlMetaDecl.class);
  }

  @Nullable
  public PsiElement getAbstract() {
    return findChildByType(S_ABSTRACT);
  }

  @Override
  @Nullable
  public EdlQid getQid() {
    return findChildByType(S_QID);
  }

  @Nullable
  public String getName() {
    S stub = getStub();
    if (stub != null) {
      return stub.getName();
    }

    EdlQid id = getQid();
    return id == null ? null : id.getCanonicalName();
  }

  @Nullable
  public PsiElement setName(@NotNull String name) {
    EdlQid id = getQid();
    if (id == null) return null;
    else {
      id.setName(name);
      return this;
    }
  }

  @Nullable
  public PsiElement getNameIdentifier() {
    EdlQid qid = getQid();
    return qid == null ? null : qid.getId();
  }

  @Nullable
  @Override
  public Qn getNamespace() {
    S stub = getStub();
    if (stub != null) {
      return Qn.fromNullableDotSeparated(stub.getNamespace());
    }

    return NamespaceManager.getNamespace(this);
  }

  @Nullable
  @Override
  public Qn getQn() {
    String name = getName();
    if (name == null) return null;
    Qn namespace = getNamespace();
    if (namespace == null) return new Qn(name);
    return namespace.append(name);
  }

  public int getTextOffset() {
    PsiElement nameIdentifier = getNameIdentifier();
    return nameIdentifier == null ? 0 : nameIdentifier.getTextOffset();
  }

  @NotNull
  public abstract TypeKind getKind();

  public Icon getIcon(int flags) {
    return EdlPresentationUtil.getIcon(this);
  }

  @Override
  public void delete() throws IncorrectOperationException {
    getParent().delete();
  }

  @NotNull
  public List<EdlTypeDef> extendsParents() {
    S stub = getStub();

    if (stub != null) {
      List<SerializedFqnTypeRef> extendsTypeRefs = stub.getExtendsTypeRefs();
      if (extendsTypeRefs != null) {
        return extendsTypeRefs.stream()
            .map(tr -> tr.resolveTypeDef(getProject(), EdlSearchScopeUtil.getSearchScope(getContainingFile())))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
      }
    }

    EdlExtendsDecl extendsDecl = getExtendsDecl();
    if (extendsDecl == null) return Collections.emptyList();
    List<EdlQnTypeRef> typeRefList = extendsDecl.getQnTypeRefList();
    if (typeRefList.isEmpty()) return Collections.emptyList();

    List<EdlTypeDef> result = new ArrayList<>(typeRefList.size());
    for (EdlQnTypeRef typeRef : typeRefList) {
      EdlTypeDef resolved = typeRef.resolve();
      if (resolved != null) result.add(resolved);
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;

    if (obj instanceof EdlTypeDef) {
      EdlTypeDef other = (EdlTypeDef) obj;

      Qn thisNamespace = getNamespace();
      Qn otherNamespace = other.getNamespace();

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
    Qn namespace = getNamespace();
    String name = getName();

    if (namespace == null && name == null) return super.hashCode();

    int hash = 31 + (namespace == null ? 0 : namespace.hashCode());
    hash = 31 * hash + (name == null ? 0 : name.hashCode());
    return hash;
  }

  @Override
  public String toString() {
    return EdlPresentationUtil.psiToString(this);
  }
}
