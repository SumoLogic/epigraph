package io.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import io.epigraph.schema.parser.psi.SchemaEnumTypeDef;
import io.epigraph.schema.parser.psi.impl.SchemaEnumTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaEnumTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaEnumTypeDefStub, SchemaEnumTypeDef> {
  public SchemaEnumTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "enumtypedef");
  }

  @Override
  public SchemaEnumTypeDef createPsi(@NotNull SchemaEnumTypeDefStub stub) {
    return new SchemaEnumTypeDefImpl(stub, this);
  }

  @Override
  public SchemaEnumTypeDefStub createStub(@NotNull SchemaEnumTypeDef typeDef, StubElement parentStub) {
    return new SchemaEnumTypeDefStubImpl(parentStub);
  }
}
