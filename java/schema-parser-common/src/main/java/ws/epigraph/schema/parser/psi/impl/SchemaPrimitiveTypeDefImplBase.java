package ws.epigraph.schema.parser.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import ws.epigraph.schema.parser.psi.SchemaPrimitiveTypeDef;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import ws.epigraph.schema.parser.psi.TypeKind;
import ws.epigraph.schema.parser.psi.stubs.SchemaPrimitiveTypeDefStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
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
