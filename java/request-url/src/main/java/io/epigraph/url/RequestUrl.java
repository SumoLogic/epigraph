package io.epigraph.url;

import io.epigraph.gdata.GDatum;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.projections.req.output.ReqOutputFieldProjection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class RequestUrl {
  @NotNull
  private final String fieldName;
  @NotNull
  private final StepsAndProjection<ReqOutputFieldProjection> fieldProjection;
  @NotNull
  private final Map<String, GDatum> parameters;

  public RequestUrl(@NotNull String fieldName,
                    @NotNull StepsAndProjection<ReqOutputFieldProjection> fieldProjection,
                    @NotNull Map<String, GDatum> parameters) {

    this.fieldName = fieldName;
    this.fieldProjection = fieldProjection;
    this.parameters = parameters;
  }

  @NotNull
  public String fieldName() { return fieldName; }

  @NotNull
  public StepsAndProjection<ReqOutputFieldProjection> fieldProjection() { return fieldProjection; }

  @NotNull
  public Map<String, GDatum> parameters() { return parameters; }
}
