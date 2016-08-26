package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.*;
import io.epigraph.lang.schema.SchemaLanguage;
import io.epigraph.lang.parser.psi.EpigraphSupplementDef;
import io.epigraph.lang.parser.psi.impl.EpigraphSupplementDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphSupplementDefStubElementType extends IStubElementType<EpigraphSupplementDefStub, EpigraphSupplementDef> {
  public EpigraphSupplementDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, SchemaLanguage.INSTANCE);
  }

  @Override
  public EpigraphSupplementDef createPsi(@NotNull EpigraphSupplementDefStub stub) {
    return new EpigraphSupplementDefImpl(stub, this);
  }

  @Override
  public EpigraphSupplementDefStub createStub(@NotNull EpigraphSupplementDef supplementDef, StubElement parentStub) {
    return new EpigraphSupplementDefStubImpl(parentStub);
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph.supplement";
  }

  @Override
  public void serialize(@NotNull EpigraphSupplementDefStub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  @NotNull
  @Override
  public EpigraphSupplementDefStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void indexStub(@NotNull EpigraphSupplementDefStub stub, @NotNull IndexSink sink) {
  }
}
