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

package ws.epigraph.edl.parser.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import ws.epigraph.ideaplugin.edl.brains.NamespaceManager;
import ws.epigraph.ideaplugin.edl.index.SchemaSearchScopeUtil;
import ws.epigraph.ideaplugin.edl.presentation.SchemaPresentationUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.edl.parser.psi.*;
import ws.epigraph.edl.parser.psi.stubs.SchemaTypeDefStubBase;
import ws.epigraph.edl.parser.psi.stubs.SerializedFqnTypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ws.epigraph.edl.lexer.SchemaElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
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
            .map(tr -> tr.resolveTypeDef(getProject(), SchemaSearchScopeUtil.getSearchScope(getContainingFile())))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
      }
    }

    SchemaExtendsDecl extendsDecl = getExtendsDecl();
    if (extendsDecl == null) return Collections.emptyList();
    List<SchemaQnTypeRef> typeRefList = extendsDecl.getQnTypeRefList();
    if (typeRefList.isEmpty()) return Collections.emptyList();

    List<SchemaTypeDef> result = new ArrayList<>(typeRefList.size());
    for (SchemaQnTypeRef typeRef : typeRefList) {
      SchemaTypeDef resolved = typeRef.resolve();
      if (resolved != null) result.add(resolved);
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;

    if (obj instanceof SchemaTypeDef) {
      SchemaTypeDef other = (SchemaTypeDef) obj;

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
    return SchemaPresentationUtil.psiToString(this);
  }
}
