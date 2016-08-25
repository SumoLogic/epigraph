package io.epigraph.lang.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
class SchemaTypeDefStubBaseImpl<T extends SchemaTypeDef> extends StubBase<T> implements SchemaTypeDefStubBase<T> {
  private final String name;
  private final String namespace;

  @Nullable
  private List<SerializedFqnTypeRef> extendsTypeRefs;

  SchemaTypeDefStubBaseImpl(StubElement parent,
                            final IStubElementType elementType,
                            final String name,
                            final String namespace,
                            @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) {
    super(parent, elementType);
    this.name = name;
    this.namespace = namespace;
    this.extendsTypeRefs = extendsTypeRefs;
  }

  @Nullable
  public String getName() {
    return name;
  }

  @Nullable
  @Override
  public String getNamespace() {
    return namespace;
  }

  @Nullable
  @Override
  public List<SerializedFqnTypeRef> getExtendsTypeRefs() {
    return extendsTypeRefs;
  }
}
