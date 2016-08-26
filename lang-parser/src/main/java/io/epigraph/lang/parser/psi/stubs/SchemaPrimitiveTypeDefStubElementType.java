package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.parser.psi.EpigraphPrimitiveTypeDef;
import io.epigraph.lang.parser.psi.impl.EpigraphPrimitiveTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPrimitiveTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<EpigraphPrimitiveTypeDefStub, EpigraphPrimitiveTypeDef> {
  public SchemaPrimitiveTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "primitivetypedef");
  }

  @Override
  public EpigraphPrimitiveTypeDef createPsi(@NotNull EpigraphPrimitiveTypeDefStub stub) {
    return new EpigraphPrimitiveTypeDefImpl(stub, this);
  }

  @Override
  public EpigraphPrimitiveTypeDefStub createStub(@NotNull EpigraphPrimitiveTypeDef typeDef, StubElement parentStub) {
    return new EpigraphPrimitiveTypeDefStubImpl( parentStub);
  }

}
