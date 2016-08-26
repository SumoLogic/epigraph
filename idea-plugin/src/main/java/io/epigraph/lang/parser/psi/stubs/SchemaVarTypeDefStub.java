package io.epigraph.lang.parser.psi.stubs;

import io.epigraph.lang.parser.psi.SchemaVarTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaVarTypeDefStub extends SchemaTypeDefStubBase<SchemaVarTypeDef> {
  @Nullable
  List<SerializedFqnTypeRef> getSupplementedTypeRefs();
}
