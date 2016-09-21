package io.epigraph.projections.op.output;

import de.uka.ilkd.pp.DataLayouter;
import io.epigraph.projections.generic.GenericTagProjection;
import io.epigraph.types.Type;
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
        l.beginIInd(0).print(projection().params()).end();

      if (projection().customParams() != null)
        //noinspection ConstantConditions
        l.beginIInd(0).print(projection().customParams()).end();

      if (projection().metaProjection() != null)
        //noinspection ConstantConditions
        l.nl().beginIInd(0).print("meta:").brk().print(projection().metaProjection()).end();

      PrettyPrinterUtil.printWithBrkIfNonEmpty(l, projection());

      l.end().nl().print('}');
    }
  }
}
