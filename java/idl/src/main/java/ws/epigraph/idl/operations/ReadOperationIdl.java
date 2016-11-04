package ws.epigraph.idl.operations;

import ws.epigraph.idl.IdlError;
import ws.epigraph.idl.ResourceIdl;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.output.OpOutputFieldProjection;
import ws.epigraph.projections.op.path.OpFieldPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadOperationIdl extends OperationIdl {
  protected ReadOperationIdl(
      @Nullable String name,
      @NotNull Annotations annotations,
      @Nullable OpFieldPath path,
      @NotNull OpOutputFieldProjection outputProjection,
      @NotNull TextLocation location) {

    super(OperationKind.READ, HttpMethod.GET, name, annotations,
          path, null, outputProjection, location
    );
  }

  @Override
  protected void validate(@NotNull ResourceIdl resource, @NotNull List<IdlError> errors) {
    super.validate(resource, errors);

    ensureProjectionStartsWithResourceType(
        resource,
        outputProjection().projection(),
        "output",
        errors
    );

  }
}
