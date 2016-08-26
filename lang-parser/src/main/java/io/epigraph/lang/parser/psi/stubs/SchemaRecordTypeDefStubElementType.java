package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.parser.psi.EpigraphRecordTypeDef;
import io.epigraph.lang.parser.psi.impl.EpigraphRecordTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaRecordTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<EpigraphRecordTypeDefStub, EpigraphRecordTypeDef> {
  public SchemaRecordTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "recordtypedef");
  }

  @Override
  public EpigraphRecordTypeDef createPsi(@NotNull EpigraphRecordTypeDefStub stub) {
    return new EpigraphRecordTypeDefImpl(stub, this);
  }

  @Override
  public EpigraphRecordTypeDefStub createStub(@NotNull EpigraphRecordTypeDef typeDef, StubElement parentStub) {
    return new EpigraphRecordTypeDefStubImpl(parentStub);
  }
}
