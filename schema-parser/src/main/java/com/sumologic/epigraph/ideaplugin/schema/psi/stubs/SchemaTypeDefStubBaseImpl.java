package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
class SchemaTypeDefStubBaseImpl<T extends SchemaTypeDef> extends StubBase<T> implements SchemaTypeDefStubBase<T> {
  SchemaTypeDefStubBaseImpl(StubElement parent, final IStubElementType elementType) {
    super(parent, elementType);
  }
}
