package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
class SchemaTypeDefStubBaseImpl<T extends EpigraphTypeDef> extends StubBase<T> implements SchemaTypeDefStubBase<T> {
  SchemaTypeDefStubBaseImpl(StubElement parent, final IStubElementType elementType) {
    super(parent, elementType);
  }
}
