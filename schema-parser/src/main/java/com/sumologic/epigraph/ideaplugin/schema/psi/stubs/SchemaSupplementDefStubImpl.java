package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.sumologic.epigraph.ideaplugin.schema.lexer.SchemaElementTypes;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaSupplementDef;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaSupplementDefStubImpl extends StubBase<SchemaSupplementDef> implements SchemaSupplementDefStub {
  protected SchemaSupplementDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) SchemaElementTypes.S_SUPPLEMENT_DEF);
  }
}
