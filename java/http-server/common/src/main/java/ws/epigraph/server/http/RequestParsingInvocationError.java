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
import ws.epigraph.psi.PsiProcessingMessage;
import ws.epigraph.schema.operations.OperationKind;
import ws.epigraph.util.HttpStatusCode;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class RequestParsingInvocationError extends PsiProcessingInvocationError
    implements HtmlCapableInvocationError {

  private final @NotNull String resourceName;
  private final @NotNull OperationKind operationKind;
  private final @Nullable String operationName;
  private final @NotNull String request;
  private final @NotNull List<PsiProcessingMessage> errors;

  public RequestParsingInvocationError(
      @NotNull String resourceName,
      @NotNull OperationKind operationKind,
      @Nullable String operationName,
      @NotNull String request,
      @NotNull List<PsiProcessingMessage> errors) {

    this.resourceName = resourceName;
    this.operationKind = operationKind;
    this.operationName = operationName;
    this.request = request;
    this.errors = errors;
  }

  @Override
  public int statusCode() { return HttpStatusCode.BAD_REQUEST; }

  @Override
  public @NotNull String message() {
    return message("\n\n", psiParsingErrorsReport(new StringBuilder(), request, errors, false));
  }

  @Override
  public @NotNull String htmlMessage() {
    return message("<br/><br/>", psiParsingErrorsReport(new StringBuilder(), request, errors, true));
  }

  private @NotNull String message(String newLine, String errorsReport) {
    return String.format(
        "Failed to parse %s%s operation %srequest in resource '%s'%s%s",
        operationName == null ? "default " : "",
        operationKind.toString().toLowerCase(),
        operationName == null ? "" : "'"+operationName+"' ",
        resourceName,
        newLine,
        errorsReport
    );
  }

}
