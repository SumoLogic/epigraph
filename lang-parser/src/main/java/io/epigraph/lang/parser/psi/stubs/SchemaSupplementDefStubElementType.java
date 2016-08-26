package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.*;
import io.epigraph.lang.schema.SchemaLanguage;
import io.epigraph.lang.parser.psi.SchemaSupplementDef;
import io.epigraph.lang.parser.psi.impl.SchemaSupplementDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaSupplementDefStubElementType extends IStubElementType<SchemaSupplementDefStub, SchemaSupplementDef> {
  public SchemaSupplementDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, SchemaLanguage.INSTANCE);
  }

  @Override
  public SchemaSupplementDef createPsi(@NotNull SchemaSupplementDefStub stub) {
    return new SchemaSupplementDefImpl(stub, this);
  }

  @Override
  public SchemaSupplementDefStub createStub(@NotNull SchemaSupplementDef supplementDef, StubElement parentStub) {
    return new SchemaSupplementDefStubImpl(parentStub);
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph_schema.supplement";
  }

  @Override
  public void serialize(@NotNull SchemaSupplementDefStub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  @NotNull
  @Override
  public SchemaSupplementDefStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void indexStub(@NotNull SchemaSupplementDefStub stub, @NotNull IndexSink sink) {
  }
}
