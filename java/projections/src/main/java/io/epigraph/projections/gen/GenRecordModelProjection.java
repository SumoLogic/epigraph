package io.epigraph.projections.gen;

import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenRecordModelProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<MP>,
    MP extends GenModelProjection</*MP*/?, ?>,
    RMP extends GenRecordModelProjection<VP, TP, MP, RMP, FPE, FP, M>,
    FPE extends GenFieldProjectionEntry<VP, TP, MP, FP>,
    FP extends GenFieldProjection<VP, TP, MP>,
    M extends RecordType
    > extends GenModelProjection<RMP, M> {

  @NotNull Map<String, FPE> fieldProjections();

  /**
   * @return single field projection if there's just one, {@code null} otherwise
   */
  @Nullable
  default FPE pathFieldProjection() {
    @NotNull final Map<String, FPE> fieldProjections = fieldProjections();
    if (fieldProjections.size() == 1) return fieldProjections.values().iterator().next();
    else return null;
  }
}
