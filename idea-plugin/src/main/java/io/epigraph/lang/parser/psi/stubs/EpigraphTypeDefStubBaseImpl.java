package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
class EpigraphTypeDefStubBaseImpl<T extends EpigraphTypeDef> extends StubBase<T> implements EpigraphTypeDefStubBase<T> {
  private final String name;
  private final String namespace;

  @Nullable
  private List<SerializedFqnTypeRef> extendsTypeRefs;

  EpigraphTypeDefStubBaseImpl(StubElement parent,
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
