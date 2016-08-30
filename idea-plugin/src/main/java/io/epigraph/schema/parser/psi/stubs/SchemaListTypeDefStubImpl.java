package io.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.schema.lexer.SchemaElementTypes;
import io.epigraph.schema.parser.psi.SchemaListTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaListTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<SchemaListTypeDef> implements SchemaListTypeDefStub {
  SchemaListTypeDefStubImpl(StubElement parent,
                            String name,
                            String namespace,
                            @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) {
    super(parent, (IStubElementType) SchemaElementTypes.S_LIST_TYPE_DEF, name, namespace, extendsTypeRefs);
  }
}
