package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaEnumTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.TypeKind;
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaEnumTypeDefStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
abstract public class SchemaEnumTypeDefImplBase extends SchemaTypeDefImplBase<SchemaEnumTypeDefStub, SchemaEnumTypeDef> implements SchemaTypeDef {
  SchemaEnumTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  SchemaEnumTypeDefImplBase(@NotNull SchemaEnumTypeDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @NotNull
  @Override
  public TypeKind getKind() {
    return TypeKind.ENUM;
  }
}
