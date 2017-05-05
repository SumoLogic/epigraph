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
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.invocation.OperationInvocationError;
import ws.epigraph.projections.op.input.OpInputVarProjection;
import ws.epigraph.projections.req.input.ReqInputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputModelProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.projections.req.update.ReqUpdateVarProjection;
import ws.epigraph.schema.operations.OperationDeclaration;
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
  private final @NotNull FormatReader.Factory<OpInputFormatReader> opInputFormatReaderFactory;
  private final @NotNull FormatReader.Factory<ReqInputFormatReader> reqInputFormatReaderFactory;
  private final @NotNull FormatReader.Factory<ReqUpdateFormatReader> reqUpdateFormatReaderFactory;

  private final @NotNull FormatWriter.Factory formatWriterFactory;

  public FormatBasedServerProtocol(
      @NotNull Function<C, HttpExchange> httpExchangeFactory,
      @NotNull FormatReader.Factory<OpInputFormatReader> opInputFormatReaderFactory,
      @NotNull FormatReader.Factory<ReqInputFormatReader> reqInputFormatReaderFactory,
      @NotNull FormatReader.Factory<ReqUpdateFormatReader> reqUpdateFormatReaderFactory,
      @NotNull FormatWriter.Factory formatWriterFactory) {
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
    if (reqInputProjection == null) {
      OpInputFormatReader formatReader = opInputFormatReaderFactory.newFormatReader(httpExchange.getInputStream());
      return formatReader.readData(opInputProjection);
    } else {
      ReqInputFormatReader formatReader = reqInputFormatReaderFactory.newFormatReader(httpExchange.getInputStream());
      return formatReader.readData(reqInputProjection);
    }
  }

  @Override
  public @Nullable Data readUpdateInput(
      @NotNull OpInputVarProjection opInputProjection,
      @Nullable ReqUpdateVarProjection reqUpdateProjection,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext) throws FormatException, IOException {

    HttpExchange httpExchange = httpExchangeFactory.apply(httpInvocationContext);
    if (reqUpdateProjection == null) {
      OpInputFormatReader formatReader = opInputFormatReaderFactory.newFormatReader(httpExchange.getInputStream());
      return formatReader.readData(opInputProjection);
    } else {
      @NotNull ReqUpdateFormatReader formatReader = reqUpdateFormatReaderFactory.newFormatReader(httpExchange.getInputStream());
      return formatReader.readData(reqUpdateProjection);
    }
  }


  @Override
  public void writeDataResponse(
      @NotNull OperationDeclaration operationDeclaration,
      @NotNull ReqOutputVarProjection projection,
      @Nullable Data data,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext) {

    writeFormatResponse(
        getSuccessStatusCode(operationDeclaration),
        httpInvocationContext,
        operationInvocationContext,
        writer -> writer.writeData(projection, data)
    );

  }

  @Override
  public void writeDatumResponse(
      @NotNull OperationDeclaration operationDeclaration,
      @NotNull ReqOutputModelProjection<?, ?, ?> projection,
      @Nullable Datum datum,
      @NotNull C httpInvocationContext,
      @NotNull OperationInvocationContext operationInvocationContext) {

    writeFormatResponse(
        getSuccessStatusCode(operationDeclaration),
        httpInvocationContext,
        operationInvocationContext,
        writer -> writer.writeDatum(projection, datum)
    );

  }

  @Override
  public void writeErrorResponse(
      @NotNull OperationDeclaration operationDeclaration,
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
      final @NotNull OperationDeclaration operationDeclaration,
      final @NotNull C httpInvocationContext,
      final @NotNull OperationInvocationContext operationInvocationContext) {

    writeFormatResponse(
        getSuccessStatusCode(operationDeclaration),
        httpInvocationContext,
        operationInvocationContext,
        writer -> writer.writeData(null)
    );

  }

  private int getSuccessStatusCode(final @NotNull OperationDeclaration operationDeclaration) {
    return operationDeclaration.kind() == OperationKind.CREATE ? HttpStatusCode.CREATED : HttpStatusCode.OK;
  }

  @Override
  public void writeInvocationErrorResponse(
      final @NotNull OperationDeclaration operationDeclaration,
      final @NotNull OperationInvocationError error,
      final @NotNull C httpInvocationContext,
      final @NotNull OperationInvocationContext operationInvocationContext) {

    HttpExchange httpExchange = httpExchangeFactory.apply(httpInvocationContext);

    httpExchange.setStatusCode(error.statusCode());

    OutputStreamWriter sw = new OutputStreamWriter(httpExchange.getOutputStream(), StandardCharsets.UTF_8);
    try {
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
        Collections.singletonMap(CONTENT_TYPE_HEADER, formatWriterFactory.characterEncoding());

    httpExchange.setHeaders(headers);

    try {
      FormatWriter formatWriter = formatWriterFactory.newFormatWriter(httpExchange.getOutputStream());
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
}
