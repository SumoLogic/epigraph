package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.parser.psi.EpigraphVarTypeDef;
import io.epigraph.lang.parser.psi.impl.EpigraphVarTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphVarTypeDefStubElementType extends EpigraphTypeDefStubElementTypeBase<EpigraphVarTypeDefStub, EpigraphVarTypeDef> {
  public EpigraphVarTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "vartypedef");
  }

  @Override
  public EpigraphVarTypeDef createPsi(@NotNull EpigraphVarTypeDefStub stub) {
    return new EpigraphVarTypeDefImpl(stub, this);
  }

  @Override
  public EpigraphVarTypeDefStub createStub(@NotNull EpigraphVarTypeDef typeDef, StubElement parentStub) {
    return new EpigraphVarTypeDefStubImpl(parentStub);
  }
}
