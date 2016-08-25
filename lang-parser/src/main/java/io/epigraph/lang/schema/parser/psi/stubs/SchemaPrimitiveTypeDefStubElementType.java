package io.epigraph.lang.schema.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.schema.parser.psi.SchemaPrimitiveTypeDef;
import io.epigraph.lang.schema.parser.psi.impl.SchemaPrimitiveTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPrimitiveTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaPrimitiveTypeDefStub, SchemaPrimitiveTypeDef> {
  public SchemaPrimitiveTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "primitivetypedef");
  }

  @Override
  public SchemaPrimitiveTypeDef createPsi(@NotNull SchemaPrimitiveTypeDefStub stub) {
    return new SchemaPrimitiveTypeDefImpl(stub, this);
  }

  @Override
  public SchemaPrimitiveTypeDefStub createStub(@NotNull SchemaPrimitiveTypeDef typeDef, StubElement parentStub) {
    return new SchemaPrimitiveTypeDefStubImpl( parentStub);
  }

}
