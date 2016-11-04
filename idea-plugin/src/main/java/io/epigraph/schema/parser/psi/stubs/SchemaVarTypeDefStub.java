package io.epigraph.schema.parser.psi.stubs;

import io.epigraph.schema.parser.psi.SchemaVarTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public interface SchemaVarTypeDefStub extends SchemaTypeDefStubBase<SchemaVarTypeDef> {
  @Nullable
  List<SerializedFqnTypeRef> getSupplementedTypeRefs();
}
