// This is a generated file. Not intended for manual editing.
package com.sumologic.dohyo.plugin.schema.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SchemaTypeDefs extends PsiElement {

  @NotNull
  List<SchemaEnumTypeDef> getEnumTypeDefList();

  @NotNull
  List<SchemaListTypeDef> getListTypeDefList();

  @NotNull
  List<SchemaMapTypeDef> getMapTypeDefList();

  @NotNull
  List<SchemaMultiTypeDef> getMultiTypeDefList();

  @NotNull
  List<SchemaPrimitiveTypeDef> getPrimitiveTypeDefList();

  @NotNull
  List<SchemaRecordTypeDef> getRecordTypeDefList();

  @NotNull
  List<SchemaUnionTypeDef> getUnionTypeDefList();

}
