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

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import ws.epigraph.ideaplugin.edl.index.EdlSearchScopeUtil;
import ws.epigraph.ideaplugin.edl.presentation.EdlPresentationUtil;
import ws.epigraph.edl.parser.psi.stubs.*;
import ws.epigraph.edl.parser.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ws.epigraph.edl.parser.psi.impl.EdlPsiImplUtil.sourceRef;
import static ws.epigraph.edl.parser.psi.impl.EdlPsiImplUtil.supplementedRefs;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class EdlPsiImplUtilExt {

  // record --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static List<EdlTypeDef> supplemented(@NotNull EdlRecordTypeDef recordTypeDef) {
    EdlRecordTypeDefStub stub = recordTypeDef.getStub();
    if (stub != null) {
      List<SerializedFqnTypeRef> supplementedTypeRefs = stub.getSupplementedTypeRefs();
      return resolveSerializedTypeRefs(supplementedTypeRefs, recordTypeDef.getProject(), EdlSearchScopeUtil.getSearchScope(recordTypeDef));
    }

    EdlSupplementsDecl supplementsDecl = recordTypeDef.getSupplementsDecl();
    if (supplementsDecl == null) return Collections.emptyList();
    return resolveTypeRefs(supplementsDecl.getQnTypeRefList());
  }

  // var --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static List<EdlTypeDef> supplemented(@NotNull EdlVarTypeDef varTypeDef) {
    EdlVarTypeDefStub stub = varTypeDef.getStub();
    if (stub != null) {
      List<SerializedFqnTypeRef> supplementedTypeRefs = stub.getSupplementedTypeRefs();
      return resolveSerializedTypeRefs(supplementedTypeRefs, varTypeDef.getProject(), EdlSearchScopeUtil.getSearchScope(varTypeDef));
    }

    EdlSupplementsDecl supplementsDecl = varTypeDef.getSupplementsDecl();
    if (supplementsDecl == null) return Collections.emptyList();
    return resolveTypeRefs(supplementsDecl.getQnTypeRefList());
  }

  // supplement --------------------------------------------

  @Contract(pure = true)
  @Nullable
  public static EdlTypeDef source(@NotNull EdlSupplementDef supplementDef) {
    EdlSupplementDefStub stub = supplementDef.getStub();
    if (stub != null) {
      SerializedFqnTypeRef sourceTypeRef = stub.getSourceTypeRef();
      if (sourceTypeRef == null) return null;
      return sourceTypeRef.resolveTypeDef(supplementDef.getProject(), EdlSearchScopeUtil.getSearchScope(supplementDef));
    }

    EdlQnTypeRef ref = sourceRef(supplementDef);
    if (ref == null) return null;
    return ref.resolve();
  }

  @Contract(pure = true)
  @NotNull
  public static List<EdlTypeDef> supplemented(@NotNull EdlSupplementDef supplementDef) {
    EdlSupplementDefStub stub = supplementDef.getStub();
    if (stub != null) {
      List<SerializedFqnTypeRef> supplementedTypeRefs = stub.getSupplementedTypeRefs();
      return resolveSerializedTypeRefs(supplementedTypeRefs, supplementDef.getProject(), EdlSearchScopeUtil.getSearchScope(supplementDef));
    }

    return resolveTypeRefs(supplementedRefs(supplementDef));
  }

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull EdlSupplementDef supplementDef) {
    return EdlPresentationUtil.getPresentation(supplementDef, false);
  }

  // member decls --------------------------------------------
  // field decl

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull EdlFieldDecl fieldDecl) {
    return EdlPresentationUtil.getPresentation(fieldDecl, false);
  }

  // varTypeMember decl

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull EdlVarTagDecl varTagDecl) {
    return EdlPresentationUtil.getPresentation(varTagDecl, false);
  }

//  /////////////

  private static List<EdlTypeDef> resolveTypeRefs(List<EdlQnTypeRef> refs) {
    return refs.stream()
        .map(EdlQnTypeRef::resolve)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private static List<EdlTypeDef> resolveSerializedTypeRefs(@Nullable List<SerializedFqnTypeRef> refs,
                                                               @NotNull Project project,
                                                               @NotNull GlobalSearchScope searchScope) {
    if (refs == null) return Collections.emptyList();
    return refs.stream()
        .map(tr -> tr.resolveTypeDef(project, searchScope))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

}
