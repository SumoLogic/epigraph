package io.epigraph.projections.op.output;

import de.uka.ilkd.pp.DataLayouter;
import io.epigraph.projections.generic.GenericTagProjection;
import io.epigraph.types.Type;
import io.epigraph.util.pp.DataPrettyPrinter;
import io.epigraph.util.pp.PrettyPrinterUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputTagProjection extends GenericTagProjection<OpOutputModelProjection<?>> {
  public OpOutputTagProjection(@NotNull Type.Tag tag, @NotNull OpOutputModelProjection<?> projection) {
    super(tag, projection);
  }

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    if (projection().params() == null && projection().customParams() == null) {
      l.beginCInd();
      if (projection().includeInDefault()) l.print('+');
      l.print(tag().name());

      PrettyPrinterUtil.printWithBrkIfNonEmpty(l, projection());
      l.end();
    } else {
      l.beginCInd();
      if (projection().includeInDefault()) l.print('+');
      l.print(tag().name());
      l.print(" {");

      if (projection().params() != null)
        //noinspection ConstantConditions
        l.print(projection().params());

      if (projection().customParams() != null)
        //noinspection ConstantConditions
        l.print(projection().customParams());

      if (projection().metaProjection() != null)
        //noinspection ConstantConditions
        l.brk().beginIInd(0).print("meta:").brk().print(projection().metaProjection()).end();

      PrettyPrinterUtil.printWithBrkIfNonEmpty(l, projection());

      l.brk(1, -DataPrettyPrinter.DEFAULT_INDENTATION).end().print('}');
    }
  }
}
