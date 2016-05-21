package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDefElement;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
class SchemaTypeDefStubBaseImpl<T extends SchemaTypeDefElement> extends StubBase<T> implements SchemaTypeDefStubBase<T> {
  private final String name;  // TODO rename to fullName

  SchemaTypeDefStubBaseImpl(StubElement parent, final String name, final IStubElementType elementType) {
    super(parent, elementType);
    this.name = name;
  }

  @Nullable
  public String getName() {
    return name;
  }
}
