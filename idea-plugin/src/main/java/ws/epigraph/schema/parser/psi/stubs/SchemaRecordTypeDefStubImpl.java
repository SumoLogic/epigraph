package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import ws.epigraph.schema.lexer.SchemaElementTypes;
import ws.epigraph.schema.parser.psi.SchemaRecordTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaRecordTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<SchemaRecordTypeDef> implements SchemaRecordTypeDefStub {
  private final List<SerializedFqnTypeRef> supplementedTypeRefs;

  SchemaRecordTypeDefStubImpl(StubElement parent,
                              String name,
                              String namespace,
                              @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs,
                              @Nullable final List<SerializedFqnTypeRef> supplementedTypeRefs) {
    super(parent, (IStubElementType) SchemaElementTypes.S_RECORD_TYPE_DEF, name, namespace, extendsTypeRefs);
    this.supplementedTypeRefs = supplementedTypeRefs;
  }

  @Nullable
  @Override
  public List<SerializedFqnTypeRef> getSupplementedTypeRefs() {
    return supplementedTypeRefs;
  }
}
