package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.parser.psi.EpigraphEnumTypeDef;
import io.epigraph.lang.parser.psi.impl.EpigraphEnumTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphEnumTypeDefStubElementType extends EpigraphTypeDefStubElementTypeBase<EpigraphEnumTypeDefStub, EpigraphEnumTypeDef> {
  public EpigraphEnumTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "enumtypedef");
  }

  @Override
  public EpigraphEnumTypeDef createPsi(@NotNull EpigraphEnumTypeDefStub stub) {
    return new EpigraphEnumTypeDefImpl(stub, this);
  }

  @Override
  public EpigraphEnumTypeDefStub createStub(@NotNull EpigraphEnumTypeDef typeDef, StubElement parentStub) {
    return new EpigraphEnumTypeDefStubImpl(parentStub);
  }
}
