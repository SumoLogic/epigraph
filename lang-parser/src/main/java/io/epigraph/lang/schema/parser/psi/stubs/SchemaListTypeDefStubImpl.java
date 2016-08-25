package io.epigraph.lang.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.schema.parser.psi.SchemaListTypeDef;
import io.epigraph.lang.lexer.EpigraphElementTypes;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaListTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<SchemaListTypeDef> implements SchemaListTypeDefStub {
  SchemaListTypeDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) EpigraphElementTypes.E_LIST_TYPE_DEF);
  }
}
