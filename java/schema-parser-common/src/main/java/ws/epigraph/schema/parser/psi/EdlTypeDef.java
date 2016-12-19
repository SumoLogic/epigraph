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

package ws.epigraph.schema.parser.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.util.IncorrectOperationException;
import ws.epigraph.lang.Qn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface EdlTypeDef extends PsiNameIdentifierOwner {
  EdlQid getQid();

  @Nullable
  String getName();

  @Nullable
  PsiElement setName(@NotNull String name);

  @Nullable
  PsiElement getNameIdentifier();

  @Nullable
  Qn getNamespace();

  @Nullable
  Qn getQn();

  int getTextOffset();

  @Nullable
  PsiElement getAbstract() ;

  @Nullable
  EdlExtendsDecl getExtendsDecl();

  @Nullable
  EdlSupplementsDecl getSupplementsDecl();

  @Nullable
  EdlMetaDecl getMetaDecl();

  @NotNull
  TypeKind getKind();

  Icon getIcon(int flags);

  @NotNull
  List<EdlTypeDef> extendsParents();

  @Override
  void delete() throws IncorrectOperationException;
}
