package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaEnumTypeDef;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaEnumTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<SchemaEnumTypeDef> implements SchemaEnumTypeDefStub {
  SchemaEnumTypeDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) SchemaElementTypes.S_ENUM_TYPE_DEF);
  }
}
