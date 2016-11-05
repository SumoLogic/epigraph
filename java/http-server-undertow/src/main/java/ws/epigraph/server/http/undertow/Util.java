/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.server.http.undertow;

import com.intellij.psi.PsiErrorElement;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import io.undertow.util.URLUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.server.http.routing.OperationSearchFailure;
import ws.epigraph.service.operations.Operation;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ws.epigraph.server.http.undertow.Constants.HTML;
import static ws.epigraph.server.http.undertow.Constants.TEXT;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Util {
  @NotNull
  static String getDecodedRequestString(@NotNull HttpServerExchange exchange) {
    // any way to disable request parsing in Undertow? we don't really need it..

    final String uri = exchange.getRequestURI(); // this doesn't include query params?!
    final String queryString = exchange.getQueryString();

    final String encodedReq;

    if (queryString == null || queryString.isEmpty()) encodedReq = uri;
    else encodedReq = uri + "%3f" + queryString; // question mark gets removed

    return URLUtils.decode(
        encodedReq,
        exchange.getConnection().getUndertowOptions().get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name()),
        false,
        new StringBuilder()
    );
  }

  static void badRequest(@Nullable String message, @NotNull String contentType, @NotNull HttpServerExchange exchange) {
    writeResponse(StatusCodes.BAD_REQUEST, message, contentType, exchange);
  }

  static void serverError(
      @Nullable String message,
      @NotNull String contentType,
      @NotNull HttpServerExchange exchange) {
    writeResponse(StatusCodes.INTERNAL_SERVER_ERROR, message, contentType, exchange);
  }

  static void writeResponse(
      int statusCode,
      @Nullable String response,
      @NotNull String contentType,
      @NotNull HttpServerExchange exchange) {

    exchange.setStatusCode(statusCode);
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);
    if (response != null) exchange.getResponseSender().send(response);
  }

  static void reportOperationSearchFailureAndFail(
      @NotNull String text,
      @NotNull OperationSearchFailure<?> failure,
      @NotNull HttpServerExchange exchange) throws RequestFailedException {

    StringBuilder sb = new StringBuilder();
    final boolean isHtml = htmlAccepted(exchange);
    final Map<? extends Operation<?, ?, ?>, List<PsiProcessingError>> failedOperations = failure.errors();

    if (failedOperations.size() == 1) {
      reportPsiProcessingErrors(text, failedOperations.values().iterator().next(), exchange);
    } else {
      sb.append("Operation matching failure");

      nl(sb, 2, isHtml);

      boolean first = true;
      for (final Map.Entry<? extends Operation<?, ?, ?>, List<PsiProcessingError>> entry : failedOperations.entrySet()) {
        if (first) first = false;
        else sep(sb, isHtml);

        final Operation<?, ?, ?> operation = entry.getKey();
        final List<PsiProcessingError> errors = entry.getValue();

        sb.append("Operation declared at ").append(operation.declaration().location());
        sb.append(" was not picked because of the following errors:");

        nl(sb, 1, isHtml);
        reportPsiProcessingErrors(text, errors, exchange);
      }

      badRequest(sb.toString(), isHtml ? HTML : TEXT, exchange);
    }

    throw RequestFailedException.INSTANCE;
  }

  @NotNull
  static List<PsiProcessingError> psiErrorsToPsiProcessingErrors(@NotNull List<PsiErrorElement> errors) {
    return errors.stream().map(e -> new PsiProcessingError(e.getErrorDescription(), e)).collect(Collectors.toList());
  }

  static void reportPsiProcessingErrorsAndFail(
      @NotNull String text,
      @NotNull List<PsiProcessingError> errors,
      @NotNull HttpServerExchange exchange) throws RequestFailedException {

    if (!errors.isEmpty()) {
      reportPsiProcessingErrors(text, errors, exchange);
      throw RequestFailedException.INSTANCE;
    }
  }

  static void reportPsiProcessingErrors(
      @NotNull String text,
      @NotNull List<PsiProcessingError> errors,
      @NotNull HttpServerExchange exchange) {

    StringBuilder sb = new StringBuilder();
    boolean isHtml = htmlAccepted(exchange);

    if (isHtml) appendHtmlErrorHeader(sb);

    boolean first = true;
    for (final PsiProcessingError error : errors) {
      if (first) first = false;
      else sep(sb, isHtml);

      reportPsiProcessingError(text, error, isHtml, sb);
    }

    if (isHtml) appendHtmlErrorFooter(sb);

    badRequest(sb.toString(), isHtml ? HTML : TEXT, exchange);
  }

  static void reportPsiProcessingError(
      @NotNull String text,
      @NotNull PsiProcessingError error,
      boolean isHtml,
      @NotNull StringBuilder sb) {

    @NotNull TextLocation textRange = error.location();
    String errorDescription = error.message();

    if (isHtml)
      addPsiErrorHtml(sb, text, textRange, errorDescription);
    else
      addPsiErrorPlainText(sb, text, textRange, errorDescription);
  }


  static void addPsiErrorPlainText(
      StringBuilder sb,
      String projectionString,
      TextLocation textLocation,
      String errorDescription) {

    final int startOffset = textLocation.startOffset();
    final int endOffset = textLocation.endOffset();

    sb.append(errorDescription).append("\n\n");

    if (startOffset != 0 || endOffset != 0) {
      int i = 0;

      sb.append(projectionString).append('\n');


      while (i < startOffset) {
        sb.append(' ');
        i++;
      }

      while (i < endOffset) {
        sb.append('^');
        i++;
      }

      if (startOffset == endOffset) sb.append('^'); // fix grammar so this never happens?

      sb.append('\n');
    }
  }

  static void addPsiErrorHtml(
      StringBuilder sb,
      String projectionString,
      TextLocation textLocation,
      String errorDescription) {

    int startOffset = textLocation.startOffset();
    int endOffset = textLocation.endOffset();

    sb.append(errorDescription).append("<br/><br/>");

    if (startOffset != 0 || endOffset != 0) {
      if (startOffset == endOffset) {
        endOffset++;
        if (endOffset > projectionString.length()) {
          projectionString += " "; // to have something to point to at the end of the string
        }
      }

      String s1 = projectionString.substring(0, startOffset);
      String s2 = projectionString.substring(startOffset, endOffset);
      String s3 = projectionString.substring(endOffset);

      sb.append(s1);
      sb.append("<div class=\"err\">").append(s2).append("</div>");
      sb.append(s3);

      sb.append("<br/>");
    }
  }

  static void nl(StringBuilder sb, int n, boolean isHtml) {
    for (int i = 0; i < n; i++) {
      sb.append(isHtml ? "<br/>" : "\n");
    }
  }

  static void sep(StringBuilder sb, boolean isHtml) {
    if (isHtml)
      sb.append("<br/><hr width=\"90%\"/><br/>");
    else
      sb.append("\n---------------------------------------\n\n");
  }

  static void appendHtmlErrorHeader(@NotNull StringBuilder sb) {
    sb.append("<!DOCTYPE html><head><style>")
      .append("body {")
      .append("  font-family: monospace;")
      .append("}")
      .append("")
      .append(".err {")
      .append("  border-bottom:2px dotted red;")
      .append("  display: inline-block;")
      .append("  position: relative;")
      .append("}")
      .append("")
      .append(".err:after {")
      .append("  content: '';")
      .append("  width: 100%;")
      .append("  height: 5px;")
      .append("  border-bottom:2px dotted red;")
      .append("  position: absolute;")
      .append("  bottom: -3px;")
      .append("  left: -2px;")
      .append("  display: inline-block;")
      .append("}")
      .append("</style></head><body>");
  }

  static void appendHtmlErrorFooter(@NotNull StringBuilder sb) {
    sb.append("</body>");
  }

  static boolean htmlAccepted(@NotNull HttpServerExchange exchange) {
    final HeaderValues contentTypeHeader = exchange.getRequestHeaders().get(Headers.ACCEPT);
    if (contentTypeHeader == null) return false;
    for (String header : contentTypeHeader) if (header.toLowerCase().contains(HTML)) return true;
    return false;
  }

}
