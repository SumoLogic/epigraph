package io.epigraph.lang.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.schema.parser.psi.SchemaPrimitiveTypeDef;
import io.epigraph.lang.lexer.EpigraphElementTypes;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaPrimitiveTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<SchemaPrimitiveTypeDef> implements SchemaPrimitiveTypeDefStub {
  SchemaPrimitiveTypeDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) EpigraphElementTypes.E_PRIMITIVE_TYPE_DEF);
  }
}
