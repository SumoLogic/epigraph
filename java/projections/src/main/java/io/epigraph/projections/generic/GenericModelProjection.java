package io.epigraph.projections.generic;

import de.uka.ilkd.pp.PrettyPrintable;
import io.epigraph.types.DatumType;
import io.epigraph.util.pp.DataPrettyPrinter;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GenericModelProjection<M extends DatumType> implements PrettyPrintable {
  @NotNull
  protected final M model;

  public GenericModelProjection(@NotNull M model) {
    this.model = model;
  }

  public M model() { return model; }

  @Override
  public String toString() { return DataPrettyPrinter.prettyPrint(this); }
}
