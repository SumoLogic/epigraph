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
import ws.epigraph.lang.TextLocation;
import ws.epigraph.psi.PsiProcessingError;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class PsiProcessingInvocationError {
  protected static @NotNull String psiParsingErrorsReport(
      @NotNull String text,
      @NotNull List<PsiProcessingError> errors,
      boolean isHtml) {

    StringBuilder sb = new StringBuilder();
    if (isHtml) appendHtmlErrorHeader(sb);

    boolean first = true;
    for (final PsiProcessingError error : errors) {
      if (first) first = false;
      else sep(sb, isHtml);

      addPsiProcessingError(text, error, isHtml, sb);
    }

    if (isHtml) appendHtmlErrorFooter(sb);

    return sb.toString();
  }

  private static void addPsiProcessingError(
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
      @NotNull StringBuilder sb,
      @NotNull String projectionString,
      @NotNull TextLocation textLocation,
      @NotNull String errorDescription) {

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

  private static void addPsiErrorHtml(
      @NotNull StringBuilder sb,
      @NotNull String projectionString,
      @NotNull TextLocation textLocation,
      @NotNull String errorDescription) {

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


  protected static void sep(StringBuilder sb, boolean isHtml) {
    if (isHtml)
      sb.append("<br/><hr width=\"90%\"/><br/>");
    else
      sb.append("\n---------------------------------------\n\n");
  }

  private static void appendHtmlErrorHeader(@NotNull StringBuilder sb) {
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

  private static void appendHtmlErrorFooter(@NotNull StringBuilder sb) { sb.append("</body>"); }
}
