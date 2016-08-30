package com.sumologic.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import com.sumologic.epigraph.schema.parser.psi.SchemaSupplementDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaSupplementDefStub extends StubElement<SchemaSupplementDef> {
  SerializedFqnTypeRef getSourceTypeRef();

  @Nullable
  List<SerializedFqnTypeRef> getSupplementedTypeRefs();
}
