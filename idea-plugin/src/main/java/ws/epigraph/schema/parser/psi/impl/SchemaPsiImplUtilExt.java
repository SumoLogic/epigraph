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

package ws.epigraph.schema.parser.psi.impl;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import ws.epigraph.ideaplugin.schema.index.SchemaSearchScopeUtil;
import ws.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import ws.epigraph.schema.parser.psi.stubs.*;
import ws.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ws.epigraph.schema.parser.psi.impl.SchemaPsiImplUtil.sourceRef;
import static ws.epigraph.schema.parser.psi.impl.SchemaPsiImplUtil.supplementedRefs;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class SchemaPsiImplUtilExt {

  // record --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static List<SchemaTypeDef> supplemented(@NotNull SchemaRecordTypeDef recordTypeDef) {
    SchemaRecordTypeDefStub stub = recordTypeDef.getStub();
    if (stub != null) {
      List<SerializedFqnTypeRef> supplementedTypeRefs = stub.getSupplementedTypeRefs();
      return resolveSerializedTypeRefs(supplementedTypeRefs, recordTypeDef.getProject(), SchemaSearchScopeUtil.getSearchScope(recordTypeDef));
    }

    SchemaSupplementsDecl supplementsDecl = recordTypeDef.getSupplementsDecl();
    if (supplementsDecl == null) return Collections.emptyList();
    return resolveTypeRefs(supplementsDecl.getQnTypeRefList());
  }

  // entity --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static List<SchemaTypeDef> supplemented(@NotNull SchemaEntityTypeDef entityDef) {
    SchemaEntityTypeDefStub stub = entityDef.getStub();
    if (stub != null) {
      List<SerializedFqnTypeRef> supplementedTypeRefs = stub.getSupplementedTypeRefs();
      return resolveSerializedTypeRefs(supplementedTypeRefs, entityDef.getProject(), SchemaSearchScopeUtil.getSearchScope(
          entityDef));
    }

    SchemaSupplementsDecl supplementsDecl = entityDef.getSupplementsDecl();
    if (supplementsDecl == null) return Collections.emptyList();
    return resolveTypeRefs(supplementsDecl.getQnTypeRefList());
  }

  // supplement --------------------------------------------

  @Contract(pure = true)
  @Nullable
  public static SchemaTypeDef source(@NotNull SchemaSupplementDef supplementDef) {
    SchemaSupplementDefStub stub = supplementDef.getStub();
    if (stub != null) {
      SerializedFqnTypeRef sourceTypeRef = stub.getSourceTypeRef();
      if (sourceTypeRef == null) return null;
      return sourceTypeRef.resolveTypeDef(supplementDef.getProject(), SchemaSearchScopeUtil.getSearchScope(supplementDef));
    }

    SchemaQnTypeRef ref = sourceRef(supplementDef);
    if (ref == null) return null;
    return ref.resolve();
  }

  @Contract(pure = true)
  @NotNull
  public static List<SchemaTypeDef> supplemented(@NotNull SchemaSupplementDef supplementDef) {
    SchemaSupplementDefStub stub = supplementDef.getStub();
    if (stub != null) {
      List<SerializedFqnTypeRef> supplementedTypeRefs = stub.getSupplementedTypeRefs();
      return resolveSerializedTypeRefs(supplementedTypeRefs, supplementDef.getProject(), SchemaSearchScopeUtil.getSearchScope(supplementDef));
    }

    return resolveTypeRefs(supplementedRefs(supplementDef));
  }

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull SchemaSupplementDef supplementDef) {
    return SchemaPresentationUtil.getPresentation(supplementDef, false);
  }

  // member decls --------------------------------------------
  // field decl

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull SchemaFieldDecl fieldDecl) {
    return SchemaPresentationUtil.getPresentation(fieldDecl, false);
  }

  // entityMember decl

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull SchemaEntityTagDecl entityTagDecl) {
    return SchemaPresentationUtil.getPresentation(entityTagDecl, false);
  }

//  /////////////

  private static List<SchemaTypeDef> resolveTypeRefs(List<SchemaQnTypeRef> refs) {
    return refs.stream()
        .map(SchemaQnTypeRef::resolve)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private static List<SchemaTypeDef> resolveSerializedTypeRefs(@Nullable List<SerializedFqnTypeRef> refs,
                                                               @NotNull Project project,
                                                               @NotNull GlobalSearchScope searchScope) {
    if (refs == null) return Collections.emptyList();
    return refs.stream()
        .map(tr -> tr.resolveTypeDef(project, searchScope))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

}
