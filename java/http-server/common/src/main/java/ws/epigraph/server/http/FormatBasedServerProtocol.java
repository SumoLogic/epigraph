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
import ws.epigraph.http.ContentType;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.invocation.OperationInvocationError;
import ws.epigraph.projections.op.input.OpInputVarProjection;
import ws.epigraph.projections.req.input.ReqInputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputModelProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.projections.req.update.ReqUpdateVarProjection;
import ws.epigraph.schema.operations.OperationKind;
import ws.epigraph.util.HttpStatusCode;
import ws.epigraph.wire.FormatException;
import ws.epigraph.wire.FormatFactories;
import ws.epigraph.wire.FormatWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.function.Function;

import static ws.epigraph.http.Headers.ACCEPT;
import static ws.epigraph.http.Headers.CONTENT_TYPE;
import static ws.epigraph.http.MimeTypes.HTML;
import static ws.epigraph.http.MimeTypes.TEXT;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class FormatBasedServerProtocol<C extends HttpInvocationContext> implements ServerProtocol<C> {
  private final @NotNull Function<C, HttpExchange> httpExchangeFactory;
  private final @NotNull FormatSelector<C> formatSelector;

  public FormatBasedServerProtocol(
      @NotNull Function<C, HttpExchange> httpExchangeFactory,
      @NotNull FormatSelector<C> formatSelector) {
    this.httpExchangeFactory = httpExchangeFactory;
    this.formatSelector = formatSelector;
  }

  @Override
  public @Nullable Data readInput(
      @NotNull OpInputVarProjection opInputProjection,
      @Nullable ReqInputVarProjection reqInputProjection,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext) throws IOException {

    HttpExchange httpExchange = httpExchangeFactory.apply(httpInvocationContext);

    try {
      FormatFactories factories = formatSelector.getFactories(httpInvocationContext);

      return reqInputProjection == null
             ? factories.opInputReaderFactory()
                 .newFormatReader(httpExchange.getInputStream())
                 .readData(opInputProjection)
             : factories.reqInputReaderFactory()
                 .newFormatReader(httpExchange.getInputStream())
                 .readData(reqInputProjection);
    } catch (FormatException e) {
      throw new IOException(e.getMessage(), e);
    }
  }

  @Override
  public @Nullable Data readUpdateInput(
      @NotNull OpInputVarProjection opInputProjection,
      @Nullable ReqUpdateVarProjection reqUpdateProjection,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext) throws IOException {

    HttpExchange httpExchange = httpExchangeFactory.apply(httpInvocationContext);

    try {
      FormatFactories factories = formatSelector.getFactories(httpInvocationContext);

      return reqUpdateProjection == null
             ? factories.opInputReaderFactory()
                 .newFormatReader(httpExchange.getInputStream())
                 .readData(opInputProjection)
             : factories.reqUpdateReaderFactory()
                 .newFormatReader(httpExchange.getInputStream())
                 .readData(reqUpdateProjection);
    } catch (FormatException e) {
      throw new IOException(e.getMessage(), e);
    }
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

    Charset charset = Util.getCharset(httpExchange);
    try {
      OutputStreamWriter sw = new OutputStreamWriter(httpExchange.getOutputStream(), charset);
      if (error instanceof HtmlCapableOperationInvocationError && htmlAccepted(httpExchange)) {
        httpExchange.setHeaders(Collections.singletonMap(CONTENT_TYPE, ContentType.get(HTML, charset).toString()));
        sw.write(((HtmlCapableOperationInvocationError) error).htmlMessage());
      } else {
        httpExchange.setHeaders(Collections.singletonMap(CONTENT_TYPE, ContentType.get(TEXT, charset).toString()));
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
    Charset charset = Util.getCharset(httpExchange);

    try {
      FormatFactories factories = formatSelector.getFactories(httpInvocationContext);
      FormatWriter.Factory writerFactory = factories.writerFactory();

      httpExchange.setStatusCode(statusCode);
      httpExchange.setHeaders(Collections.singletonMap(
          CONTENT_TYPE,
          ContentType.get(writerFactory.format().mimeType(), charset).toString()
      ));

      try (FormatWriter formatWriter = writerFactory.newFormatWriter(httpExchange.getOutputStream(), charset)) {
        formatResponseWriter.write(formatWriter);
      }

    } catch (IOException e) {
      httpInvocationContext.logger().error("Error writing response", e);
    } catch (FormatException e) {
      handleFormatException(httpInvocationContext, httpExchange, e);
    } finally {
      try {
        httpExchange.close();
      } catch (IOException e) {
        httpInvocationContext.logger().error("Error closing HTTP exchange", e);
      }
    }
  }

  private void handleFormatException(
      final @NotNull C httpInvocationContext,
      final HttpExchange httpExchange,
      final FormatException e) {

    Charset charset = Util.getCharset(httpExchange);
    httpExchange.setStatusCode(HttpStatusCode.BAD_REQUEST);
    try {
      OutputStreamWriter sw = new OutputStreamWriter(httpExchange.getOutputStream(), charset);
      sw.write(e.getMessage());
      sw.close();
    } catch (IOException ioe) {
      httpInvocationContext.logger().error("Error writing out FormatException error", ioe);
    }
  }

  protected interface FormatResponseWriter {
    void write(@NotNull FormatWriter formatWriter) throws IOException;
  }

  private static boolean htmlAccepted(@NotNull HttpExchange exchange) {
    String accept = exchange.getHeader(ACCEPT);
    return accept != null && accept.contains(HTML);
  }

  public static class Factory<C extends HttpInvocationContext> {

    public @NotNull FormatBasedServerProtocol<C> newServerProtocol(
        @NotNull Function<C, HttpExchange> httpExchangeFactory,
        @NotNull FormatSelector<C> formatSelector) {

      return new FormatBasedServerProtocol<>(httpExchangeFactory, formatSelector);
    }
  }

}
