package com.sumologic.epigraph.schema.parser.psi.impl;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaSearchScopeUtil;
import com.sumologic.epigraph.ideaplugin.schema.presentation.SchemaPresentationUtil;
import com.sumologic.epigraph.schema.parser.psi.stubs.*;
import com.sumologic.epigraph.schema.parser.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sumologic.epigraph.schema.parser.psi.impl.SchemaPsiImplUtil.sourceRef;
import static com.sumologic.epigraph.schema.parser.psi.impl.SchemaPsiImplUtil.supplementedRefs;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
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
    return resolveTypeRefs(supplementsDecl.getFqnTypeRefList());
  }

  // var --------------------------------------------

  @Contract(pure = true)
  @NotNull
  public static List<SchemaTypeDef> supplemented(@NotNull SchemaVarTypeDef varTypeDef) {
    SchemaVarTypeDefStub stub = varTypeDef.getStub();
    if (stub != null) {
      List<SerializedFqnTypeRef> supplementedTypeRefs = stub.getSupplementedTypeRefs();
      return resolveSerializedTypeRefs(supplementedTypeRefs, varTypeDef.getProject(), SchemaSearchScopeUtil.getSearchScope(varTypeDef));
    }

    SchemaSupplementsDecl supplementsDecl = varTypeDef.getSupplementsDecl();
    if (supplementsDecl == null) return Collections.emptyList();
    return resolveTypeRefs(supplementsDecl.getFqnTypeRefList());
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

    SchemaFqnTypeRef ref = sourceRef(supplementDef);
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

  // varTypeMember decl

  @Contract(pure = true)
  @NotNull
  public static ItemPresentation getPresentation(@NotNull SchemaVarTagDecl varTagDecl) {
    return SchemaPresentationUtil.getPresentation(varTagDecl, false);
  }

//  /////////////

  private static List<SchemaTypeDef> resolveTypeRefs(List<SchemaFqnTypeRef> refs) {
    return refs.stream()
        .map(SchemaFqnTypeRef::resolve)
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
