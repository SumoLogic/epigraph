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

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import ws.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static ws.epigraph.schema.parser.psi.impl.SchemaPsiImplUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaPsiImplUtilExt {

  // record --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static List<SchemaTypeDef> supplemented(@NotNull SchemaRecordTypeDef recordTypeDef) {
    return Collections.emptyList();
  }
  
  // var --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static List<SchemaTypeDef> supplemented(@NotNull SchemaVarTypeDef varTypeDef) {
    return Collections.emptyList();
  }

  // fqn type ref --------------------------------------------

  // not exposed through PSI
  @Contract(pure = true)
  @Nullable
  public static PsiReference getReference(@NotNull SchemaQnTypeRef typeRef) {
    List<SchemaQnSegment> fqnSegmentList = typeRef.getQn().getQnSegmentList();
    if (fqnSegmentList.isEmpty()) return null;
    return fqnSegmentList.get(fqnSegmentList.size() - 1).getReference();
  }

  @Contract(pure = true)
  @Nullable
  public static SchemaTypeDef resolve(@NotNull SchemaQnTypeRef typeRef) {
    PsiReference reference = getReference(typeRef);
    if (reference == null) return null;
    PsiElement element = reference.resolve();
    if (element instanceof SchemaTypeDef) return (SchemaTypeDef) element;
    return null;
  }

  // supplement --------------------------------------------

  // can't use SchemaSupplementDef::getQnTypeRefList as it will include both source and all supplemented

  @Contract(pure = true)
  @Nullable
  public static SchemaTypeDef source(@NotNull SchemaSupplementDef supplementDef) {
    SchemaQnTypeRef ref = sourceRef(supplementDef);
    if (ref == null) return null;
    return ref.resolve();
  }

  @Contract(pure = true)
  @NotNull
  public static List<SchemaTypeDef> supplemented(@NotNull SchemaSupplementDef supplementDef) {
    throw new UnsupportedOperationException();
  }

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull SchemaSupplementDef supplementDef) {
    throw new UnsupportedOperationException();
  }

  // member decls --------------------------------------------
  // field decl

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull SchemaFieldDecl fieldDecl) {
    throw new UnsupportedOperationException();
  }

  // varTypeMember decl

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull SchemaVarTagDecl varTagDecl) {
    throw new UnsupportedOperationException();
  }

}
