package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.lexer.EpigraphElementTypes;
import io.epigraph.lang.parser.psi.EpigraphTypeDefWrapper;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
class EpigraphTypeDefWrapperStubImpl extends StubBase<EpigraphTypeDefWrapper> implements EpigraphTypeDefWrapperStub {
  EpigraphTypeDefWrapperStubImpl(StubElement parent) {
    super(parent, (IStubElementType) EpigraphElementTypes.E_TYPE_DEF_WRAPPER);
  }
}
