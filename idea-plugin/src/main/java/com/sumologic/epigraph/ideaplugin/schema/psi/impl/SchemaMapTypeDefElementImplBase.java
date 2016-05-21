package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaMapTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDefElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.TypeKind;
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaMapTypeDefStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
abstract public class SchemaMapTypeDefElementImplBase extends SchemaTypeDefElementImplBase<SchemaMapTypeDefStub, SchemaMapTypeDef> implements SchemaTypeDefElement {
  SchemaMapTypeDefElementImplBase(@NotNull ASTNode node) {
    super(node);
  }

  SchemaMapTypeDefElementImplBase(@NotNull SchemaMapTypeDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @NotNull
  @Override
  public TypeKind getKind() {
    return TypeKind.MAP;
  }
}
