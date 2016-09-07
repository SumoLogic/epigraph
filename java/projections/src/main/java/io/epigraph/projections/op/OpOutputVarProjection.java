package io.epigraph.projections.op;

import de.uka.ilkd.pp.DataLayouter;
import de.uka.ilkd.pp.PrettyPrintable;
import io.epigraph.types.Type;
import io.epigraph.util.pp.DataPrettyPrinter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputVarProjection implements PrettyPrintable {
  @NotNull
  private final Type type;
  @NotNull
  private final LinkedHashSet<OpOutputTagProjection> tagProjections;

  public OpOutputVarProjection(@NotNull Type type,
                               @NotNull LinkedHashSet<OpOutputTagProjection> tagProjections) {
    this.type = type;
    this.tagProjections = tagProjections;
  }

  public OpOutputVarProjection(@NotNull Type type,
                               OpOutputTagProjection... tagProjections) {
    this(type, new LinkedHashSet<>(Arrays.asList(tagProjections)));
  }


  @NotNull
  public Type type() { return type; }

  public @NotNull LinkedHashSet<OpOutputTagProjection> tagProjections() { return tagProjections; }

  // todo projection by tag


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    OpOutputVarProjection that = (OpOutputVarProjection) o;

    return type.equals(that.type) && tagProjections.equals(that.tagProjections);
  }

  @Override
  public int hashCode() {
    return 31 * type.hashCode() + tagProjections.hashCode();
  }

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    // TODO treat single-branch/samo- vars in a special way?
    l.beginCInd().print("var ").print(type.name().toString()).print(" {");
    for (OpOutputTagProjection tagProjection : tagProjections) {
      l.nl().print(tagProjection);
    }
    l.end().brk().print("}");
  }

  @Override
  public String toString() { return DataPrettyPrinter.prettyPrint(this); }
}
