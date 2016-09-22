package io.epigraph.projections.op.input;

import de.uka.ilkd.pp.DataLayouter;
import io.epigraph.data.MapDatum;
import io.epigraph.projections.op.OpCustomParams;
import io.epigraph.types.MapType;
import io.epigraph.util.pp.DataPrettyPrinter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputMapModelProjection extends OpInputModelProjection<MapType, MapDatum> {
  @NotNull
  private final OpInputVarProjection itemsProjection;

  public OpInputMapModelProjection(@NotNull MapType model,
                                   boolean required,
                                   @Nullable MapDatum defaultValue,
                                   @Nullable OpCustomParams customParams,
                                   @Nullable OpInputModelProjection<?, ?> metaProjection,
                                   @NotNull OpInputVarProjection valuesProjection) {
    super(model, required, defaultValue, customParams, metaProjection);
    this.itemsProjection = valuesProjection;
  }

  @NotNull
  public OpInputVarProjection itemsProjection() { return itemsProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpInputMapModelProjection that = (OpInputMapModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    l.beginIInd();

    l.print("[](").brk();
    l.print(itemsProjection());
    l.brk(1, -DataPrettyPrinter.DEFAULT_INDENTATION).end().print(')');
  }
}
