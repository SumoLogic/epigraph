package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaVarTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDefElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.TypeKind;
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaVarTypeDefStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
abstract public class SchemaVarTypeDefElementImplBase extends SchemaTypeDefElementImplBase<SchemaVarTypeDefStub, SchemaVarTypeDef> implements SchemaTypeDefElement {
  SchemaVarTypeDefElementImplBase(@NotNull ASTNode node) {
    super(node);
  }

  SchemaVarTypeDefElementImplBase(@NotNull SchemaVarTypeDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @NotNull
  @Override
  public TypeKind getKind() {
    return TypeKind.VAR;
  }
}
