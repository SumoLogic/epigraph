package io.epigraph.projections.gen;

import io.epigraph.types.MapType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenMapModelProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<MP>,
    MP extends GenModelProjection</*MP*/?, ?>,
    MMP extends GenMapModelProjection<VP, TP, MP, MMP, M>,
    M extends MapType
    > extends GenModelProjection<MMP, M> {

  @NotNull VP itemsProjection();
}
