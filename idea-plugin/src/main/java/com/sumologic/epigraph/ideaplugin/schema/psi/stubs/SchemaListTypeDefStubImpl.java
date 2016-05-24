package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaListTypeDef;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaListTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<SchemaListTypeDef> implements SchemaListTypeDefStub {
  SchemaListTypeDefStubImpl(StubElement parent, String name, String namespace) {
    super(parent, (IStubElementType) SchemaElementTypes.S_LIST_TYPE_DEF, name, namespace);
  }
}
