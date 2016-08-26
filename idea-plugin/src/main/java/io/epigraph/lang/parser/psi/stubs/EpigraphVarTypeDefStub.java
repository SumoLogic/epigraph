package io.epigraph.lang.parser.psi.stubs;

import io.epigraph.lang.parser.psi.EpigraphVarTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface EpigraphVarTypeDefStub extends EpigraphTypeDefStubBase<EpigraphVarTypeDef> {
  @Nullable
  List<SerializedFqnTypeRef> getSupplementedTypeRefs();
}
