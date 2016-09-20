package io.epigraph.projections.op;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.PrettyPrintable;
import io.epigraph.projections.op.input.OpInputModelProjection;
import io.epigraph.util.pp.DataPrettyPrinter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpParam implements PrettyPrintable { // rename to OpOutputParam?
  @NotNull
  private final String name;
  @NotNull
  private final OpInputModelProjection<?, ?> projection;

  public OpParam(@NotNull String name, @NotNull OpInputModelProjection<?, ?> projection) {
    this.name = name;
    this.projection = projection;
  }

  @NotNull
  public static Set<OpParam> params(OpParam... params) {
    if (params.length == 0) return Collections.emptySet();
    return new HashSet<>(Arrays.asList(params));
  }

  @NotNull
  public String name() { return name; }

  public OpInputModelProjection<?, ?> projection() { return projection; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpParam opParam = (OpParam) o;
    return Objects.equals(name, opParam.name) &&
           Objects.equals(projection, opParam.projection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, projection);
  }

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    l.beginCInd().print(name).print(":").brk().print(projection).end();
  }

  @Override
  public String toString() { return DataPrettyPrinter.prettyPrint(this); }
}
