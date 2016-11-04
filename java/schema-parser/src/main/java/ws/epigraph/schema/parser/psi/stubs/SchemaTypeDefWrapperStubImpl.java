package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import ws.epigraph.schema.lexer.SchemaElementTypes;
import ws.epigraph.schema.parser.psi.SchemaTypeDefWrapper;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class SchemaTypeDefWrapperStubImpl extends StubBase<SchemaTypeDefWrapper> implements SchemaTypeDefWrapperStub {
  SchemaTypeDefWrapperStubImpl(StubElement parent) {
    super(parent, (IStubElementType) SchemaElementTypes.S_TYPE_DEF_WRAPPER);
  }
}
