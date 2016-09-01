package io.epigraph.projections.op;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.PrettyPrintable;
import io.epigraph.types.DatumType;
import io.epigraph.util.pp.DataPrettyPrinter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpParam implements PrettyPrintable {
  public static final OpParam[] EMPTY_PARAMS = new OpParam[0];

  @NotNull
  private final String name;
  @NotNull
  private final DatumType model;

  // todo projection

  public OpParam(@NotNull String name, @NotNull DatumType model) {
    this.name = name;
    this.model = model;
  }

  @NotNull
  public static Set<OpParam> params(OpParam... params) {
    if (params.length == 0) return Collections.emptySet();
    return new HashSet<>(Arrays.asList(params));
  }

  @NotNull
  public String name() {
    return name;
  }

  @NotNull
  public DatumType model() {
    return model;
  }

  // todo equals, hashcode, tostring

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    l.print(name).print(": ").print(model.name().toString());
    // todo projection
  }

  @Override
  public String toString() {
    return DataPrettyPrinter.prettyPrint(this);
  }
}
