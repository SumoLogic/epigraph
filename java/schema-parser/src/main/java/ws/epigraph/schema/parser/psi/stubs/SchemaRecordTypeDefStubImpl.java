package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import ws.epigraph.schema.lexer.SchemaElementTypes;
import ws.epigraph.schema.parser.psi.SchemaRecordTypeDef;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaRecordTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<SchemaRecordTypeDef> implements SchemaRecordTypeDefStub {

  SchemaRecordTypeDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) SchemaElementTypes.S_RECORD_TYPE_DEF);
  }

}
