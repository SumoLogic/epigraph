package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import ws.epigraph.schema.parser.psi.SchemaRecordTypeDef;
import ws.epigraph.schema.parser.psi.impl.SchemaRecordTypeDefImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaRecordTypeDefStubElementType extends SchemaTypeDefStubElementTypeBase<SchemaRecordTypeDefStub, SchemaRecordTypeDef> {
  public SchemaRecordTypeDefStubElementType(@NotNull @NonNls String debugName) {
    super(debugName, "recordtypedef");
  }

  @Override
  public SchemaRecordTypeDef createPsi(@NotNull SchemaRecordTypeDefStub stub) {
    return new SchemaRecordTypeDefImpl(stub, this);
  }

  @Override
  public SchemaRecordTypeDefStub createStub(@NotNull SchemaRecordTypeDef typeDef, StubElement parentStub) {
    return new SchemaRecordTypeDefStubImpl(parentStub);
  }
}
