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

package ws.epigraph.client.http;

import org.apache.http.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.epigraph.data.Data;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.invocation.OperationInvocationContext;
import ws.epigraph.invocation.OperationInvocationResult;
import ws.epigraph.projections.req.output.ReqOutputTagProjectionEntry;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.types.Type;
import ws.epigraph.util.HttpStatusCode;
import ws.epigraph.util.IOUtil;
import ws.epigraph.wire.FormatException;
import ws.epigraph.wire.FormatFactories;
import ws.epigraph.wire.FormatReader;
import ws.epigraph.wire.ReqOutputFormatReader;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class FormatBasedServerProtocol implements ServerProtocol {
  private static final Logger LOG = LoggerFactory.getLogger(FormatBasedServerProtocol.class);

  private final @NotNull FormatReader.Factory<? extends ReqOutputFormatReader> reqOutputReaderFactory;

  public FormatBasedServerProtocol(final @NotNull FormatFactories formatFactories) {
    reqOutputReaderFactory = formatFactories.reqOutputReaderFactory();
  }

  @Override
  public @Nullable OperationInvocationResult<ReadOperationResponse<?>> readResponse(
      final @NotNull ReqOutputVarProjection projection,
      final @NotNull OperationInvocationContext operationInvocationContext,
      final @NotNull HttpResponse httpResponse) {

    int statusCode = httpResponse.getStatusLine().getStatusCode();
    try {
      if (statusCode == HttpStatusCode.OK) {
        try (InputStream inputStream = httpResponse.getEntity().getContent()) {
          ReqOutputFormatReader formatReader = reqOutputReaderFactory.newFormatReader(inputStream);
          Data data = formatReader.readData(projection);
          return OperationInvocationResult.success(new ReadOperationResponse<>(data));
        } catch (FormatException e) {
          return OperationInvocationResult.failure(new MalformedOutputInvocationError(e));
        }
      } else {
        // todo IMPORTANT! this can be a json error or something else, there must be a way to tell between the two!
        // use response content type? we should set accept correctly as well, to both json and plain text

//        ErrorValue error = readError(httpResponse);
//        Data data = createErrorData(projection, error);
//        return OperationInvocationResult.success(new ReadOperationResponse<>(data));

        try (InputStream is = httpResponse.getEntity().getContent();) {
          return OperationInvocationResult.failure(
              new ServerSideInvocationError(
                  statusCode,
                  IOUtil.readInputStream(is, StandardCharsets.UTF_8)
              )
          );
        }

      }
    } catch (IOException e) {
      if (operationInvocationContext.isDebug())
        LOG.error("Error reading operation response", e);

      return OperationInvocationResult.failure(new IOExceptionInvocationError(e));
    }
  }

//  private @NotNull ErrorValue readError(@NotNull HttpResponse response) throws IOException {
//    // read response text fully first as we might need to use it twice
//
//    Charset charset = StandardCharsets.UTF_8;
//    StringBuilder textBuilder = new StringBuilder();
//
//    try (
//        InputStream inputStream = response.getEntity().getContent();
//        Reader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(charset.name())))
//    ) {
//      for (int c = reader.read(); c != -1; c = reader.read()) { textBuilder.append((char) c); }
//    }
//
//    String string = textBuilder.toString();
//    try {
//      ReqOutputFormatReader formatReader = reqOutputReaderFactory.newFormatReader(
//          new ByteArrayInputStream(string.getBytes(charset))
//      );
//      return formatReader.readError();
//    } catch (FormatException ignored) { // log it? not all messages are guaranteed to be in proper format
//      return new ErrorValue(response.getStatusLine().getStatusCode(), string);
//    }
//  }

//  private @NotNull Data createErrorData(@NotNull ReqOutputVarProjection projection, @NotNull ErrorValue errorValue) {
//    // create data instance with all requested tags set to error
//
//    Type type = (Type) projection.type();
//    Data.Builder builder = type.createDataBuilder();
//
//    for (final ReqOutputTagProjectionEntry tpe : projection.tagProjections().values()) {
//      builder._raw().setError((Type.Tag) tpe.tag(), errorValue);
//    }
//
//    return builder;
//  }
}
