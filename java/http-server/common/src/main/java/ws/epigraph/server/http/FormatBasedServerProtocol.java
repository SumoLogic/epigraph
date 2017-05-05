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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.invocation.OperationInvocationError;
import ws.epigraph.projections.op.input.OpInputVarProjection;
import ws.epigraph.projections.req.input.ReqInputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputModelProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.projections.req.update.ReqUpdateVarProjection;
import ws.epigraph.schema.operations.OperationKind;
import ws.epigraph.util.HttpStatusCode;
import ws.epigraph.wire.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static ws.epigraph.server.http.Constants.CONTENT_TYPE_HTML;
import static ws.epigraph.server.http.Constants.CONTENT_TYPE_TEXT;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class FormatBasedServerProtocol<C extends HttpInvocationContext> implements ServerProtocol<C> {
  public static final String ACCEPT__HEADER = "Accept";
  public static final String CONTENT_TYPE_HEADER = "Content-Type";

  private final @NotNull Function<C, HttpExchange> httpExchangeFactory;
  private final @NotNull Function<C, FormatReader.Factory<? extends OpInputFormatReader>> opInputFormatReaderFactory;
  private final @NotNull Function<C, FormatReader.Factory<? extends ReqInputFormatReader>> reqInputFormatReaderFactory;
  private final @NotNull Function<C, FormatReader.Factory<? extends ReqUpdateFormatReader>> reqUpdateFormatReaderFactory;

  private final @NotNull Function<C, FormatWriter.Factory> formatWriterFactory;

  public FormatBasedServerProtocol(
      @NotNull Function<C, HttpExchange> httpExchangeFactory,
      @NotNull Function<C, FormatReader.Factory<? extends OpInputFormatReader>> opInputFormatReaderFactory,
      @NotNull Function<C, FormatReader.Factory<? extends ReqInputFormatReader>> reqInputFormatReaderFactory,
      @NotNull Function<C, FormatReader.Factory<? extends ReqUpdateFormatReader>> reqUpdateFormatReaderFactory,
      @NotNull Function<C, FormatWriter.Factory> formatWriterFactory) {
    this.httpExchangeFactory = httpExchangeFactory;
    this.opInputFormatReaderFactory = opInputFormatReaderFactory;
    this.reqInputFormatReaderFactory = reqInputFormatReaderFactory;
    this.reqUpdateFormatReaderFactory = reqUpdateFormatReaderFactory;
    this.formatWriterFactory = formatWriterFactory;
  }

  @Override
  public @Nullable Data readInput(
      @NotNull OpInputVarProjection opInputProjection,
      @Nullable ReqInputVarProjection reqInputProjection,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext) throws FormatException, IOException {

    HttpExchange httpExchange = httpExchangeFactory.apply(httpInvocationContext);
    return reqInputProjection == null
           ? opInputFormatReaderFactory
               .apply(httpInvocationContext)
               .newFormatReader(httpExchange.getInputStream())
               .readData(opInputProjection)
           : reqInputFormatReaderFactory
               .apply(httpInvocationContext)
               .newFormatReader(httpExchange.getInputStream())
               .readData(reqInputProjection);
  }

  @Override
  public @Nullable Data readUpdateInput(
      @NotNull OpInputVarProjection opInputProjection,
      @Nullable ReqUpdateVarProjection reqUpdateProjection,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext) throws FormatException, IOException {

    HttpExchange httpExchange = httpExchangeFactory.apply(httpInvocationContext);
    return reqUpdateProjection == null
           ? opInputFormatReaderFactory
               .apply(httpInvocationContext)
               .newFormatReader(httpExchange.getInputStream())
               .readData(opInputProjection)
           : reqUpdateFormatReaderFactory
               .apply(httpInvocationContext)
               .newFormatReader(httpExchange.getInputStream())
               .readData(reqUpdateProjection);
  }

  @Override
  public void writeDataResponse(
      @NotNull OperationKind operationKind,
      @NotNull ReqOutputVarProjection projection,
      @Nullable Data data,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext) {

    writeFormatResponse(
        getSuccessStatusCode(operationKind),
        httpInvocationContext,
        operationInvocationContext,
        writer -> writer.writeData(projection, data)
    );

  }

  @Override
  public void writeDatumResponse(
      @NotNull OperationKind operationKind,
      @NotNull ReqOutputModelProjection<?, ?, ?> projection,
      @Nullable Datum datum,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext) {

    writeFormatResponse(
        getSuccessStatusCode(operationKind),
        httpInvocationContext,
        operationInvocationContext,
        writer -> writer.writeDatum(projection, datum)
    );

  }

  @Override
  public void writeErrorResponse(
      @NotNull OperationKind operationKind,
      @NotNull ErrorValue error,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext) {

    writeFormatResponse(
        error.statusCode(), /*HttpStatusCode.OK, */
        httpInvocationContext,
        operationInvocationContext,
        writer -> writer.writeError(error)
    );

  }

  @Override
  public void writeEmptyResponse(
      final @NotNull OperationKind operationKind,
      final @NotNull C httpInvocationContext,
      final @NotNull OperationInvocationContext operationInvocationContext) {

    writeFormatResponse(
        getSuccessStatusCode(operationKind),
        httpInvocationContext,
        operationInvocationContext,
        writer -> writer.writeData(null)
    );

  }

  @Contract(pure = true)
  private int getSuccessStatusCode(final @NotNull OperationKind operationKind) {
    return operationKind == OperationKind.CREATE ? HttpStatusCode.CREATED : HttpStatusCode.OK;
  }

  @Override
  public void writeInvocationErrorResponse(
      final @NotNull OperationInvocationError error,
      final @NotNull C httpInvocationContext,
      final @NotNull OperationInvocationContext operationInvocationContext) {

    HttpExchange httpExchange = httpExchangeFactory.apply(httpInvocationContext);

    httpExchange.setStatusCode(error.statusCode());

    try {
      OutputStreamWriter sw = new OutputStreamWriter(httpExchange.getOutputStream(), StandardCharsets.UTF_8);
      if (error instanceof HtmlCapableOperationInvocationError && htmlAccepted(httpExchange)) {
        httpExchange.setHeaders(Collections.singletonMap(CONTENT_TYPE_HEADER, CONTENT_TYPE_HTML));
        sw.write(((HtmlCapableOperationInvocationError) error).htmlMessage());
      } else {
        httpExchange.setHeaders(Collections.singletonMap(CONTENT_TYPE_HEADER, CONTENT_TYPE_TEXT));
        sw.write(error.message() + "\n");
      }
      sw.close();
    } catch (IOException e) {
      httpInvocationContext.logger().error("Error writing response", e);
    }
  }

  protected void writeFormatResponse(
      int statusCode,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext,
      @NotNull FormatBasedServerProtocol.FormatResponseWriter formatResponseWriter) {

    HttpExchange httpExchange = httpExchangeFactory.apply(httpInvocationContext);
    httpExchange.setStatusCode(statusCode);

    Map<String, String> headers =
        Collections.singletonMap(
            CONTENT_TYPE_HEADER,
            formatWriterFactory.apply(httpInvocationContext).characterEncoding()
        );

    httpExchange.setHeaders(headers);

    try {
      FormatWriter formatWriter =
          formatWriterFactory.apply(httpInvocationContext).newFormatWriter(httpExchange.getOutputStream());
      formatResponseWriter.write(formatWriter);
      formatWriter.close();
      httpExchange.close();
    } catch (IOException e) {
      httpInvocationContext.logger().error("Error writing response", e);
    }
  }

  protected interface FormatResponseWriter {
    void write(@NotNull FormatWriter formatWriter) throws IOException;
  }

  private static boolean htmlAccepted(@NotNull HttpExchange exchange) {
    String accept = exchange.getHeader(ACCEPT__HEADER);
    return accept != null && accept.contains(CONTENT_TYPE_HTML);
  }

  public static class Factory<C extends HttpInvocationContext>
      /*implements ServerProtocol.Factory<C, FormatBasedServerProtocol<C>>*/ {

    // @Override
    public @NotNull FormatBasedServerProtocol<C> newServerProtocol(
        final @NotNull Function<C, HttpExchange> httpExchangeFactory,
        final @NotNull Function<C, FormatReader.Factory<? extends OpInputFormatReader>> opInputFormatReaderFactory,
        final @NotNull Function<C, FormatReader.Factory<? extends ReqInputFormatReader>> reqInputFormatReaderFactory,
        final @NotNull Function<C, FormatReader.Factory<? extends ReqUpdateFormatReader>> reqUpdateFormatReaderFactory,
        final @NotNull Function<C, FormatWriter.Factory> formatWriterFactory) {

      return new FormatBasedServerProtocol<>(
          httpExchangeFactory,
          opInputFormatReaderFactory,
          reqInputFormatReaderFactory,
          reqUpdateFormatReaderFactory,
          formatWriterFactory
      );
    }
  }

}
