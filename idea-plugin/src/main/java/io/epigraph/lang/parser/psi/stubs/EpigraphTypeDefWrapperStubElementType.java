package io.epigraph.lang.parser.psi.stubs;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import io.epigraph.lang.parser.psi.EpigraphTypeDefWrapper;
import io.epigraph.lang.schema.SchemaLanguage;
import io.epigraph.lang.parser.psi.impl.EpigraphTypeDefWrapperImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphTypeDefWrapperStubElementType extends IStubElementType<EpigraphTypeDefWrapperStub, EpigraphTypeDefWrapper> {
  public EpigraphTypeDefWrapperStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, SchemaLanguage.INSTANCE);
  }

  @Override
  public boolean shouldCreateStub(ASTNode node) {
    return false;
  }

  @Override
  public EpigraphTypeDefWrapper createPsi(@NotNull EpigraphTypeDefWrapperStub stub) {
    return new EpigraphTypeDefWrapperImpl(stub, this);
  }

  @Override
  public EpigraphTypeDefWrapperStub createStub(@NotNull EpigraphTypeDefWrapper typeDef, StubElement parentStub) {
    return new EpigraphTypeDefWrapperStubImpl(parentStub);
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph_schema.typedef";
  }

  @Override
  public void serialize(@NotNull EpigraphTypeDefWrapperStub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  @NotNull
  @Override
  public EpigraphTypeDefWrapperStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new EpigraphTypeDefWrapperStubImpl(parentStub);
  }

  @Override
  public void indexStub(@NotNull EpigraphTypeDefWrapperStub stub, @NotNull IndexSink sink) {

  }
}
