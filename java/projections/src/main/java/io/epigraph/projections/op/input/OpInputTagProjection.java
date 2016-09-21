package io.epigraph.projections.op.input;

import de.uka.ilkd.pp.DataLayouter;
import io.epigraph.projections.generic.GenericTagProjection;
import io.epigraph.types.Type;
import io.epigraph.util.pp.PrettyPrinterUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputTagProjection extends GenericTagProjection<OpInputModelProjection<?, ?>> {
  public OpInputTagProjection(@NotNull Type.Tag tag, @NotNull OpInputModelProjection<?, ?> projection) {
    super(tag, projection);
  }

  @Override
  public <Exc extends Exception> void prettyPrint(DataLayouter<Exc> l) throws Exc {
    if (projection().defaultValue() == null && projection().customParams() == null &&
        projection().metaProjection() == null) {

      l.beginCInd();
      if (projection().required()) l.print('+');
      l.print(tag().name());

      PrettyPrinterUtil.printWithBrkIfNonEmpty(l, projection());
      l.end();
    } else {
      l.beginCInd();
      if (projection().required()) l.print('+');
      l.print(tag().name());
      l.print(" {");

      if (projection().defaultValue() != null)
        l.nl().beginCInd().print("default:").brk().print(projection().defaultValue()).end();

      if (projection().metaProjection() != null)
        //noinspection ConstantConditions
        l.nl().beginIInd().print("meta:").brk().print(projection().metaProjection()).end();

      if (projection().customParams() != null)
        //noinspection ConstantConditions
        l.print(projection().customParams());

      PrettyPrinterUtil.printWithBrkIfNonEmpty(l, projection());

      l.end().nl().print('}');
    }
  }
}
