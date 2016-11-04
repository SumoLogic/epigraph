package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class SchemaTypeDefStubBaseImpl<T extends SchemaTypeDef> extends StubBase<T> implements SchemaTypeDefStubBase<T> {
  SchemaTypeDefStubBaseImpl(StubElement parent, final IStubElementType elementType) {
    super(parent, elementType);
  }
}
