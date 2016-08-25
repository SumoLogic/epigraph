package io.epigraph.lang.schema.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.schema.parser.psi.SchemaMapTypeDef;
import io.epigraph.lang.schema.parser.psi.impl.SchemaMapTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaMapTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaMapTypeDefStub, SchemaMapTypeDef> {
  public SchemaMapTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "maptypedef");
  }

  @Override
  public SchemaMapTypeDef createPsi(@NotNull SchemaMapTypeDefStub stub) {
    return new SchemaMapTypeDefImpl(stub, this);
  }

  @Override
  public SchemaMapTypeDefStub createStub(@NotNull SchemaMapTypeDef typeDef, StubElement parentStub) {
    return new SchemaMapTypeDefStubImpl(parentStub);
  }
}
