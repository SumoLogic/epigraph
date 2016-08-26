package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.parser.psi.SchemaSupplementDef;
import io.epigraph.lang.lexer.EpigraphElementTypes;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphSupplementDefStubImpl extends StubBase<SchemaSupplementDef> implements EpigraphSupplementDefStub {
  protected EpigraphSupplementDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) EpigraphElementTypes.E_SUPPLEMENT_DEF);
  }
}
