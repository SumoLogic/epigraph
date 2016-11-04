package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import ws.epigraph.schema.parser.psi.SchemaListTypeDef;
import ws.epigraph.schema.parser.psi.impl.SchemaListTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaListTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaListTypeDefStub, SchemaListTypeDef> {
  public SchemaListTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "listtypedef");
  }

  @Override
  public SchemaListTypeDef createPsi(@NotNull SchemaListTypeDefStub stub) {
    return new SchemaListTypeDefImpl(stub, this);
  }

  @Override
  public SchemaListTypeDefStub createStub(@NotNull SchemaListTypeDef typeDef, StubElement parentStub) {
    return new SchemaListTypeDefStubImpl(parentStub);
  }

}
