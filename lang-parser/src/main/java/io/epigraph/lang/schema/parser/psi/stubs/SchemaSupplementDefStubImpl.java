package io.epigraph.lang.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.schema.parser.psi.SchemaSupplementDef;
import io.epigraph.lang.lexer.EpigraphElementTypes;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaSupplementDefStubImpl extends StubBase<SchemaSupplementDef> implements SchemaSupplementDefStub {
  protected SchemaSupplementDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) EpigraphElementTypes.E_SUPPLEMENT_DEF);
  }
}
