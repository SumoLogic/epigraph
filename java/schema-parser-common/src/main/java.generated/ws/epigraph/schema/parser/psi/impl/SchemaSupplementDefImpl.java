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

// This is a generated file. Not intended for manual editing.
package ws.epigraph.schema.parser.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static ws.epigraph.schema.lexer.SchemaElementTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import ws.epigraph.schema.parser.psi.stubs.SchemaSupplementDefStub;
import ws.epigraph.schema.parser.psi.*;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;

public class SchemaSupplementDefImpl extends StubBasedPsiElementBase<SchemaSupplementDefStub> implements SchemaSupplementDef {

  public SchemaSupplementDefImpl(SchemaSupplementDefStub stub, IStubElementType type) {
    super(stub, type);
  }

  public SchemaSupplementDefImpl(ASTNode node) {
    super(node);
  }

  public SchemaSupplementDefImpl(SchemaSupplementDefStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitSupplementDef(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SchemaQnTypeRef> getQnTypeRefList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaQnTypeRef.class);
  }

  @Override
  @NotNull
  public PsiElement getSupplement() {
    return notNullChild(findChildByType(S_SUPPLEMENT));
  }

  @Override
  @Nullable
  public PsiElement getWith() {
    return findChildByType(S_WITH);
  }

  @Nullable
  public SchemaQnTypeRef sourceRef() {
    return SchemaPsiImplUtil.sourceRef(this);
  }

  @NotNull
  public List<SchemaQnTypeRef> supplementedRefs() {
    return SchemaPsiImplUtil.supplementedRefs(this);
  }

  @Nullable
  public SchemaTypeDef source() {
    return SchemaPsiImplUtil.source(this);
  }

  @NotNull
  public List<SchemaTypeDef> supplemented() {
    return SchemaPsiImplUtil.supplemented(this);
  }

  @NotNull
  public ItemPresentation getPresentation() {
    return SchemaPsiImplUtil.getPresentation(this);
  }

  @NotNull
  public String toString() {
    return SchemaPsiImplUtil.toString(this);
  }

}
