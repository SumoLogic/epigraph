package com.sumologic.epigraph.schema.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.sumologic.epigraph.schema.parser.psi.SchemaTypeDef;
import com.sumologic.epigraph.schema.parser.psi.SchemaVarTypeDef;
import com.sumologic.epigraph.schema.parser.psi.TypeKind;
import com.sumologic.epigraph.schema.parser.psi.stubs.SchemaVarTypeDefStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
abstract public class SchemaVarTypeDefImplBase extends SchemaTypeDefImplBase<SchemaVarTypeDefStub, SchemaVarTypeDef> implements SchemaTypeDef {
  SchemaVarTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  SchemaVarTypeDefImplBase(@NotNull SchemaVarTypeDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @NotNull
  @Override
  public TypeKind getKind() {
    return TypeKind.VAR;
  }
}
