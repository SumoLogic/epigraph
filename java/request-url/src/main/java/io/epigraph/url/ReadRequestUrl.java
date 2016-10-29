package io.epigraph.url;

import io.epigraph.gdata.GDatum;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.projections.req.output.ReqOutputFieldProjection;
import io.epigraph.projections.req.path.ReqVarPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadRequestUrl extends RequestUrl {
  public ReadRequestUrl(
      @NotNull String fieldName,
      @Nullable ReqVarPath path,
      @NotNull StepsAndProjection<ReqOutputFieldProjection> outputProjection,
      @NotNull Map<String, GDatum> parameters) {
    super(fieldName, path, outputProjection, parameters);
  }
}
