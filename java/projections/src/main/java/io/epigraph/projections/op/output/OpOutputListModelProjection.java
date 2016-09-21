package io.epigraph.projections.op.output;

import de.uka.ilkd.pp.DataLayouter;
import io.epigraph.projections.op.OpCustomParams;
import io.epigraph.projections.op.OpParams;
import io.epigraph.types.ListType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputListModelProjection extends OpOutputModelProjection<ListType> {
  @NotNull
  private OpOutputVarProjection itemsProjection;

  public OpOutputListModelProjection(@NotNull ListType model,
                                     boolean includeInDefault,
                                     @Nullable OpParams params,
                                     @Nullable OpCustomParams customParams,
                                     @NotNull OpOutputVarProjection itemsProjection) {
    super(model, includeInDefault, params, customParams);
    this.itemsProjection = itemsProjection;
  }

  @NotNull
  public OpOutputVarProjection itemsProjection() { return itemsProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpOutputListModelProjection that = (OpOutputListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    l.beginCInd();
    l.print("*(").brk();
    l.print(itemsProjection());
    l.end().brk().print(')');
  }
}
