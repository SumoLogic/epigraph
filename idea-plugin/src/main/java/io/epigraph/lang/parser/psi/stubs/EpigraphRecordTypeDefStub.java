package io.epigraph.lang.parser.psi.stubs;

import io.epigraph.lang.parser.psi.EpigraphRecordTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface EpigraphRecordTypeDefStub extends EpigraphTypeDefStubBase<EpigraphRecordTypeDef> {
  @Nullable
  List<SerializedFqnTypeRef> getSupplementedTypeRefs();
}
