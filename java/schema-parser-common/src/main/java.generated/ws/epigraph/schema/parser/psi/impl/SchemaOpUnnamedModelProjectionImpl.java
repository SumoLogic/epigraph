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

public class SchemaOpUnnamedModelProjectionImpl extends ASTWrapperPsiElement implements SchemaOpUnnamedModelProjection {

  public SchemaOpUnnamedModelProjectionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SchemaVisitor visitor) {
    visitor.visitOpUnnamedModelProjection(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SchemaVisitor) accept((SchemaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SchemaOpListModelProjection getOpListModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpListModelProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpMapModelProjection getOpMapModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpMapModelProjection.class);
  }

  @Override
  @Nullable
  public SchemaOpModelPolymorphicTail getOpModelPolymorphicTail() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpModelPolymorphicTail.class);
  }

  @Override
  @NotNull
  public List<SchemaOpModelProperty> getOpModelPropertyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SchemaOpModelProperty.class);
  }

  @Override
  @Nullable
  public SchemaOpRecordModelProjection getOpRecordModelProjection() {
    return PsiTreeUtil.getChildOfType(this, SchemaOpRecordModelProjection.class);
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
