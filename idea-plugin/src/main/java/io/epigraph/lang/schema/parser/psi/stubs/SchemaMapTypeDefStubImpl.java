package io.epigraph.lang.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.lexer.EpigraphElementTypes;
import io.epigraph.lang.schema.parser.psi.SchemaMapTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaMapTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<SchemaMapTypeDef> implements SchemaMapTypeDefStub {
  SchemaMapTypeDefStubImpl(StubElement parent,
                           String name,
                           String namespace,
                           @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) {
    super(parent, (IStubElementType) EpigraphElementTypes.E_MAP_TYPE_DEF, name, namespace, extendsTypeRefs);
  }
}
