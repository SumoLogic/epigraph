package io.epigraph.lang.schema.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import io.epigraph.lang.schema.parser.psi.SchemaPrimitiveTypeDef;
import io.epigraph.lang.schema.parser.psi.SchemaTypeDef;
import io.epigraph.lang.schema.parser.psi.TypeKind;
import io.epigraph.lang.schema.parser.psi.stubs.SchemaPrimitiveTypeDefStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
abstract public class SchemaPrimitiveTypeDefImplBase extends SchemaTypeDefImplBase<SchemaPrimitiveTypeDefStub, SchemaPrimitiveTypeDef> implements SchemaTypeDef {
  SchemaPrimitiveTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  SchemaPrimitiveTypeDefImplBase(@NotNull SchemaPrimitiveTypeDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @NotNull
  @Override
  public TypeKind getKind() {
    return TypeKind.PRIMITIVE;
  }
}
