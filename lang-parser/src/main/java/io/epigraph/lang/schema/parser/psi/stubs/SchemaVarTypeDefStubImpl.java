package io.epigraph.lang.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.schema.parser.psi.SchemaVarTypeDef;
import io.epigraph.lang.lexer.EpigraphElementTypes;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaVarTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<SchemaVarTypeDef> implements SchemaVarTypeDefStub {
  SchemaVarTypeDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) EpigraphElementTypes.E_VAR_TYPE_DEF);
  }
}
