package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
class SchemaTypeDefStubBaseImpl<T extends SchemaTypeDef> extends StubBase<T> implements SchemaTypeDefStubBase<T> {
  private final String name;
  private final String namespace;

  SchemaTypeDefStubBaseImpl(StubElement parent, final IStubElementType elementType, final String name, final String namespace) {
    super(parent, elementType);
    this.name = name;
    this.namespace = namespace;
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
}
