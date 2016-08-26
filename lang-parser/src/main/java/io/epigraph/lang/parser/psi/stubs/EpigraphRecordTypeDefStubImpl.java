package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.parser.psi.EpigraphRecordTypeDef;
import io.epigraph.lang.lexer.EpigraphElementTypes;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphRecordTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<EpigraphRecordTypeDef> implements EpigraphRecordTypeDefStub {

  EpigraphRecordTypeDefStubImpl(StubElement parent) {
    super(parent, (IStubElementType) EpigraphElementTypes.E_RECORD_TYPE_DEF);
  }

}
