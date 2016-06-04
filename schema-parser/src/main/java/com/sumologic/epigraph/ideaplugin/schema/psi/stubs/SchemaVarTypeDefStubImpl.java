package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaVarTypeDef;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaVarTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<SchemaVarTypeDef> implements SchemaVarTypeDefStub {
  SchemaVarTypeDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) SchemaElementTypes.S_VAR_TYPE_DEF);
  }
}
