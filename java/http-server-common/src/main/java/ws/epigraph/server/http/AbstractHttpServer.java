/*
 * Copyright 2017 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.server.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.invocation.*;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import ws.epigraph.projections.op.input.OpInputVarProjection;
import ws.epigraph.projections.req.input.ReqInputFieldProjection;
import ws.epigraph.projections.req.input.ReqInputModelProjection;
import ws.epigraph.projections.req.input.ReqInputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.output.ReqOutputModelProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.schema.operations.OperationKind;
import ws.epigraph.server.http.routing.*;
import ws.epigraph.service.AmbiguousPathException;
import ws.epigraph.service.OutputProjectionPathRemover;
import ws.epigraph.service.Resource;
import ws.epigraph.service.operations.*;
import ws.epigraph.url.CreateRequestUrl;
import ws.epigraph.url.ReadRequestUrl;
import ws.epigraph.url.RequestUrl;
import ws.epigraph.url.parser.UrlSubParserDefinitions;
import ws.epigraph.url.parser.psi.UrlCreateUrl;
import ws.epigraph.url.parser.psi.UrlReadUrl;
import ws.epigraph.url.parser.psi.UrlUrl;
import ws.epigraph.wire.FormatException;
import ws.epigraph.wire.FormatReader;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractHttpServer<C extends InvocationContext> {

  protected abstract @NotNull OperationInvocations<Data> operationInvocations();

  protected abstract long responseTimeout(@NotNull C context);

  // can't do this: need to set status code and write everything in one go (in case of Undertow)
//  protected abstract @NotNull FormatReader<ReqOutputVarProjection, ReqOutputModelProjection<?, ?, ?>> reqOutputWriter(@NotNull C context);

  protected abstract @NotNull FormatReader<OpInputVarProjection, OpInputModelProjection<?, ?, ?, ?>> opInputReader(@NotNull C context);

  protected abstract @NotNull FormatReader<ReqInputVarProjection, ReqInputModelProjection<?, ?, ?>> reqInputReader(@NotNull C context);

  protected abstract void writeDataResponse(
      int statusCode,
      @NotNull ReqOutputVarProjection projection,
      @Nullable Data data,
      @NotNull C context);

  protected abstract void writeDatumResponse(
      int statusCode,
      @NotNull ReqOutputModelProjection<?, ?, ?> projection,
      @Nullable Datum datum,
      @NotNull C context);

  protected abstract void writeErrorResponse(@NotNull ErrorValue error, @NotNull C context);

  protected abstract void writeEmptyResponse(@NotNull C context);

  protected abstract void writeInvocationErrorResponse(@NotNull OperationInvocationError error, @NotNull C context);

  // ----------------------------------- READ

  protected void handleReadRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context) {
    handleReadResponse(HttpStatusCode.OK, invokeReadRequest(resource, operationName, decodedUri, context), context);
  }

  private static final class ReadResult {
    final @Nullable Data data;
    final int pathSteps;
    final @NotNull ReqOutputVarProjection projection;

    ReadResult(
        final @Nullable Data data,
        final int steps,
        final @NotNull ReqOutputVarProjection projection) {
      this.data = data;
      pathSteps = steps;
      this.projection = projection;
    }
  }

  private @NotNull CompletionStage<OperationInvocationResult<ReadResult>> invokeReadRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context) {

    // pre-check; custom operation can be called with wrong HTTP method and Url parsing error will be confusing
    if (operationName != null && resource.namedReadOperation(operationName) == null)
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new OperationNotFoundError(
                  resource.declaration().fieldName(),
                  OperationKind.READ,
                  operationName
              )
          )
      );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();
    UrlReadUrl urlPsi = parseReadUrlPsi(decodedUri, errorsAccumulator, context);

    if (errorsAccumulator.hasErrors())
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new RequestParsingInvocationError(
                  resource.declaration().fieldName(),
                  OperationKind.READ,
                  operationName,
                  decodedUri,
                  Util.psiErrorsToPsiProcessingErrors(errorsAccumulator.errors())
              )
          )
      );

    return withReadOperation(
        decodedUri,
        resource,
        operationName,
        urlPsi,
        operationSearchResult -> {
          ReadRequestUrl requestUrl = operationSearchResult.requestUrl();
          StepsAndProjection<ReqOutputFieldProjection> outputProjection = requestUrl.outputProjection();

          ReadOperation<Data> operation = operationSearchResult.operation();

          OperationInvocation<ReadOperationRequest, ReadOperationResponse<Data>>
              operationInvocation = operationInvocations().readOperationInvocation(operation);

          return operationInvocation.invoke(
              new ReadOperationRequest(
                  requestUrl.path(),
                  outputProjection.projection()
              )
          ).thenApply(result -> result.mapSuccess(success ->
              new ReadResult(
                  success.getData(),
                  outputProjection.pathSteps(),
                  outputProjection.projection().varProjection()
              )
          ));
        },
        context
    );
  }

  private @NotNull UrlReadUrl parseReadUrlPsi(
      @NotNull String urlString,
      @NotNull EpigraphPsiUtil.ErrorProcessor errorsAccumulator,
      @NotNull C context) {

    UrlReadUrl urlPsi = EpigraphPsiUtil.parseText(
        urlString,
        UrlSubParserDefinitions.READ_URL.rootElementType(),
        UrlReadUrl.class,
        UrlSubParserDefinitions.READ_URL,
        errorsAccumulator
    );

    if (context.isDebugMode())
      context.logger().info(Util.dumpUrl(urlPsi));

    return urlPsi;
  }

  @SuppressWarnings("unchecked")
  private <R> CompletionStage<OperationInvocationResult<R>> withReadOperation(
      @NotNull String requestText,
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UrlReadUrl urlPsi,
      @NotNull Function<OperationSearchSuccess<ReadOperation<Data>, ReadRequestUrl>, CompletableFuture<OperationInvocationResult<R>>> continuation,
      @NotNull C context) {

    return this.withOperation(
        requestText,
        resource,
        operationName,
        urlPsi,
        ReadOperationRouter.INSTANCE,
        OperationKind.READ,
        continuation,
        context
    );
  }

  private void handleReadResponse(
      final int statusCode,
      @NotNull CompletionStage<OperationInvocationResult<ReadResult>> responseFuture,
      @NotNull C context) {

    final Consumer<OperationInvocationResult<ReadResult>> resultConsumer =
        invocationResult ->
            invocationResult.consume(

                readResult -> {
                  try {
                    writeData(statusCode, readResult.pathSteps, readResult.projection, readResult.data, context);
                  } catch (RuntimeException e) {
                    writeInvocationErrorResponse(
                        new GenericServerInvocationError(e.toString()),
                        context
                    );
                  }
                },

                error -> writeInvocationErrorResponse(error, context)
            );

    final Function<Throwable, Void> failureConsumer = throwable -> {
      writeInvocationErrorResponse(
          new GenericServerInvocationError(throwable.getMessage()), context
      );
      return null;
    };

    long timeout = responseTimeout(context);
    if (timeout > 0) {
      CompletionStage<OperationInvocationResult<ReadResult>> timeoutStage = Util.failAfter(Duration.ofMillis(timeout));
      responseFuture.acceptEither(timeoutStage, resultConsumer).exceptionally(failureConsumer);
    } else {
      responseFuture.thenAccept(resultConsumer).exceptionally(failureConsumer);
    }
  }

  // ----------------------------------- CREATE

  protected void handleCreateRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context) {
    handleReadResponse(
        HttpStatusCode.CREATED,
        invokeCreateRequest(resource, operationName, decodedUri, context),
        context
    );
  }

  private @NotNull CompletionStage<OperationInvocationResult<ReadResult>> invokeCreateRequest(
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull String decodedUri,
      @NotNull C context) {

    // pre-check; custom operation can be called with wrong HTTP method and Url parsing error will be confusing
    if (operationName != null && resource.namedReadOperation(operationName) == null)
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new OperationNotFoundError(
                  resource.declaration().fieldName(),
                  OperationKind.CREATE,
                  operationName
              )
          )
      );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();
    UrlCreateUrl urlPsi = parseCreateUrlPsi(decodedUri, errorsAccumulator, context);

    if (errorsAccumulator.hasErrors())
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new RequestParsingInvocationError(
                  resource.declaration().fieldName(),
                  OperationKind.CREATE,
                  operationName,
                  decodedUri,
                  Util.psiErrorsToPsiProcessingErrors(errorsAccumulator.errors())
              )
          )
      );

    return withCreateOperation(
        decodedUri,
        resource,
        operationName,
        urlPsi,
        operationSearchResult -> {
          @NotNull CreateRequestUrl requestUrl = operationSearchResult.requestUrl();
          ReqInputFieldProjection inputProjection = requestUrl.inputProjection();
          StepsAndProjection<ReqOutputFieldProjection> outputProjection = requestUrl.outputProjection();

          @NotNull CreateOperation<Data> operation = operationSearchResult.operation();

          final Data body;
          try {
            if (inputProjection == null) {
              FormatReader<OpInputVarProjection, OpInputModelProjection<?, ?, ?, ?>> reader = opInputReader(context);
              body = reader.readData(operation.declaration().inputProjection().varProjection());
            } else {
              FormatReader<ReqInputVarProjection, ReqInputModelProjection<?, ?, ?>> reader = reqInputReader(context);
              body = reader.readData(inputProjection.varProjection());
            }
          } catch (FormatException | IOException e) {
            return CompletableFuture.completedFuture(
                OperationInvocationResult.failure(
                    new MalformedInputInvocationError("Error reading request body: " + e.getMessage())
                )
            );
          }

          OperationInvocation<CreateOperationRequest, ReadOperationResponse<Data>>
              operationInvocation = operationInvocations().createOperationInvocation(operation);

          return operationInvocation.invoke(
              new CreateOperationRequest(
                  requestUrl.path(),
                  body,
                  inputProjection,
                  outputProjection.projection()
              )
          ).thenApply(result -> result.mapSuccess(success ->
              new ReadResult(
                  success.getData(),
                  outputProjection.pathSteps(),
                  outputProjection.projection().varProjection()
              )
          ));
        },
        context
    );
  }

  private @NotNull UrlCreateUrl parseCreateUrlPsi(
      @NotNull String urlString,
      @NotNull EpigraphPsiUtil.ErrorProcessor errorsAccumulator,
      @NotNull C context) {

    UrlCreateUrl urlPsi = EpigraphPsiUtil.parseText(
        urlString,
        UrlSubParserDefinitions.CREATE_URL.rootElementType(),
        UrlCreateUrl.class,
        UrlSubParserDefinitions.CREATE_URL,
        errorsAccumulator
    );

    if (context.isDebugMode())
      context.logger().info(Util.dumpUrl(urlPsi));

    return urlPsi;
  }

  @SuppressWarnings("unchecked")
  private <R> CompletionStage<OperationInvocationResult<R>> withCreateOperation(
      @NotNull String requestText,
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UrlCreateUrl urlPsi,
      @NotNull Function<OperationSearchSuccess<CreateOperation<Data>, CreateRequestUrl>, CompletableFuture<OperationInvocationResult<R>>> continuation,
      @NotNull C context) {

    return this.withOperation(
        requestText,
        resource,
        operationName,
        urlPsi,
        CreateOperationRouter.INSTANCE,
        OperationKind.CREATE,
        continuation,
        context
    );
  }

  // ---------------------------------

  private void writeData(
      int statusCode,
      final int pathSteps,
      @NotNull ReqOutputVarProjection reqProjection,
      @Nullable Data data,
      @NotNull C context) {

    try {
      if (data == null) {
        writeEmptyResponse(context);
      } else {
        DataPathRemover.PathRemovalResult noPathData = DataPathRemover.removePath(reqProjection, data, pathSteps);

        if (noPathData.error == null) {
          final OutputProjectionPathRemover.PathRemovalResult noPathProjection =
              OutputProjectionPathRemover.removePath(reqProjection, pathSteps);

          final @Nullable ReqOutputVarProjection varProjection = noPathProjection.varProjection();
          final @Nullable ReqOutputModelProjection<?, ?, ?> modelProjection = noPathProjection.modelProjection();

          if (varProjection != null) {
            writeDataResponse(statusCode, varProjection, noPathData.data, context);
          } else if (modelProjection != null) {
            writeDatumResponse(statusCode, modelProjection, noPathData.datum, context);
          } else {
            writeEmptyResponse(context);
          }
        } else {
          writeErrorResponse(noPathData.error, context);
        }
      }

    } catch (AmbiguousPathException ignored) {
      writeInvocationErrorResponse(
          new GenericServerInvocationError(
//              String.format(
//                  "Can't remove %d path steps from data: \n%s\n",
//                  pathSteps == 0 ? 0 : pathSteps - 1,
//                  dataToString(data)
//              )
              String.format(
                  "Can't remove %d path steps from data",
                  pathSteps == 0 ? 0 : pathSteps - 1
              )
          ),
          context
      );
    } catch (RuntimeException e) {
      context.logger().error("Error writing response", e);
      writeInvocationErrorResponse(
          new GenericServerInvocationError(
              "Error writing response: " + e.getMessage()
          ),
          context
      );
    }
  }

  @SuppressWarnings("unchecked")
  private <
      O extends Operation<?, ?, ?>,
      U extends RequestUrl,
      UP extends UrlUrl,
      R extends AbstractOperationRouter<UP, ?, ?, U>,
      Rsp
      >
  CompletionStage<OperationInvocationResult<Rsp>> withOperation(
      @NotNull String requestText,
      @NotNull Resource resource,
      @Nullable String operationName,
      @NotNull UP urlPsi,
      @NotNull R router,
      @NotNull OperationKind operationKind,
      @NotNull Function<OperationSearchSuccess<O, U>, CompletableFuture<OperationInvocationResult<Rsp>>> continuation,
      @NotNull C context
  ) {

    try {
      OperationSearchResult<?> searchResult =
          router.findOperation(operationName, urlPsi, resource, context.typesResolver());

      if (searchResult instanceof OperationNotFound<?>) {
        return CompletableFuture.completedFuture(
            OperationInvocationResult.failure(
                new OperationNotFoundError(
                    resource.declaration().fieldName(),
                    operationKind,
                    operationName
                )
            )
        );
      }

      if (searchResult instanceof OperationSearchFailure<?>) {
        return CompletableFuture.completedFuture(
            OperationInvocationResult.failure(
                new OperationSearchFailureInvocationError(
                    resource.declaration().fieldName(),
                    requestText,
                    (OperationSearchFailure<?>) searchResult
                )
            )
        );
      }

      assert searchResult instanceof OperationSearchSuccess;
      OperationSearchSuccess<O, U> searchSuccessResult = (OperationSearchSuccess<O, U>) searchResult;
      return continuation.apply(searchSuccessResult);

    } catch (PsiProcessingException e) {
      return CompletableFuture.completedFuture(
          OperationInvocationResult.failure(
              new RequestParsingInvocationError(
                  resource.declaration().fieldName(),
                  OperationKind.READ,
                  operationName,
                  requestText,
                  e.errors()
              )
          )
      );
    }
  }

}
