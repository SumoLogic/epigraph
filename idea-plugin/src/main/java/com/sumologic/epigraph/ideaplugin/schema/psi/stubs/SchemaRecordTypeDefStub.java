package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaRecordTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaRecordTypeDefStub extends SchemaTypeDefStubBase<SchemaRecordTypeDef> {
  @Nullable
  List<SerializedFqnTypeRef> getSupplementedTypeRefs();
}