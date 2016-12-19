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
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.schema.parser.psi.stubs.EdlTypeDefStubBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ws.epigraph.schema.lexer.EdlElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class EdlTypeDefImplBase<S extends EdlTypeDefStubBase<T>, T extends EdlTypeDef>
    extends StubBasedPsiElementBase<S> implements EdlTypeDef {

//  private final static Logger LOG = Logger.getInstance(EdlTypeDefImplBase.class);

  protected EdlTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  protected EdlTypeDefImplBase(@NotNull S stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
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
    EdlQid id = getQid();
    return id == null ? null : id.getCanonicalName();
  }

  @Nullable
  public PsiElement setName(@NotNull String name) {
    PsiElement id = getQid();
    if (id == null) return null;
    else {
      PsiElement newId = EdlElementFactory.createId(getProject(), name);
      id.replace(newId);
      return id;
    }
  }

  @Nullable
  public PsiElement getNameIdentifier() {
    return getQid();
  }

  @Nullable
  @Override
  public Qn getNamespace() {
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete() throws IncorrectOperationException {
    getParent().delete();
  }

  @NotNull
  public List<EdlTypeDef> extendsParents() {
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
  public String toString() {
    return getClass().getSimpleName() + "(" + getNode().getElementType().toString() + ")";
  }
}
