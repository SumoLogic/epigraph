package ws.epigraph.url;

import ws.epigraph.gdata.GDatum;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqFieldPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadRequestUrl extends RequestUrl {
  public ReadRequestUrl(
      @NotNull String fieldName,
      @Nullable ReqFieldPath path,
      @NotNull StepsAndProjection<ReqOutputFieldProjection> outputProjection,
      @NotNull Map<String, GDatum> parameters) {
    super(fieldName, path, outputProjection, parameters);
  }
}
