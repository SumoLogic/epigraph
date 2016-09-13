package io.epigraph.projections.op.input;

import de.uka.ilkd.pp.DataLayouter;
import io.epigraph.data.ListDatum;
import io.epigraph.types.ListType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputListModelProjection extends OpInputModelProjection<ListType, ListDatum> {
  @NotNull
  private OpInputVarProjection itemsProjection;

  public OpInputListModelProjection(@NotNull ListType model,
                                    boolean required,
                                    @Nullable ListDatum defaultValue,
                                    @NotNull OpInputVarProjection itemsProjection) {
    super(model, required, defaultValue);
    this.itemsProjection = itemsProjection;
  }

  @NotNull
  public OpInputVarProjection itemsProjection() { return itemsProjection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OpInputListModelProjection that = (OpInputListModelProjection) o;
    return Objects.equals(itemsProjection, that.itemsProjection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), itemsProjection);
  }

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    prettyPrintModel(l);
    l.beginCInd().print(" {");

    prettyPrintDefaultValueBlock(l);

    l.brk().beginCInd().print("items:").brk();
    l.print(itemsProjection);
    l.end();

    l.end().brk().print("}");
  }
}
