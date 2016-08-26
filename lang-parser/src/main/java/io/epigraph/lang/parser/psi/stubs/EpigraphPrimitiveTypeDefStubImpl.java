package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.parser.psi.EpigraphPrimitiveTypeDef;
import io.epigraph.lang.lexer.EpigraphElementTypes;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphPrimitiveTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<EpigraphPrimitiveTypeDef> implements EpigraphPrimitiveTypeDefStub {
  EpigraphPrimitiveTypeDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) EpigraphElementTypes.E_PRIMITIVE_TYPE_DEF);
  }
}
