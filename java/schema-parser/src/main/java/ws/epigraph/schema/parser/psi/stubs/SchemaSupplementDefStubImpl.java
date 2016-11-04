package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import ws.epigraph.schema.lexer.SchemaElementTypes;
import ws.epigraph.schema.parser.psi.SchemaSupplementDef;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaSupplementDefStubImpl extends StubBase<SchemaSupplementDef> implements SchemaSupplementDefStub {
  protected SchemaSupplementDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) SchemaElementTypes.S_SUPPLEMENT_DEF);
  }
}
