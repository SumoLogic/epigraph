package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaVarTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaVarTypeDefStub extends SchemaTypeDefStubBase<SchemaVarTypeDef> {
  @Nullable
  List<SerializedFqnTypeRef> getSupplementedTypeRefs();
}
