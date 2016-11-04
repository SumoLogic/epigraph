package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import ws.epigraph.schema.lexer.SchemaElementTypes;
import ws.epigraph.schema.parser.psi.SchemaEnumTypeDef;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaEnumTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<SchemaEnumTypeDef> implements SchemaEnumTypeDefStub {
  SchemaEnumTypeDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) SchemaElementTypes.S_ENUM_TYPE_DEF);
  }
}
