package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.parser.psi.EpigraphVarTypeDef;
import io.epigraph.lang.parser.psi.impl.EpigraphVarTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaVarTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaVarTypeDefStub, EpigraphVarTypeDef> {
  public SchemaVarTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "vartypedef");
  }

  @Override
  public EpigraphVarTypeDef createPsi(@NotNull SchemaVarTypeDefStub stub) {
    return new EpigraphVarTypeDefImpl(stub, this);
  }

  @Override
  public SchemaVarTypeDefStub createStub(@NotNull EpigraphVarTypeDef typeDef, StubElement parentStub) {
    return new SchemaVarTypeDefStubImpl(parentStub);
  }
}
