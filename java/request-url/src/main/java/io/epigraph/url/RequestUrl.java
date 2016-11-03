package io.epigraph.url;

import io.epigraph.gdata.GDatum;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.projections.req.output.ReqOutputFieldProjection;
import io.epigraph.projections.req.path.ReqFieldPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class RequestUrl {
  @NotNull
  private final String fieldName;
  @Nullable
  private final ReqFieldPath path;
  @NotNull
  private final StepsAndProjection<ReqOutputFieldProjection> outputProjection;
  @NotNull
  private final Map<String, GDatum> parameters;

  public RequestUrl(
      @NotNull String fieldName,
      @Nullable ReqFieldPath path,
      @NotNull StepsAndProjection<ReqOutputFieldProjection> outputProjection,
      @NotNull Map<String, GDatum> parameters) {

    this.fieldName = fieldName;
    this.path = path;
    this.outputProjection = outputProjection;
    this.parameters = parameters;
  }

  @NotNull
  public String fieldName() { return fieldName; }

  public @Nullable ReqFieldPath path() { return path; }

  @NotNull
  public StepsAndProjection<ReqOutputFieldProjection> outputProjection() { return outputProjection; }

  @NotNull
  public Map<String, GDatum> parameters() { return parameters; }
}
