package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaEnumTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDefElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.TypeKind;
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaEnumTypeDefStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
abstract public class SchemaEnumTypeDefElementImplBase extends SchemaTypeDefElementImplBase<SchemaEnumTypeDefStub, SchemaEnumTypeDef> implements SchemaTypeDefElement {
  SchemaEnumTypeDefElementImplBase(@NotNull ASTNode node) {
    super(node);
  }

  SchemaEnumTypeDefElementImplBase(@NotNull SchemaEnumTypeDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @NotNull
  @Override
  public TypeKind getKind() {
    return TypeKind.ENUM;
  }
}
