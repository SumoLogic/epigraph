package ws.epigraph.projections.gen;

import ws.epigraph.types.ListType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenListModelProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<MP>,
    MP extends GenModelProjection</*MP*/?, ?>,
    LMP extends GenListModelProjection<VP, TP, MP, LMP, M>,
    M extends ListType
    > extends GenModelProjection<LMP, M> {

  @NotNull VP itemsProjection();
}
