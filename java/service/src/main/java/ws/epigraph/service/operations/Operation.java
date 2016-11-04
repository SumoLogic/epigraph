package ws.epigraph.service.operations;

import ws.epigraph.idl.operations.OperationIdl;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class Operation<
    Decl extends OperationIdl,
    Req extends OperationRequest,
    Rsp extends OperationResponse> {

  @NotNull
  private final Decl declaration;

  protected Operation(@NotNull Decl declaration) {this.declaration = declaration;}

  @NotNull
  public Decl declaration() { return declaration; }

  @NotNull
  public abstract CompletableFuture<? extends Rsp> process(@NotNull Req request);
}
