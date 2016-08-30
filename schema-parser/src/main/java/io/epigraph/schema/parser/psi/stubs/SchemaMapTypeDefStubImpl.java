package io.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.schema.lexer.SchemaElementTypes;
import io.epigraph.schema.parser.psi.SchemaMapTypeDef;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaMapTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<SchemaMapTypeDef> implements SchemaMapTypeDefStub {
  SchemaMapTypeDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) SchemaElementTypes.S_MAP_TYPE_DEF);
  }
}
