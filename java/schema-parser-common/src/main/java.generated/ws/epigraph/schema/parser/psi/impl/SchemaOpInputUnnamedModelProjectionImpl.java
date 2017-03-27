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
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import ws.epigraph.schema.parser.psi.*;

public class SchemaOpInputUnnamedModelProjectionImpl extends ASTWrapperPsiElement implements SchemaOpInputUnnamedModelProjection {

  public SchemaOpInputUnnamedModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpInputUnnamedModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaOpInputListModelProjection getOpInputListModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpInputListModelProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpInputMapModelProjection getOpInputMapModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpInputMapModelProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpInputModelPolymorphicTail getOpInputModelPolymorphicTail() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpInputModelPolymorphicTail.class);
  }

  @Override
  @NotNull
  public List<SchemaOpInputModelProperty> getOpInputModelPropertyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaOpInputModelProperty.class);
  }

  @Override
  @Nullable
  public SchemaOpInputRecordModelProjection getOpInputRecordModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpInputRecordModelProjection.class);
  }

  @Override
  @Nullable
  public PsiElement getCurlyLeft() {
    return findChildByType(S_CURLY_LEFT);
  }

  @Override
  @Nullable
  public PsiElement getCurlyRight() {
    return findChildByType(S_CURLY_RIGHT);
  }

}
