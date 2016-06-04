package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDefWrapper;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
class SchemaTypeDefWrapperStubImpl extends StubBase<SchemaTypeDefWrapper> implements SchemaTypeDefWrapperStub {
  SchemaTypeDefWrapperStubImpl(StubElement parent) {
    super(parent, (IStubElementType) SchemaElementTypes.S_TYPE_DEF_WRAPPER);
  }
}
