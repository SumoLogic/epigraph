package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.parser.psi.EpigraphEnumTypeDef;
import io.epigraph.lang.lexer.EpigraphElementTypes;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaEnumTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<EpigraphEnumTypeDef> implements SchemaEnumTypeDefStub {
  SchemaEnumTypeDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) EpigraphElementTypes.E_ENUM_TYPE_DEF);
  }
}
