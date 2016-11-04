package ws.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import ws.epigraph.schema.lexer.SchemaElementTypes;
import ws.epigraph.schema.parser.psi.SchemaPrimitiveTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaPrimitiveTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<SchemaPrimitiveTypeDef> implements SchemaPrimitiveTypeDefStub {
  SchemaPrimitiveTypeDefStubImpl(StubElement parent,
                                 String name,
                                 String namespace,
                                 @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) {
    super(parent, (IStubElementType) SchemaElementTypes.S_PRIMITIVE_TYPE_DEF, name, namespace, extendsTypeRefs);
  }
}
